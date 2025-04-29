package com.gtel.srpingtutorial.entity;

import com.gtel.srpingtutorial.entity.converters.UserStatusConverter;
import com.gtel.srpingtutorial.utils.USER_STATUS;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Table(name = "roles")
@Entity
public class RoleEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;
}
