/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apmem.tools.layouts.FlowLayout;
import org.mariotaku.sqliteqb.library.Expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.BuildConfig;
import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ChatActivity;
import catchla.yep.activity.ProviderContentActivity;
import catchla.yep.activity.ProviderOAuthActivity;
import catchla.yep.activity.SkillActivity;
import catchla.yep.activity.SkillSelectorActivity;
import catchla.yep.model.Conversation;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.provider.YepDataStore.Friendships;
import catchla.yep.util.ContentValuesCreator;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.MathUtils;
import catchla.yep.util.MenuUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.HeaderSpaceLayout;
import catchla.yep.view.iface.IExtendedView;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UserFragment extends Fragment implements Constants,
        HeaderDrawerLayout.DrawerCallback, IExtendedView.OnFitSystemWindowsListener {

    private static final int REQUEST_SELECT_MASTER_SKILLS = 111;
    private static final int REQUEST_SELECT_LEARNING_SKILLS = 112;
    private HeaderDrawerLayout mHeaderDrawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ScrollView mScrollView;
    private ImageView mProfileImageView;
    private HeaderSpaceLayout mHeaderSpaceLayout;
    private TextView mIntroductionView;
    private FlowLayout mMasterSkills, mLearningSkills;
    private LinearLayout mProvidersContainer;
    private View mSayHelloContainer;
    private View mSayHelloButton;
    private User mCurrentUser;
    private UpdateSkillsTask mTask;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        final Account currentAccount = Utils.getCurrentAccount(getActivity());

        mHeaderDrawerLayout.setDrawerCallback(this);


        final Bundle args = getArguments();
        final User user;
        if (args != null && args.containsKey(EXTRA_USER)) {
            try {
                user = LoganSquare.parse(args.getString(EXTRA_USER), User.class);
                displayUser(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            user = null;
            displayUser(Utils.getAccountUser(getActivity(), currentAccount));
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUser(currentAccount);
            }
        });
    }

    private void refreshUser(final Account currentAccount) {
        final User user = mCurrentUser;
        TaskRunnable<Triple<Context, Account, User>, TaskResponse<User>, UserFragment> task
                = new TaskRunnable<Triple<Context, Account, User>, TaskResponse<User>, UserFragment>() {
            @Override
            public TaskResponse<User> doLongOperation(final Triple<Context, Account, User> param)
                    throws InterruptedException {
                final Context context = param.getLeft();
                final Account account = param.getMiddle();
                final YepAPI yep = YepAPIFactory.getInstance(context, account);
                try {
                    final User user;
                    if (param.getRight() != null) {
                        user = yep.showUser(param.getRight().getId());
                    } else {
                        user = yep.getUser();
                    }
                    final ContentValues values = ContentValuesCreator.fromUser(user);
                    final String where = Expression.equalsArgs(Friendships.FRIEND_ID).getSQL();
                    final String[] whereArgs = {user.getId()};
                    context.getContentResolver().update(Friendships.CONTENT_URI, values, where,
                            whereArgs);
                    Utils.saveUserInfo(context, account, user);
                    return TaskResponse.getInstance(user);
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void callback(final UserFragment handler, final TaskResponse<User> result) {
                handler.mSwipeRefreshLayout.setRefreshing(false);
                if (result.hasData()) {
                    handler.displayUser(result.getData());
                } else if (result.hasException()) {
                    if (BuildConfig.DEBUG) {
                        Log.w(LOGTAG, result.getException());
                    }
                    final String error = Utils.getErrorMessage(result.getException());
                    if (TextUtils.isEmpty(error)) {
                        Toast.makeText(getActivity(), R.string.unable_to_get_profile, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        task.setParams(new ImmutableTriple<Context, Account, User>(getActivity(), currentAccount, user));
        task.setResultHandler(UserFragment.this);
        AsyncManager.runBackgroundTask(task);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_LEARNING_SKILLS: {
                if (resultCode != Activity.RESULT_OK) return;
                if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) return;
                final List<Skill> skills;
                try {
                    skills = LoganSquare.parseList(data.getStringExtra(EXTRA_SKILLS), Skill.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mTask = new UpdateSkillsTask(this, Utils.getCurrentAccount(getActivity()), mCurrentUser, skills, true);
                return;
            }
            case REQUEST_SELECT_MASTER_SKILLS: {
                if (resultCode != Activity.RESULT_OK) return;
                if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) return;
                final List<Skill> skills;
                try {
                    skills = LoganSquare.parseList(data.getStringExtra(EXTRA_SKILLS), Skill.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mTask = new UpdateSkillsTask(this, Utils.getCurrentAccount(getActivity()), mCurrentUser, skills, false);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.PENDING) {
            mTask.execute();
        }
    }

    private void displayUser(final User user) {
        if (user == null) return;
        mCurrentUser = user;
        final String avatarUrl = user.getAvatarUrl();
        if (TextUtils.isEmpty(avatarUrl)) {
            Picasso.with(getActivity()).cancelRequest(mProfileImageView);
            mProfileImageView.setImageResource(R.drawable.ic_profile_image_default);
        } else {
            Picasso.with(getActivity()).load(avatarUrl).into(mProfileImageView);
        }
        final String introduction = user.getIntroduction();
        if (TextUtils.isEmpty(introduction)) {
            mIntroductionView.setText(R.string.no_introduction_yet);
        } else {
            mIntroductionView.setText(introduction);
        }

        View.OnClickListener skillOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Skill skill = (Skill) v.getTag();
                final Intent intent = new Intent(getActivity(), SkillActivity.class);
                try {
                    intent.putExtra(EXTRA_SKILL, LoganSquare.mapperFor(Skill.class).serialize(skill));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                startActivity(intent);
            }
        };

        final boolean isMySelf = Utils.isMySelf(getActivity(), Utils.getCurrentAccount(getActivity()), user);

        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final List<Skill> learningSkills = user.getLearningSkills();
        mLearningSkills.removeAllViews();
        if (learningSkills != null) {
            for (Skill skill : learningSkills) {
                final View view = Utils.inflateSkillItemView(getActivity(), inflater, skill, mLearningSkills);
                final View skillButton = view.findViewById(R.id.skill_button);
                skillButton.setTag(skill);
                skillButton.setOnClickListener(skillOnClickListener);
                mLearningSkills.addView(view);
            }
        }
        if (isMySelf) {
            final View view = Utils.inflateAddSkillView(getActivity(), inflater, mLearningSkills);
            final View skillButton = view.findViewById(R.id.skill_button);
            skillButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Intent intent = new Intent(getActivity(), SkillSelectorActivity.class);
                    try {
                        intent.putExtra(EXTRA_SKILLS, LoganSquare.serialize(learningSkills, Skill.class));
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Error serializing", e);
                    }
                    startActivityForResult(intent, REQUEST_SELECT_LEARNING_SKILLS);
                }
            });
            mLearningSkills.addView(view);
        }
        final List<Skill> masterSkills = user.getMasterSkills();
        mMasterSkills.removeAllViews();
        if (masterSkills != null) {
            for (Skill skill : masterSkills) {
                final View view = Utils.inflateSkillItemView(getActivity(), inflater, skill, mMasterSkills);
                final View skillButton = view.findViewById(R.id.skill_button);
                skillButton.setTag(skill);
                skillButton.setOnClickListener(skillOnClickListener);
                mMasterSkills.addView(view);
            }
        }
        if (isMySelf) {
            final View view = Utils.inflateAddSkillView(getActivity(), inflater, mMasterSkills);
            final View skillButton = view.findViewById(R.id.skill_button);
            skillButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Intent intent = new Intent(getActivity(), SkillSelectorActivity.class);
                    try {
                        intent.putExtra(EXTRA_SKILLS, LoganSquare.serialize(masterSkills, Skill.class));
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Error serializing", e);
                    }
                    startActivityForResult(intent, REQUEST_SELECT_MASTER_SKILLS);
                }
            });
            mMasterSkills.addView(view);
        }
        final List<Provider> providers = user.getProviders();
        mProvidersContainer.removeAllViews();
        View.OnClickListener providerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Provider provider = (Provider) v.getTag();
                final Intent intent;
                if (provider.isSupported()) {
                    intent = new Intent(getActivity(), ProviderContentActivity.class);
                } else {
                    if (isMySelf) {
                        intent = new Intent(getActivity(), ProviderOAuthActivity.class);
                    } else {
                        return;
                    }
                }
                intent.putExtra(EXTRA_PROVIDER_NAME, provider.getName());
                try {
                    intent.putExtra(EXTRA_USER, LoganSquare.mapperFor(User.class).serialize(user));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                startActivity(intent);
            }
        };
        if (providers != null) {
            for (Provider provider : providers) {
                if (!provider.isSupported()) continue;
                final View view = Utils.inflateProviderItemView(getActivity(),
                        inflater, provider, mProvidersContainer);
                view.setTag(provider);
                view.setOnClickListener(providerOnClickListener);
                mProvidersContainer.addView(view);
            }
            if (isMySelf) {
                for (Provider provider : providers) {
                    if (provider.isSupported()) continue;
                    final View view = Utils.inflateProviderItemView(getActivity(),
                            inflater, provider, mProvidersContainer);
                    view.setTag(provider);
                    view.setOnClickListener(providerOnClickListener);
                    mProvidersContainer.addView(view);
                }
            }
        }
        mSayHelloContainer.setVisibility(isMySelf ? View.GONE : View.VISIBLE);
        mSayHelloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EXTRA_CONVERSATION, JsonSerializer.serialize(Conversation.fromUser(user),
                        Conversation.class));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderDrawerLayout = (HeaderDrawerLayout) view.findViewById(R.id.header_drawer);
        mScrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        mProfileImageView = (ImageView) view.findViewById(R.id.profile_image);
        mHeaderSpaceLayout = ((HeaderSpaceLayout) view.findViewById(R.id.header_space));
        mIntroductionView = (TextView) view.findViewById(R.id.introduction);
        mMasterSkills = (FlowLayout) view.findViewById(R.id.master_skills);
        mLearningSkills = (FlowLayout) view.findViewById(R.id.learning_skills);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mProvidersContainer = (LinearLayout) view.findViewById(R.id.providers_container);
        mSayHelloContainer = view.findViewById(R.id.say_hello_container);
        mSayHelloButton = view.findViewById(R.id.say_hello);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public boolean canScroll(float dy) {
        return ViewCompat.canScrollVertically(mScrollView, (int) dy);
    }

    @Override
    public void cancelTouch() {
        mScrollView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
    }

    @Override
    public void fling(float velocity) {
        mScrollView.fling((int) velocity);
    }

    @Override
    public boolean isScrollContent(float x, float y) {
        final ScrollView v = mScrollView;
        final int[] location = new int[2];
        v.getLocationInWindow(location);
        return x >= location[0] && x <= location[0] + v.getWidth()
                && y >= location[1] && y <= location[1] + v.getHeight();
    }

    @Override
    public void scrollBy(float dy) {
        mScrollView.scrollBy(0, (int) dy);
    }

    @Override
    public boolean shouldLayoutHeaderBottom() {
        return true;
    }

    @Override
    public void topChanged(int offset) {
        mProfileImageView.setTranslationY(MathUtils.clamp(offset, 0, -mProfileImageView.getHeight()) * 0.3f);
        final FragmentActivity activity = getActivity();
        if (activity instanceof HeaderDrawerLayout.DrawerCallback) {
            ((HeaderDrawerLayout.DrawerCallback) activity).topChanged(offset);
        }
    }

    @Override
    public void onFitSystemWindows(Rect insets) {
        mHeaderDrawerLayout.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mHeaderSpaceLayout.setMinusTop(insets.top);
    }

    public int getHeaderSpaceHeight() {
        if (mHeaderSpaceLayout == null) return 0;
        return mHeaderSpaceLayout.getMeasuredHeight();
    }

    public int getHeaderPaddingTop() {
        if (mHeaderDrawerLayout == null) return 0;
        return mHeaderDrawerLayout.getPaddingTop();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final boolean isMySelf = Utils.isMySelf(getActivity(), Utils.getCurrentAccount(getActivity()), mCurrentUser);
        MenuUtils.setMenuGroupAvailability(menu, R.id.group_menu_friend, !isMySelf);
        MenuUtils.setMenuGroupAvailability(menu, R.id.group_menu_myself, isMySelf);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Utils.openSettings(getActivity());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private static class UpdateSkillsTask extends AsyncTask<Object, Object, TaskResponse<User>> {
        private static final String UPDATE_SKILLS_DIALOG_FRAGMENT_TAG = "update_skills";
        private final UserFragment mFragment;
        private final Account mAccount;
        private final User mUser;
        private final List<Skill> mSkills;
        private final boolean mLearning;

        public UpdateSkillsTask(final UserFragment fragment, final Account account, final User user, final List<Skill> skills, final boolean learning) {
            mFragment = fragment;
            mAccount = account;
            mUser = user;
            mSkills = skills;
            mLearning = learning;
        }

        @Override
        protected TaskResponse<User> doInBackground(final Object... params) {
            final YepAPI yep = YepAPIFactory.getInstance(mFragment.getActivity(), mAccount);
            List<Skill> currentSkills;
            if (mLearning) {
                currentSkills = mUser.getLearningSkills();
            } else {
                currentSkills = mUser.getMasterSkills();
            }
            if (currentSkills == null) {
                currentSkills = new ArrayList<>();
            }
            for (Skill current : currentSkills) {
                final String id = current.getId();
                if (Utils.findSkill(mSkills, id) == null) {
                    try {
                        if (mLearning) {
                            yep.removeLearningSkill(id);
                        } else {
                            yep.removeMasterSkill(id);
                        }
                    } catch (YepException e) {

                    }
                }
            }
            for (Skill skill : mSkills) {
                final String id = skill.getId();
                if (Utils.findSkill(currentSkills, id) == null) {
                    try {
                        if (mLearning) {
                            yep.addLearningSkill(id);
                        } else {
                            yep.addMasterSkill(id);
                        }
                    } catch (YepException e) {

                    }
                }
            }
            try {
                final User user = yep.getUser();
                Utils.saveUserInfo(mFragment.getActivity(), mAccount, user);
                return TaskResponse.getInstance(user);
            } catch (YepException e) {
                return TaskResponse.getInstance(e);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialogFragment df = ProgressDialogFragment.show(mFragment.getActivity(), UPDATE_SKILLS_DIALOG_FRAGMENT_TAG);
            df.setCancelable(false);
        }

        @Override
        protected void onPostExecute(final TaskResponse<User> result) {
            final FragmentManager fm = mFragment.getFragmentManager();
            final Fragment fragment = fm.findFragmentByTag(UPDATE_SKILLS_DIALOG_FRAGMENT_TAG);
            if (fragment instanceof DialogFragment) {
                ((DialogFragment) fragment).dismiss();
            }
            if (result.hasData()) {
                mFragment.displayUser(result.getData());
            }
            super.onPostExecute(result);
        }
    }
}
