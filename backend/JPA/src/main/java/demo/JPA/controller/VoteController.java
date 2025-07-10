package demo.JPA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VoteController {

    /**
     * '/vote/{token}' 경로로 들어오는 요청을 받아
     * 실제 정적 파일인 'votePage.html'로 전달(forward)합니다.
     * 브라우저의 URL은 바뀌지 않습니다.
     */
    @GetMapping("/vote/{token}")
    public String showVotePage(@PathVariable String token) {
        return "forward:/votePage.html";
    }
}