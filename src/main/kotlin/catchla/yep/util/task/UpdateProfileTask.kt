package catchla.yep.util.task

import android.accounts.Account
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import catchla.yep.R
import catchla.yep.fragment.ProgressDialogFragment
import catchla.yep.model.ProfileUpdate
import catchla.yep.model.TaskResponse
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import org.apache.commons.lang3.ArrayUtils
import org.mariotaku.restfu.http.ContentType
import org.mariotaku.restfu.http.mime.FileBody
import org.mariotaku.restfu.http.mime.MultipartBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created by mariotaku on 15/11/1.
 */
class UpdateProfileTask(
        private val activity: FragmentActivity,
        private val account: Account,
        private val profileUpdate: ProfileUpdate,
        private val profileImageUri: Uri?
) : AsyncTask<Any, Any, TaskResponse<User>>() {

    override fun doInBackground(vararg params: Any): TaskResponse<User> {
        val yep = YepAPIFactory.getInstance(activity, account)
        val profileUpdate = profileUpdate
        if (profileImageUri != null) {
            var fis: FileInputStream? = null
            try {
                val imageFile = File(profileImageUri.path)
                val mimeType = Utils.getImageMimeType(imageFile)
                val body = MultipartBody()
                fis = FileInputStream(imageFile)
                body.add("avatar", FileBody(fis, Utils.getFilename(imageFile, mimeType),
                        imageFile.length(), ContentType.parse(mimeType)))
                yep.setAvatarRaw(body)
            } catch (e: YepException) {
                return TaskResponse(null, e)
            } catch (e: IOException) {
                return TaskResponse(null, e)
            } finally {
                Utils.closeSilently(fis)
            }
        }
        try {
            if (!ArrayUtils.isEmpty(profileUpdate.keys())) {
                yep.updateProfile(profileUpdate)
            }
            val data = yep.getUser()
            Utils.saveUserInfo(activity, account, data)
            return TaskResponse(data)
        } catch (e: YepException) {
            return TaskResponse(exception = e)
        }

    }


    override fun onPreExecute() {
        super.onPreExecute()
        val df = ProgressDialogFragment.show(activity, UPDATE_PROFILE_DIALOG_FRAGMENT_TAG)
        df!!.isCancelable = false
    }

    override fun onPostExecute(result: TaskResponse<User>) {
        val fm = activity.supportFragmentManager
        val fragment = fm.findFragmentByTag(UPDATE_PROFILE_DIALOG_FRAGMENT_TAG)
        if (fragment is DialogFragment) {
            fragment.dismiss()
        }
        if (result.data != null) {
            if (activity is Callback) {
                activity.onProfileUpdated(result.data)
            }
        } else {
            Toast.makeText(activity, R.string.unable_to_update_profile, Toast.LENGTH_SHORT).show()
        }
        super.onPostExecute(result)
    }

    interface Callback {

        fun onProfileUpdated(user: User)
    }

    companion object {
        private val UPDATE_PROFILE_DIALOG_FRAGMENT_TAG = "update_profile"
    }
}
