package catchla.yep.util;

import android.support.v4.util.SimpleArrayMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import catchla.yep.model.ResponseCode;
import retrofit.CallAdapter;
import retrofit.Retrofit;

/**
 * Created by mariotaku on 15/12/2.
 */
public class YepCallAdapterFactory implements CallAdapter.Factory {
    private static final SimpleArrayMap<Type, CallAdapter<?>> sResponseConverters = new SimpleArrayMap<>();

    static {
        sResponseConverters.put(ResponseCode.class, new ResponseCode.CallAdapter());
    }

    @Override
    public CallAdapter<?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit) {
        return sResponseConverters.get(returnType);
    }
}
