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
import catchla.yep.model.TaskResponse;
import catchla.yep.model.VerificationMethod;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

public class SignInActivity extends ContentActivity implements Constants, ViewPager.OnPageChangeListener,
        View.OnClickListener {

    private ViewPager mViewPager;
    private TabsAdapter mAdapter;

    private Button mNextButton;

    private String mPhoneNumber, mCountryCode;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAdapter = new TabsAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setEnabled(false);
        mViewPager.setOnPageChangeListener(this);

        mNextButton.setOnClickListener(this);

        mAdapter.addTab(EditPhoneNumberFragment.class, null, 0, null);
        mAdapter.addTab(VerifyPhoneNumberFragment.class, null, 0, null);
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
                if (fragment instanceof AbsSignInPageFragment) {
                    ((AbsSignInPageFragment) fragment).onNextPage();
                }
                break;
            }
        }
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
        ProgressDialogFragment.show(this, "send_verify");
        final TaskRunnable<String[], TaskResponse<Pair<String, String>>, SignInActivity> task
                = new TaskRunnable<String[], TaskResponse<Pair<String, String>>, SignInActivity>() {

            @Override
            public TaskResponse<Pair<String, String>> doLongOperation(final String[] args) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstanceWithToken(SignInActivity.this, null);
                try {
                    yep.sendVerifyCode(args[0], args[1], VerificationMethod.SMS);
                    return TaskResponse.getInstance(Pair.create(args[0], args[1]));
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final SignInActivity handler, final TaskResponse<Pair<String, String>> result) {
                final Fragment f = handler.getSupportFragmentManager().findFragmentByTag("send_verify");
                if (f instanceof DialogFragment) {
                    ((DialogFragment) f).dismiss();
                }
                if (result.hasData()) {
                    Pair<String, String> data = result.getData();
                    handler.setPhoneNumber(data.first, data.second);
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
        task.setParams(new String[]{phoneNumber, countryCode});
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private void setPhoneNumber(final String phoneNumber, final String countryCode) {
        mPhoneNumber = phoneNumber;
        mCountryCode = countryCode;
    }

    private void verifyPhoneNumber(final String verifyCode) {
        ProgressDialogFragment.show(this, "update_registration");
        final TaskRunnable<String[], TaskResponse<AccessToken>, SignInActivity> task
                = new TaskRunnable<String[], TaskResponse<AccessToken>, SignInActivity>() {

            @Override
            public TaskResponse<AccessToken> doLongOperation(final String[] args) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstanceWithToken(SignInActivity.this, null);
                try {
                    return TaskResponse.getInstance(yep.updateRegistration(args[0], args[1], args[2],
                            Client.OFFICIAL, 0));
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final SignInActivity handler, final TaskResponse<AccessToken> result) {
                final Fragment f = handler.getSupportFragmentManager().findFragmentByTag("update_registration");
                if (f instanceof DialogFragment) {
                    ((DialogFragment) f).dismiss();
                }
                if (result.hasData()) {
                    handler.finishAddAddAccount(result.getData());
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
        task.setParams(new String[]{mPhoneNumber, mCountryCode, verifyCode});
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
    }

    private abstract static class AbsSignInPageFragment extends Fragment {
        protected abstract void updateNextButton();

        public abstract void onNextPage();

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                updateNextButton();
            }
        }

        public SignInActivity getSignInActivity() {
            return (SignInActivity) getActivity();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            updateNextButton();
        }
    }

    public static class EditPhoneNumberFragment extends AbsSignInPageFragment {
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
            final SignInActivity signInActivity = getSignInActivity();
            if (signInActivity == null || mEditPhoneNumber == null || mEditCountryCode == null)
                return;
            signInActivity.setNextEnabled(mEditPhoneNumber.length() > 0 && mEditCountryCode.length() > 0);
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
            getSignInActivity().sendVerifyCode(phoneNumber, countryCode);
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

    public static class VerifyPhoneNumberFragment extends AbsSignInPageFragment {

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
            final SignInActivity signInActivity = getSignInActivity();
            if (signInActivity == null || mEditVerifyCode == null)
                return;
            signInActivity.setNextEnabled(mEditVerifyCode.length() > 0);
        }

        @Override
        public void onNextPage() {
            final String verifyCode = ParseUtils.parseString(mEditVerifyCode.getText());
            getSignInActivity().verifyPhoneNumber(verifyCode);

        }
    }

    private void finishAddAddAccount(final AccessToken token) {
        final Intent data = new Intent();
        try {
            data.putExtra(EXTRA_TOKEN, LoganSquare.serialize(token));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setResult(RESULT_OK, data);
        finish();
    }
}