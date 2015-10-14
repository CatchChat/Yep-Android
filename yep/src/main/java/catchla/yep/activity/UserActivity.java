package catchla.yep.activity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Conversation;
import catchla.yep.model.Provider;
import catchla.yep.model.Skill;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;

public class UserActivity extends SwipeBackContentActivity implements Constants, View.OnClickListener, LoaderManager.LoaderCallbacks<TaskResponse<User>> {

    private static final int REQUEST_SELECT_MASTER_SKILLS = 111;
    private static final int REQUEST_SELECT_LEARNING_SKILLS = 112;

    private FloatingActionButton mActionButton;
    private ImageView mProfileImageView;
    private TextView mIntroductionView;
    private FlowLayout mMasterSkills, mLearningSkills;
    private LinearLayout mProvidersContainer;
    private User mCurrentUser;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mIntroductionView = (TextView) findViewById(R.id.introduction);
        mMasterSkills = (FlowLayout) findViewById(R.id.master_skills);
        mLearningSkills = (FlowLayout) findViewById(R.id.learning_skills);
        mProvidersContainer = (LinearLayout) findViewById(R.id.providers_container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final User currentUser;
        final Intent intent = getIntent();
        final Account account = getAccount();

        if (intent.hasExtra(EXTRA_USER)) {
            currentUser = intent.getParcelableExtra(EXTRA_USER);
        } else {
            currentUser = Utils.getAccountUser(this, account);
        }

        if (currentUser == null) {
            finish();
            return;
        }

        mActionButton.setOnClickListener(this);
        setTitle(Utils.getDisplayName(currentUser));
        displayUser(currentUser, Utils.getAccountId(this, account));

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void displayUser(final User user, final String accountId) {
        if (user == null) return;
        mCurrentUser = user;
        final String avatarUrl = user.getAvatarUrl();
        mImageLoader.displayProfileImage(avatarUrl, mProfileImageView);
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
                final Intent intent = new Intent(UserActivity.this, SkillActivity.class);
                intent.putExtra(EXTRA_ACCOUNT, getAccount());
                intent.putExtra(EXTRA_SKILL, skill);
                startActivity(intent);
            }
        };

        final boolean isMySelf = Utils.isMySelf(UserActivity.this, getAccount(), user);

        final LayoutInflater inflater = UserActivity.this.getLayoutInflater();

        final ArrayList<Skill> learningSkills = Utils.arrayListFrom(user.getLearningSkills());
        mLearningSkills.removeAllViews();
        if (learningSkills != null) {
            for (Skill skill : learningSkills) {
                final View view = Utils.inflateSkillItemView(UserActivity.this, inflater, skill, mLearningSkills);
                final View skillButton = view.findViewById(R.id.skill_button);
                skillButton.setTag(skill);
                skillButton.setOnClickListener(skillOnClickListener);
                mLearningSkills.addView(view);
            }
        }
        if (isMySelf) {
            final View view = Utils.inflateAddSkillView(UserActivity.this, inflater, mLearningSkills);
            final View skillButton = view.findViewById(R.id.skill_button);
            skillButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Intent intent = new Intent(UserActivity.this, SkillSelectorActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_SKILLS, learningSkills);
                    startActivityForResult(intent, REQUEST_SELECT_LEARNING_SKILLS);
                }
            });
            mLearningSkills.addView(view);
        } else {
            //TODO: Add empty view
        }
        final ArrayList<Skill> masterSkills = Utils.arrayListFrom(user.getMasterSkills());
        mMasterSkills.removeAllViews();
        if (masterSkills != null) {
            for (Skill skill : masterSkills) {
                final View view = Utils.inflateSkillItemView(UserActivity.this, inflater, skill, mMasterSkills);
                final View skillButton = view.findViewById(R.id.skill_button);
                skillButton.setTag(skill);
                skillButton.setOnClickListener(skillOnClickListener);
                mMasterSkills.addView(view);
            }
        }
        if (isMySelf) {
            final View view = Utils.inflateAddSkillView(UserActivity.this, inflater, mMasterSkills);
            final View skillButton = view.findViewById(R.id.skill_button);
            skillButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Intent intent = new Intent(UserActivity.this, SkillSelectorActivity.class);
                    intent.putParcelableArrayListExtra(EXTRA_SKILLS, masterSkills);
                    startActivityForResult(intent, REQUEST_SELECT_MASTER_SKILLS);
                }
            });
            mMasterSkills.addView(view);
        } else {
            //TODO: Add empty view
        }
        final List<Provider> providers = user.getProviders();
        mProvidersContainer.removeAllViews();
        View.OnClickListener providerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Provider provider = (Provider) v.getTag();
                final Intent intent;
                if (provider.isSupported()) {
                    intent = new Intent(UserActivity.this, ProviderContentActivity.class);
                } else {
                    if (isMySelf) {
                        intent = new Intent(UserActivity.this, ProviderOAuthActivity.class);
                    } else {
                        return;
                    }
                }
                intent.putExtra(EXTRA_PROVIDER_NAME, provider.getName());
                intent.putExtra(EXTRA_USER, user);
                startActivity(intent);
            }
        };
        if (providers != null) {
            for (Provider provider : providers) {
                if (!provider.isSupported()) continue;
                final View view = Utils.inflateProviderItemView(UserActivity.this,
                        inflater, provider, mProvidersContainer);
                view.setTag(provider);
                view.setOnClickListener(providerOnClickListener);
                mProvidersContainer.addView(view);
            }
            if (isMySelf) {
                for (Provider provider : providers) {
                    if (provider.isSupported()) continue;
                    final View view = Utils.inflateProviderItemView(UserActivity.this,
                            inflater, provider, mProvidersContainer);
                    view.setTag(provider);
                    view.setOnClickListener(providerOnClickListener);
                    mProvidersContainer.addView(view);
                }
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fab: {
                final Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(EXTRA_CONVERSATION, Conversation.fromUser(getCurrentUser(), Utils.getAccountId(this, getAccount())));
                startActivity(intent);
                break;
            }
        }
    }

    private User getCurrentUser() {
        return mCurrentUser;
    }

    @Override
    public Loader<TaskResponse<User>> onCreateLoader(final int id, final Bundle args) {
        return new UserLoader(this, getAccount(), getCurrentUser().getId());
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<User>> loader, final TaskResponse<User> data) {
        if (data.hasData()) {
            final User user = data.getData();
            final Account account = getAccount();
            final String accountId = Utils.getAccountId(this, account);
            displayUser(user, accountId);
            if (StringUtils.equals(user.getId(), accountId)) {
                Utils.saveUserInfo(UserActivity.this, account, user);
            }
        }
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<User>> loader) {

    }
}
