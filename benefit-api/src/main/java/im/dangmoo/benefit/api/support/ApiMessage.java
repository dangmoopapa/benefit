package im.dangmoo.benefit.api.support;

public enum ApiMessage {

    SUCCESS("성공"),
    INTERNAL_ERROR("일시적인 오류가 발생했습니다"),
    NOT_FOUND("대상을 찾을 수 없습니다"),
    INVALID_STATUS("현재 상태에서는 처리할 수 없습니다"),
    INSUFFICIENT_POINT("포인트가 부족합니다"),
    INVALID_AMOUNT("포인트 금액이 올바르지 않습니다"),
    ALREADY_REVOKED("이미 회수된 지급입니다"),
    ALREADY_RESTORED("이미 복구된 사용입니다"),
    ALREADY_ISSUED("이미 발급된 쿠폰입니다"),
    ISSUE_NOT_ALLOWED("발급 조건을 만족하지 않습니다"),
    USAGE_LIMIT_EXCEEDED("사용 한도를 초과했습니다");

    private final String message;

    ApiMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
