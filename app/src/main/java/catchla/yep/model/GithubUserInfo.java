package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import catchla.yep.model.util.GithubRepoListConverter;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class GithubUserInfo extends RealmObject {

    @PrimaryKey
    private String yepUserId;

    @JsonField(name = "user")
    private GithubUser user;

    @JsonField(name = "repos", typeConverter = GithubRepoListConverter.class)
    private RealmList<GithubRepo> repos;

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

    public RealmList<GithubRepo> getRepos() {
        return repos;
    }

    public void setRepos(final RealmList<GithubRepo> repos) {
        this.repos = repos;
    }
}
