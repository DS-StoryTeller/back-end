package com.cojac.storyteller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")  // 소셜 로그인을 테스트하기 위한 페이지입니다.
    public String loginPage() {
        return "login";
    }
}
