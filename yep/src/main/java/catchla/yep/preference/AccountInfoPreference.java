package catchla.yep.preference;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ProfileEditorActivity;
import catchla.yep.model.User;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/5/11.
 */
public class AccountInfoPreference extends Preference implements Constants {

    private final AccountManager mAccountManager;
    private final Account mAccount;
    private final User mAccountUser;

    public AccountInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.layout_preference_account_info);
        mAccountManager = AccountManager.get(context);
        mAccount = Utils.getCurrentAccount(context);
        mAccountUser = Utils.getCurrentAccountUser(context);
    }

    public AccountInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    @Override
    protected void onBindView(@NonNull final View view) {
        super.onBindView(view);
        if (mAccount != null) {
            final ImageView profileImageView = (ImageView) view.findViewById(R.id.account_profile_image);
            final TextView nameView = (TextView) view.findViewById(R.id.account_name);
            final TextView introductionView = (TextView) view.findViewById(R.id.account_introduction);
            Glide.with(getContext()).load(mAccountUser.getAvatarUrl()).placeholder(R.drawable.ic_profile_image_default).into(profileImageView);
            nameView.setText(mAccountUser.getNickname());
            introductionView.setText(mAccountUser.getIntroduction());
        }
    }

    public AccountInfoPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onClick() {
        getContext().startActivity(new Intent(getContext(), ProfileEditorActivity.class));
    }
}
