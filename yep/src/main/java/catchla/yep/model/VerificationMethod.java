package catchla.yep.model;

import android.support.annotation.StringDef;

/**
 * Created by mariotaku on 15/5/26.
 */
@StringDef({VerificationMethod.SMS, VerificationMethod.CALL})
public @interface VerificationMethod {
    String SMS = "sms";
    String CALL = "call";
}
