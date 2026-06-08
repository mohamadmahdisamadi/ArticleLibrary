package ir.ac.ut.ece.ie.server.common.resultpattern;


public class ApiResult {
    private final static String defaultSuccessMessage = "Beyond all the ideas of right and wrong there's a field, I'll meet you there";
    private final static String defaultFailureMessage = "";
    private boolean isSuccess;
    private String message;

    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return message; }

    public void setIsSuccess(boolean isSuccess) { this.isSuccess = isSuccess; }
    public boolean getIsSuccess() { return isSuccess; }

    public ApiResult() {}

    public ApiResult(boolean isSuccess, String message) {
        setMessage(message);
        setIsSuccess(isSuccess);
    }

    public static ApiResult Succeed() {
        return new ApiResult(true, defaultSuccessMessage);
    }

    public static ApiResult Failure() {
        return new ApiResult(false, defaultFailureMessage);
    }

    public static ApiResult Failure(String message) {
        return new ApiResult(false, message);
    }

    public String toString() {
        return new StringBuilder()
            .append("{\"isSuccess\":")
            .append(isSuccess)
            .append(",\"message\":\"")
            .append(message)
            .append("\"")
            .append("}")
            .toString();
    }
}
