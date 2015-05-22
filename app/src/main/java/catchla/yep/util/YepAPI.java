package catchla.yep.util;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by mariotaku on 15/5/12.
 */
public interface YepAPI {

    @POST("/v1/registration/create")
    @FormUrlEncoded
    void createRegistration(@Field("mobile") String mobile, @Field("nickname") String nickname,
                            @Field("phone_code") String phoneCode, @Field("longitude") double longitude,
                            @Field("latitude") double latitude);

}
