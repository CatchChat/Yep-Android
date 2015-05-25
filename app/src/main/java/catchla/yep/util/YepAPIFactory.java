package catchla.yep.util;

import org.mariotaku.restfu.ExceptionFactory;
import org.mariotaku.restfu.RestAPIFactory;
import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;

import catchla.yep.util.net.OkHttpRestClient;

/**
 * Created by mariotaku on 15/5/23.
 */
public class YepAPIFactory {

    public static YepAPI getInstance(String accessToken) {
        RestAPIFactory factory = new RestAPIFactory();
        factory.setEndpoint(new Endpoint("http://park.catchchatchina.com/api/"));
        factory.setClient(new OkHttpRestClient());
        factory.setConverter(new LoganSquareConverter());
        factory.setExceptionFactory(new ExceptionFactory() {
            @Override
            public Exception newException(final Throwable cause, final RestHttpRequest request, final RestHttpResponse response) {
                return new YepException(cause);
            }
        });
        return factory.build(YepAPI.class);
    }

}
