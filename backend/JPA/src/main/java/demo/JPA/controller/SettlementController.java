package demo.JPA.controller;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Settlement;
import demo.JPA.service.SettlementProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 클래스가 RESTful API의 컨트롤러임을 알립니다.
@RequestMapping("/settlement") // 이 컨트롤러의 모든 메서드는 /settlement 경로를 기본으로 합니다.
@RequiredArgsConstructor // final SettlementService 필드에 대한 생성자 주입(DI)
public class SettlementController {

    //DI 추가
    private final SettlementProcessService settlementProcessService;



    @PostMapping
    public ResponseEntity<Settlement> createSettlement(@RequestBody SettlementCreateRequestDto requestDto) {

        Settlement createdSettlement = settlementProcessService.createSettlementProcess(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSettlement);
    }
}