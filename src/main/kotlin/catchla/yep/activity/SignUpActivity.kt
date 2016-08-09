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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.adapter.TabsAdapter
import catchla.yep.fragment.ProgressDialogFragment
import catchla.yep.model.AccessToken
import catchla.yep.model.Client
import catchla.yep.model.CreateRegistrationResult
import catchla.yep.model.YepException
import catchla.yep.util.YepAPIFactory
import kotlinx.android.synthetic.main.activity_sign_in_sign_up.*
import me.philio.pinentry.PinEntryView
import nl.komponents.kovenant.task
import nl.komponents.kovenant.ui.alwaysUi
import nl.komponents.kovenant.ui.failUi
import nl.komponents.kovenant.ui.successUi

class SignUpActivity : ContentActivity(), Constants, ViewPager.OnPageChangeListener, View.OnClickListener {


    private var mName: String? = null
    private var mCreateRegistrationResult: CreateRegistrationResult? = null
    private var mAccessToken: AccessToken? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)
        val adapter = TabsAdapter(this, supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.isEnabled = false
        viewPager.addOnPageChangeListener(this)

        nextButton.setOnClickListener(this)

        adapter.addTab(EditNameFragment::class.java, null, 0, null)
        adapter.addTab(EditPhoneNumberFragment::class.java, null, 0, null)
        adapter.addTab(VerifyPhoneNumberFragment::class.java, null, 0, null)
        adapter.addTab(EditAvatarFragment::class.java, null, 0, null)
    }

    override fun onContentChanged() {
        super.onContentChanged()
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
        nextButton.isEnabled = enabled
    }

    private fun gotoNextPage() {
        val currentItem = viewPager.currentItem
        if (currentItem == viewPager.adapter.count - 1) return
        viewPager.currentItem = currentItem + 1
    }

    private fun sendVerifyCode(phoneNumber: String, countryCode: String) {
        ProgressDialogFragment.show(this, "create_registration")
        task {
            val yep = YepAPIFactory.getInstanceWithToken(this@SignUpActivity, null)
            yep.createRegistration(phoneNumber, countryCode, mName!!, 0.0, 0.0)
        }.successUi {
            setCreateRegistrationResult(it)
            gotoNextPage()
        }.failUi {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            val exception = it as? YepException
            val error = exception?.error
            if (error != null && !TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.unable_to_register, Toast.LENGTH_SHORT).show()
            }
            val fragment = currentFragment
            if (fragment is VerifyPhoneNumberFragment) {
                fragment.clearPin()
            }
        }.alwaysUi {
            val f = supportFragmentManager.findFragmentByTag("create_registration")
            if (f is DialogFragment) {
                f.dismiss()
            }
        }

    }

    private val currentFragment: Fragment?
        get() = viewPager.adapter.instantiateItem(viewPager, viewPager.currentItem) as Fragment?

    private fun verifyPhoneNumber(verifyCode: String) {
        ProgressDialogFragment.show(this, "update_registration")
        task {
            val yep = YepAPIFactory.getInstanceWithToken(this@SignUpActivity, null)
            val result = mCreateRegistrationResult!!
            yep.updateRegistration(result.mobile, result.phoneCode, verifyCode, Client.OFFICIAL, 0)
        }.successUi {
            setUpdateRegistrationResult(it)
            gotoNextPage()
        }.failUi {
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            val exception = it as? YepException
            val error = exception?.error
            if (TextUtils.isEmpty(error)) {
                Toast.makeText(this, R.string.unable_to_verify_phone, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }.alwaysUi {
            val f = supportFragmentManager.findFragmentByTag("update_registration")
            if (f is DialogFragment) {
                f.dismiss()
            }
        }
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