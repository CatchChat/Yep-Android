package catchla.yep.model

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

/**
 * Created by mariotaku on 15/11/11.
 */
@JsonObject
data class UrlResponse(
        @JsonField(name = arrayOf("url"))
        var url: String? = null
)
