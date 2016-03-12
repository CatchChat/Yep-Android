package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class GithubUserInfo {

    private String yepUserId;

    @JsonField(name = "user")
    private GithubUser user;

    @JsonField(name = "repos")
    private List<GithubRepo> repos;

    public String getYepUserId() {
        return yepUserId;
    }

    public void setYepUserId(final String yepUserId) {
        this.yepUserId = yepUserId;
    }

    public GithubUser getUser() {
        return user;
    }

    public void setUser(final GithubUser user) {
        this.user = user;
    }

    public List<GithubRepo> getRepos() {
        return repos;
    }

    public void setRepos(final List<GithubRepo> repos) {
        this.repos = repos;
    }
}
