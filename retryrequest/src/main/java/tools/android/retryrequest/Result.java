package tools.android.retryrequest;

public interface Result<T> {
    void onSuccess(T t);
    void onFailure(int code);
}
