package ir.ac.ut.ece.ie.api.common.resultpattern;

public class ApiResult<T> {
    private static final String DEFAULT_SUCCESS_MESSAGE = "Beyond all the ideas of right and wrong there's a field, I'll be meeting you there";
    private static final String DEFAULT_FAILURE_MESSAGE = "To be is not the way to be";
    private boolean success;
    private String message;
    private T data;

    private ApiResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public String getMessage() { return message; }
    public boolean getSuccess() { return success; }
    public T getData() { return data; }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(true, message, data);
    }
    public static <T> ApiResult<T> success(T data) {
        return success(DEFAULT_SUCCESS_MESSAGE, data);
    }
    public static ApiResult success() {
        return success(DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static ApiResult failure(String message) {
        return new ApiResult<>(false, message, null);
    }
    public static ApiResult failure() {
        return failure(DEFAULT_FAILURE_MESSAGE);
    }
}
