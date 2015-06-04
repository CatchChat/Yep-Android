package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.ISO8601DateConverter;
import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class GithubRepo extends RealmObject {

    @JsonField(name = "id")
    private long id;
    @JsonField(name = "name")
    private String name;
    @JsonField(name = "full_name")
    private String fullName;
    @JsonField(name = "owner")
    private GithubUser owner;
    @JsonField(name = "html_url")
    private String htmlUrl;
    @JsonField(name = "description")
    private String description;
    @JsonField(name = "fork")
    private boolean fork;
    @JsonField(name = "created_at", typeConverter = ISO8601DateConverter.class)
    private Date createdAt;
    @JsonField(name = "updated_at", typeConverter = ISO8601DateConverter.class)
    private Date updatedAt;
    @JsonField(name = "pushed_at", typeConverter = ISO8601DateConverter.class)
    private Date pushedAt;
    @JsonField(name = "homepage")
    private String homepage;
    @JsonField(name = "stargazers_count")
    private long stargazersCount;
    @JsonField(name = "watchers_count")
    private long watchersCount;
    @JsonField(name = "forks_count")
    private long forksCount;
    @JsonField(name = "language")
    private String language;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public GithubUser getOwner() {
        return owner;
    }

    public void setOwner(final GithubUser owner) {
        this.owner = owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(final String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(final boolean fork) {
        this.fork = fork;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(final Date pushedAt) {
        this.pushedAt = pushedAt;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(final String homepage) {
        this.homepage = homepage;
    }

    public long getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(final long stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public long getWatchersCount() {
        return watchersCount;
    }

    public void setWatchersCount(final long watchersCount) {
        this.watchersCount = watchersCount;
    }

    public long getForksCount() {
        return forksCount;
    }

    public void setForksCount(final long forksCount) {
        this.forksCount = forksCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }
}
