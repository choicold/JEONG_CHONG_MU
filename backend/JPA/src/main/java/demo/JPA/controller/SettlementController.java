package demo.JPA.controller;

import demo.JPA.config.security.PrincipalDetails;
import demo.JPA.dto.*;
import demo.JPA.entity.Member;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.SettlementRepository;
import demo.JPA.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*; // GetMapping, PostMapping 등 포함

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/settlements") // ✨ [통합] API 경로를 '정산' 리소스 중심으로 표준화
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementProcessService settlementProcessService;
    private final SettlementDetailService settlementDetailService;
    private final SettlementSearchDataService settlementSearchDataService;
    private final SettlementListService settlementListService;
    private final VoteSubmitService voteSubmitService;
    private final SettlementFinalizationService settlementFinalizationService;
    private final SettlementRepository settlementRepository;
    private final SettlementCorrectionService settlementCorrectionService;

    /**
     * 신규 정산 생성 API
     * @PostMapping: 리소스를 생성하므로 POST 메소드 사용
     */
    @PostMapping
    public ResponseEntity<SettlementCreateResponseDto> createSettlement(@RequestBody SettlementCreateRequestDto requestDto,
                                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 서비스 호출하여 정산 생성 프로세스 처리
        Member host = principalDetails.getMember();

        String settlementUrl = settlementProcessService.createSettlementProcess(requestDto, host);
        SettlementCreateResponseDto responseDto = new SettlementCreateResponseDto(settlementUrl);//json 생성
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 특정 정산 상세 정보 조회 API
     * 어플의 상세페이지 api
     * @GetMapping: 리소스를 조회하므로 GET 메소드 사용
     *
     */
    @GetMapping("/{settlementUuid}")
    public ResponseEntity<SettlementDetailResponseDto> getSettlementDetail(@PathVariable UUID settlementUuid) {
        SettlementDetailResponseDto responseDto = settlementDetailService.getSettlementDetail(settlementUuid);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * ✨ [추가] 앱 검색창에 필요한 정산 데이터 목록 전체를 제공하는 API
     * @param principalDetails 인증된 사용자 정보 (Access Token에서 추출)
     * @return 검색에 필요한 데이터 필드를 담은 JSON 리스트
     */
    @GetMapping("/search-data")
    public ResponseEntity<List<SettlementSearchResponseDto>> getSettlementDataForSearch(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long userId = principalDetails.getMember().getId();
        List<SettlementSearchResponseDto> searchData = settlementSearchDataService.getSettlementSearchDataForUser(userId);
        return ResponseEntity.ok(searchData);
    }

    /**
     * ✨ [추가] 인증된 사용자(나)의 모든 정산 목록을 조회하는 API
     * @param principalDetails Access Token을 통해 주입되는 사용자 정보
     * @return 사용자의 정산 목록
     */
    @GetMapping("/my-list")
    public ResponseEntity<List<SettlementSimpleResponseDto>> getMySettlements(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long userId = principalDetails.getMember().getId();
        List<SettlementSimpleResponseDto> result = settlementListService.getSettlementsForAuthenticatedUser(userId);
        return ResponseEntity.ok(result);
    }

//    // 총무를 위한 인앱 투표 제출 API
//    @PostMapping("/{settlementUuid}/vote")
//    public ResponseEntity<String> submitVoteAuthenticated(
//            @PathVariable UUID settlementUuid,
//            @AuthenticationPrincipal PrincipalDetails principalDetails,
//            @RequestBody AuthenticatedVoteSubmitRequestDto requestDto) {
//
//        String message = voteSubmitService.submitVote(settlementUuid, requestDto, principalDetails.getMember());
//        return ResponseEntity.ok(message);
//    }

    // 총무가 최종 정산 전 참여자 이름과 OcrItem(true/false)만 수정하는 API
    @PutMapping("/{settlementUuid}/corrections")
    public ResponseEntity<Void> applyFinalCorrections(
            @PathVariable UUID settlementUuid,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody FinalCorrectionRequestDto requestDto) {

        // 1. DB에서 정산 정보 조회
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new EntityNotFoundException("해당 UUID의 정산을 찾을 수 없습니다: " + settlementUuid));

        // 2. 현재 로그인한 사용자가 정산의 호스트인지 검증
        if (!settlement.getHostMember().getId().equals(principalDetails.getMember().getId())) {
            throw new AccessDeniedException("이 정산을 수정할 권한이 없습니다.");
        }

        // 3. 이름 및 OcrItem(true/fals) 수정 서비스 호출
        settlementCorrectionService.applyFinalCorrections(requestDto);

        return ResponseEntity.ok().build();
    }

    // 최종 정산 토스 링크 생성
    @PostMapping("/{settlementUuid}/finalize")
    public ResponseEntity<Map<String, String>> finalizeSettlement(@PathVariable UUID settlementUuid) {

        String finalLink = settlementFinalizationService.finalizeAndGenerateLink(settlementUuid);
        Map<String, String> response = Map.of("finalResultLink", finalLink);
        return ResponseEntity.ok(response);
    }

    // 정산 삭제 API
    @DeleteMapping("/{settlementUuid}")
    public ResponseEntity<Void> deleteSettlement(
            @PathVariable UUID settlementUuid,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        settlementProcessService.deleteSettlement(settlementUuid, principalDetails.getMember());

        // 삭제가 완료되면 204 No Content 상태를 반환
        return ResponseEntity.noContent().build();
    }
}