package catchla.yep.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import nz.bradcampbell.paperparcel.PaperParcelable

/**
 * Created by mariotaku on 15/10/10.
 */
@JsonObject
data class UserSettings(
        @JsonField(name = arrayOf("blocked"))
        var blocked: Boolean = false,
        @JsonField(name = arrayOf("do_not_disturb"))
        var doNotDisturb: Boolean = false
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(UserSettings::class.java)
    }
}
