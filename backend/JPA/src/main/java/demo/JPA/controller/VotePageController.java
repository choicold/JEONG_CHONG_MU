package demo.JPA.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VotePageController {
    @GetMapping("/vote/{uuid}")
    public String showVotePage(@PathVariable String uuid) {
        return "forward:/votePage.html"; // 실제 파일 이름으로 포워딩
    }
}