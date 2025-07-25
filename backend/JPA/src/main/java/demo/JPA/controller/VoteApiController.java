package demo.JPA.controller;
import demo.JPA.dto.*;
import demo.JPA.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import demo.JPA.dto.ParticipantChoicesResponseDto; // ✨ [추가] DTO 임포트 (아래에서 생성)
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vote")
public class VoteApiController {

    private final VotePageLoadService votePageLoadService;
    private final VoteSubmitService voteSubmitService;

    // 투표 페이지에 필요한 데이터를 제공하는 API
    @GetMapping("/{uuid}")
    public ResponseEntity<VotePageLoadDto> getVoteData(@PathVariable UUID uuid) {
        VotePageLoadDto voteData = votePageLoadService.getVotePageData(uuid);
        return ResponseEntity.ok(voteData);
    }

    // 투표 결과를 제출받아 처리하는 API
    @PostMapping("/{uuid}")
    public ResponseEntity<String> submitVote(@PathVariable UUID uuid, @RequestBody VoteSubmitRequestDto requestDto) {
        try {
            String message = voteSubmitService.submitVote(uuid, requestDto);
            return ResponseEntity.ok(message);
        } catch (IllegalStateException e) {
            // 중복 제출과 같은 비즈니스 로직 에러
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        } catch (IllegalArgumentException e) {
            // 잘못된 요청 데이터
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /**
     * ✨ [추가] 특정 참여자의 기존 투표 내역을 조회하는 API
     */
    @GetMapping("/{uuid}/choices")
    public ResponseEntity<ParticipantChoicesResponseDto> getParticipantChoices(
            @PathVariable UUID uuid,
            @RequestParam("name") String participantName) {

        ParticipantChoicesResponseDto choices = votePageLoadService.getParticipantChoices(uuid, participantName);
        return ResponseEntity.ok(choices);
    }
}