package im.dangmoo.benefit.admin.usecase;

public enum ApiMessage {

    SUCCESS("성공"),
    NOT_FOUND("대상을 찾을 수 없습니다"),
    DUPLICATE_KEY("이미 존재하는 키입니다"),
    INVALID_STATUS("현재 상태에서는 처리할 수 없습니다"),
    CONDITION_NOT_SATISFIED("조건을 만족하지 않습니다"),
    INSUFFICIENT_POINT("포인트가 부족합니다"),
    STOCK_EXHAUSTED("수량이 소진되었습니다"),
    PREPARING_MEMBERSHIP("준비 중인 멤버십입니다"),
    INVALID_PROMOTION_FEATURE("프로모션 특수 기능이 올바르지 않습니다"),
    ALREADY_DRAWN_PROMOTION("이미 추첨이 완료된 프로모션입니다"),
    LOTTERY_NOT_READY("추첨할 수 없는 상태입니다");

    private final String message;

    ApiMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
