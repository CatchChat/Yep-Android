package catchla.yep.util;

import android.support.annotation.StringDef;

import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;

import catchla.yep.model.AccessToken;
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
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by mariotaku on 15/5/12.
 */
public interface YepAPI {

    @POST("v1/registration/create")
    @FormUrlEncoded
    CreateRegistrationResult createRegistration(@Field("mobile") String mobile,
                                                @Field("phone_code") String phoneCode,
                                                @Field("nickname") String nickname,
                                                @Field("longitude") double longitude,
                                                @Field("latitude") double latitude) throws YepException;

    @PUT("v1/registration/update")
    @FormUrlEncoded
    AccessToken updateRegistration(@Field("mobile") String mobile,
                                   @Field("phone_code") String phoneCode,
                                   @Field("token") String token,
                                   @Field("client") Client client,
                                   @Field("expiring") long expiringInSeconds) throws YepException;

    @POST("v1/auth/token_by_mobile")
    @FormUrlEncoded
    AccessToken tokenByMobile(@Field("mobile") String mobile,
                              @Field("phone_code") String phoneCode,
                              @Field("verify_code") String verifyCode,
                              @Field("client") Client client,
                              @Field("expiring") long expiringInSeconds) throws YepException;

    @POST("v1/sms_verification_codes")
    @FormUrlEncoded
    ResponseCode sendVerifyCode(@Field("mobile") String mobile,
                                @Field("phone_code") String phoneCode,
                                @Field("method") VerificationMethod method) throws YepException;

    @PATCH("v1/user")
    @FormUrlEncoded
    User updateProfile(@FieldMap ProfileUpdate profileUpdate) throws YepException;

    @GET("v1/user")
    User getUser() throws YepException;

    @POST("v1/user/set_avatar")
    @Multipart
    AvatarResponse setAvatar(@Part("file") RequestBody file) throws YepException;

    @GET("v1/users/{id}")
    User showUser(@Path("id") String userId) throws YepException;

    @GET("v1/user/discover")
    ResponseList<User> getDiscover(@QueryMap DiscoverQuery query, @QueryMap Paging paging) throws YepException;

    @GET("v1/skill_categories")
    ResponseList<SkillCategory> getSkillCategories() throws YepException;

    @GET("v1/friendships")
    ResponseList<Friendship> getFriendships(@QueryMap Paging paging) throws YepException;

    @GET("v1/messages/unread")
    ResponseList<Message> getUnreadMessages() throws YepException;

    @GET("v1/{recipient_type}/{recipient_id}/messages")
    ResponseList<Message> getHistoricalMessages(@PathRecipientType @Path("recipient_type") String recipientType,
                                                @Path("recipient_id") String recipientId,
                                                @QueryMap Paging paging) throws YepException;

    @GET("v1/messages/sent_unread")
    ResponseList<Message> getSentUnreadMessages(@QueryMap Paging paging) throws YepException;

    @GET("v1/users/{id}/dribbble")
    DribbbleShots getDribbbleShots(@Path("id") String userId) throws YepException;

    @GET("v1/users/{id}/github")
    GithubUserInfo getGithubUserInfo(@Path("id") String userId) throws YepException;

    @GET("v1/users/{id}/instagram")
    InstagramMediaList getInstagramMediaList(@Path("id") String userId) throws YepException;

    @POST("v1/{recipient_type}/{recipient_id}/messages")
    Message createMessage(@Path("recipient_type") String recipientType, @Path("recipient_id") String recipientId,
                          @Body NewMessage message) throws YepException;

    @DELETE("v1/learning_skills/{id}")
    ResponseCode removeLearningSkill(@Path("id") String id) throws YepException;

    @POST("v1/learning_skills")
    @FormUrlEncoded
    ResponseCode addLearningSkill(@Field("skill_id") String id) throws YepException;

    @DELETE("v1/master_skills/{id}")
    ResponseCode removeMasterSkill(@Path("id") String id) throws YepException;

    @POST("v1/master_skills")
    @FormUrlEncoded
    ResponseCode addMasterSkill(@Field("skill_id") String id) throws YepException;

    @POST("v1/do_not_disturb_users")
    @FormUrlEncoded
    ResponseCode addDoNotDisturb(@Field("user_id") String id) throws YepException;

    @POST("v1/user_reports")
    @FormUrlEncoded
    ResponseCode reportUser(@Field("recipient_id") String id, @Field("report_type") int reportType, @Field("reason") String reason) throws YepException;

    @DELETE("v1/do_not_disturb_users/{user_id}")
    ResponseCode removeDoNotDisturb(@Path("user_id") String id) throws YepException;

    @GET("v1/attachments/{kind}/s3_upload_form_fields")
    S3UploadToken getS3UploadToken(@Path("kind") String kind) throws YepException;

    @POST("v1/attachments")
    @Multipart
    IdResponse uploadAttachment(@Part("file") RequestBody file,
                                @AttachableType @Part("attachable_type") String attachableType,
                                @Part("metadata") String metadata) throws YepException;

    @POST("v1/contacts/upload")
    @FormUrlEncoded
    ArrayList<User> uploadContact(@Field("contacts") ContactUpload contactUpload) throws YepException;

    @GET("v1/users/search")
    ResponseList<User> searchUsers(@Query("q") String query, @QueryMap Paging paging) throws YepException;

    @PATCH("v1/{recipient_type}/{recipient_id}/messages/batch_mark_as_read")
    @FormUrlEncoded
    MarkAsReadResult batchMarkAsRead(@PathRecipientType @Path("recipient_type") String recipientType, @Path("recipient_id") String recipientId,
                                     @Field("max_id") String maxId) throws YepException;

    @GET("v1/blocked_users")
    ResponseList<User> getBlockedUsers(@QueryMap Paging paging) throws YepException;

    @POST("v1/blocked_users")
    ResponseCode blockUser(@Field("user_id") String id) throws YepException;

    @DELETE("v1/blocked_users/{id}")
    ResponseCode unblockUser(@Path("id") String id) throws YepException;

    @GET("v1/users/{id}/settings_with_current_user")
    UserSettings getUserSettings(@Path("id") String id) throws YepException;

    @GET("v1/topics/discover")
    ResponseList<Topic> getDiscoverTopics(@Query("sort") @Topic.SortOrder String sortOrder, @QueryMap Paging paging) throws YepException;

    @GET("v1/topics")
    ResponseList<Topic> getTopics(@QueryMap Paging paging) throws YepException;

    @GET("v1/users/{id}/topics")
    ResponseList<Topic> getTopics(@Path("id") String userId, @QueryMap Paging paging) throws YepException;

    @POST("v1/topics")
    Topic postTopic(@Body NewTopic topic) throws YepException;

    @PUT("v1/topics/{id}")
    @FormUrlEncoded
    ResponseCode updateTopic(@Path("id") String id, @Field("allow_comment") boolean allowComment);

    @DELETE("v1/topics/{id}")
    ResponseCode deleteTopic(@Path("id") String id);

    @POST("v1/feedbacks")
    @FormUrlEncoded
    ResponseCode postFeedback(@Field("content") String content, @Field("device_info") String deviceInfo) throws YepException;

    @POST("v1/circles/{id}/share")
    UrlResponse getCircleShareUrl(@Path("id") String id) throws YepException;

    @POST("v1/circles/{id}/join")
    Circle joinCircle(@Path("id") String circleId) throws YepException;

    @DELETE("v1/circles/{id}/leave")
    Circle leaveCircle(@Path("id") String circleId) throws YepException;

    @GET("v1/circles")
    ResponseList<Circle> getCircles(@QueryMap Paging paging) throws YepException;


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

