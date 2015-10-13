/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity;

import android.accounts.Account;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.FixedLinearLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.mariotaku.restfu.http.RestHttpClient;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.adapter.BaseRecyclerViewAdapter;
import catchla.yep.loader.MessagesLoader;
import catchla.yep.message.AudioPlayEvent;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.Message.Attachment.AudioMetadata;
import catchla.yep.model.Message.Attachment.ImageMetadata;
import catchla.yep.model.NewMessage;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.EditTextEnterHandler;
import catchla.yep.util.GestureViewHelper;
import catchla.yep.util.ImageLoaderWrapper;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.ThemeUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import catchla.yep.util.task.SendMessageTask;
import catchla.yep.view.AudioSampleView;
import catchla.yep.view.MediaSizeImageView;
import catchla.yep.view.TintedStatusFrameLayout;
import catchla.yep.view.VoiceWaveView;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by mariotaku on 15/4/30.
 */
public class ChatActivity extends SwipeBackContentActivity implements Constants, LoaderManager.LoaderCallbacks<List<Message>> {

    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_TAKE_PHOTO = 102;
    private RecyclerView mRecyclerView;
    private TintedStatusFrameLayout mMainContent;
    private ImageView mAttachSendButton;
    private FixedLinearLayoutManager mLayoutManager;
    private ChatAdapter mAdapter;
    private EditText mEditText;
    private Conversation mConversation;
    private PopupMenu mAttachPopupMenu;
    private View mVoiceToggle;
    private View mEditTextContainer;
    private Button mVoiceRecordButton;
    private VoiceWaveView mVoiceWaveView;
    private View mVoiceWaveContainer;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onStop() {
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
    public void onContentChanged() {
        super.onContentChanged();
        mEditText = (EditText) findViewById(R.id.edit_text);
        mEditTextContainer = findViewById(R.id.edit_text_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMainContent = (TintedStatusFrameLayout) findViewById(R.id.main_content);
        mAttachSendButton = (ImageView) findViewById(R.id.attachment_send);
        mVoiceToggle = findViewById(R.id.voice_toggle);
        mVoiceRecordButton = (Button) findViewById(R.id.voice_record);
        mVoiceWaveContainer = findViewById(R.id.voice_wave_container);
        mVoiceWaveView = (VoiceWaveView) findViewById(R.id.voice_wave_view);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
            case REQUEST_TAKE_PHOTO: {
                if (resultCode != RESULT_OK) return;
                sendImage(data.getData());
                return;
            }
        }
    }

    private void sendImage(final Uri imageUri) {
        sendMessage(new SendMessageHandler() {
            @Override
            public NewMessage.Attachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                final String path = imageUri.getPath();

                final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.MESSAGE);
                final RestHttpClient client = YepAPIFactory.getHttpClient(yep);
                try {
                    Utils.uploadToS3(client, token, new File(path));
                } catch (IOException e) {
                    throw new YepException(e);
                }
                return new NewMessage.ImageAttachment(token, message.getMetadataValue("metadata", null));
            }

            @Nullable
            @Override
            Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage) {
                Message.LocalMetadata[] metadata = new Message.LocalMetadata[2];
                final String path = imageUri.getPath();
                final String imageMetadata = JsonSerializer.serialize(ImageMetadata.getImageMetadata(path), ImageMetadata.class);
                metadata[0] = new Message.LocalMetadata("image", imageUri.toString());
                metadata[1] = new Message.LocalMetadata("metadata", imageMetadata);
                return metadata;
            }

            @Override
            String getMediaType() {
                return Message.MediaType.IMAGE;
            }


        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        final int primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0);
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true));

        mMainContent.setDrawColor(true);
        mMainContent.setDrawShadow(false);
        mMainContent.setColor(primaryColor);
        mLayoutManager = new FixedLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mLayoutManager.setStackFromEnd(false);
        mAdapter = new ChatAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAttachSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mEditText.length() > 0) {
                    sendTextMessage();
                } else {
                    openAttachmentMenu();
                }
            }
        });

        final EditTextEnterHandler handler = EditTextEnterHandler.attach(mEditText, new EditTextEnterHandler.EnterListener() {
            @Override
            public void onHitEnter() {
                sendTextMessage();
            }
        }, true);
        handler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                mAttachSendButton.setImageResource(s.length() > 0 ? R.drawable.ic_action_send : R.drawable.ic_action_attachment);
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });

        final Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_CONVERSATION)) {
            finish();
            return;
        }
        mAttachPopupMenu = new PopupMenu(mAttachSendButton.getContext(), mAttachSendButton);
        mAttachPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gallery: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(ChatActivity.this).pickImage().build(), REQUEST_PICK_IMAGE);
                        return true;
                    }
                    case R.id.camera: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(ChatActivity.this).takePhoto().build(), REQUEST_TAKE_PHOTO);
                        return true;
                    }
                    case R.id.location: {
                        sendLocation();
                        return true;
                    }
                }
                return false;
            }
        });
        mVoiceToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final boolean newState = mVoiceRecordButton.getVisibility() != View.VISIBLE;
                mVoiceRecordButton.setVisibility(newState ? View.VISIBLE : View.GONE);
                mEditTextContainer.setVisibility(newState ? View.GONE : View.VISIBLE);
                mAttachSendButton.setVisibility(newState ? View.GONE : View.VISIBLE);
            }
        });
        final GestureViewHelper helper = new GestureViewHelper(this);
        helper.setOnGestureListener(new VoicePressListener());
        mVoiceRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                helper.onTouchEvent(event);
                return false;
            }
        });
        mAttachPopupMenu.inflate(R.menu.action_attach_send);
        markAsRead(JsonSerializer.parse(intent.getStringExtra(EXTRA_CONVERSATION), Conversation.class));
        getSupportLoaderManager().initLoader(0, intent.getExtras(), this);
    }

    private void markAsRead(final Conversation conversation) {
        AsyncManager.runBackgroundTask(new TaskRunnable() {
            @Override
            public Object doLongOperation(final Object o) throws InterruptedException {
                final YepAPI yep = YepAPIFactory.getInstance(ChatActivity.this, Utils.getCurrentAccount(ChatActivity.this));
                try {
                    yep.batchMarkAsRead(conversation.getRecipientId(), conversation.getRecipientType(), System.currentTimeMillis() / 100f);
                } catch (YepException e) {
                    Log.w(LOGTAG, e);
                } catch (Throwable t) {
                    Log.wtf(LOGTAG, t);
                }
                return null;
            }
        });
    }

    private void sendLocation() {
        sendMessage(new SendMessageHandler() {
            @Override
            public NewMessage.Attachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                final Location location = Utils.getCachedLocation(ChatActivity.this);
                if (location == null) return null;
                message.location(location.getLatitude(), location.getLongitude());
                return null;
            }

            @Override
            String getMediaType() {
                return Message.MediaType.LOCATION;
            }

        });
    }

    private void sendTextMessage() {
        sendMessage(new SendMessageHandler() {

            @Override
            String getMediaType() {
                return Message.MediaType.TEXT;
            }

        });
    }

    private void openAttachmentMenu() {
        mAttachPopupMenu.show();
    }

    private void sendMessage(final SendMessageHandler sendMessageHandler) {
        final Account account = Utils.getCurrentAccount(ChatActivity.this);
        final Conversation conversation = mConversation;
        if (account == null || conversation == null) return;
        final TaskRunnable<NewMessage, TaskResponse<Message>, ChatActivity> task = new SendMessageTask<ChatActivity>(this, account) {
            @Override
            public void callback(final ChatActivity handler, final TaskResponse<Message> result) {
                if (result.hasData()) {
                    getSupportLoaderManager().restartLoader(0, getIntent().getExtras(), ChatActivity.this);
                }
                super.callback(handler, result);
            }

            @Override
            protected NewMessage.Attachment uploadAttachment(final YepAPI yep, final NewMessage newMessage) throws YepException {
                return sendMessageHandler.uploadAttachment(yep, newMessage);
            }

            @NonNull
            @Override
            protected String getMediaType() {
                return sendMessageHandler.getMediaType();
            }

            @Override
            protected Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage) {
                return sendMessageHandler.getLocalMetadata(newMessage);
            }
        };
        final NewMessage newMessage = new NewMessage();
        newMessage.textContent(String.valueOf(mEditText.getText()));
        newMessage.accountId(conversation.getAccountId());
        newMessage.conversationId(conversation.getId());
        newMessage.recipientId(conversation.getRecipientId());
        newMessage.recipientType(conversation.getRecipientType());
        newMessage.circle(conversation.getCircle());
        newMessage.sender(conversation.getSender());
        newMessage.createdAt(System.currentTimeMillis());
        task.setParams(newMessage);
        task.setResultHandler(this);
        AsyncManager.runBackgroundTask(task);
        mEditText.setText("");
    }

    @Override
    public Loader<List<Message>> onCreateLoader(final int id, final Bundle args) {
        final Conversation conversation;
        conversation = JsonSerializer.parse(args.getString(EXTRA_CONVERSATION), Conversation.class);
        mConversation = conversation;
        return new MessagesLoader(this, conversation);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public void onLoadFinished(final Loader<List<Message>> loader, final List<Message> data) {
        final Conversation conversation = mConversation;
        if (conversation != null) {
            setTitle(Utils.getDisplayName(conversation.getUser()));
        }
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(final Loader<List<Message>> loader) {
        mAdapter.setData(null);
    }

    private void playAudio(final Message.Attachment attachment) {
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
                        mMediaPlayer.setDataSource(ChatActivity.this, Uri.parse(attachment.getFile().getUrl()));
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

    public static class ChatAdapter extends BaseRecyclerViewAdapter {

        private static final int FLAG_MESSAGE_OUTGOING = 0xF0000000;
        private static final int VIEW_SUBTYPE_MESSAGE_TEXT = 0x0001;
        private static final int VIEW_SUBTYPE_MESSAGE_LOCATION = 0x0002;
        private static final int VIEW_SUBTYPE_MESSAGE_IMAGE = 0x0003;
        private static final int VIEW_SUBTYPE_MESSAGE_AUDIO = 0x0004;

        private final ChatActivity mActivity;
        private final LayoutInflater mInflater;
        private List<Message> mData;

        ChatAdapter(ChatActivity activity) {
            super(activity);
            mActivity = activity;
            mInflater = LayoutInflater.from(activity);
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
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_container);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_location, attachmentContainer);
                    return new LocationChatViewHolder(baseView, isOutgoing, this);
                }
                case VIEW_SUBTYPE_MESSAGE_IMAGE: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_container);
                    attachmentContainer.setVisibility(View.VISIBLE);
                    View.inflate(attachmentContainer.getContext(), R.layout.layout_message_attachment_image, attachmentContainer);
                    return new ImageChatViewHolder(baseView, isOutgoing, this);
                }
                case VIEW_SUBTYPE_MESSAGE_AUDIO: {
                    final ViewGroup attachmentContainer = (ViewGroup) baseView.findViewById(R.id.attachment_container);
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
            if (mData == null) return 0;
            return mData.size();
        }

        public void setData(final List<Message> data) {
            mData = data;
            notifyDataSetChanged();
        }

        public Message getMessage(final int position) {
            if (mData == null) return null;
            return mData.get(position);
        }

        private void playAudio(final Message.Attachment attachment) {
            mActivity.playAudio(attachment);
        }

        private static class MessageViewHolder extends RecyclerView.ViewHolder {

            protected final ChatAdapter adapter;

            private final TextView text1;
            private final TextView state;

            public MessageViewHolder(final View itemView, boolean outgoing, ChatAdapter adapter) {
                super(itemView);
                this.adapter = adapter;
                text1 = (TextView) itemView.findViewById(android.R.id.text1);
                state = (TextView) itemView.findViewById(R.id.state);
            }

            public void displayMessage(Message message) {
                text1.setText(message.getTextContent());
                text1.setVisibility(text1.length() > 0 ? View.VISIBLE : View.GONE);
                if (state != null) {
                    state.setText(message.getState());
                }
            }
        }

        private static class LocationChatViewHolder extends MessageViewHolder {
            private final MapView mapView;

            public LocationChatViewHolder(final View itemView, final boolean outgoing, final ChatAdapter adapter) {
                super(itemView, outgoing, adapter);
                mapView = (MapView) itemView.findViewById(R.id.map_view);
                mapView.setTilesScaledToDpi(true);
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        return true;
                    }
                });
            }

            @Override
            public void displayMessage(Message message) {
                super.displayMessage(message);
                final GeoPoint gp = new GeoPoint((int) (message.getLatitude() * 1E6), (int) (message.getLongitude() * 1E6));
                final IMapController mc = mapView.getController();
                mc.setZoom(12);
                mc.setCenter(gp);
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
                final ImageMetadata metadata;
                final List<Message.LocalMetadata> localMetadata = message.getLocalMetadata();
                final List<Message.Attachment> attachments = message.getAttachments();
                if (localMetadata != null && !localMetadata.isEmpty()) {
                    url = Message.LocalMetadata.get(localMetadata, "image");
                    metadata = JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"), ImageMetadata.class);
                } else if (attachments != null && !attachments.isEmpty()) {
                    final Message.Attachment attachment = attachments.get(0);
                    url = attachment.getFile().getUrl();
                    metadata = JsonSerializer.parse(attachment.getMetadata(), ImageMetadata.class);
                } else {
                    return;
                }
                if (metadata != null) {
                    imageView.setMediaSize(metadata.getWidth(), metadata.getHeight());
                    final BitmapDrawable placeholder = Utils.getMetadataBitmap(imageView.getResources(), metadata);
                    final ImageLoaderWrapper imageLoader = adapter.getImageLoader();
                    imageLoader.displayProfileImage(url, imageView);
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
                final AudioMetadata metadata = getAudioMetadata(message);
                if (metadata != null) {
                    audioLengthView.setText(String.format("%.1f", metadata.getDuration()));
                    sampleView.setSamples(metadata.getSamples());
                }
            }

            private AudioMetadata getAudioMetadata(Message message) {
                final List<Message.Attachment> attachments = message.getAttachments();
                final List<Message.LocalMetadata> localMetadata = message.getLocalMetadata();
                if (localMetadata != null && !localMetadata.isEmpty()) {
                    return JsonSerializer.parse(Message.LocalMetadata.get(localMetadata, "metadata"), AudioMetadata.class);
                } else if (attachments != null && !attachments.isEmpty()) {
                    final Message.Attachment attachment = attachments.get(0);
                    return JsonSerializer.parse(attachment.getMetadata(), AudioMetadata.class);
                } else {
                    return null;
                }
            }

            @Override
            public void onClick(final View v) {
                final Message message = adapter.getMessage(getLayoutPosition());
                final List<Message.Attachment> attachments = message.getAttachments();
                if (attachments == null || attachments.isEmpty()) return;
                adapter.playAudio(attachments.get(0));
            }
        }


    }

    private class VoicePressListener extends GestureDetector.SimpleOnGestureListener
            implements GestureViewHelper.OnUpListener, GestureViewHelper.OnCancelListener {
        private MediaRecorder mRecorder;
        private RecordMetersThread mTimerTask;
        private String mCurrentRecordPath;

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(final MotionEvent e) {
            return startRecording();
        }

        private boolean startRecording() {
            final MediaRecorder recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(mCurrentRecordPath = getRecordFilePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                recorder.prepare();
            } catch (IOException ioe) {
                return false;
            }
            recorder.start();
            recorder.getMaxAmplitude();
            RecordMetersThread task = new RecordMetersThread();
            mTimerTask = task;
            task.start();
            mRecorder = recorder;
            mVoiceWaveContainer.setVisibility(View.VISIBLE);
            mVoiceWaveView.startRecording();
            return true;
        }

        private String getRecordFilePath() {
            return new File(getCacheDir(), "record_" + System.currentTimeMillis()).getAbsolutePath();
        }

        @Override
        public void onUp(final MotionEvent event) {
            stopRecording(false);
        }

        @Override
        public void onCancel(final MotionEvent event) {
            stopRecording(true);
        }

        private void stopRecording(final boolean cancel) {
            mVoiceWaveContainer.setVisibility(View.GONE);
            final float[] samples = mVoiceWaveView.stopRecording();
            final MediaRecorder recorder = mRecorder;
            if (recorder == null) return;
            recorder.stop();
            recorder.release();
            mRecorder = null;
            final RecordMetersThread task = mTimerTask;
            if (task != null) {
                task.cancel();
            }
            mTimerTask = null;
            final String recordPath = mCurrentRecordPath;
            if (cancel) {
                if (recordPath != null) {
                    final File file = new File(recordPath);
                    file.delete();
                }
                return;
            }
            sendMessage(new SendMessageHandler() {
                @Nullable
                @Override
                Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage) {
                    final Message.LocalMetadata[] metadata = new Message.LocalMetadata[1];
                    final MediaPlayer player = MediaPlayer.create(getApplicationContext(), Uri.parse(recordPath));
                    final AudioMetadata metadataItem = new AudioMetadata();
                    metadataItem.setDuration(player.getDuration() / 1000f);
                    metadataItem.setSamples(samples);
                    metadata[0] = new Message.LocalMetadata("metadata", JsonSerializer.serialize(metadataItem, AudioMetadata.class));
                    player.release();
                    return metadata;
                }

                @Override
                public NewMessage.Attachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                    final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.MESSAGE);
                    final RestHttpClient client = YepAPIFactory.getHttpClient(yep);
                    try {
                        Utils.uploadToS3(client, token, new File(recordPath));
                    } catch (IOException e) {
                        throw new YepException(e);
                    }
                    return new NewMessage.AudioAttachment(token, message.getMetadataValue("metadata", null));
                }

                @Override
                String getMediaType() {
                    return Message.MediaType.AUDIO;
                }
            });

        }

        private class RecordMetersThread extends Thread {

            public static final long INTERVAL = 16L;
            private AtomicBoolean cancelled = new AtomicBoolean();

            public void cancel() {
                cancelled.set(true);
            }

            @Override
            public void run() {
                while (!cancelled.get()) {
                    try {
                        updateView();
                        Thread.sleep(Math.max(0, INTERVAL));
                    } catch (Exception ignored) {
                    }
                }
            }

            private long updateView() {
                final long callStart = System.currentTimeMillis();
                if (cancelled.get()) return System.currentTimeMillis() - callStart;
                final MediaRecorder recorder = mRecorder;
                if (recorder == null) {
                    cancel();
                    return System.currentTimeMillis() - callStart;
                }
                final int maxAmplitude = recorder.getMaxAmplitude();
                mVoiceWaveView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (maxAmplitude == 0) {
                            mVoiceWaveView.setAmplitude(mVoiceWaveView.getAmplitude());
                        } else {
                            mVoiceWaveView.setAmplitude(maxAmplitude);
                        }
                    }
                });
                return System.currentTimeMillis() - callStart;
            }

        }
    }

    abstract class SendMessageHandler {
        @Nullable
        NewMessage.Attachment uploadAttachment(YepAPI yep, NewMessage message) throws YepException {
            return null;
        }

        abstract String getMediaType();

        @Nullable
        Message.LocalMetadata[] getLocalMetadata(NewMessage newMessage) {
            return null;
        }
    }
}
