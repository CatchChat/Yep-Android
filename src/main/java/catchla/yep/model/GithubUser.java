package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.ISO8601DateConverter;

/**
 * Created by mariotaku on 15/6/4.
 */
@JsonObject
public class GithubUser {

    @JsonField(name = "id")
    private long id;
    @JsonField(name = "login")
    private String login;
    @JsonField(name = "avatar_url")
    private String avatarUrl;
    @JsonField(name = "html_url")
    private String htmlUrl;
    @JsonField(name = "name")
    private String name;
    @JsonField(name = "company")
    private String company;
    @JsonField(name = "blog")
    private String blog;
    @JsonField(name = "location")
    private String location;
    @JsonField(name = "bio")
    private String bio;
    @JsonField(name = "public_repos")
    private long publicRepos;
    @JsonField(name = "public_gists")
    private long publicGists;
    @JsonField(name = "followers")
    private long followers;
    @JsonField(name = "following")
    private long following;
    @JsonField(name = "created_at", typeConverter = ISO8601DateConverter.class)
    private Date createdAt;
    @JsonField(name = "updated_at", typeConverter = ISO8601DateConverter.class)
    private Date updatedAt;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(final String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(final String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(final String bio) {
        this.bio = bio;
    }

    public long getPublicRepos() {
        return publicRepos;
    }

    public void setPublicRepos(final long publicRepos) {
        this.publicRepos = publicRepos;
    }

    public long getPublicGists() {
        return publicGists;
    }

    public void setPublicGists(final long publicGists) {
        this.publicGists = publicGists;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(final long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(final long following) {
        this.following = following;
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
}
