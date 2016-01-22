package catchla.yep.util;

import android.support.annotation.StringDef;

import com.squareup.okhttp.RequestBody;

import org.mariotaku.restfu.annotation.method.DELETE;
import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.method.PATCH;
import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.annotation.method.PUT;
import org.mariotaku.restfu.annotation.param.Param;
import org.mariotaku.restfu.annotation.param.Path;
import org.mariotaku.restfu.annotation.param.Query;
import org.mariotaku.restfu.annotation.param.Raw;

import java.util.ArrayList;

import catchla.yep.model.AccessToken;
import catchla.yep.model.AttachmentUpload;
import catchla.yep.model.AvatarResponse;
import catchla.yep.model.Circle;
import catchla.yep.model.Client;
import catchla.yep.model.ContactUpload;
import catchla.yep.model.CreateRegistrationResult;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.DribbbleShots;
import catchla.yep.model.Friendship;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.model.IdResponse;
import catchla.yep.model.InstagramMediaList;
import catchla.yep.model.MarkAsReadResult;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Paging;
import catchla.yep.model.ProfileUpdate;
import catchla.yep.model.ResponseCode;
import catchla.yep.model.ResponseList;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.SkillCategory;
import catchla.yep.model.Topic;
import catchla.yep.model.UrlResponse;
import catchla.yep.model.User;
import catchla.yep.model.UserSettings;
import catchla.yep.model.VerificationMethod;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/5/12.
 */
public interface YepAPI {

    @POST("registration/create")
    CreateRegistrationResult createRegistration(@Param("mobile") String mobile,
                                                @Param("phone_code") String phoneCode,
                                                @Param("nickname") String nickname,
                                                @Param("longitude") double longitude,
                                                @Param("latitude") double latitude) throws YepException;

    @PUT("registration/update")
    AccessToken updateRegistration(@Param("mobile") String mobile,
                                   @Param("phone_code") String phoneCode,
                                   @Param("token") String token,
                                   @Param("client") Client client,
                                   @Param("expiring") long expiringInSeconds) throws YepException;

    @POST("auth/token_by_mobile")
    AccessToken tokenByMobile(@Param("mobile") String mobile,
                              @Param("phone_code") String phoneCode,
                              @Param("verify_code") String verifyCode,
                              @Param("client") Client client,
                              @Param("expiring") long expiringInSeconds) throws YepException;

    @POST("sms_verification_codes")
    ResponseCode sendVerifyCode(@Param("mobile") String mobile,
                                @Param("phone_code") String phoneCode,
                                @Param("method") VerificationMethod method) throws YepException;

    @PATCH("user")
    User updateProfile(@Param ProfileUpdate profileUpdate) throws YepException;

    @GET("user")
    User getUser() throws YepException;

    @POST("user/set_avatar")
    AvatarResponse setAvatar(@Param("avatar") RequestBody file) throws YepException;

    @PATCH("user/set_avatar")
    AvatarResponse setAvatarRaw(@Raw RequestBody file) throws YepException;

    @GET("users/{id}")
    User showUser(@Path("id") String userId) throws YepException;

    @GET("user/discover")
    ResponseList<User> getDiscover(@Query DiscoverQuery query, @Query Paging paging) throws YepException;

    @GET("skill_categories")
    ResponseList<SkillCategory> getSkillCategories() throws YepException;

    @GET("friendships")
    ResponseList<Friendship> getFriendships(@Query Paging paging) throws YepException;

    @GET("messages/unread")
    ResponseList<Message> getUnreadMessages() throws YepException;

    @GET("{recipient_type}/{recipient_id}/messages")
    ResponseList<Message> getHistoricalMessages(@PathRecipientType @Path("recipient_type") String recipientType,
                                                @Path("recipient_id") String recipientId,
                                                @Query Paging paging) throws YepException;

    @GET("messages/sent_unread")
    ResponseList<Message> getSentUnreadMessages(@Query Paging paging) throws YepException;

    @GET("users/{id}/dribbble")
    DribbbleShots getDribbbleShots(@Path("id") String userId) throws YepException;

    @GET("users/{id}/github")
    GithubUserInfo getGithubUserInfo(@Path("id") String userId) throws YepException;

    @GET("users/{id}/instagram")
    InstagramMediaList getInstagramMediaList(@Path("id") String userId) throws YepException;

    @POST("{recipient_type}/{recipient_id}/messages")
    Message createMessage(@Path("recipient_type") String recipientType, @Path("recipient_id") String recipientId,
                          @Raw NewMessage message) throws YepException;

    @DELETE("learning_skills/{id}")
    ResponseCode removeLearningSkill(@Path("id") String id) throws YepException;

    @POST("learning_skills")
    ResponseCode addLearningSkill(@Param("skill_id") String id) throws YepException;

    @DELETE("master_skills/{id}")
    ResponseCode removeMasterSkill(@Path("id") String id) throws YepException;

    @POST("master_skills")
    ResponseCode addMasterSkill(@Param("skill_id") String id) throws YepException;

    @POST("do_not_disturb_users")
    ResponseCode addDoNotDisturb(@Param("user_id") String id) throws YepException;

    @POST("user_reports")
    ResponseCode reportUser(@Param("recipient_id") String id, @Param("report_type") int reportType, @Param("reason") String reason) throws YepException;

    @DELETE("do_not_disturb_users/{user_id}")
    ResponseCode removeDoNotDisturb(@Path("user_id") String id) throws YepException;

    @GET("attachments/{kind}/s3_upload_form_fields")
    S3UploadToken getS3UploadToken(@Path("kind") String kind) throws YepException;

    @POST("attachments")
    IdResponse uploadAttachment(@Param("file") RequestBody file,
                                @AttachableType @Param("attachable_type") String attachableType,
                                @Param("metadata") String metadata) throws YepException;

    @POST("attachments")
    IdResponse uploadAttachment(@Raw AttachmentUpload attachmentUpload) throws YepException;

    @POST("contacts/upload")
    ArrayList<User> uploadContact(@Param("contacts") ContactUpload contactUpload) throws YepException;

    @GET("users/search")
    ResponseList<User> searchUsers(@Query("q") String query, @Query Paging paging) throws YepException;

    @PATCH("{recipient_type}/{recipient_id}/messages/batch_mark_as_read")
    MarkAsReadResult batchMarkAsRead(@PathRecipientType @Path("recipient_type") String recipientType, @Path("recipient_id") String recipientId,
                                     @Param("max_id") String maxId) throws YepException;

    @GET("blocked_users")
    ResponseList<User> getBlockedUsers(@Query Paging paging) throws YepException;

    @POST("blocked_users")
    ResponseCode blockUser(@Param("user_id") String id) throws YepException;

    @DELETE("blocked_users/{id}")
    ResponseCode unblockUser(@Path("id") String id) throws YepException;

    @GET("users/{id}/settings_with_current_user")
    UserSettings getUserSettings(@Path("id") String id) throws YepException;

    @GET("topics/discover")
    ResponseList<Topic> getDiscoverTopics(@Query("sort") @Topic.SortOrder String sortOrder, @Query Paging paging) throws YepException;

    @GET("topics")
    ResponseList<Topic> getTopics(@Query Paging paging) throws YepException;

    @GET("users/{id}/topics")
    ResponseList<Topic> getTopics(@Path("id") String userId, @Query Paging paging) throws YepException;

    @POST("topics")
    Topic postTopic(@Raw NewTopic topic) throws YepException;

    @PUT("topics/{id}")
    ResponseCode updateTopic(@Path("id") String id, @Param("allow_comment") boolean allowComment);

    @DELETE("topics/{id}")
    ResponseCode deleteTopic(@Path("id") String id);

    @POST("feedbacks")
    ResponseCode postFeedback(@Param("content") String content, @Param("device_info") String deviceInfo) throws YepException;

    @POST("circles/{id}/share")
    UrlResponse getCircleShareUrl(@Path("id") String id) throws YepException;

    @POST("circles/{id}/join")
    Circle joinCircle(@Path("id") String circleId) throws YepException;

    @DELETE("circles/{id}/leave")
    Circle leaveCircle(@Path("id") String circleId) throws YepException;

    @GET("circles")
    ResponseList<Circle> getCircles(@Query Paging paging) throws YepException;


    @DELETE("auth/logout")
    ResponseCode logout() throws YepException;

    @interface AttachmentKind {
        String MESSAGE = "message";
        String TOPIC = "topic";
        String AVATAR = "avatar";
    }

    @StringDef({AttachableType.MESSAGE, AttachableType.TOPIC})
    @interface AttachableType {
        String MESSAGE = "Message";
        String TOPIC = "Topic";
    }

    @StringDef({PathRecipientType.USERS, PathRecipientType.CIRCLES})
    @interface PathRecipientType {
        String USERS = "users";
        String CIRCLES = "circles";
    }

}

