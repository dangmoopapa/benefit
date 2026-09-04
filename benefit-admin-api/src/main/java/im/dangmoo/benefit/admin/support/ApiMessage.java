package im.dangmoo.benefit.admin.support;

public enum ApiMessage {

    SUCCESS("성공"),
    INTERNAL_ERROR("일시적인 오류가 발생했습니다"),
    NOT_FOUND("대상을 찾을 수 없습니다"),
    DUPLICATE_CODE("이미 존재하는 쿠폰 코드입니다"),
    INVALID_STATUS("현재 상태에서는 수정할 수 없습니다");

    private final String message;

    ApiMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
