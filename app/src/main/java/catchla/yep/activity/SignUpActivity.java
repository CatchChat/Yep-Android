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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import catchla.yep.R;
import catchla.yep.adapter.TabsAdapter;

public class SignUpActivity extends ContentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager;
    private TabsAdapter mAdapter;

    private Button mNextButton;


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

    private abstract static class AbsSignUpPageFragment extends Fragment {
        public abstract void onNextPage();


        public SignUpActivity getSignUpActivity() {
            return (SignUpActivity) getActivity();
        }
    }

    public static class EditNameFragment extends AbsSignUpPageFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_edit_name, container, false);
        }

        @Override
        public void onNextPage() {
            getSignUpActivity().gotoNextPage();
        }
    }

    private void gotoNextPage() {
        final int currentItem = mViewPager.getCurrentItem();
        if (currentItem >= mAdapter.getCount() - 1) return;
        mViewPager.setCurrentItem(currentItem + 1);
    }

    public static class EditPhoneNumberFragment extends AbsSignUpPageFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_phone_number, container, false);
        }

        @Override
        public void onNextPage() {

        }
    }

    public static class VerifyPhoneNumberFragment extends AbsSignUpPageFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sign_up_verify_phone, container, false);
        }

        @Override
        public void onNextPage() {

        }
    }
}