package catchla.yep.util.task;

import android.accounts.Account;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.mariotaku.restfu.http.RestHttpResponse;

import java.io.File;
import java.io.IOException;

import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.ProfileUpdate;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/11/1.
 */
public class UpdateProfileTask extends AsyncTask<Object, Object, TaskResponse<User>> {
    private static final String UPDATE_PROFILE_DIALOG_FRAGMENT_TAG = "update_profile";
    private final FragmentActivity mActivity;
    private final Account mAccount;
    private final ProfileUpdate mProfileUpdate;

    public UpdateProfileTask(final FragmentActivity activity, final Account account, final ProfileUpdate profileUpdate) {
        mActivity = activity;
        mAccount = account;
        mProfileUpdate = profileUpdate;
    }

    @Override
    protected TaskResponse<User> doInBackground(final Object... params) {
        final YepAPI yep = YepAPIFactory.getInstance(mActivity, mAccount);
        final ProfileUpdate profileUpdate = mProfileUpdate;
        final Uri profileImageUri = profileUpdate.getAvatarUri();
        if (profileImageUri != null) {
            try {
                final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.AVATAR);
                final RestHttpResponse response = Utils.uploadToS3(YepAPIFactory.getHttpClient(yep), token, new File(profileImageUri.getPath()));
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
            if (mActivity instanceof Callback) {
                ((Callback) mActivity).onProfileUpdated(result.getData());
            }
        }
        super.onPostExecute(result);
    }

    public interface Callback {

        void onProfileUpdated(final User user);
    }
}
