package im.dangmoo.benefit.api.support;

public enum ApiMessage {

    SUCCESS("성공"),
    INTERNAL_ERROR("일시적인 오류가 발생했습니다"),
    NOT_FOUND("대상을 찾을 수 없습니다"),
    INVALID_STATUS("현재 상태에서는 처리할 수 없습니다"),
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
