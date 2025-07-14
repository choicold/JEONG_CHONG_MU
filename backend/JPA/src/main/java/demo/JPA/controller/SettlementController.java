package demo.JPA.controller;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.dto.SettlementCreateResponseDto;
import demo.JPA.dto.SettlementDetailResponseDto;
import demo.JPA.service.SettlementProcessService;
import demo.JPA.service.SettlementDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // GetMapping, PostMapping 등 포함

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settlements") // ✨ [통합] API 경로를 '정산' 리소스 중심으로 표준화
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementProcessService settlementProcessService;
    private final SettlementDetailService settlementDetailService;

    /**
     * 신규 정산 생성 API
     * @PostMapping: 리소스를 생성하므로 POST 메소드 사용
     */
    @PostMapping
    public ResponseEntity<SettlementCreateResponseDto> createSettlement(@RequestBody SettlementCreateRequestDto requestDto) {
        // 서비스 호출하여 정산 생성 프로세스 처리
        String settlementUrl = settlementProcessService.createSettlementProcess(requestDto);
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

//    /**
//     * 정산 목록 조회 API
//     * @GetMapping: 리소스를 조회하므로 GET 메소드 사용
//     */
//    @GetMapping
//    public ResponseEntity<List<SettlementSimpleResponseDto>> getSettlementList() {
//        List<SettlementSimpleResponseDto> responseDtoList = settlementListService.getSettlementsForCurrentUser();
//        return ResponseEntity.ok(responseDtoList);
//    }

    // 향후 수정(PUT), 삭제(DELETE) 기능이 추가되어도 이 컨트롤러에 메소드를 추가하면 됩니다.
}