package com.gtel.srpingtutorial.entity;

import com.gtel.srpingtutorial.entity.converters.UserStatusConverter;
import com.gtel.srpingtutorial.utils.USER_STATUS;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "users")
@Entity
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;


    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Convert(converter = UserStatusConverter.class)
    private USER_STATUS status;

}
