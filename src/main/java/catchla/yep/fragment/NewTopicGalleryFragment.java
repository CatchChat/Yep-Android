package catchla.yep.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.mariotaku.abstask.library.AbstractTask;
import org.mariotaku.abstask.library.TaskStarter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ThemedImagePickerActivity;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.model.AttachmentUpload;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.IdResponse;
import catchla.yep.model.NewTopic;
import catchla.yep.model.Topic;
import catchla.yep.model.YepException;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.YepAPI;

/**
 * Created by mariotaku on 16/1/3.
 */
public class NewTopicGalleryFragment extends NewTopicMediaFragment implements Constants {

    private static final int REQUEST_PICK_IMAGE = 102;
    private static final String EXTRA_ADAPTER_MEDIA = "adapter_media";
    private static final String KEY_TOPIC_DRAFTS_MEDIA = "topic_drafts_media";

    private static final String EXTRA_MEDIA = "media";
    private RecyclerView mTopicMediaView;
    private TopicMediaAdapter mTopicMediaAdapter;

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTopicMediaView.setLayoutManager(layoutManager);
        mTopicMediaAdapter = new TopicMediaAdapter(this);
        mTopicMediaView.setAdapter(mTopicMediaAdapter);

        if (savedInstanceState != null) {
            mTopicMediaAdapter.addAllMedia(savedInstanceState.getStringArray(EXTRA_ADAPTER_MEDIA));
        } else {
            mTopicMediaAdapter.addAllMedia(mPreferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null));
        }
    }

    @Override
    public boolean hasMedia() {
        return mTopicMediaAdapter.getItemCount() > 0;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(EXTRA_ADAPTER_MEDIA, mTopicMediaAdapter.getMedia());
    }

    @Override
    public boolean saveDraft() {
        final Set<String> media = mTopicMediaAdapter.getMediaStringSet();
        boolean draftChanged = false;
        final SharedPreferences.Editor editor = mPreferences.edit();
        if (!media.equals(mPreferences.getStringSet(KEY_TOPIC_DRAFTS_MEDIA, null))) {
            editor.putStringSet(KEY_TOPIC_DRAFTS_MEDIA, media);
            draftChanged = true;
        }
        editor.apply();
        return draftChanged;
    }

    @Override
    @WorkerThread
    public void uploadMedia(final YepAPI yep, final NewTopic newTopic) throws YepException {
        final String[] media = mTopicMediaAdapter.getMedia();
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
    }

    @Override
    public void clearDraft() {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(KEY_TOPIC_DRAFTS_MEDIA);
        editor.apply();
    }

    @Override
    public void onBaseViewCreated(final View view, final Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mTopicMediaView = (RecyclerView) view.findViewById(R.id.topic_media);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_topic_gallery, container, false);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE: {
                if (resultCode == Activity.RESULT_OK) {
                    mTopicMediaAdapter.addMedia(String.valueOf(data.getData()));
                }
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPickMedia() {
        startActivityForResult(ThemedImagePickerActivity.withThemed(getContext()).build(), REQUEST_PICK_IMAGE);
    }

    private void requestRemoveMedia(final String media) {
        RemoveMediaConfirmDialogFragment df = new RemoveMediaConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_MEDIA, media);
        df.setArguments(args);
        df.show(getChildFragmentManager(), "remove_topic_media_confirm");
    }

    private void removeMedia(final String media) {
        mTopicMediaAdapter.removeMedia(media);
        TaskStarter.execute(new AbstractTask<Object, Object, Object>() {
            @Override
            public Object doLongOperation(final Object o) {
                final Uri uri = Uri.parse(media);
                return new File(uri.getPath()).delete();
            }
        });
    }

    private static class TopicMediaAdapter extends LoadMoreSupportAdapter {
        private static final int VIEW_TYPE_ADD = 1;
        private static final int VIEW_TYPE_ITEM = 2;
        private final LayoutInflater mInflater;
        private final NewTopicGalleryFragment mFragment;
        private List<String> mMedia;

        public TopicMediaAdapter(final NewTopicGalleryFragment fragment) {
            super(fragment.getContext());
            mMedia = new ArrayList<>();
            mFragment = fragment;
            mInflater = LayoutInflater.from(fragment.getContext());
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
            mFragment.requestPickMedia();
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
            mFragment.requestRemoveMedia(media);
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
                    final NewTopicGalleryFragment activity = (NewTopicGalleryFragment) getParentFragment();
                    if (activity == null) return;
                    final String media = getArguments().getString(EXTRA_MEDIA);
                    activity.removeMedia(media);
                    break;
                }
            }
        }
    }
}
