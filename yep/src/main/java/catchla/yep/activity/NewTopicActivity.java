package catchla.yep.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.ArrayAdapter;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.fragment.ProgressDialogFragment;
import catchla.yep.model.AttachmentUpload;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.IdResponse;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Skill;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.Topic;
import catchla.yep.model.User;
import catchla.yep.model.YepException;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/10/13.
 */
public class NewTopicActivity extends SwipeBackContentActivity implements Constants {

    private static final int REQUEST_PICK_IMAGE = 102;
    private static final String EXTRA_MEDIA = "media";
    private static final String EXTRA_ADAPTER_MEDIA = "adapter_media";
    private static final String FRAGMENT_TAG_POSTING_TOPIC = "posting_topic";
    private static final String KEY_TOPIC_DRAFTS_TEXT = "topic_drafts_text";
    private static final String KEY_TOPIC_DRAFTS_MEDIA = "topic_drafts_media";
    private EditText mEditText;
    private RecyclerView mTopicMediaView;
    private TopicMediaAdapter mTopicMediaAdapter;
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
        mTopicMediaView = (RecyclerView) findViewById(R.id.topic_media);
        mTopicSpinner = (Spinner) findViewById(R.id.topics_spinner);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_new_topic);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTopicMediaView.setLayoutManager(layoutManager);
        mTopicMediaAdapter = new TopicMediaAdapter(this);
        mTopicMediaView.setAdapter(mTopicMediaAdapter);

        if (savedInstanceState != null) {
            mTopicMediaAdapter.addAllMedia(savedInstanceState.getStringArray(EXTRA_ADAPTER_MEDIA));
        } else {
            mEditText.setText(mPreferences.getString(KEY_TOPIC_DRAFTS_TEXT, null));
            mTopicMediaAdapter.addAllMedia(mPreferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null));
            mEditText.setSelection(mEditText.length());
        }
        final Intent intent = getIntent();
        final Skill skill = intent.getParcelableExtra(EXTRA_SKILL);
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
        final Set<String> media = mTopicMediaAdapter.getMediaStringSet();
        if (TextUtils.isEmpty(text) && media.isEmpty()) {
            clearDrafts();
            return false;
        }
        boolean draftsChanged = false;
        final SharedPreferences.Editor editor = mPreferences.edit();
        if (!text.equals(mPreferences.getString(KEY_TOPIC_DRAFTS_TEXT, null))) {
            editor.putString(KEY_TOPIC_DRAFTS_TEXT, text);
            draftsChanged = true;
        }
        if (!media.equals(mPreferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null))) {
            editor.putStringSet(KEY_TOPIC_DRAFTS_MEDIA, media);
            draftsChanged = true;
        }
        editor.apply();
        return draftsChanged;
    }

    private void clearDrafts() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_TOPIC_DRAFTS_TEXT);
        editor.remove(KEY_TOPIC_DRAFTS_MEDIA);
        editor.apply();
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE: {
                if (resultCode == RESULT_OK) {
                    mTopicMediaAdapter.addMedia(String.valueOf(data.getData()));
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postTopic() {
        final String body = ParseUtils.parseString(mEditText.getText());
        final Location location = Utils.getCachedLocation(this);
        if (TextUtils.isEmpty(body)) {
            mEditText.setError(getString(R.string.no_content));
            return;
        }
        if (location == null) {
            Toast.makeText(this, R.string.unable_to_get_location, Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] media = mTopicMediaAdapter.getMedia();
        final NewTopic newTopic = new NewTopic();
        newTopic.body(body);
        final Skill skill = (Skill) mTopicSpinner.getSelectedItem();
        if (skill != null && skill.getId() != null) {
            newTopic.skillId(skill.getId());
        }
        newTopic.location(location.getLatitude(), location.getLongitude());
        final TaskRunnable<NewTopic, TaskResponse<Topic>, NewTopicActivity> taskRunnable = new TaskRunnable<NewTopic, TaskResponse<Topic>, NewTopicActivity>() {
            @Override
            public TaskResponse<Topic> doLongOperation(final NewTopic params) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(NewTopicActivity.this, getAccount());
                try {
                    List<IdResponse> files = new ArrayList<>();
                    for (String mediaItem : media) {
                        final String path = Uri.parse(mediaItem).getPath();
                        final FileAttachment.ImageMetadata metadata = FileAttachment.ImageMetadata.getImageMetadata(path);
                        final IdResponse attachmentId = yep.uploadAttachment(AttachmentUpload.create(new File(path),
                                metadata.getMimeType(), YepAPI.AttachableType.TOPIC, JsonSerializer.serialize(metadata)));
                        files.add(attachmentId);
                    }
                    if (!files.isEmpty()) {
                        newTopic.attachments(files);
                        newTopic.kind(Topic.Kind.IMAGE);
                    } else {
                        newTopic.kind(Topic.Kind.TEXT);
                    }
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
            public void callback(final NewTopicActivity handler, final TaskResponse<Topic> response) {
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
        AsyncManager.runBackgroundTask(taskRunnable);
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
        clearDrafts();
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

    private void requestPickMedia() {
        startActivityForResult(ThemedImagePickerActivity.withThemed(this).build(), REQUEST_PICK_IMAGE);
    }

    private void requestRemoveMedia(final String media) {
        RemoveMediaConfirmDialogFragment df = new RemoveMediaConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MEDIA, media);
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "remove_topic_media_confirm");
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(EXTRA_ADAPTER_MEDIA, mTopicMediaAdapter.getMedia());
    }

    private void removeMedia(final String media) {
        mTopicMediaAdapter.removeMedia(media);
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(final Object o) throws InterruptedException {
                final Uri uri = Uri.parse(media);
                return new File(uri.getPath()).delete();
            }
        });
    }

    private static class TopicMediaAdapter extends LoadMoreSupportAdapter {
        private static final int VIEW_TYPE_ADD = 1;
        private static final int VIEW_TYPE_ITEM = 2;
        private final LayoutInflater mInflater;
        private final NewTopicActivity mActivity;
        private List<String> mMedia;

        public TopicMediaAdapter(final NewTopicActivity activity) {
            super(activity);
            mMedia = new ArrayList<>();
            mActivity = activity;
            mInflater = LayoutInflater.from(activity);
        }

        @Override
        public int getItemViewType(final int position) {
            if (position == 0) return VIEW_TYPE_ADD;
            return VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            switch (viewType) {
                case VIEW_TYPE_ITEM: {
                    final View view = mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false);
                    return new TopicMediaItemHolder(view, this);
                }
                case VIEW_TYPE_ADD: {
                    final View view = mInflater.inflate(R.layout.adapter_item_topic_media_add, parent, false);
                    return new TopicMediaAddHolder(view, this);
                }
            }
            throw new UnsupportedOperationException("Unsupported itemType " + viewType);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_ITEM: {
                    final TopicMediaItemHolder itemHolder = (TopicMediaItemHolder) holder;
                    itemHolder.displayMedia(getMedia(position));
                    break;
                }
            }
        }

        private String getMedia(final int position) {
            return mMedia.get(position - 1);
        }

        @Override
        public int getItemCount() {
            return 1 + mMedia.size();
        }

        private void requestPickMedia() {
            mActivity.requestPickMedia();
        }

        public void addMedia(final String data) {
            mMedia.add(data);
            notifyDataSetChanged();
        }

        public String[] getMedia() {
            return mMedia.toArray(new String[mMedia.size()]);
        }

        public void removeMedia(final String media) {
            mMedia.remove(media);
            notifyDataSetChanged();
        }

        private void requestRemoveMedia(final String media) {
            mActivity.requestRemoveMedia(media);
        }

        public void addAllMedia(final String[] media) {
            if (media != null) {
                Collections.addAll(mMedia, media);
            }
            notifyDataSetChanged();
        }

        public void addAllMedia(final Collection<String> media) {
            if (media != null) {
                mMedia.addAll(media);
            }
            notifyDataSetChanged();
        }

        public Set<String> getMediaStringSet() {
            return new HashSet<>(mMedia);
        }

        private class TopicMediaItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TopicMediaAdapter adapter;
            private final ImageView mediaPreviewView;
            private final ImageView mediaRemoveView;

            public TopicMediaItemHolder(final View itemView, final TopicMediaAdapter adapter) {
                super(itemView);
                this.adapter = adapter;
                this.mediaPreviewView = (ImageView) itemView.findViewById(R.id.media_preview);
                this.mediaRemoveView = (ImageView) itemView.findViewById(R.id.media_remove);

                mediaRemoveView.setOnClickListener(this);
            }

            public void displayMedia(final String media) {
                final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                imageLoader.displayImage(media, mediaPreviewView);
            }

            @Override
            public void onClick(final View v) {
                switch (v.getId()) {
                    case R.id.media_remove: {
                        adapter.requestRemoveMedia(adapter.getMedia(getLayoutPosition()));
                        break;
                    }
                }
            }
        }

        private class TopicMediaAddHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TopicMediaAdapter adapter;

            public TopicMediaAddHolder(final View itemView, final TopicMediaAdapter adapter) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.adapter = adapter;
            }

            @Override
            public void onClick(final View v) {
                adapter.requestPickMedia();
            }
        }
    }

    public static class RemoveMediaConfirmDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final Context context = getContext();
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.remove_topic_media_confirm);
            builder.setPositiveButton(R.string.remove, this);
            builder.setNegativeButton(android.R.string.cancel, null);
            return builder.create();
        }

        @Override
        public void onClick(final DialogInterface dialog, final int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    final NewTopicActivity activity = (NewTopicActivity) getActivity();
                    if (activity == null) return;
                    final String media = getArguments().getString(EXTRA_MEDIA);
                    activity.removeMedia(media);
                    break;
                }
            }
        }
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
