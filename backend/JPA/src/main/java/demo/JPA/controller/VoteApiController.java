package demo.JPA.controller;

import demo.JPA.dto.VoteLoadPageDto;
import demo.JPA.dto.VoteSubmitRequestDto;
import demo.JPA.service.VoteLoadService;
import demo.JPA.service.VoteSubmitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VoteApiController {

    private final VoteLoadService voteLoadService;
    private final VoteSubmitService voteSubmitService;

    @GetMapping("/vote/{token}")
    public ResponseEntity<?> getVoteData(@PathVariable String token) {
        try {
            VoteLoadPageDto voteData = voteLoadService.getVotePageData(token);
            // 투표할 항목이 하나도 없는 경우
            if (voteData.receiptGroups().isEmpty()) {
                return ResponseEntity.ok().body("이미 모든 항목에 투표를 완료했습니다.");
            }
            return ResponseEntity.ok(voteData);
        } catch (IllegalArgumentException e) {
            // 서비스에서 토큰이 유효하지 않을 때 던진 예외를 처리
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            // 기타 서버 에러
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }
    }

    @PostMapping("/votes")
    public ResponseEntity<String> submitVote(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody VoteSubmitRequestDto requestDto) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            voteSubmitService.submitVote(token, requestDto);
            return ResponseEntity.ok("투표가 성공적으로 제출되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict: 이미 처리된 요청
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 내부 오류가 발생했습니다.");
        }
    }
}