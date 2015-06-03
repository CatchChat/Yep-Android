package catchla.yep.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
    private EditText mEditNickname;
    private EditText mEditIntroduction;

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
            mEditNickname.setText(user.getNickname());
            mEditIntroduction.setText(user.getIntroduction());
        }
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final LogoutConfirmDialogFragment df = new LogoutConfirmDialogFragment();
                df.show(getFragmentManager(), "logout_confirm");
            }
        });
    }

    public static class LogoutConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final AccountManager am = AccountManager.get(getActivity());
                    final Account account = Utils.getCurrentAccount(getActivity());
                    am.removeAccountExplicitly(account);
                    final Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, null);
            return builder.create();
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mCountryCodeView = (TextView) findViewById(R.id.country_code);
        mPhoneNumberView = (TextView) findViewById(R.id.phone_number);
        mLogoutButton = findViewById(R.id.logout);
        mEditNickname = (EditText) findViewById(R.id.edit_nickname);
        mEditIntroduction = (EditText) findViewById(R.id.edit_introduction);
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
