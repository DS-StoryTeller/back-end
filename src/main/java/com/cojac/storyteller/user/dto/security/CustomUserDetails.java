package com.cojac.storyteller.user.dto.security;

import com.cojac.storyteller.user.entity.LocalUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final LocalUserEntity localUserEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return localUserEntity.getRole();
            }
        });

        return collection;
    }

    public Integer getId() {
        return localUserEntity.getId();
    }

    public String getEmail() {

        return localUserEntity.getEmail();
    }

    @Override
    public String getPassword() {

        return localUserEntity.getPassword();
    }

    @Override
    public String getUsername() {

        return localUserEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
