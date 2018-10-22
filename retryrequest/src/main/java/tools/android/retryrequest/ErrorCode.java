package tools.android.retryrequest;

public enum ErrorCode {
    UNKNOWN(0), // unknown
    // 请求网络原因
    REQUEST_CONNECTION_ERROR(1), // 各种设备当前网络原因（SocketTimeoutException／SocketException）
    REQUEST_SERVER_ERROR(2), // 服务端错误（FileNotFoundException服务器找不到文件）
    REQUEST_OTHER_ERROR(3), // 其他错误（其他Exception，可能其中也有属于网络原因，需要继续确认）

    // 请求结果原因
    REQUEST_NO_RESPONSE_BODY(4), // response == null || response.body() == null
    REQUEST_RESPONSE_CODE_NOT_200(5), // response.code != 200
    REQUEST_JSON_GSON_ERROR(6), // 无法gson成对象
    REQUEST_OBJECT_NO_DATA(7), // 解析结果是"{}"空对象
    REQUEST_OBJECT_NOT_LEGAL(8), // isLegal() == false;
    // 极限值
    MAX_ERROR_VALUE(9); // could not happen

    private int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }

    public static int formatErrorCode(ErrorCode firstError, ErrorCode secondError) {
        // 注意：需要查看ErrorCode继续定位出两次问题都是什么
        int errorCode = 0;
        int firstCode, secondCode;
        if (firstError == null) {
            firstCode = UNKNOWN.code();
        } else {
            firstCode = firstError.code();
        }
        if (firstCode > MAX_ERROR_VALUE.code()) {
            firstCode = MAX_ERROR_VALUE.code();
        }
        if (secondError == null) {
            secondCode = UNKNOWN.code();
        } else {
            secondCode = secondError.code();
        }
        if (secondCode > MAX_ERROR_VALUE.code()) {
            secondCode = MAX_ERROR_VALUE.code();
        }
        return errorCode - firstCode - 10 * secondCode;
    }
}
