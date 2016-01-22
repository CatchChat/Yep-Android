package catchla.yep.util.task;

import android.accounts.Account;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;

import catchla.yep.R;
import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.ProfileUpdate;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.http.FileRequestBody;

/**
 * Created by mariotaku on 15/11/1.
 */
public class UpdateProfileTask extends AsyncTask<Object, Object, TaskResponse<User>> {
    private static final String UPDATE_PROFILE_DIALOG_FRAGMENT_TAG = "update_profile";
    private final FragmentActivity mActivity;
    private final Account mAccount;
    private final ProfileUpdate mProfileUpdate;
    private final Uri mProfileImageUri;

    public UpdateProfileTask(final FragmentActivity activity, final Account account,
                             final ProfileUpdate profileUpdate, final Uri profileImageUri) {
        mActivity = activity;
        mAccount = account;
        mProfileUpdate = profileUpdate;
        mProfileImageUri = profileImageUri;
    }

    @Override
    protected TaskResponse<User> doInBackground(final Object... params) {
        final YepAPI yep = YepAPIFactory.getInstance(mActivity, mAccount);
        final ProfileUpdate profileUpdate = mProfileUpdate;
        if (mProfileImageUri != null) {
            try {
                final File imageFile = new File(mProfileImageUri.getPath());
                final String mimeType = Utils.getImageMimeType(imageFile);
                final MultipartBuilder builder = new MultipartBuilder();
                builder.addFormDataPart("avatar", Utils.getFilename(imageFile, mimeType),
                        FileRequestBody.create(MediaType.parse(mimeType), imageFile));
                yep.setAvatarRaw(builder.build());
            } catch (YepException e) {
                return TaskResponse.getInstance(e);
            }
        }
        try {
            if (!ArrayUtils.isEmpty(profileUpdate.keys())) {
                yep.updateProfile(profileUpdate);
            }
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
        } else {
            Toast.makeText(mActivity, R.string.unable_to_update_profile, Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }

    public interface Callback {

        void onProfileUpdated(final User user);
    }
}
