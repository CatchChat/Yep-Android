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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;
import catchla.yep.model.CreateRegistrationResult;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

public class SignUpActivity extends ContentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager;
    private TabsAdapter mAdapter;

    private Button mNextButton;
    private String mName;


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
        if (currentItem >= mAdapter.getCount() - 1) return;
        mViewPager.setCurrentItem(currentItem + 1);
    }

    private void sendVerifyCode(final String phoneNumber, final String countryCode) {
        final TaskRunnable<String[], TaskResponse<CreateRegistrationResult>, SignUpActivity> task
                = new TaskRunnable<String[], TaskResponse<CreateRegistrationResult>, SignUpActivity>() {

            @Override
            public TaskResponse<CreateRegistrationResult> doLongOperation(final String[] args) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(null);
                try {
                    return TaskResponse.getInstance(yep.createRegistration(args[1], args[2], args[0], 0, 0));
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final SignUpActivity handler, final TaskResponse<CreateRegistrationResult> result) {
                if (result.hasData()) {
                    handler.gotoNextPage();
                } else {

                }
            }
        };
        task.setParams(new String[]{mName, phoneNumber, countryCode});
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
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

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_phone_number, container, false);
        }

        @Override
        protected void updateNextButton() {
            if (!getUserVisibleHint()) return;
            final SignUpActivity signUpActivity = getSignUpActivity();
            if (signUpActivity == null || mEditPhoneNumber == null) return;
            signUpActivity.setNextEnabled(mEditPhoneNumber.length() > 0);
        }

        @Override
        public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mEditPhoneNumber = (EditText) view.findViewById(R.id.edit_phone_number);
        }

        @Override
        public void onNextPage() {
            final String phoneNumber = ParseUtils.parseString(mEditPhoneNumber.getText());
            getSignUpActivity().sendVerifyCode(phoneNumber, null);
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
        }
    }

    public static class VerifyPhoneNumberFragment extends AbsSignUpPageFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_verify_phone, container, false);
        }

        @Override
        protected void updateNextButton() {

        }

        @Override
        public void onNextPage() {

        }
    }
}