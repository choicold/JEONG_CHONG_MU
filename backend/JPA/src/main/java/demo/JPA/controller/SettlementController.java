package demo.JPA.controller;

import demo.JPA.config.security.PrincipalDetails;
import demo.JPA.dto.*;
import demo.JPA.service.SettlementListService;
import demo.JPA.service.SettlementProcessService;
import demo.JPA.service.SettlementDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*; // GetMapping, PostMapping 등 포함
import demo.JPA.service.SettlementSearchDataService;
import demo.JPA.config.security.PrincipalDetails;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settlements") // ✨ [통합] API 경로를 '정산' 리소스 중심으로 표준화
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementProcessService settlementProcessService;
    private final SettlementDetailService settlementDetailService;
    private final SettlementSearchDataService settlementSearchDataService;
    private final SettlementListService settlementListService;

    /**
     * 신규 정산 생성 API
     * @PostMapping: 리소스를 생성하므로 POST 메소드 사용
     */
    @PostMapping
    public ResponseEntity<SettlementCreateResponseDto> createSettlement(@RequestBody SettlementCreateRequestDto requestDto,
                                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 서비스 호출하여 정산 생성 프로세스 처리
        Long hostMemberId = principalDetails.getMember().getId();

        String settlementUrl = settlementProcessService.createSettlementProcess(requestDto, hostMemberId);
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
}