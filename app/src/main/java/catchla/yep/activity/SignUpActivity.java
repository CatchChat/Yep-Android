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

package catchla.yep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.AccessToken;
import catchla.yep.model.Client;
import catchla.yep.model.CreateRegistrationResult;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

public class SignUpActivity extends ContentActivity implements Constants, ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private ViewPager mViewPager;
    private TabsAdapter mAdapter;

    private Button mNextButton;
    private String mName;
    private CreateRegistrationResult mCreateRegistrationResult;
    private AccessToken mAccessToken;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setEnabled(false);
        mViewPager.setOnPageChangeListener(this);

        mNextButton.setOnClickListener(this);

        mAdapter.addTab(EditNameFragment.class, null, 0, null);
        mAdapter.addTab(EditPhoneNumberFragment.class, null, 0, null);
        mAdapter.addTab(VerifyPhoneNumberFragment.class, null, 0, null);
        mAdapter.addTab(EditAvatarFragment.class, null, 0, null);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNextButton = (Button) findViewById(R.id.next_button);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button: {
                final Fragment fragment = mAdapter.getPrimaryItem();
                if (fragment instanceof AbsSignUpPageFragment) {
                    ((AbsSignUpPageFragment) fragment).onNextPage();
                }
                break;
            }
        }
    }

    private void setName(final String name) {
        mName = name;
    }

    private void setNextEnabled(boolean enabled) {
        mNextButton.setEnabled(enabled);
    }

    private void gotoNextPage() {
        final int currentItem = mViewPager.getCurrentItem();
        if (currentItem == mAdapter.getCount() - 1) return;
        mViewPager.setCurrentItem(currentItem + 1);
    }

    private void sendVerifyCode(final String phoneNumber, final String countryCode) {
        ProgressDialogFragment.show(this, "create_registration");
        final TaskRunnable<String[], TaskResponse<CreateRegistrationResult>, SignUpActivity> task
                = new TaskRunnable<String[], TaskResponse<CreateRegistrationResult>, SignUpActivity>() {

            @Override
            public TaskResponse<CreateRegistrationResult> doLongOperation(final String[] args) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstanceWithToken(SignUpActivity.this, null);
                try {
                    return TaskResponse.getInstance(yep.createRegistration(args[1], args[2], args[0], 0, 0));
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final SignUpActivity handler, final TaskResponse<CreateRegistrationResult> result) {
                final Fragment f = handler.getSupportFragmentManager().findFragmentByTag("create_registration");
                if (f instanceof DialogFragment) {
                    ((DialogFragment) f).dismiss();
                }
                if (result.hasData()) {
                    handler.setCreateRegistrationResult(result.getData());
                    handler.gotoNextPage();
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    final YepException exception = (YepException) result.getException();
                    final String error = exception.getError();
                    if (TextUtils.isEmpty(error)) {
                        Toast.makeText(handler, R.string.unable_to_register, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(handler, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };
        task.setParams(new String[]{mName, phoneNumber, countryCode});
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private void verifyPhoneNumber(final String verifyCode) {
        ProgressDialogFragment.show(this, "update_registration");
        final TaskRunnable<Pair<CreateRegistrationResult, String>, TaskResponse<AccessToken>, SignUpActivity> task
                = new TaskRunnable<Pair<CreateRegistrationResult, String>, TaskResponse<AccessToken>, SignUpActivity>() {

            @Override
            public TaskResponse<AccessToken> doLongOperation(final Pair<CreateRegistrationResult, String> args) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstanceWithToken(SignUpActivity.this, null);
                try {
                    final CreateRegistrationResult result = args.first;
                    final String verifyCode = args.second;
                    return TaskResponse.getInstance(yep.updateRegistration(result.getMobile(),
                            result.getPhoneCode(), verifyCode, Client.OFFICIAL, 0));
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final SignUpActivity handler, final TaskResponse<AccessToken> result) {
                final Fragment f = handler.getSupportFragmentManager().findFragmentByTag("update_registration");
                if (f instanceof DialogFragment) {
                    ((DialogFragment) f).dismiss();
                }
                if (result.hasData()) {
                    handler.setUpdateRegistrationResult(result.getData());
                    handler.gotoNextPage();
                } else {
                    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                    final YepException exception = (YepException) result.getException();
                    final String error = exception.getError();
                    if (TextUtils.isEmpty(error)) {
                        Toast.makeText(handler, R.string.unable_to_verify_phone, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(handler, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };
        task.setParams(Pair.create(mCreateRegistrationResult, verifyCode));
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private void setUpdateRegistrationResult(final AccessToken result) {
        mAccessToken = result;
    }

    public void setCreateRegistrationResult(final CreateRegistrationResult result) {
        mCreateRegistrationResult = result;
    }

    private abstract static class AbsSignUpPageFragment extends Fragment {
        protected abstract void updateNextButton();

        public abstract void onNextPage();

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                updateNextButton();
            }
        }

        public SignUpActivity getSignUpActivity() {
            return (SignUpActivity) getActivity();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updateNextButton();
        }
    }

    public static class EditNameFragment extends AbsSignUpPageFragment {
        private EditText mEditName;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_edit_name, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mEditName = (EditText) view.findViewById(R.id.edit_name);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mEditName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateNextButton();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        @Override
        protected void updateNextButton() {
            if (!getUserVisibleHint()) return;
            final SignUpActivity signUpActivity = getSignUpActivity();
            if (signUpActivity == null || mEditName == null) return;
            signUpActivity.setNextEnabled(mEditName.length() > 0);
        }

        @Override
        public void onNextPage() {
            final SignUpActivity signUpActivity = getSignUpActivity();
            signUpActivity.setName(ParseUtils.parseString(mEditName.getText()));
            signUpActivity.gotoNextPage();
        }
    }

    public static class EditPhoneNumberFragment extends AbsSignUpPageFragment {
        private EditText mEditPhoneNumber;
        private EditText mEditCountryCode;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_phone_number, container, false);
        }

        @Override
        protected void updateNextButton() {
            if (!getUserVisibleHint()) return;
            final SignUpActivity signUpActivity = getSignUpActivity();
            if (signUpActivity == null || mEditPhoneNumber == null || mEditCountryCode == null)
                return;
            signUpActivity.setNextEnabled(mEditPhoneNumber.length() > 0 && mEditCountryCode.length() > 0);
        }

        @Override
        public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mEditPhoneNumber = (EditText) view.findViewById(R.id.edit_phone_number);
            mEditCountryCode = (EditText) view.findViewById(R.id.edit_country_code);
        }

        @Override
        public void onNextPage() {
            final String phoneNumber = ParseUtils.parseString(mEditPhoneNumber.getText());
            final String countryCode = ParseUtils.parseString(mEditCountryCode.getText());
            getSignUpActivity().sendVerifyCode(phoneNumber, countryCode);
        }

        @Override
        public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mEditPhoneNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateNextButton();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            mEditCountryCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

                }

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    updateNextButton();
                }

                @Override
                public void afterTextChanged(final Editable s) {

                }
            });
        }
    }

    public static class VerifyPhoneNumberFragment extends AbsSignUpPageFragment {

        private EditText mEditVerifyCode;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_verify_phone, container, false);
        }

        @Override
        public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mEditVerifyCode = (EditText) view.findViewById(R.id.edit_verify_code);
        }

        @Override
        public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mEditVerifyCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

                }

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    updateNextButton();
                }

                @Override
                public void afterTextChanged(final Editable s) {

                }
            });
        }

        @Override
        protected void updateNextButton() {
            if (!getUserVisibleHint()) return;
            final SignUpActivity signUpActivity = getSignUpActivity();
            if (signUpActivity == null || mEditVerifyCode == null)
                return;
            signUpActivity.setNextEnabled(mEditVerifyCode.length() > 0);
        }

        @Override
        public void onNextPage() {
            final String verifyCode = ParseUtils.parseString(mEditVerifyCode.getText());
            getSignUpActivity().verifyPhoneNumber(verifyCode);

        }
    }

    public static class EditAvatarFragment extends AbsSignUpPageFragment {


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_edit_avatar, container, false);
        }

        @Override
        protected void updateNextButton() {
            if (!getUserVisibleHint()) return;
            final SignUpActivity signUpActivity = getSignUpActivity();
            if (signUpActivity == null) return;
            signUpActivity.setNextEnabled(true);
        }

        @Override
        public void onNextPage() {
            final SignUpActivity signUpActivity = getSignUpActivity();
            final Intent data = new Intent();
            signUpActivity.finishAddAddAccount();
        }

    }

    private void finishAddAddAccount() {
        if (mAccessToken == null) return;
        final Intent data = new Intent();
        try {
            data.putExtra(EXTRA_TOKEN, LoganSquare.serialize(mAccessToken));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setResult(RESULT_OK, data);
        finish();
    }
}