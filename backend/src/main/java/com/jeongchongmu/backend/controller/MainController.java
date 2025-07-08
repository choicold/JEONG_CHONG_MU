package com.jeongchongmu.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
/*
 * @RestController → 백엔드애서의 테스트를 위한 시각화를 html로 구현하려고 @Controller를 사용했지만,
 * 프론트엔드 화면이 구현되면 이 어노테이션으로 변경해야 함
 * @Controller는 응답 형태가 html인 반면에 @RestController는  JSON/XML 형태이기 때문
 */
@RequestMapping("/main")
public class MainController {
    @GetMapping
    public String mainPage() {
        return "main";
    }
}