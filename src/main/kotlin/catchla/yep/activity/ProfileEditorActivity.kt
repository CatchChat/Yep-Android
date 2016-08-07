package catchla.yep.activity

import android.Manifest
import android.accounts.AccountManager
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Menu
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.model.ProfileUpdate
import catchla.yep.model.User
import catchla.yep.util.Utils
import catchla.yep.util.task.UpdateProfileTask
import kotlinx.android.synthetic.main.activity_profile_editor.*

class ProfileEditorActivity : ContentActivity(), UpdateProfileTask.Callback, Constants {

    // Data fields
    private var mProfileImageUri: Uri? = null
    private var currentUser: User? = null
    private var mTask: UpdateProfileTask? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode != Activity.RESULT_OK) return
                mProfileImageUri = data.data
                loadUser()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editor)

        loadUser()
        logout.setOnClickListener {
            val df = LogoutConfirmDialogFragment()
            df.show(supportFragmentManager, "logout_confirm")
        }
        profileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@ProfileEditorActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickProfileImage()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val permissions: Array<String>
                permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(this@ProfileEditorActivity, permissions,
                        REQUEST_REQUEST_PICK_IMAGE_PERMISSION)
            } else {
                pickProfileImage()
            }
        }
        editBadge.setOnClickListener {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_REQUEST_PICK_IMAGE_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickProfileImage()
                }
            }
        }
    }

    private fun pickProfileImage() {
        val intent = ThemedImagePickerActivity.withThemed(this@ProfileEditorActivity).aspectRatio(1, 1).maximumSize(512, 512).build()
        intent.setClass(this@ProfileEditorActivity, ThemedImagePickerActivity::class.java)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    private fun loadUser() {
        val account = account
        displayUser(Utils.getAccountUser(this, account))
    }

    override fun onProfileUpdated(user: User) {
        displayUser(user)
        finish()
    }

    private fun displayUser(user: User) {
        currentUser = user
        val url = if (mProfileImageUri != null) mProfileImageUri!!.toString() else user.avatarUrl
        imageLoader.displayProfileImage(url, profileImage)
        countryCode.text = user.phoneCode
        phoneNumber.text = user.mobile
        editNickname.setText(user.nickname)
        editUsername.setText(user.username)
        val canEditUsername = TextUtils.isEmpty(user.username)
        editUsername.isEnabled = canEditUsername
        editUsername.isFocusable = canEditUsername
        editIntroduction.setText(user.introduction)
        editWebsite.setText(user.websiteUrl)
        editBadge.setImageResource(user.badge?.icon ?: 0)
    }

    class LogoutConfirmDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.logout_confirm_message)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                val am = AccountManager.get(activity)
                val account = (activity as ProfileEditorActivity).account
                am.removeAccount(account, { }, Handler())
                val intent = Intent(activity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            return builder.create()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_profile_editor, menu)
        return true
    }

    override fun onBackPressed() {
        if (mTask != null && mTask!!.status == AsyncTask.Status.RUNNING) return
        var changed = mProfileImageUri != null
        val update = ProfileUpdate()
        val currentUser = currentUser!!
        if (!TextUtils.equals(currentUser.nickname ?: "", editNickname.text)) {
            changed = changed or true
            update.setNickname(editNickname.text.toString())
        }
        if (!TextUtils.equals(currentUser.introduction ?: "", editIntroduction.text)) {
            changed = changed or true
            update.setIntroduction(editIntroduction.text.toString())
        }
        if (!TextUtils.equals(currentUser.username ?: "", editUsername.text)) {
            changed = changed or true
            update.setUsername(editUsername.text.toString())
        }
        if (!TextUtils.equals(currentUser.websiteUrl ?: "", editWebsite.text)) {
            changed = changed or true
            update.setWebsite(editWebsite.text.toString())
        }
        if (changed) {
            mTask = UpdateProfileTask(this, Utils.getCurrentAccount(this)!!, update, mProfileImageUri)
            mTask!!.execute()
            return
        }
        super.onBackPressed()
    }

    companion object {

        private val REQUEST_PICK_IMAGE = 101
        private val REQUEST_REQUEST_PICK_IMAGE_PERMISSION = 201
    }

}
