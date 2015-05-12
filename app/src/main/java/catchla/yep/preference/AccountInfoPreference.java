package catchla.yep.preference;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

import catchla.yep.R;
import catchla.yep.activity.ProfileEditorActivity;

/**
 * Created by mariotaku on 15/5/11.
 */
public class AccountInfoPreference extends Preference {

    public AccountInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.layout_preference_account_info);
    }

    public AccountInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public AccountInfoPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onClick() {
        getContext().startActivity(new Intent(getContext(), ProfileEditorActivity.class));
    }
}
