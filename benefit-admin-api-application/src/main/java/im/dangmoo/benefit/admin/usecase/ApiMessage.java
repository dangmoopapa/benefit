package im.dangmoo.benefit.admin.usecase;

public enum ApiMessage {

    SUCCESS("성공"),
    NOT_FOUND("대상을 찾을 수 없습니다"),
    DUPLICATE_KEY("이미 존재하는 키입니다"),
    INVALID_STATUS("현재 상태에서는 처리할 수 없습니다"),
    CONDITION_NOT_SATISFIED("조건을 만족하지 않습니다");

    private final String message;

    ApiMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
