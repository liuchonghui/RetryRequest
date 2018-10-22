package tools.android.retryrequest;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RetryRequest {

    private RetryRequest() {
        super();
    }

    public static RetryRequest get() {
        return new RetryRequest();
    }

    public RetryRequest setEnableLogcat(boolean enable) {
        LogUtil.enableLogcat(enable);
        return this;
    }

    private String TAG = "RR";

    public RetryRequest setLogtag(String tag) {
        if (tag != null && tag.length() > 0) {
            this.TAG = tag;
        }
        return this;
    }

    private long delayMillis = 1333L;

    public RetryRequest setDelayMillis(long millis) {
        if (millis < 333L || millis > 3333L) {
            millis = 1333L;
        }
        this.delayMillis = millis;
        return this;
    }

    Handler mHandler;

    private void init() {
        if (mHandler == null) {
            HandlerThread ht = new HandlerThread("retryrequest-single-work-thread");
            ht.start();
            mHandler = new Handler(ht.getLooper());
        }
    }

    public <T> void request(final String url, final Result<T> result, final Class<T> classOfT) {
        if (url == null || url.length() == 0 || result == null || classOfT == null) {
            return;
        }
        init();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                request1(url, new Result1<T>() {
                    @Override
                    public void onResult1(boolean success, final T t, final ErrorCode error1) {
                        if (success) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        result.onSuccess(t);
                                    } catch (Throwable t) {
                                    }
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        LogUtil.d(TAG, "wait for " + delayMillis + " millis..");
                                        Thread.sleep(delayMillis);
                                        LogUtil.d(TAG, delayMillis + " millis delay ok");
                                    } catch (Exception t) {
                                    }
                                    request2(url, new Result2<T>() {
                                        @Override
                                        public void onResult2(boolean success, final T t, final ErrorCode error2) {
                                            if (success) {
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            result.onSuccess(t);
                                                        } catch (Throwable t) {
                                                        }
                                                    }
                                                });
                                            } else {
                                                mHandler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            result.onFailure(ErrorCode.formatErrorCode(error1, error2));
                                                        } catch (Throwable t) {
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }, classOfT);
                                }
                            });
                        }
                    }
                }, classOfT);
            }
        });
    }

    private <T> void request1(final String url, final Result1<T> result1, final Class<T> classOfT) {
        LogUtil.d(TAG, "1.request1|" + url);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String msg = "";
                if (e != null) {
                    msg = e.getMessage();
                }
                ErrorCode error = parseIOException(e);
                LogUtil.d(TAG, "1.request1|" + url + "|onFailure|" + msg + "|error|" + error);
                result1.onResult1(false, null, error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                State<T> state = parseResponse(response, classOfT);
                LogUtil.d(TAG, "1.request1|" + url + "|onResponse|" + state.success + (state.success ? "" : "|" + state.error + "|"));
                result1.onResult1(state.success, state.t, state.error);
            }
        });
    }

    private <T> void request2(final String url, final Result2<T> result2, final Class<T> classOfT) {
        LogUtil.d(TAG, "2.request2|" + url);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String msg = "";
                if (e != null) {
                    msg = e.getMessage();
                }
                ErrorCode error = parseIOException(e);
                LogUtil.d(TAG, "2.request2|" + url + "|onFailure|" + msg + "|error|" + error);
                result2.onResult2(false, null, error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                State<T> state = parseResponse(response, classOfT);
                LogUtil.d(TAG, "2.request2|" + url + "|onResponse|" + state.success + (state.success ? "" : "|" + state.error + "|"));
                result2.onResult2(state.success, state.t, state.error);
            }
        });
    }

    private ErrorCode parseIOException(IOException e) {
        ErrorCode error = null;
        if (e instanceof SocketTimeoutException) {
            error = ErrorCode.REQUEST_CONNECTION_ERROR;
        } else if (e instanceof SocketException) {
            error = ErrorCode.REQUEST_CONNECTION_ERROR;
        } else if (e instanceof FileNotFoundException) {
            error = ErrorCode.REQUEST_SERVER_ERROR;
        } else {
            error = ErrorCode.REQUEST_OTHER_ERROR;
        }
        return error;
    }

    private <T> State<T> parseResponse(Response response, Class<T> classOfT) {
        T t = null;
        State<T> state = null;
        if (response == null || response.body() == null) {
            state = new State<T>();
            state.error = ErrorCode.REQUEST_NO_RESPONSE_BODY;
            return state;
        }
        String jsonResult = null, objectResult = null;
        int responseCode = response == null ? -1 : response.code();
        LogUtil.d(TAG, "Response|code|" + responseCode);
        try {
            jsonResult = response.body().string();
        } catch (Exception e) {
        }
        LogUtil.d(TAG, "Response|code|" + responseCode + "|responseStr|" + jsonResult);
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                t = (T) GsonUtil.fromJson(jsonResult, classOfT);
                objectResult = GsonUtil.toJson(t);
            } catch (Exception e) {
            }
            if (t != null) {
                LogUtil.d(TAG, "parse|" + classOfT + "|ok|" + objectResult);
            }
        }
        if (t == null) {
            // 错误类型：无法gson
            LogUtil.d(TAG, "requestToken|result|noObject");
            state = new State();
            state.error = ErrorCode.REQUEST_JSON_GSON_ERROR;
            return state;
        }
        if ("{}".equals(objectResult)) {
            // 错误类型：转化失败
            if (!response.isSuccessful()) {
                // 这种情况是因为502引起的，虽然能gson，但是不是我们要的token信息
                LogUtil.d(TAG, "requestToken|result|responseStr|" + jsonResult + "|responseCode|" + responseCode);
                state = new State();
                state.error = ErrorCode.REQUEST_RESPONSE_CODE_NOT_200;
                return state;
            } else {
                // 这种情况就是平常的转化失败
                LogUtil.d(TAG, "requestToken|result|noData");
                state = new State();
                state.error = ErrorCode.REQUEST_OBJECT_NO_DATA;
                return state;
            }
        }
        if (t instanceof Legally && !((Legally) t).isLegal()) {
            // 这种情况是不符合对象的isLegal方法判断
            LogUtil.d(TAG, "requestToken|result|illegal");
            state = new State();
            state.error = ErrorCode.REQUEST_OBJECT_NOT_LEGAL;
            return state;
        }
        state = new State();
        state.success = true;
        state.t = t;
        state.error = null;
        LogUtil.d(TAG, "requestToken|result|success|");
        return state;
    }
}
