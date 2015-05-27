package catchla.yep.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import catchla.yep.R;
import catchla.yep.model.User;
import catchla.yep.util.Utils;

public class ProfileEditorActivity extends ContentActivity {

    private ImageView mProfileImageView;
    private TextView mCountryCodeView;
    private TextView mPhoneNumberView;
    private View mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        final Account account = Utils.getCurrentAccount(this);
        final User user = Utils.getAccountUser(this, account);
        if (user != null) {
            Picasso.with(this).load(user.getAvatarUrl()).into(mProfileImageView);
            mCountryCodeView.setText(user.getPhoneCode());
            mPhoneNumberView.setText(user.getMobile());
        }
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AccountManager am = AccountManager.get(ProfileEditorActivity.this);
                am.removeAccountExplicitly(account);
                final Intent intent = new Intent(ProfileEditorActivity.this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mCountryCodeView = (TextView) findViewById(R.id.country_code);
        mPhoneNumberView = (TextView) findViewById(R.id.phone_number);
        mLogoutButton = findViewById(R.id.logout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

}
