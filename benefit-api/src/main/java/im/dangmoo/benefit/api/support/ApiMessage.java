package im.dangmoo.benefit.api.support;

public enum ApiMessage {

    SUCCESS("성공"),
    INTERNAL_ERROR("일시적인 오류가 발생했습니다"),
    NOT_FOUND("대상을 찾을 수 없습니다");

    private final String message;

    ApiMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
