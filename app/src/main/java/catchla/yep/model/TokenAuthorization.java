package catchla.yep.model;

import org.mariotaku.restfu.RestRequestInfo;
import org.mariotaku.restfu.http.Authorization;
import org.mariotaku.restfu.http.Endpoint;

import java.util.Locale;

/**
 * Created by mariotaku on 15/6/3.
 */
public class TokenAuthorization implements Authorization {
    private final String accessToken;

    public TokenAuthorization(final String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getHeader(final Endpoint endpoint, final RestRequestInfo restRequestInfo) {
        return String.format(Locale.ROOT, "Token token=\"%s\"", accessToken);
    }

    @Override
    public boolean hasAuthorization() {
        return accessToken!=null;
    }
}
