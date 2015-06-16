/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apmem.tools.layouts.FlowLayout;

import java.io.IOException;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.AddSkillActivity;
import catchla.yep.activity.ChatActivity;
import catchla.yep.activity.ProviderContentActivity;
import catchla.yep.activity.ProviderOAuthActivity;
import catchla.yep.activity.SkillActivity;
import catchla.yep.activity.UserActivity;
import catchla.yep.model.Conversation;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.MathUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import catchla.yep.view.HeaderDrawerLayout;
import catchla.yep.view.HeaderSpaceLayout;
import catchla.yep.view.iface.IExtendedView;
import io.realm.RealmList;

/**
 * Created by mariotaku on 15/4/29.
 */
public class UserFragment extends Fragment implements Constants,
        HeaderDrawerLayout.DrawerCallback, IExtendedView.OnFitSystemWindowsListener {

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                TaskRunnable<Triple<Context, Account, User>, TaskResponse<User>, UserFragment> task
                        = new TaskRunnable<Triple<Context, Account, User>, TaskResponse<User>, UserFragment>() {
                    @Override
                    public TaskResponse<User> doLongOperation(final Triple<Context, Account, User> param)
                            throws InterruptedException {
                        final YepAPI yep = YepAPIFactory.getInstance(param.getLeft(), param.getMiddle());
                        try {
                            if (param.getRight() != null) throw new YepException();
                            final User user = yep.getUser();
                            final AccountManager am = AccountManager.get(param.getLeft());
                            final Bundle userData = new Bundle();
                            Utils.writeUserToUserData(user, userData);
                            for (String key : userData.keySet()) {
                                am.setUserData(param.getMiddle(), key, userData.getString(key));
                            }
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
                        }
                    }
                };
                task.setParams(new ImmutableTriple<Context, Account, User>(getActivity(), currentAccount, user));
                task.setResultHandler(UserFragment.this);
                AsyncManager.runBackgroundTask(task);
            }
        });
    }

    private void displayUser(final User user) {
        if (user == null) return;
        Picasso.with(getActivity()).load(user.getAvatarUrl()).into(mProfileImageView);
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

        final RealmList<Skill> learningSkills = user.getLearningSkills();
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
                    final Intent intent = new Intent(getActivity(), AddSkillActivity.class);
                    try {
                        intent.putExtra(EXTRA_SKILLS, LoganSquare.serialize(learningSkills, Skill.class));
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Error serializing", e);
                    }
                    startActivity(intent);
                }
            });
            mLearningSkills.addView(view);
        }
        final RealmList<Skill> masterSkills = user.getMasterSkills();
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
                    final Intent intent = new Intent(getActivity(), AddSkillActivity.class);
                    try {
                        intent.putExtra(EXTRA_SKILLS, LoganSquare.serialize(masterSkills, Skill.class));
                    } catch (IOException e) {
                        Log.e(LOGTAG, "Error serializing", e);
                    }
                    startActivity(intent);
                }
            });
            mMasterSkills.addView(view);
        }
        final RealmList<Provider> providers = user.getProviders();
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
                try {
                    intent.putExtra(EXTRA_CONVERSATION, LoganSquare.mapperFor(Conversation.class)
                            .serialize(Conversation.fromUser(user)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
}
