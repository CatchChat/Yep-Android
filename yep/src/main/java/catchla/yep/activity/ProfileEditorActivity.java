package catchla.yep.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.mariotaku.restfu.http.RestHttpResponse;

import java.io.File;
import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.ProfileUpdate;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

public class ProfileEditorActivity extends ContentActivity implements Constants {

    private static final int REQUEST_PICK_IMAGE = 101;

    // Views
    private ImageView mProfileImageView;
    private TextView mCountryCodeView;
    private TextView mPhoneNumberView;
    private View mLogoutButton;
    private EditText mEditNickname;
    private EditText mEditIntroduction;

    // Data fields
    private Uri mProfileImageUri;
    private User mCurrentUser;
    private UpdateProfileTask mTask;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE: {
                if (resultCode != RESULT_OK) return;
                mProfileImageUri = data.getData();
                loadUser();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);

        loadUser();
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final LogoutConfirmDialogFragment df = new LogoutConfirmDialogFragment();
                df.show(getSupportFragmentManager(), "logout_confirm");
            }
        });
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = ThemedImagePickerActivity.withThemed(ProfileEditorActivity.this).aspectRatio(1, 1).maximumSize(512, 512).build();
                intent.setClass(ProfileEditorActivity.this, ThemedImagePickerActivity.class);
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });
    }

    private void loadUser() {
        final Account account = getAccount();
        final User user = Utils.getAccountUser(this, account);
        if (user != null) {
            displayUser(user);
        }
    }

    private void displayUser(final User user) {
        mCurrentUser = user;
        final String url = mProfileImageUri != null ? mProfileImageUri.toString() : user.getAvatarUrl();
        mImageLoader.displayProfileImage(url, mProfileImageView);
        mCountryCodeView.setText(user.getPhoneCode());
        mPhoneNumberView.setText(user.getMobile());
        mEditNickname.setText(user.getNickname());
        mEditIntroduction.setText(user.getIntroduction());
    }

    public static class LogoutConfirmDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.logout_confirm_message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final AccountManager am = AccountManager.get(getActivity());
                    final Account account = ((ProfileEditorActivity) getActivity()).getAccount();
                    am.removeAccount(account, new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(final AccountManagerFuture<Boolean> future) {

                        }
                    }, new Handler());
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
    public void onBackPressed() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) return;
        boolean changed = mProfileImageUri != null;
        if (!TextUtils.equals(mCurrentUser.getNickname(), mEditNickname.getText())) {
            changed |= true;
        }
        if (!TextUtils.equals(mCurrentUser.getIntroduction(), mEditIntroduction.getText())) {
            changed |= true;
        }
        if (changed) {
            mTask = new UpdateProfileTask(this, Utils.getCurrentAccount(this),
                    String.valueOf(mEditNickname.getText()), String.valueOf(mEditIntroduction.getText()),
                    mProfileImageUri);
            mTask.execute();
            return;
        }
        super.onBackPressed();
    }

    private static class UpdateProfileTask extends AsyncTask<Object, Object, TaskResponse<User>> {
        private static final String UPDATE_PROFILE_DIALOG_FRAGMENT_TAG = "update_profile";
        private final ProfileEditorActivity mActivity;
        private final Account mAccount;
        private final String mNickname;
        private final String mIntroduction;
        private final Uri mProfileImageUri;

        public UpdateProfileTask(final ProfileEditorActivity activity, final Account account, final String nickname, final String introduction, final Uri profileImageUri) {
            mActivity = activity;
            mAccount = account;
            mNickname = nickname;
            mIntroduction = introduction;
            mProfileImageUri = profileImageUri;
        }

        @Override
        protected TaskResponse<User> doInBackground(final Object... params) {
            final YepAPI yep = YepAPIFactory.getInstance(mActivity, mAccount);
            final ProfileUpdate profileUpdate = new ProfileUpdate();
            if (mProfileImageUri != null) {
                try {
                    final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.AVATAR);
                    final RestHttpResponse response = Utils.uploadToS3(YepAPIFactory.getHttpClient(yep), token, new File(mProfileImageUri.getPath()));
                    if (response.isSuccessful()) {
                        profileUpdate.setAvatarUrl(response.getHeader("Location"));
                    } else {
                        throw new YepException("Unable to upload to s3", response);
                    }
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                } catch (IOException e) {
                    return TaskResponse.getInstance(e);
                }
            }
            profileUpdate.setNickname(mNickname);
            profileUpdate.setIntroduction(mIntroduction);
            try {
                yep.updateProfile(profileUpdate);
                final User data = yep.getUser();
                Utils.saveUserInfo(mActivity, mAccount, data);
                return TaskResponse.getInstance(data);
            } catch (YepException e) {
                return TaskResponse.getInstance(e);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogFragment df = ProgressDialogFragment.show(mActivity, UPDATE_PROFILE_DIALOG_FRAGMENT_TAG);
            df.setCancelable(false);
        }

        @Override
        protected void onPostExecute(final TaskResponse<User> result) {
            final FragmentManager fm = mActivity.getSupportFragmentManager();
            final Fragment fragment = fm.findFragmentByTag(UPDATE_PROFILE_DIALOG_FRAGMENT_TAG);
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).dismiss();
            }
            if (result.hasData()) {
                mActivity.displayUser(result.getData());
                mActivity.finish();
            }
            super.onPostExecute(result);
        }
    }
}
