package catchla.yep.preference;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ProfileEditorActivity;
import catchla.yep.model.User;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.Utils;
import catchla.yep.util.dagger.GeneralComponentHelper;

/**
 * Created by mariotaku on 15/5/11.
 */
public class AccountInfoPreference extends Preference implements Constants {

    private final Account mAccount;
    private final User mAccountUser;
    @Inject
    ImageLoaderWrapper mImageLoader;

    public AccountInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        GeneralComponentHelper.build(context).inject(this);
        setLayoutResource(R.layout.layout_preference_account_info);
        mAccount = Utils.getCurrentAccount(context);
        mAccountUser = Utils.getCurrentAccountUser(context);
    }

    public AccountInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public AccountInfoPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(@NonNull final View view) {
        super.onBindView(view);
        if (mAccount != null) {
            final ImageView profileImageView = (ImageView) view.findViewById(R.id.account_profile_image);
            final TextView nameView = (TextView) view.findViewById(R.id.account_name);
            final TextView introductionView = (TextView) view.findViewById(R.id.account_introduction);
            mImageLoader.displayProfileImage(mAccountUser.getAvatarThumbUrl(), profileImageView);
            nameView.setText(mAccountUser.getNickname());
            introductionView.setText(mAccountUser.getIntroduction());
        }
    }

    @Override
    protected void onClick() {
        final Context context = getContext();
        final Intent intent = new Intent(context, ProfileEditorActivity.class);
        intent.putExtra(EXTRA_ACCOUNT, mAccount);
        context.startActivity(intent);
    }
}
