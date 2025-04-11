package com.gtel.srpingtutorial.service;

import com.gtel.srpingtutorial.domains.OtpDomain;
import com.gtel.srpingtutorial.entity.UserEntity;
import com.gtel.srpingtutorial.exception.ApplicationException;
import com.gtel.srpingtutorial.model.request.ConfirmOtpRegisterRequest;
import com.gtel.srpingtutorial.model.request.RegisterRequest;
import com.gtel.srpingtutorial.model.response.RegisterResponse;
import com.gtel.srpingtutorial.redis.entities.RegisterUserEntity;
import com.gtel.srpingtutorial.redis.repository.RegisterUserRedisRepository;
import com.gtel.srpingtutorial.repository.UserRepository;
import com.gtel.srpingtutorial.utils.ERROR_CODE;
import com.gtel.srpingtutorial.utils.PhoneNumberUtils;
import com.gtel.srpingtutorial.utils.USER_STATUS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService  extends BaseService{

    private final OtpDomain otpDomain;

    private final UserRepository userRepository;

    private final RegisterUserRedisRepository registerUserRedisRepositoy;

    public UserService(OtpDomain otpDomain, UserRepository userRepository, RegisterUserRedisRepository registerUserRedisRepository) {
        this.otpDomain = otpDomain;

        this.userRepository = userRepository;

        this.registerUserRedisRepositoy = registerUserRedisRepository;

    }

    public RegisterResponse registerUser(RegisterRequest request) throws ApplicationException {

        //validate request
        this.validateUserRegisterRequest(request);

        // check user exist on db
        String phoneNumber = PhoneNumberUtils.validatePhoneNumber(request.getPhoneNumber());
        log.info("[registerUser] - user register with phone {} START", phoneNumber);

        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber);

        if (userEntity != null){
            log.info("[registerUser] request fail : user already exists with phone {}", phoneNumber);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "PhoneNumber is already exists");
        }
        // otp gen
        RegisterUserEntity otpEntity = otpDomain.genOtpWhenUserRegister(phoneNumber, request.getPassword());
        //log.debug("[registerUser] - user register with phone entity {} ", otpEntity);
        log.info("[registerUser] - user register with phone {} DONE", request.getPhoneNumber());
        return new RegisterResponse(otpEntity);
    }

    public RegisterResponse resendOTP(String transactionId) {

        log.info("[resendOTP] - resend OTP with transaction id {} START", transactionId);
        Optional<RegisterUserEntity> registerUser = registerUserRedisRepositoy.findById(transactionId);

        //check transaction exists
        if (registerUser.isEmpty()){
            log.info("[resendOTP] request fail: transaction with transaction id {} is not exists", transactionId);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Transaction is not exists");
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime < registerUser.get().getOtpResendTime()) {
            log.info("[resendOTP] request fail: resend too early for transaction id {}", transactionId);
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Resend OTP too early. Please wait.");
        }

        //gen otp
        RegisterUserEntity otpEntity = otpDomain.genOtpWhenResend(transactionId, registerUser.get());

        log.info("[resendOTP] - resend OTP with transaction id {} DONE", transactionId);
        return new RegisterResponse(otpEntity);
    }

    public void confirmOtpRegister(ConfirmOtpRegisterRequest request) {

        //validate request
        this.validateConfirmOtpRegisterRequest(request);

        log.info("[confirmOtpRegister] - confirm OTP register with transaction id {} START", request.getTransactionId());
        Optional<RegisterUserEntity> registerUser = registerUserRedisRepositoy.findById(request.getTransactionId());

        //check transaction exists
        if (registerUser.isEmpty()){
            log.info("[confirmOtpRegister] request fail: transaction with transaction id {} is not exists", request.getTransactionId());
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "Transaction is not exists");
        }

        //check expire otp
        if (registerUser.get().getOtpExpiredTime() < System.currentTimeMillis()/1000) {
            log.info("[confirmOtpRegister] request fail: OTP expired with transaction id {}", request.getTransactionId());
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP has expired");
        }

        //check limit attempts
        if (registerUser.get().getOtpFail() >= 5) {
            log.info("[confirmOtpRegister] request fail: reached limit input OTP attempts with transaction id {}", request.getTransactionId());
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "input OTP attempts limit reached");
        }

        //check correct otp
        if(!request.getOtp().equals(registerUser.get().getOtp())) {
            registerUser.get().setOtpFail(registerUser.get().getOtpFail() + 1);
            registerUserRedisRepositoy.save(registerUser.get());
            log.info("[confirmOtpRegister] request fail: incorrect OTP with transaction id {}", request.getTransactionId());
            throw new ApplicationException(ERROR_CODE.INVALID_REQUEST, "OTP is incorrect");
        }

        //save user register data to database
        UserEntity user = new UserEntity();
        user.setPhoneNumber(registerUser.get().getPhoneNumber());
        user.setPassword(registerUser.get().getPassword());
        user.setStatus(USER_STATUS.ACTIVE);
        userRepository.save(user);

        //delete otp transaction in redis
        registerUserRedisRepositoy.delete(registerUser.get());

        log.info("[confirmOtpRegister] - confirm OTP register with transaction id {} END", request.getTransactionId());
    }


    protected void validateUserRegisterRequest(RegisterRequest request) throws ApplicationException {
        if (StringUtils.isBlank(request.getPhoneNumber())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "phoneNumber is invalid");
        }


        if (StringUtils.isBlank(request.getPassword())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "password is invalid");
        }

        com.gtel.srpingtutorial.utils.StringUtils.validatePassword(request.getPassword());
    }


    protected void validateConfirmOtpRegisterRequest(ConfirmOtpRegisterRequest request) throws ApplicationException {
        if (StringUtils.isBlank(request.getTransactionId())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "Transaction id is invalid");
        }


        if (StringUtils.isBlank(request.getOtp())) {
            throw new ApplicationException(ERROR_CODE.INVALID_PARAMETER , "OTP is invalid");
        }

    }
}
