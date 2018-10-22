package tools.android.retryrequest;

public interface Result2<T> {
    void onResult2(boolean success, T t, ErrorCode error2);
}
