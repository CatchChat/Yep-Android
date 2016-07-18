/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package catchla.yep.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.fragment.MessageDialogFragment
import catchla.yep.fragment.ProgressDialogFragment
import catchla.yep.model.*
import catchla.yep.util.YepAPIFactory
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.android.synthetic.main.activity_sign_in_sign_up.*
import me.philio.pinentry.PinEntryView
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter
import java.util.*

class SignInActivity : ContentActivity(), Constants, ViewPager.OnPageChangeListener, View.OnClickListener {

    val TAG_VERIFY_PHONE = "verify_phone"

    private var mPhoneNumber: String? = null
    private var mCountryCode: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)
        val adapter = TabsAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.isEnabled = false
        viewPager.addOnPageChangeListener(this)

        nextButton.setOnClickListener(this)

        adapter.addTab(EditPhoneNumberFragment::class.java, null, 0, null)
        adapter.addTab(VerifyPhoneNumberFragment::class.java, null, 0, null)
    }

    override fun onDestroy() {
        viewPager.removeOnPageChangeListener(this)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onClick(v: View) {
        when (v) {
            nextButton -> {
                val fragment = (viewPager.adapter as TabsAdapter).primaryItem
                if (fragment is AbsSignInPageFragment) {
                    fragment.onNextPage()
                }
            }
        }
    }

    private fun setNextEnabled(enabled: Boolean) {
        nextButton!!.isEnabled = enabled
    }

    private fun gotoNextPage() {
        val currentItem = viewPager.currentItem
        if (currentItem == viewPager.adapter.count - 1) return
        viewPager.currentItem = currentItem + 1
    }

    private fun sendVerifyCode(phoneNumber: String, countryCode: String) {
        ProgressDialogFragment.show(this, "send_verify")
        val task = object : AbstractTask<Array<String>, TaskResponse<Pair<String, String>>, SignInActivity>() {

            public override fun doLongOperation(args: Array<String>): TaskResponse<Pair<String, String>> {
                val yep = YepAPIFactory.getInstanceWithToken(this@SignInActivity, null)
                try {
                    val code = yep.sendVerifyCode(args[0], args[1], VerificationMethod.SMS)
                    System.identityHashCode(code)
                    return TaskResponse(Pair.create(args[0], args[1]))
                } catch (e: YepException) {
                    return TaskResponse(exception = e)
                }

            }

            override fun afterExecute(handler: SignInActivity?, result: TaskResponse<Pair<String, String>>) {
                val f = handler!!.supportFragmentManager.findFragmentByTag("send_verify")
                if (f is DialogFragment) {
                    f.dismiss()
                }
                if (result.data != null) {
                    val data = result.data
                    handler.setPhoneNumber(data.first, data.second)
                    handler.gotoNextPage()
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    val exception = result.exception as YepException
                    val error = exception.error
                    if (TextUtils.isEmpty(error)) {
                        MessageDialogFragment.show(handler, getString(R.string.unable_to_verify_phone), "unable_to_verify_phone")
                    } else {
                        MessageDialogFragment.show(handler, error, "unable_to_verify_phone")
                    }
                }
            }

        }
        task.setParams(arrayOf(phoneNumber, countryCode))
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private fun setPhoneNumber(phoneNumber: String, countryCode: String) {
        mPhoneNumber = phoneNumber
        mCountryCode = countryCode
    }

    private fun verifyPhoneNumber(verifyCode: String) {
        ProgressDialogFragment.show(this, TAG_VERIFY_PHONE)
        val task = object : AbstractTask<Array<String>, TaskResponse<AccessToken>, SignInActivity>() {

            public override fun doLongOperation(args: Array<String>): TaskResponse<AccessToken> {
                val yep = YepAPIFactory.getInstanceWithToken(this@SignInActivity, null)
                try {
                    val token = yep.tokenByMobile(args[0], args[1], args[2],
                            Client.OFFICIAL, 0)
                    token.user = yep.getUser()
                    return TaskResponse(token)
                } catch (e: YepException) {
                    return TaskResponse<AccessToken>(exception = e)
                }

            }

            public override fun afterExecute(handler: SignInActivity?, result: TaskResponse<AccessToken>) {
                val f = handler!!.supportFragmentManager.findFragmentByTag(TAG_VERIFY_PHONE)
                if (f is DialogFragment) {
                    f.dismiss()
                }
                if (result.data != null) {
                    handler.finishAddAddAccount(result.data)
                    handler.gotoNextPage()
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    val exception = result.exception as YepException
                    val error = exception.error
                    if (TextUtils.isEmpty(error)) {
                        MessageDialogFragment.show(handler, getString(R.string.unable_to_verify_phone), "unable_to_verify_phone")
                    } else {
                        MessageDialogFragment.show(handler, error, "unable_to_verify_phone")
                    }
                    val fragment = handler.currentFragment
                    if (fragment is VerifyPhoneNumberFragment) {
                        fragment.clearPin()
                    }
                }
            }

        }
        task.setParams(arrayOf<String>(mPhoneNumber!!, mCountryCode!!, verifyCode))
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private val currentFragment: Fragment?
        get() = viewPager.adapter.instantiateItem(viewPager, viewPager.currentItem) as Fragment?

    abstract class AbsSignInPageFragment : Fragment() {
        protected abstract fun updateNextButton()

        abstract fun onNextPage()

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if (isVisibleToUser) {
                updateNextButton()
            }
        }

        val signInActivity: SignInActivity?
            get() = activity as SignInActivity?

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            updateNextButton()
        }
    }

    class EditPhoneNumberFragment : AbsSignInPageFragment() {
        private var editPhoneNumber: EditText? = null
        private var editCountryCode: EditText? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_sign_up_phone_number, container, false)
        }

        override fun updateNextButton() {
            if (!userVisibleHint) return
            val signInActivity = signInActivity ?: return
            if (editPhoneNumber == null || editCountryCode == null)
                return
            signInActivity.setNextEnabled(editPhoneNumber!!.length() > 0 && editCountryCode!!.length() > 0)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            editPhoneNumber = view!!.findViewById(R.id.edit_phone_number) as EditText
            editCountryCode = view.findViewById(R.id.edit_country_code) as EditText
        }

        override fun onNextPage() {
            val phoneNumber = editPhoneNumber!!.text.toString()
            val countryCode = editCountryCode!!.text.toString()
            signInActivity!!.sendVerifyCode(phoneNumber, countryCode)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            editPhoneNumber!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
            editCountryCode!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
            if (savedInstanceState == null) {
                val util = PhoneNumberUtil.getInstance()
                editCountryCode!!.setText(util.getCountryCodeForRegion(Locale.getDefault().country).toString())
            }
        }
    }

    class VerifyPhoneNumberFragment : AbsSignInPageFragment() {

        private var mEditVerifyCode: PinEntryView? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_sign_up_verify_phone, container, false)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mEditVerifyCode = view!!.findViewById(R.id.edit_verify_code) as PinEntryView
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            mEditVerifyCode!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                    if (verifyPin()) {
                        onNextPage()
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }

        override fun updateNextButton() {
            if (!userVisibleHint) return
            val signInActivity = signInActivity
            if (signInActivity == null || mEditVerifyCode == null)
                return
            signInActivity.setNextEnabled(verifyPin())
        }

        private fun verifyPin(): Boolean {
            return mEditVerifyCode!!.text.length == 4
        }

        override fun onNextPage() {
            val verifyCode = mEditVerifyCode!!.text.toString()
            signInActivity!!.verifyPhoneNumber(verifyCode)
        }

        fun clearPin() {
            mEditVerifyCode!!.clearText()
        }
    }

    private fun finishAddAddAccount(token: AccessToken) {
        val data = Intent()
        data.putExtra(Constants.EXTRA_TOKEN, token)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

}