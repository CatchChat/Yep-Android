package catchla.yep.util;

import android.support.v4.util.SimpleArrayMap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import catchla.yep.model.ResponseCode;
import catchla.yep.model.YepException;
import retrofit.Call;
import retrofit.CallAdapter;
import retrofit.Response;
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
        final CallAdapter<?> adapter = sResponseConverters.get(returnType);
        if (adapter != null) return adapter;
        return new DefaultCallAdapter(returnType);
    }

    private class DefaultCallAdapter implements CallAdapter<Object> {
        private final Type returnType;

        public DefaultCallAdapter(final Type returnType) {
            this.returnType = returnType;
        }

        @Override
        public Type responseType() {
            return returnType;
        }

        @Override
        public <R> Object adapt(final Call<R> call) throws YepException {
            try {
                final Response<R> execute = call.execute();
                if (!execute.isSuccess()) throw new YepException(execute.message());
                return execute.body();
            } catch (IOException e) {
                throw new YepException(e);
            }
        }
    }
}
