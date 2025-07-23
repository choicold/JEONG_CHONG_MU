package demo.JPA.entity;

public enum SettlementStatus {
    VOTING,
    COMPLETED, // 투표를 모두 제출했지만, 수정이 가능한 상태
    FINALIZED // 수정이 불가한 상태, 찐 최종
}