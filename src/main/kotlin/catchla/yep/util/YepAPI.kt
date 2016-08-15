package catchla.yep.util

import catchla.yep.annotation.AttachableType
import catchla.yep.annotation.PathRecipientType
import catchla.yep.model.*
import org.mariotaku.restfu.annotation.method.*
import org.mariotaku.restfu.annotation.param.*
import org.mariotaku.restfu.http.mime.Body
import java.util.*

/**
 * Created by mariotaku on 15/5/12.
 */
@Headers(KeyValue(key = "Accept", value = "application/json"), KeyValue(key = "Accept-Language", valueKey = "accept_language"))
interface YepAPI {

    @POST("registration/create")
    @Throws(YepException::class)
    fun createRegistration(@Param("mobile") mobile: String,
                           @Param("phone_code") phoneCode: String,
                           @Param("nickname") nickname: String,
                           @Param("longitude") longitude: Double,
                           @Param("latitude") latitude: Double): CreateRegistrationResult

    @PUT("registration/update")
    @Throws(YepException::class)
    fun updateRegistration(@Param("mobile") mobile: String,
                           @Param("phone_code") phoneCode: String,
                           @Param("token") token: String,
                           @Param("client") @Client client: Int,
                           @Param("expiring") expiringInSeconds: Long): AccessToken

    @POST("auth/token_by_mobile")
    @Throws(YepException::class)
    fun tokenByMobile(@Param("mobile") mobile: String,
                      @Param("phone_code") phoneCode: String,
                      @Param("verify_code") verifyCode: String,
                      @Param("client") @Client client: Int,
                      @Param("expiring") expiringInSeconds: Long): AccessToken

    @POST("sms_verification_codes")
    @Throws(YepException::class)
    fun sendVerifyCode(@Param("mobile") mobile: String,
                       @Param("phone_code") phoneCode: String,
                       @Param("method") @VerificationMethod method: String): ResponseCode

    @PATCH("user")
    @Throws(YepException::class)
    fun updateProfile(@Param profileUpdate: ProfileUpdate): User

    @GET("user")
    @Throws(YepException::class)
    fun getUser(): User

    @POST("user/set_avatar")
    @Throws(YepException::class)
    fun setAvatar(@Param("avatar") file: Body): AvatarResponse

    @PATCH("user/set_avatar")
    @Throws(YepException::class)
    fun setAvatarRaw(@Raw file: Body): AvatarResponse

    @GET("users/{id}")
    @Throws(YepException::class)
    fun showUser(@Path("id") userId: String): User

    @GET("user/discover")
    @Throws(YepException::class)
    fun getDiscover(@Query(arrayDelimiter = ',') query: DiscoverQuery, @Query paging: Paging,
                    @Query("sort") sortOrder: String): ResponseList<User>


    @GET("messages/unread")
    @Throws(YepException::class)
    fun getUnreadMessages(): ResponseList<Message>


    @GET("skill_categories")
    @Throws(YepException::class)
    fun getSkillCategories(): ResponseList<SkillCategory>

    @GET("friendships")
    @Throws(YepException::class)
    fun getFriendships(@Query paging: Paging): ResponseList<Friendship>

    @GET("{recipient_type}/{recipient_id}/messages")
    @Throws(YepException::class)
    fun getHistoricalMessages(@PathRecipientType @Path("recipient_type") recipientType: String,
                              @Path("recipient_id") recipientId: String,
                              @Query paging: Paging): ResponseList<Message>

    @GET("messages/sent_unread")
    @Throws(YepException::class)
    fun getSentUnreadMessages(@Query paging: Paging): ResponseList<Message>

    @GET("users/{id}/dribbble")
    @Throws(YepException::class)
    fun getDribbbleShots(@Path("id") userId: String): DribbbleShots

    @GET("users/{id}/github")
    @Throws(YepException::class)
    fun getGithubUserInfo(@Path("id") userId: String): GithubUserInfo

    @GET("users/{id}/instagram")
    @Throws(YepException::class)
    fun getInstagramMediaList(@Path("id") userId: String): InstagramMediaList

    @POST("{recipient_type}/{recipient_id}/messages")
    @Throws(YepException::class)
    fun createMessage(@Path("recipient_type") recipientType: String, @Path("recipient_id") recipientId: String,
                      @Raw message: NewMessage): Message

    @DELETE("learning_skills/{id}")
    @Throws(YepException::class)
    fun removeLearningSkill(@Path("id") id: String): ResponseCode

    @POST("learning_skills")
    @Throws(YepException::class)
    fun addLearningSkill(@Param("skill_id") id: String): ResponseCode

    @DELETE("master_skills/{id}")
    @Throws(YepException::class)
    fun removeMasterSkill(@Path("id") id: String): ResponseCode

    @POST("master_skills")
    @Throws(YepException::class)
    fun addMasterSkill(@Param("skill_id") id: String): ResponseCode

    @POST("do_not_disturb_users")
    @Throws(YepException::class)
    fun addDoNotDisturb(@Param("user_id") id: String): ResponseCode

    @POST("users/{id}/reports")
    @Throws(YepException::class)
    fun reportUser(@Path("id") id: String, @Param("report_type") reportType: Int, @Param("reason") reason: String?): ResponseCode

    @POST("messages/{id}/reports")
    @Throws(YepException::class)
    fun reportMessage(@Path("id") id: String, @Param("report_type") reportType: Int, @Param("reason") reason: String?): ResponseCode

    @POST("topics/{id}/reports")
    @Throws(YepException::class)
    fun reportTopic(@Path("id") id: String, @Param("report_type") reportType: Int, @Param("reason") reason: String?): ResponseCode

    @DELETE("do_not_disturb_users/{user_id}")
    @Throws(YepException::class)
    fun removeDoNotDisturb(@Path("user_id") id: String): ResponseCode

    @GET("attachments/{kind}/s3_upload_form_fields")
    @Throws(YepException::class)
    fun getS3UploadToken(@Path("kind") kind: String): S3UploadToken

    @POST("attachments")
    @Throws(YepException::class)
    fun uploadAttachment(@Param("file") file: Body,
                         @AttachableType @Param("attachable_type") attachableType: String,
                         @Param("metadata") metadata: String): FileAttachment

    @POST("attachments")
    @Throws(YepException::class)
    fun uploadAttachment(@Raw attachmentUpload: AttachmentUpload): FileAttachment

    @POST("contacts/upload")
    @Throws(YepException::class)
    fun uploadContact(@Param("contacts") contactUpload: ContactUpload): ArrayList<User>

    @GET("users/search")
    @Throws(YepException::class)
    fun searchUsers(@Query("q") query: String, @Query paging: Paging): ResponseList<User>

    @PATCH("{recipient_type}/{recipient_id}/messages/batch_mark_as_read")
    @Throws(YepException::class)
    fun batchMarkAsRead(@PathRecipientType @Path("recipient_type") recipientType: String,
                        @Path("recipient_id") recipientId: String,
                        @Param("max_id") maxId: String): LastReadResponse

    @GET("/{recipient_type}/{recipient_id}/messages/sent_last_read_at")
    @Throws(YepException::class)
    fun getSentLastRead(@PathRecipientType @Path("recipient_type") recipientType: String,
                        @Path("recipient_id") recipientId: String): LastReadResponse

    @GET("blocked_users")
    @Throws(YepException::class)
    fun getBlockedUsers(@Query paging: Paging): ResponseList<User>

    @POST("blocked_users")
    @Throws(YepException::class)
    fun blockUser(@Param("user_id") id: String): ResponseCode

    @DELETE("blocked_users/{id}")
    @Throws(YepException::class)
    fun unblockUser(@Path("id") id: String): ResponseCode

    @GET("users/{id}/settings_with_current_user")
    @Throws(YepException::class)
    fun getUserSettings(@Path("id") id: String): UserSettings

    @GET("topics/discover")
    @Throws(YepException::class)
    fun getDiscoverTopics(@Query("sort") @TopicSortOrder sortOrder: String?, @Query paging: Paging,
                          @Query("skill_id") skillId: String? = null): ResponseList<Topic>

    @GET("topics")
    @Throws(YepException::class)
    fun getTopics(@Query paging: Paging): ResponseList<Topic>

    @GET("users/{id}/topics")
    @Throws(YepException::class)
    fun getTopics(@Path("id") userId: String, @Query paging: Paging): ResponseList<Topic>

    @POST("topics")
    @Throws(YepException::class)
    fun postTopic(@Raw topic: NewTopic): Topic

    @PUT("topics/{id}")
    fun updateTopic(@Path("id") id: String, @Param("allow_comment") allowComment: Boolean): ResponseCode

    @DELETE("topics/{id}")
    fun deleteTopic(@Path("id") id: String): ResponseCode

    @POST("feedbacks")
    @Throws(YepException::class)
    fun postFeedback(@Param("content") content: String, @Param("device_info") deviceInfo: String): ResponseCode

    @POST("circles/{id}/share")
    @Throws(YepException::class)
    fun getCircleShareUrl(@Path("id") id: String): UrlResponse

    @POST("circles/{id}/join")
    @Throws(YepException::class)
    fun joinCircle(@Path("id") circleId: String): Circle

    @DELETE("circles/{id}/leave")
    @Throws(YepException::class)
    fun leaveCircle(@Path("id") circleId: String): Circle

    @GET("circles")
    @Throws(YepException::class)
    fun getCircles(@Query paging: Paging): ResponseList<Circle>

    @GET("conversations")
    @Throws(YepException::class)
    fun getConversations(@Query("max_id", "per_page") paging: Paging): ConversationsResponse


    @DELETE("auth/logout")
    @Throws(YepException::class)
    fun logout(): ResponseCode

}

