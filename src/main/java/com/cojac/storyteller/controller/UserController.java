package com.cojac.storyteller.controller;

import com.cojac.storyteller.code.ResponseCode;
import com.cojac.storyteller.dto.response.ResponseDTO;
import com.cojac.storyteller.dto.user.UserDTO;
import com.cojac.storyteller.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(UserDTO userDTO) {
        UserDTO res = userService.registerUser(userDTO);
        return ResponseEntity
                .status(ResponseCode.SUCCESS_REGISTER.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_REGISTER, res));
    }

    // 로그인 이후 유저 아이디 및 role 확인 방법
    @GetMapping("test")
    public ResponseEntity test() {
        // 사용자 아이디(username)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 사용자 role
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        String res = String.format("{username: %s, role: %s}", username, role);

        return ResponseEntity
                .status(ResponseCode.SUCCESS_TEST.getStatus().value())
                .body(new ResponseDTO<>(ResponseCode.SUCCESS_TEST, res));
    }

}
