package catchla.yep.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.mariotaku.abstask.library.AbstractTask;
import org.mariotaku.abstask.library.TaskStarter;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.ArrayAdapter;
import catchla.yep.fragment.NewTopicGalleryFragment;
import catchla.yep.fragment.NewTopicLocationFragment;
import catchla.yep.fragment.NewTopicMediaFragment;
import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Skill;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/10/13.
 */
public class NewTopicActivity extends SwipeBackContentActivity implements Constants {

    public static final String TYPE_PHOTOS_TEXT = "photos_text";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_LOCATION = "location";

    private static final String FRAGMENT_TAG_POSTING_TOPIC = "posting_topic";
    private static final String KEY_TOPIC_DRAFTS_TEXT = "topic_drafts_text";
    private EditText mEditText;

    private Runnable mDismissUploadingDialogRunnable;
    private Spinner mTopicSpinner;
    private SharedPreferences mPreferences;
    private boolean mDraftsSaved;
    private boolean mShouldSkipSaveDrafts;
    private boolean mFragmentResumed;

    @Override
    protected void onPause() {
        mFragmentResumed = false;
        super.onPause();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mFragmentResumed = true;
        invokeFragmentRunnable();
    }

    private void invokeFragmentRunnable() {
        if (mFragmentResumed && mDismissUploadingDialogRunnable != null) {
            mDismissUploadingDialogRunnable.run();
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mEditText = (EditText) findViewById(R.id.edit_text);
        mTopicSpinner = (Spinner) findViewById(R.id.topics_spinner);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_new_topic);

        if (savedInstanceState == null) {
            mEditText.setText(mPreferences.getString(KEY_TOPIC_DRAFTS_TEXT, null));
            mEditText.setSelection(mEditText.length());
        }
        final Intent intent = getIntent();
        final Skill skill = intent.getParcelableExtra(EXTRA_SKILL);
        String newTopicsType = intent.getStringExtra(EXTRA_NEW_TOPIC_TYPE);
        if (newTopicsType == null) {
            newTopicsType = TYPE_PHOTOS_TEXT;
        }
        switch (newTopicsType) {
            case TYPE_PHOTOS_TEXT: {
                final FragmentManager fm = getSupportFragmentManager();
                final FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.new_topic_media, new NewTopicGalleryFragment());
                ft.commit();
                break;
            }
            case TYPE_AUDIO: {
                break;
            }
            case TYPE_LOCATION: {
                final FragmentManager fm = getSupportFragmentManager();
                final FragmentTransaction ft = fm.beginTransaction();
                final NewTopicLocationFragment fragment = new NewTopicLocationFragment();
                final Bundle args = new Bundle();
                args.putParcelable(EXTRA_ATTACHMENT, intent.getParcelableExtra(EXTRA_ATTACHMENT));
                fragment.setArguments(args);
                ft.replace(R.id.new_topic_media, fragment);
                ft.commit();
                break;
            }
            default: {
                throw new UnsupportedOperationException(newTopicsType);
            }
        }
        final SkillSpinnerAdapter adapter = new SkillSpinnerAdapter(this);
        final User accountUser = Utils.getAccountUser(this, getAccount());
        if (accountUser != null) {
            adapter.add(Skill.getDummy());
            if (skill != null) {
                adapter.add(skill);
            }
            adapter.addAll(Utils.emptyIfNull(accountUser.getMasterSkills()));
            adapter.addAll(Utils.emptyIfNull(accountUser.getLearningSkills()));
        }
        mTopicSpinner.setAdapter(adapter);
        if (skill != null) {
            mTopicSpinner.setSelection(adapter.findPositionBySkillId(skill.getId()));
        }
    }

    @Override
    protected void onStop() {
        mDraftsSaved = saveDrafts();
        super.onStop();
    }

    private boolean saveDrafts() {
        if (mShouldSkipSaveDrafts) return false;
        final String text = ParseUtils.parseString(mEditText.getText());
        NewTopicMediaFragment fragment = getNewTopicMediaFragment();
        if (TextUtils.isEmpty(text) && !fragment.hasMedia()) {
            clearDraft();
            return false;
        }
        boolean draftChanged = false;
        final SharedPreferences.Editor editor = mPreferences.edit();
        if (!text.equals(mPreferences.getString(KEY_TOPIC_DRAFTS_TEXT, null))) {
            editor.putString(KEY_TOPIC_DRAFTS_TEXT, text);
            draftChanged = true;
        }
        draftChanged |= fragment.saveDraft();
        editor.apply();
        return draftChanged;
    }

    private NewTopicMediaFragment getNewTopicMediaFragment() {
        return (NewTopicMediaFragment) getSupportFragmentManager().findFragmentById(R.id.new_topic_media);
    }

    private void clearDraft() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_TOPIC_DRAFTS_TEXT);
        editor.apply();
        getNewTopicMediaFragment().clearDraft();
    }

    @Override
    protected void onDestroy() {
        if (mDraftsSaved) {
            Toast.makeText(this, R.string.drafts_saved, Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send: {
                postTopic();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void postTopic() {
        final String body = ParseUtils.parseString(mEditText.getText());
        final Location location = Utils.getCachedLocation(this);
        if (TextUtils.isEmpty(body)) {
            mEditText.setError(getString(R.string.no_content));
            return;
        }
        final NewTopic newTopic = new NewTopic();
        newTopic.body(body);
        final Skill skill = (Skill) mTopicSpinner.getSelectedItem();
        if (skill != null && skill.getId() != null) {
            newTopic.skillId(skill.getId());
        }
        if (location != null) {
            newTopic.location(location.getLatitude(), location.getLongitude());
        } else {
            newTopic.location(0, 0);
        }
        final AbstractTask<NewTopic, TaskResponse<Topic>, NewTopicActivity> taskRunnable = new AbstractTask<NewTopic, TaskResponse<Topic>, NewTopicActivity>() {
            @Override
            public TaskResponse<Topic> doLongOperation(final NewTopic params) {
                final YepAPI yep = YepAPIFactory.getInstance(NewTopicActivity.this, getAccount());
                try {
                    NewTopicMediaFragment fragment = getNewTopicMediaFragment();
                    fragment.uploadMedia(yep, newTopic);

                    final Topic topic = yep.postTopic(params);
                    return TaskResponse.getInstance(topic);
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                } catch (Throwable t) {
                    Log.wtf(LOGTAG, t);
                    System.exit(0);
                    return null;
                }
            }

            @Override
            public void afterExecute(final NewTopicActivity handler, final TaskResponse<Topic> response) {
                if (response.hasData()) {
                    handler.finishPosting();
                } else {
                    handler.dismissUploadingDialog();
                    Toast.makeText(handler, R.string.unable_to_create_topic, Toast.LENGTH_SHORT).show();
                    if (response.hasException()) {
                        Log.w(LOGTAG, response.getException());
                    }
                }
            }
        };
        taskRunnable.setResultHandler(this);
        taskRunnable.setParams(newTopic);
        TaskStarter.execute(taskRunnable);
        ProgressDialogFragment df = new ProgressDialogFragment();
        df.setCancelable(false);
        df.show(getSupportFragmentManager(), FRAGMENT_TAG_POSTING_TOPIC);
    }

    private void dismissUploadingDialog() {
        mDismissUploadingDialogRunnable = new Runnable() {
            @Override
            public void run() {
                final FragmentManager fm = getSupportFragmentManager();
                final Fragment f = fm.findFragmentByTag(FRAGMENT_TAG_POSTING_TOPIC);
                if (f instanceof DialogFragment) {
                    ((DialogFragment) f).dismiss();
                }
            }
        };
        invokeFragmentRunnable();
    }

    private void finishPosting() {
        mShouldSkipSaveDrafts = true;
        clearDraft();
        Toast.makeText(this, R.string.topic_posted, Toast.LENGTH_SHORT).show();
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }


    private static class SkillSpinnerAdapter extends ArrayAdapter<Skill> {
        public SkillSpinnerAdapter(final NewTopicActivity activity) {
            super(activity, android.R.layout.simple_expandable_list_item_1);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            final View view = super.getDropDownView(position, convertView, parent);
            final TextView textView = (TextView) view.findViewById(android.R.id.text1);
            if (TextUtils.isEmpty(getItem(position).getId())) {
                textView.setText(R.string.none);
            } else {
                textView.setText(Utils.getDisplayName(getItem(position)));
            }
            return view;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            final TextView textView = (TextView) view.findViewById(android.R.id.text1);
            if (TextUtils.isEmpty(getItem(position).getId())) {
                textView.setText(R.string.choose_skill);
            } else {
                textView.setText(Utils.getDisplayName(getItem(position)));
            }
            return view;
        }

        public int findPositionBySkillId(final String id) {
            for (int i = 0, j = getCount(); i < j; i++) {
                if (StringUtils.equals(id, getItem(i).getId())) return i;
            }
            return ListView.INVALID_POSITION;
        }
    }
}
