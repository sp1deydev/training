package com.gtel.srpingtutorial.model.securities;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class CustomUserDetail extends UsernamePasswordAuthenticationToken {
    public CustomUserDetail(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public CustomUserDetail(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }


    public CustomUserDetail(Object principal) {
        super(principal, null,null);
    }
}