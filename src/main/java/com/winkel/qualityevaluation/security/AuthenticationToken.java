package com.winkel.qualityevaluation.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * @ClassName AuthenticationToken
 * @Description
 * @Author zyx
 * @Date 2020/4/14 19:51
 * @Blog www.winkelblog.top
 */

public class AuthenticationToken extends AbstractAuthenticationToken {

    private String jwt;

//    private List<Authority> authorities;

    public String getJwt() {
        return jwt;
    }

//    public void setAuthorities(List<Authority> authorities) {
//        this.authorities = authorities;
//    }


//    @Override
//    public List getAuthorities() {
//        return authorities;
//    }

    public AuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
    }

//    public AuthenticationToken(String jwt, boolean isAuthenticated) {
//        super(null);
//        this.jwt = jwt;
//        setAuthenticated(isAuthenticated);
//    }

    public AuthenticationToken(String jwt, Object details) {
        super(null);
        this.jwt = jwt;
        setDetails(details);
    }


//    public AuthenticationToken(Object user) {
//        super(null);
//        super.setDetails(user);
//    }

    public AuthenticationToken(String jwt, Collection<? extends GrantedAuthority> authorities, Object details) {
        super(authorities);
        this.jwt = jwt;
        super.setDetails(details);
    }


//    public AuthenticationToken(Object user, boolean isAuthenticated) {
//        super(null);
//        super.setDetails(user);
//        setAuthenticated(isAuthenticated);
//    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.getDetails();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
