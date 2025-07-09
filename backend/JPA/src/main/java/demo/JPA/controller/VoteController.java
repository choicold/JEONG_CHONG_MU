package demo.JPA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VoteController {

    @GetMapping("/vote/{token}")
    public String getVotePage(@PathVariable("token") String uniqueLinkToken, Model model) {
        // 1. 토큰을 사용해서 Participant(참여자) 정보를 DB에서 조회합니다.
        // 2. 토큰이 유효하지 않으면, 에러 페이지를 보여줍니다.
        // 3. 토큰이 유효하면, 해당 참여자가 투표해야 할 항목들을 조회합니다.
        // 4. 조회한 정보를 모델(Model)에 담아 투표 페이지(HTML)로 전달합니다.
        return "votePage"; // votePage.html 템플릿을 렌더링
    }
}
