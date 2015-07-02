package catchla.yep.util;

import org.mariotaku.restfu.annotation.method.DELETE;
import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.method.PATCH;
import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.annotation.method.PUT;
import org.mariotaku.restfu.annotation.param.Body;
import org.mariotaku.restfu.annotation.param.Form;
import org.mariotaku.restfu.annotation.param.Path;
import org.mariotaku.restfu.annotation.param.Query;
import org.mariotaku.restfu.http.BodyType;

import java.util.ArrayList;

import catchla.yep.model.AccessToken;
import catchla.yep.model.Client;
import catchla.yep.model.ContactUpload;
import catchla.yep.model.CreateRegistrationResult;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.DribbbleShots;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.model.InstagramMediaList;
import catchla.yep.model.Message;
import catchla.yep.model.NewMessage;
import catchla.yep.model.PagedFriendships;
import catchla.yep.model.PagedMessages;
import catchla.yep.model.PagedSkillCategories;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.Paging;
import catchla.yep.model.ProfileUpdate;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.User;
import catchla.yep.model.VerificationMethod;

/**
 * Created by mariotaku on 15/5/12.
 */
public interface YepAPI {

    @POST("/v1/registration/create")
    @Body(BodyType.FORM)
    CreateRegistrationResult createRegistration(@Form("mobile") String mobile,
                                                @Form("phone_code") String phoneCode,
                                                @Form("nickname") String nickname,
                                                @Form("longitude") double longitude,
                                                @Form("latitude") double latitude) throws YepException;

    @PUT("/v1/registration/update")
    @Body(BodyType.FORM)
    AccessToken updateRegistration(@Form("mobile") String mobile,
                                   @Form("phone_code") String phoneCode,
                                   @Form("token") String token,
                                   @Form("client") Client client,
                                   @Form("expiring") long expiringInseconds) throws YepException;

    @PUT("/auth/token_by_mobile")
    @Body(BodyType.FORM)
    AccessToken tokenByMobile(@Form("mobile") String mobile,
                              @Form("phone_code") String phoneCode,
                              @Form("token") String token,
                              @Form("client") Client client,
                              @Form("expiring") long expiringInseconds) throws YepException;

    @POST("/v1/sms_verification_codes")
    @Body(BodyType.FORM)
    void sendVerifyCode(@Form("mobile") String mobile,
                        @Form("phone_code") String phoneCode,
                        @Form("method") VerificationMethod method) throws YepException;

    @PATCH("/v1/user")
    @Body(BodyType.FORM)
    User updateProfile(@Form ProfileUpdate profileUpdate) throws YepException;

    @GET("/v1/user")
    User getUser() throws YepException;

    @GET("/v1/user/discover")
    PagedUsers getDiscover(@Query DiscoverQuery query, @Query Paging paging) throws YepException;

    @GET("/v1/skill_categories")
    PagedSkillCategories getSkillCategories() throws YepException;

    @GET("/v1/friendships")
    PagedFriendships getFriendships(@Query Paging paging) throws YepException;

    @GET("/v1/messages/unread")
    PagedMessages getUnreadMessages(@Query Paging paging) throws YepException;

    @GET("/v1/messages/sent_unread")
    PagedMessages getSentUnreadMessages(@Query Paging paging) throws YepException;

    @GET("/v1/users/{id}/dribbble")
    DribbbleShots getDribbbleShots(@Path("id") String userId) throws YepException;

    @GET("/v1/users/{id}/github")
    GithubUserInfo getGithubUserInfo(@Path("id") String userId) throws YepException;

    @GET("/v1/users/{id}/instagram")
    InstagramMediaList getInstagramMediaList(@Path("id") String userId) throws YepException;

    @POST("/v1/messages")
    @Body(BodyType.FORM)
    Message createMessage(@Form NewMessage message) throws YepException;

    @DELETE("/v1/learning_skills/{id}")
    void removeLearningSkill(@Path("id") String id) throws YepException;

    @POST("/v1/learning_skills")
    @Body(BodyType.FORM)
    void addLearningSkill(@Form("skill_id") String id) throws YepException;

    @DELETE("/v1/master_skills/{id}")
    void removeMasterSkill(@Path("id") String id) throws YepException;

    @POST("/v1/master_skills")
    @Body(BodyType.FORM)
    void addMasterSkill(@Form("skill_id") String id) throws YepException;

    @POST("/v1/do_not_disturb_users")
    @Body(BodyType.FORM)
    void addDoNotDisturb(@Form("user_id") String id) throws YepException;

    @POST("/v1/user_reports")
    @Body(BodyType.FORM)
    void reportUser(@Form("recipient_id") String id, @Form("report_type") int reportType, @Form("reason") String reason) throws YepException;

    @DELETE("/v1/do_not_disturb_users/{user_id}")
    void removeDoNotDisturb(@Path("user_id") String id) throws YepException;

    @GET("/v1/attachments/s3_upload_public_form_fields")
    S3UploadToken getS3UploadToken() throws YepException;

    @POST("/v1/contacts/upload")
    @Body(BodyType.FORM)
    ArrayList<User> uploadContact(@Form ContactUpload contactUpload) throws YepException;

    @GET("/v1/users/search")
    PagedUsers searchUsers(@Query("q") String query) throws YepException;
}

