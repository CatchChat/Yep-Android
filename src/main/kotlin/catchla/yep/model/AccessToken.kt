package catchla.yep.model


import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

/**
 * Created by mariotaku on 15/2/4.
 */
@JsonObject
@PaperParcel
data class AccessToken(
        @JsonField(name = arrayOf("access_token"))
        var accessToken: String? = null,
        @JsonField(name = arrayOf("user"))
        var user: User? = null
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(AccessToken::class.java)
    }
}
