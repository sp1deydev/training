package com.gtel.srpingtutorial.model.securities;

import com.gtel.srpingtutorial.entity.UserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@Data
public class UserInfoDetails implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public UserInfoDetails(){

    }

    public UserInfoDetails(UserEntity entity){
        this.username = entity.getPhoneNumber();
        this.password = entity.getPassword();
        this.authorities = List.of();
    }
}
