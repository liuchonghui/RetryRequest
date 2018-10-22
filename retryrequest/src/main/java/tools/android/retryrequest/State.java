package tools.android.retryrequest;

public class State<T> {
    public boolean success = false;
    public T t;
    public ErrorCode error;
}
