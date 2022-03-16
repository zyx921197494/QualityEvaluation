package com.winkel.qualityevaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class User implements UserDetails, Serializable {

    public static final long serialVersionUID = 8750539009428688836L;

    private String id;
    private String username;
    private String password;
    private String email;
    private String locationCode;
    private String schoolCode;
    private Integer isLocked;
    private LocalDateTime createTime;

    //权限集合
    private List<Authority> authorities;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String id, String username, String password, Integer isLocked, LocalDateTime createTIme) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLocked = isLocked;
        this.createTime = createTIme;
    }

    public User(String id, String username, String password, String locationCode, Integer isLocked, LocalDateTime createTIme) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.locationCode = locationCode;
        this.isLocked = isLocked;
        this.createTime = createTIme;
    }




    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
