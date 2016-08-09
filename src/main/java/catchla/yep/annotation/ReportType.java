package catchla.yep.annotation;

import android.support.annotation.IntDef;

/**
 * Created by mariotaku on 16/8/9.
 */
@IntDef({ReportType.SEXUAL_CONTENT, ReportType.SPAM, ReportType.PHISHING, ReportType.OTHER})
public @interface ReportType {
    int SEXUAL_CONTENT = 0;
    int SPAM = 1;
    int PHISHING = 2;
    int OTHER = 3;
}
