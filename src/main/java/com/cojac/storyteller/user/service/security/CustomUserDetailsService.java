package com.cojac.storyteller.user.service.security;

import com.cojac.storyteller.user.dto.security.CustomUserDetails;
import com.cojac.storyteller.user.repository.LocalUserRepository;
import com.cojac.storyteller.user.entity.LocalUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final LocalUserRepository localUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        LocalUserEntity localUserEntity = localUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자 이름을 가진 사용자를 찾을 수 없습니다: " + username));

        if (localUserEntity != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(localUserEntity);
        }
        return null;
    }
}
