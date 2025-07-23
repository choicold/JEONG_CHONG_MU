package demo.JPA.controller;

import demo.JPA.dto.SettlementResultDto;
import demo.JPA.service.SettlementResultService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/final-result")
@RequiredArgsConstructor
public class FinalResultController {

    private final SettlementResultService settlementResultService;

    @PostMapping("/{settlementUuid}")
    public ResponseEntity<SettlementResultDto.ParticipantResult> getMyFinalResult(
            @PathVariable UUID settlementUuid,
            @RequestBody Map<String, String> requestBody) {

        String participantName = requestBody.get("participantName");
        if (participantName == null || participantName.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        SettlementResultDto fullResult = settlementResultService.calculateAndGetResult(settlementUuid);

        SettlementResultDto.ParticipantResult myResult = fullResult.getParticipantResults().stream()
                .filter(p -> p.getParticipantName().equals(participantName))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("해당 이름의 참여자를 찾을 수 없습니다."));

        return ResponseEntity.ok(myResult);
    }
}
