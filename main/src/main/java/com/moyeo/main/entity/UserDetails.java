package com.moyeo.main.entity;

import java.io.Serializable;

public interface UserDetails extends Serializable {
//    Collection<? extends GrantedAuthority> getAuthorities();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
