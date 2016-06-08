package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import android.provider.ContactsContract
import android.support.v4.content.AsyncTaskLoader
import catchla.yep.model.ContactUpload
import catchla.yep.model.TaskResponse
import catchla.yep.model.User
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class ContactFriendsLoader(
        context: Context,
        private val account: Account
) : AsyncTaskLoader<TaskResponse<List<User>>>(context) {

    override fun loadInBackground(): TaskResponse<List<User>> {
        val yep = YepAPIFactory.getInstance(context, account)
        try {
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            val country = Locale.getDefault().country
            val contact = ContactUpload()
            val cols = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
            val selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " NOT NULL"
            val c = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, cols, selection, null, null)
            c!!.moveToFirst()
            while (!c.isAfterLast) {
                val phoneNumber = c.getString(1)
                try {
                    contact.add(c.getString(0), phoneNumberUtil.parse(phoneNumber, country).nationalNumber.toString())
                } catch (e: NumberParseException) {
                    // Ignore
                }
                c.moveToNext()
            }
            c.moveToNext()
            c.close()
            val user = yep.uploadContact(contact)
            return TaskResponse(user)
        } catch (e: YepException) {
            return TaskResponse(exception = e)
        }

    }

    override fun onStartLoading() {
        forceLoad()
    }
}
