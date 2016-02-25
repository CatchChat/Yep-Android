package catchla.yep.fragment;

import android.content.Context;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import java.io.File;
import java.util.List;

import catchla.yep.R;
import catchla.yep.adapter.LoadMoreSupportAdapter;
import catchla.yep.message.AudioPlayEvent;
import catchla.yep.model.Attachment;
import catchla.yep.model.FileAttachment;
import catchla.yep.model.Message;
import catchla.yep.provider.YepDataStore.Messages.MessageState;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.StaticMapUrlGenerator;
import catchla.yep.util.Utils;
import catchla.yep.view.AudioSampleView;
import catchla.yep.view.MediaSizeImageView;
import catchla.yep.view.StaticMapView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * List component for chat activities
 * Created by mariotaku on 15/11/16.
 */
public abstract class ChatListFragment extends AbsContentRecyclerViewFragment<ChatListFragment.ChatAdapter, LinearLayoutManager>
        implements LoaderManager.LoaderCallbacks<List<Message>> {

    private MediaPlayer mMediaPlayer;
    private boolean mJumpToLast;

    @Override
    protected void onScrollToPositionWithOffset(final LinearLayoutManager layoutManager, final int position, final int offset) {
        layoutManager.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @NonNull
    @Override
    protected ChatAdapter onCreateAdapter(final Context context) {
        return new ChatAdapter(this);
    }

    @Override
    protected void setupRecyclerView(final Context context, final RecyclerView recyclerView, final LinearLayoutManager layoutManager) {
        layoutManager.setStackFromEnd(false);
    }

    @NonNull
    @Override
    protected LinearLayoutManager onCreateLayoutManager(final Context context) {
        return new FixedLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
    }

    @Override
    public void onStop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onStop();
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        showProgress();
    }

    @Override
    public void onLoadFinished(final Loader<List<Message>> loader, final List<Message> data) {
        int backupPosition = getLayoutManager().findFirstVisibleItemPosition();
        Message backupMessage;
        final ChatAdapter adapter = getAdapter();
        if (backupPosition != RecyclerView.NO_POSITION && !mJumpToLast) {
            backupMessage = adapter.getMessage(backupPosition);
        } else {
            backupMessage = null;
        }
        mJumpToLast = false;
        adapter.setData(data);
        showContent();
        if (backupMessage != null && backupMessage.getId() != null) {
            final int position = adapter.findPosition(backupMessage.getId());
            if (position != RecyclerView.NO_POSITION) {
                scrollToPositionWithOffset(position, 0);
            }
        } else {
            scrollToStart();
        }
    }

    public void setJumpToLast(final boolean jumpToLast) {
        mJumpToLast = jumpToLast;
    }

    @Override
    public void onLoaderReset(final Loader<List<Message>> loader) {
        getAdapter().setData(null);
    }

    private void playAudio(final FileAttachment attachment) {
        if (!"audio".equals(attachment.getKind())) return;
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(final MediaPlayer mp) {
                    mBus.post(AudioPlayEvent.end(attachment));
                }
            });
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mBus.post(AudioPlayEvent.end(attachment));
        }
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(final Object o) throws InterruptedException {
                BufferedSink sink = null;
                File tempFile = null;
                try {
                    tempFile = File.createTempFile("voice_dl" + System.currentTimeMillis(), "m4a");
                    if (tempFile.length() > 0) {
                        mMediaPlayer.setDataSource(tempFile.getAbsolutePath());
                    } else {
                        OkHttpClient client = new OkHttpClient();
                        final Response response = client.newCall(new Request.Builder().url(attachment.getFile().getUrl()).build()).execute();
                        sink = Okio.buffer(Okio.sink(tempFile));
                        sink.writeAll(response.body().source());
                        sink.flush();
                        mMediaPlayer.setDataSource(getContext(), Uri.parse(attachment.getFile().getUrl()));
                    }
                    mMediaPlayer.prepare();
                } catch (Exception e) {
                    Log.w(LOGTAG, e);
                    if (tempFile != null) {
                        tempFile.delete();
                    }
                } catch (Throwable t) {
                    Log.wtf(LOGTAG, t);
                } finally {
                    Utils.closeSilently(sink);
                }
                return null;
            }

            @Override
            public void callback(final Object o) {
                if (mMediaPlayer == null) return;
                mMediaPlayer.start();
                mBus.post(AudioPlayEvent.start(attachment));
            }
        });
    }

    public static class ChatAdapter extends LoadMoreSupportAdapter {

        private static final int FLAG_MESSAGE_OUTGOING = 0xF0000000;
        private static final int VIEW_SUBTYPE_MESSAGE_TEXT = 0x0001;
        private static final int VIEW_SUBTYPE_MESSAGE_LOCATION = 0x0002;
        private static final int VIEW_SUBTYPE_MESSAGE_IMAGE = 0x0003;
        private static final int VIEW_SUBTYPE_MESSAGE_AUDIO = 0x0004;

        private final ChatListFragment mActivity;
        private final LayoutInflater mInflater;
        private List<Message> mData;

        ChatAdapter(ChatListFragment activity) {
            super(activity.getContext());
            mActivity = activity;
            mInflater = LayoutInflater.from(activity.getContext());
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final boolean isOutgoing = (viewType & FLAG_MESSAGE_OUTGOING) != 0;
            final View baseView;
            if (isOutgoing) {
                baseView = mInflater.inflate(R.layout.list_item_message_outgoing, parent, false);
            } else {
                baseView = mInflater.inflate(R.layout.list_item_message_incoming, parent, false);
            }
            final int subType = viewType & ~FLAG_MESSAGE_OUTGOING;
            switch (subType) {
                case VIEW_SUBTYPE_MESSAGE_TEXT: {
                    return new MessageViewHolder(baseView, isOutgoing, this);
                }
                case VIEW_SUBTYPE_MESSAGE_LOCATION: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_view);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_location, attachmentContainer);
                    return new LocationChatViewHolder(baseView, isOutgoing, this);
                }
                case VIEW_SUBTYPE_MESSAGE_IMAGE: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_view);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_image, attachmentContainer);
                    return new ImageChatViewHolder(baseView, isOutgoing, this);
                }
                case VIEW_SUBTYPE_MESSAGE_AUDIO: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_view);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_audio, attachmentContainer);
                    return new AudioChatViewHolder(baseView, isOutgoing, this);
                }
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MessageViewHolder) holder).displayMessage(mData.get(position));
        }

        @Override
        public int getItemViewType(final int position) {
            final Message message = mData.get(position);
            int subType = getItemViewSubType(message.getMediaType());
            if (message.isOutgoing()) return subType | FLAG_MESSAGE_OUTGOING;
            return subType;
        }

        private int getItemViewSubType(final String mediaType) {
            if (Message.MediaType.LOCATION.equals(mediaType)) return VIEW_SUBTYPE_MESSAGE_LOCATION;
            else if (Message.MediaType.IMAGE.equals(mediaType)) return VIEW_SUBTYPE_MESSAGE_IMAGE;
            else if (Message.MediaType.AUDIO.equals(mediaType)) return VIEW_SUBTYPE_MESSAGE_AUDIO;
            return VIEW_SUBTYPE_MESSAGE_TEXT;
        }

        @Override
        public int getItemCount() {
            return getMessagesCount();
        }

        public void setData(final List<Message> data) {
            mData = data;
            notifyDataSetChanged();
        }

        public Message getMessage(final int position) {
            if (mData == null) return null;
            return mData.get(position);
        }

        private void playAudio(final FileAttachment attachment) {
            mActivity.playAudio(attachment);
        }

        public int findPosition(@NonNull final String id) {
            if (mData == null) return RecyclerView.NO_POSITION;
            for (int i = 0, j = getMessagesCount(); i < j; i++) {
                if (id.equals(getMessage(i).getId())) return i;
            }
            return RecyclerView.NO_POSITION;
        }

        private int getMessagesCount() {
            if (mData == null) return 0;
            return mData.size();
        }

        public List<Message> getData() {
            return mData;
        }

        private static class MessageViewHolder extends RecyclerView.ViewHolder {

            protected final ChatAdapter adapter;

            private final ImageView profileImageView;
            private final TextView text1;
            private final ImageView stateView;

            public MessageViewHolder(final View itemView, boolean outgoing, ChatAdapter adapter) {
                super(itemView);
                this.adapter = adapter;
                text1 = (TextView) itemView.findViewById(android.R.id.text1);
                stateView = (ImageView) itemView.findViewById(R.id.state);
                profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            }

            public void displayMessage(Message message) {
                text1.setText(message.getTextContent());
                text1.setVisibility(text1.length() > 0 ? View.VISIBLE : View.GONE);
                if (profileImageView != null) {
                    adapter.getImageLoader().displayProfileImage(message.getSender().getAvatarUrl(),
                            profileImageView);
                }
                if (stateView != null) {
                    final String state = message.getState();
                    switch (Utils.emptyIfNull(state)) {
                        case MessageState.READ: {
                            stateView.setImageDrawable(null);
                            break;
                        }
                        case MessageState.FAILED: {
                            stateView.setImageResource(R.drawable.ic_message_state_retry);
                            break;
                        }
                        case MessageState.UNREAD: {
                            stateView.setImageResource(R.drawable.ic_message_state_unread);
                            break;
                        }
                        default: {
                            stateView.setImageDrawable(null);
                            break;
                        }
                    }
                }
            }
        }

        private static class LocationChatViewHolder extends MessageViewHolder {
            private final StaticMapView mapView;

            public LocationChatViewHolder(final View itemView, final boolean outgoing, final ChatAdapter adapter) {
                super(itemView, outgoing, adapter);
                mapView = (StaticMapView) itemView.findViewById(R.id.map_view);
                mapView.setProvider(new StaticMapUrlGenerator.OpenStreetMapProvider(StaticMapUrlGenerator.OpenStreetMapProvider.MapType.MAPNIK));
                mapView.setScaleToDensity(true);
            }

            @Override
            public void displayMessage(Message message) {
                super.displayMessage(message);
                final Location location = new Location("");
                location.setLatitude(message.getLatitude());
                location.setLongitude(message.getLongitude());
                mapView.display(location, 12);
            }
        }

        private static class ImageChatViewHolder extends MessageViewHolder {
            private final MediaSizeImageView imageView;

            public ImageChatViewHolder(final View itemView,
                                       final boolean outgoing, final ChatAdapter adapter) {
                super(itemView, outgoing, adapter);
                imageView = (MediaSizeImageView) itemView.findViewById(R.id.image_view);
            }

            @Override
            public void displayMessage(Message message) {
                super.displayMessage(message);
                final String url;
                final FileAttachment.ImageMetadata metadata;
                final List<Message.LocalMetadata> localMetadata = message.getLocalMetadata();
                final List<Attachment> attachments = message.getAttachments();
                if (localMetadata != null && !localMetadata.isEmpty()) {
                    url = Message.LocalMetadata.get(localMetadata, "image");
                    metadata = JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"),
                            FileAttachment.ImageMetadata.class);
                } else if (attachments != null && !attachments.isEmpty()) {
                    final FileAttachment attachment = (FileAttachment) attachments.get(0);
                    url = attachment.getFile().getUrl();
                    metadata = JsonSerializer.parse(attachment.getMetadata(), FileAttachment.ImageMetadata.class);
                } else {
                    return;
                }
                if (metadata != null) {
                    imageView.setMediaSize(metadata.getWidth(), metadata.getHeight());
                    final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                    imageLoader.displayImage(url, imageView);
                }
            }
        }

        private static class AudioChatViewHolder extends MessageViewHolder implements View.OnClickListener {

            private final TextView playPauseView;
            private final TextView audioLengthView;
            private final AudioSampleView sampleView;

            public AudioChatViewHolder(final View itemView, final boolean outgoing, final ChatAdapter adapter) {
                super(itemView, outgoing, adapter);
                playPauseView = (TextView) itemView.findViewById(R.id.play_pause);
                audioLengthView = (TextView) itemView.findViewById(R.id.audio_length);
                sampleView = (AudioSampleView) itemView.findViewById(R.id.audio_sample);

                playPauseView.setOnClickListener(this);
            }

            @Override
            public void displayMessage(Message message) {
                super.displayMessage(message);
                final FileAttachment.AudioMetadata metadata = getAudioMetadata(message);
                if (metadata != null) {
                    audioLengthView.setText(String.format("%.1f", metadata.getDuration()));
                    sampleView.setSamples(metadata.getSamples());
                }
            }

            private FileAttachment.AudioMetadata getAudioMetadata(Message message) {
                final List<Attachment> attachments = message.getAttachments();
                final List<Message.LocalMetadata> localMetadata = message.getLocalMetadata();
                if (localMetadata != null && !localMetadata.isEmpty()) {
                    return JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"), FileAttachment.AudioMetadata.class);
                } else if (attachments != null && !attachments.isEmpty()) {
                    final FileAttachment attachment = (FileAttachment) attachments.get(0);
                    return JsonSerializer.parse(attachment.getMetadata(), FileAttachment.AudioMetadata.class);
                } else {
                    return null;
                }
            }

            @Override
            public void onClick(final View v) {
                final Message message = adapter.getMessage(getLayoutPosition());
                final List<Attachment> attachments = message.getAttachments();
                if (attachments == null || attachments.isEmpty()) return;
                adapter.playAudio((FileAttachment) attachments.get(0));
            }
        }


    }
}
