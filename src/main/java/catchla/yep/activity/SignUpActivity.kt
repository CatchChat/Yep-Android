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
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.fragment.ProgressDialogFragment
import catchla.yep.model.*
import catchla.yep.util.YepAPIFactory
import me.philio.pinentry.PinEntryView
import org.mariotaku.abstask.library.AbstractTask
import org.mariotaku.abstask.library.TaskStarter

class SignUpActivity : ContentActivity(), Constants, ViewPager.OnPageChangeListener, View.OnClickListener {

    private var mViewPager: ViewPager? = null
    private var mAdapter: TabsAdapter? = null

    private var mNextButton: Button? = null
    private var mName: String? = null
    private var mCreateRegistrationResult: CreateRegistrationResult? = null
    private var mAccessToken: AccessToken? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)
        mAdapter = TabsAdapter(this, supportFragmentManager)
        mViewPager!!.adapter = mAdapter
        mViewPager!!.isEnabled = false
        mViewPager!!.setOnPageChangeListener(this)

        mNextButton!!.setOnClickListener(this)

        mAdapter!!.addTab(EditNameFragment::class.java, null, 0, null)
        mAdapter!!.addTab(EditPhoneNumberFragment::class.java, null, 0, null)
        mAdapter!!.addTab(VerifyPhoneNumberFragment::class.java, null, 0, null)
        mAdapter!!.addTab(EditAvatarFragment::class.java, null, 0, null)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        mViewPager = findViewById(R.id.view_pager) as ViewPager?
        mNextButton = findViewById(R.id.next_button) as Button?
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
        when (v.id) {
            R.id.next_button -> {
                val fragment = mAdapter!!.primaryItem
                if (fragment is AbsSignUpPageFragment) {
                    fragment.onNextPage()
                }
            }
        }
    }

    private fun setName(name: String) {
        mName = name
    }

    private fun setNextEnabled(enabled: Boolean) {
        mNextButton!!.isEnabled = enabled
    }

    private fun gotoNextPage() {
        val currentItem = mViewPager!!.currentItem
        if (currentItem == mAdapter!!.count - 1) return
        mViewPager!!.currentItem = currentItem + 1
    }

    private fun sendVerifyCode(phoneNumber: String, countryCode: String) {
        ProgressDialogFragment.show(this, "create_registration")
        val task = object : AbstractTask<Array<String>, TaskResponse<CreateRegistrationResult>, SignUpActivity>() {

            public override fun doLongOperation(args: Array<String>): TaskResponse<CreateRegistrationResult> {
                val yep = YepAPIFactory.getInstanceWithToken(this@SignUpActivity, null)
                try {
                    return TaskResponse.getInstance(yep.createRegistration(args[1], args[2], args[0], 0.0, 0.0))
                } catch (e: YepException) {
                    return TaskResponse.getInstance<CreateRegistrationResult>(e)
                }

            }

            public override fun afterExecute(handler: SignUpActivity?, result: TaskResponse<CreateRegistrationResult>?) {
                val f = handler!!.supportFragmentManager.findFragmentByTag("create_registration")
                if (f is DialogFragment) {
                    f.dismiss()
                }
                if (result!!.hasData()) {
                    handler.setCreateRegistrationResult(result.data)
                    handler.gotoNextPage()
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    val exception = result.exception as YepException
                    val error = exception.error
                    if (TextUtils.isEmpty(error)) {
                        Toast.makeText(handler, R.string.unable_to_register, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(handler, error, Toast.LENGTH_SHORT).show()
                    }
                    val fragment = handler.currentFragment
                    if (fragment is VerifyPhoneNumberFragment) {
                        fragment.clearPin()
                    }
                }
            }

        }
        task.setParams(arrayOf<String>(mName!!, phoneNumber, countryCode))
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private val currentFragment: Fragment?
        get() = mAdapter!!.instantiateItem(mViewPager!!, mViewPager!!.currentItem) as Fragment?

    private fun verifyPhoneNumber(verifyCode: String) {
        ProgressDialogFragment.show(this, "update_registration")
        val task = object : AbstractTask<Pair<CreateRegistrationResult, String>, TaskResponse<AccessToken>, SignUpActivity>() {

            public override fun doLongOperation(args: Pair<CreateRegistrationResult, String>): TaskResponse<AccessToken> {
                val yep = YepAPIFactory.getInstanceWithToken(this@SignUpActivity, null)
                try {
                    val result = args.first
                    val verifyCode = args.second
                    return TaskResponse.getInstance(yep.updateRegistration(result.mobile,
                            result.phoneCode, verifyCode, Client.OFFICIAL, 0))
                } catch (e: YepException) {
                    return TaskResponse.getInstance<AccessToken>(e)
                }

            }

            public override fun afterExecute(handler: SignUpActivity?, result: TaskResponse<AccessToken>?) {
                val f = handler!!.supportFragmentManager.findFragmentByTag("update_registration")
                if (f is DialogFragment) {
                    f.dismiss()
                }
                if (result!!.hasData()) {
                    handler.setUpdateRegistrationResult(result.data)
                    handler.gotoNextPage()
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    val exception = result.exception as YepException
                    val error = exception.error
                    if (TextUtils.isEmpty(error)) {
                        Toast.makeText(handler, R.string.unable_to_verify_phone, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(handler, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        task.setParams(Pair.create<CreateRegistrationResult, String>(mCreateRegistrationResult, verifyCode))
        task.setResultHandler(this)
        TaskStarter.execute(task)
    }

    private fun setUpdateRegistrationResult(result: AccessToken) {
        mAccessToken = result
    }

    fun setCreateRegistrationResult(result: CreateRegistrationResult) {
        mCreateRegistrationResult = result
    }

    abstract class AbsSignUpPageFragment : Fragment() {
        protected abstract fun updateNextButton()

        abstract fun onNextPage()

        override fun setUserVisibleHint(isVisibleToUser: Boolean) {
            super.setUserVisibleHint(isVisibleToUser)
            if (isVisibleToUser) {
                updateNextButton()
            }
        }

        val signUpActivity: SignUpActivity?
            get() = activity as SignUpActivity

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            updateNextButton()
        }
    }

    class EditNameFragment : AbsSignUpPageFragment() {
        private var mEditName: EditText? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_sign_up_edit_name, container, false)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mEditName = view!!.findViewById(R.id.edit_name) as EditText
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            mEditName!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }

        override fun updateNextButton() {
            if (!userVisibleHint) return
            val signUpActivity = signUpActivity
            if (signUpActivity == null || mEditName == null) return
            signUpActivity.setNextEnabled(mEditName!!.length() > 0)
        }

        override fun onNextPage() {
            val signUpActivity = signUpActivity
            signUpActivity!!.setName(mEditName!!.text.toString())
            signUpActivity.gotoNextPage()
        }
    }

    class EditPhoneNumberFragment : AbsSignUpPageFragment() {
        private var mEditPhoneNumber: EditText? = null
        private var mEditCountryCode: EditText? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_sign_up_phone_number, container, false)
        }

        override fun updateNextButton() {
            if (!userVisibleHint) return
            val signUpActivity = signUpActivity
            if (signUpActivity == null || mEditPhoneNumber == null || mEditCountryCode == null)
                return
            signUpActivity.setNextEnabled(mEditPhoneNumber!!.length() > 0 && mEditCountryCode!!.length() > 0)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mEditPhoneNumber = view!!.findViewById(R.id.edit_phone_number) as EditText
            mEditCountryCode = view.findViewById(R.id.edit_country_code) as EditText
        }

        override fun onNextPage() {
            val phoneNumber = mEditPhoneNumber!!.text.toString()
            val countryCode = mEditCountryCode!!.text.toString()
            signUpActivity!!.sendVerifyCode(phoneNumber, countryCode)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            mEditPhoneNumber!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
            mEditCountryCode!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    updateNextButton()
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }
    }

    class VerifyPhoneNumberFragment : AbsSignUpPageFragment() {

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
            val signUpActivity = signUpActivity
            if (signUpActivity == null || mEditVerifyCode == null)
                return
            signUpActivity.setNextEnabled(verifyPin())
        }

        override fun onNextPage() {
            val verifyCode = mEditVerifyCode!!.text.toString()
            signUpActivity!!.verifyPhoneNumber(verifyCode)

        }

        private fun verifyPin(): Boolean {
            return mEditVerifyCode!!.text.length == 4
        }


        fun clearPin() {
            mEditVerifyCode!!.clearText()
        }
    }

    class EditAvatarFragment : AbsSignUpPageFragment() {


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.fragment_sign_up_edit_avatar, container, false)
        }

        override fun updateNextButton() {
            if (!userVisibleHint) return
            val signUpActivity = signUpActivity ?: return
            signUpActivity.setNextEnabled(true)
        }

        override fun onNextPage() {
            val signUpActivity = signUpActivity
            val data = Intent()
            signUpActivity!!.finishAddAddAccount()
        }

    }

    private fun finishAddAddAccount() {
        if (mAccessToken == null) return
        val data = Intent()
        data.putExtra(Constants.EXTRA_TOKEN, mAccessToken)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}