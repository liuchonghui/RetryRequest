package tools.android.retryrequest;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtil {

    static private Gson mGson;

    public static String toJson(Object obj) {
        if (mGson == null) {
            mGson = new Gson();
        }
        String jsonResult = null;
        try {
            jsonResult = mGson.toJson(obj);
        } catch (Throwable t) {
            t.printStackTrace();
            jsonResult = null;
        }
        return jsonResult;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (mGson == null) {
            mGson = new Gson();
        }
        T o = null;
        try {
            o = mGson.fromJson(json, classOfT);
        } catch (Throwable t) {
            t.printStackTrace();
            o = null;
        }
        return o;
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        if (mGson == null) {
            mGson = new Gson();
        }
        T o = null;
        try {
            o = mGson.fromJson(json, typeOfT);
        } catch (Throwable t) {
            t.printStackTrace();
            o = null;
        }
        return o;
    }
}
