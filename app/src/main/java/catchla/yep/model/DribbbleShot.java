package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import catchla.yep.model.util.DribbbleShotImageConverter;
import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mariotaku on 15/6/3.
 */
@JsonObject
public class DribbbleShot extends RealmObject {

    @JsonField(name = "id")
    private long id;
    @JsonField(name = "title")
    private String title;
    @JsonField(name = "description")
    private String description;
    @JsonField(name = "html_url")
    private String htmlUrl;
    @JsonField(name = "images", typeConverter = DribbbleShotImageConverter.class)
    private RealmList<DribbbleShotImage> images;
    @JsonField(name = "views_count")
    private long viewsCount;
    @JsonField(name = "likes_count")
    private long likesCount;
    @JsonField(name = "comments_count")
    private long commentsCount;
    @JsonField(name = "attachments_count")
    private long atachmentsCount;
    @JsonField(name = "rebounds_count")
    private long reboundsCount;
    @JsonField(name = "buckets_count")
    private long bucketsCount;
    @JsonField(name = "created_at")
    private Date createdAt;
    @JsonField(name = "updated_at")
    private Date updatedAt;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public RealmList<DribbbleShotImage> getImages() {
        return images;
    }

    public void setImages(final RealmList<DribbbleShotImage> images) {
        this.images = images;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(final long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(final long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(final long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public long getAtachmentsCount() {
        return atachmentsCount;
    }

    public void setAtachmentsCount(final long atachmentsCount) {
        this.atachmentsCount = atachmentsCount;
    }

    public long getReboundsCount() {
        return reboundsCount;
    }

    public void setReboundsCount(final long reboundsCount) {
        this.reboundsCount = reboundsCount;
    }

    public long getBucketsCount() {
        return bucketsCount;
    }

    public void setBucketsCount(final long bucketsCount) {
        this.bucketsCount = bucketsCount;
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
