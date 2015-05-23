package catchla.yep.util;

import org.mariotaku.simplerestapi.RestAPIFactory;
import org.mariotaku.simplerestapi.http.Endpoint;

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
        return factory.build(YepAPI.class);
    }

}
