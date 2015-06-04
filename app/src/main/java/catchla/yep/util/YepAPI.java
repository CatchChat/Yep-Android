package catchla.yep.util;

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
import catchla.yep.model.CreateRegistrationResult;
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.DribbbleShot;
import catchla.yep.model.DribbbleShots;
import catchla.yep.model.GithubUserInfo;
import catchla.yep.model.PagedFriendships;
import catchla.yep.model.PagedMessages;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.Paging;
import catchla.yep.model.ProfileUpdate;
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
    User updateProfile(@Form ProfileUpdate profileUpdate) throws YepException;

    @GET("/v1/user")
    User getUser() throws YepException;

    @GET("/v1/user/discover")
    PagedUsers getDiscover(@Query DiscoverQuery query, @Query Paging paging) throws YepException;

    @GET("/v1/friendships")
    PagedFriendships getFriendships(@Query Paging paging) throws YepException;

    @GET("/v1/messages/unread")
    PagedMessages getUnreadMessages(@Query Paging paging) throws YepException;

    @GET("/v1/users/{id}/dribbble")
    DribbbleShots getDribbbleShots(@Path("id") String userId) throws YepException;

    @GET("/v1/users/{id}/github")
    GithubUserInfo getGithubUserInfo(@Path("id") String userId) throws YepException;
}

