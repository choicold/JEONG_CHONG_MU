package demo.JPA.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public ResponseEntity<String> healthCheck() {
        // 로드 밸런서의 상태 검사에 200 OK 응답을 보내기 위한 컨트롤러
        return ResponseEntity.ok("OK");
    }
}