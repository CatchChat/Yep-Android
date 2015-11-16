package catchla.yep.fragment;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.WorkerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.desmond.asyncmanager.AsyncManager;

import org.apache.commons.lang3.ArrayUtils;
import org.mariotaku.restfu.http.RestHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.activity.ThemedImagePickerActivity;
import catchla.yep.model.Attachment;
import catchla.yep.model.Conversation;
import catchla.yep.model.Message;
import catchla.yep.model.NewAttachment;
import catchla.yep.model.NewAudioAttachment;
import catchla.yep.model.NewImageAttachment;
import catchla.yep.model.NewMessage;
import catchla.yep.model.S3UploadToken;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.EditTextEnterHandler;
import catchla.yep.util.GestureViewHelper;
import catchla.yep.util.JsonSerializer;
import catchla.yep.util.MathUtils;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;
import catchla.yep.util.task.SendMessageTask;

/**
 * Input bar component for chat activities
 * Created by mariotaku on 15/11/16.
 */
public class ChatInputBarFragment extends BaseFragment implements Constants {

    private static final int REQUEST_PICK_IMAGE = 101;
    private static final int REQUEST_TAKE_PHOTO = 102;
    private static final int REQUEST_REQUEST_RECORD_PERMISSION = 103;

    private ImageView mAttachSendButton;
    private EditText mEditText;
    private PopupMenu mAttachPopupMenu;
    private View mVoiceToggle;
    private View mEditTextContainer;
    private Button mVoiceRecordButton;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_chat_input_panel, container, false);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_REQUEST_RECORD_PERMISSION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), R.string.record_audio_permission_required, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onBaseViewCreated(final View view, final Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mEditText = (EditText) view.findViewById(R.id.edit_text);
        mEditTextContainer = view.findViewById(R.id.edit_text_container);
        mAttachSendButton = (ImageView) view.findViewById(R.id.attachment_send);
        mVoiceToggle = view.findViewById(R.id.voice_toggle);
        mVoiceRecordButton = (Button) view.findViewById(R.id.voice_record);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        mAttachPopupMenu = new PopupMenu(mAttachSendButton.getContext(), mAttachSendButton);
        mAttachPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gallery: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(getContext()).pickImage().build(), REQUEST_PICK_IMAGE);
                        return true;
                    }
                    case R.id.camera: {
                        startActivityForResult(ThemedImagePickerActivity.withThemed(getContext()).takePhoto().build(), REQUEST_TAKE_PHOTO);
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
        final GestureViewHelper helper = new GestureViewHelper(getContext());
        helper.setOnGestureListener(new VoicePressListener(this));
        mVoiceRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                helper.onTouchEvent(event);
                return false;
            }
        });

        mAttachPopupMenu.inflate(R.menu.action_attach_send);

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
            case REQUEST_TAKE_PHOTO: {
                if (resultCode != Activity.RESULT_OK) return;
                sendImage(data.getData());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void sendLocation() {
        sendMessage(new SendMessageHandler() {
            @Override
            public NewAttachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                final Location location = Utils.getCachedLocation(getContext());
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

    private void sendImage(final Uri imageUri) {
        sendMessage(new SendMessageHandler() {
            @Override
            public NewAttachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                final String path = imageUri.getPath();

                final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.MESSAGE);
                final RestHttpClient client = YepAPIFactory.getHttpClient(yep);
                try {
                    Utils.uploadToS3(client, token, new File(path));
                } catch (IOException e) {
                    throw new YepException(e);
                }
                return new NewImageAttachment(token, message.getMetadataValue("metadata", null));
            }

            @Nullable
            @Override
            Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage) {
                Message.LocalMetadata[] metadata = new Message.LocalMetadata[2];
                final String path = imageUri.getPath();
                final String imageMetadata = JsonSerializer.serialize(Attachment.ImageMetadata.getImageMetadata(path), Attachment.ImageMetadata.class);
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

    private void sendMessage(final SendMessageHandler sendMessageHandler) {
        final Account account = Utils.getCurrentAccount(getContext());
        final Conversation conversation = getConversation();
        if (account == null || conversation == null) return;
        final SendMessageTask<ChatInputBarFragment> task = new SendMessageTask<ChatInputBarFragment>(getContext(), account) {
            @Override
            public void callback(final ChatInputBarFragment handler, final TaskResponse<Message> result) {
                if (result.hasData()) {
                    handler.mListener.onMessageSentFinished(result);
                    // TODO Reload messages
                }
                super.callback(handler, result);
            }

            @Override
            protected NewAttachment uploadAttachment(final YepAPI yep, final NewMessage newMessage) throws YepException {
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
        mListener.onMessageSentStarted(newMessage);
        AsyncManager.runBackgroundTask(task);
        mEditText.setText("");
    }

    private Conversation getConversation() {
        return getArguments().getParcelable(EXTRA_CONVERSATION);
    }

    static abstract class SendMessageHandler {
        @Nullable
        NewAttachment uploadAttachment(YepAPI yep, NewMessage message) throws YepException {
            return null;
        }

        abstract String getMediaType();

        @Nullable
        Message.LocalMetadata[] getLocalMetadata(NewMessage newMessage) {
            return null;
        }
    }

    public interface Listener {

        void onRecordStarted();

        @WorkerThread
        void postSetAmplitude(int amplitude);

        void onRecordStopped();

        void onMessageSentFinished(TaskResponse<Message> result);

        void onMessageSentStarted(NewMessage newMessage);
    }

    private Listener mListener;

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    static class SampleRecorder {

        private ArrayList<Float> mSamplesList = new ArrayList<>();

        public void start() {
            mSamplesList.clear();
        }

        private float[] get() {
            final int size = mSamplesList.size();
            final float[] rawSamplesArray = ArrayUtils.toPrimitive(mSamplesList.toArray(new Float[size]));
            final int idealSampleSize = 20;
            if (size < idealSampleSize) {
                return rawSamplesArray;
            }
            final int gap = size / idealSampleSize;
            final float[] result = new float[idealSampleSize];
            for (int i = 0; i < idealSampleSize; i++) {
                result[i] = MathUtils.avg(rawSamplesArray, i * gap, (i + 1) * gap - 1);
            }
            return result;
        }

        public void put(final float maxAmplitude) {
            mSamplesList.add(maxAmplitude / (float) Short.MAX_VALUE);
        }
    }

    private static class VoicePressListener extends GestureDetector.SimpleOnGestureListener
            implements GestureViewHelper.OnUpListener, GestureViewHelper.OnCancelListener {
        private MediaRecorder mRecorder;
        private RecordMetersThread mTimerTask;
        private String mCurrentRecordPath;
        private final SampleRecorder mSampleRecorder;
        private ChatInputBarFragment mFragment;

        public VoicePressListener(final ChatInputBarFragment fragment) {
            mFragment = fragment;
            mSampleRecorder = new SampleRecorder();
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(final MotionEvent e) {
            if (ContextCompat.checkSelfPermission(mFragment.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                final String[] permissions = {Manifest.permission.RECORD_AUDIO};
                mFragment.requestPermissions(permissions, REQUEST_REQUEST_RECORD_PERMISSION);
                return false;
            }
            return startRecording();
        }

        @RequiresPermission(Manifest.permission.RECORD_AUDIO)
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
            if (mFragment.mListener != null)
                mFragment.mListener.onRecordStarted();
            mSampleRecorder.start();
            return true;
        }

        private String getRecordFilePath() {
            return new File(mFragment.getContext().getCacheDir(), "record_" + System.currentTimeMillis()).getAbsolutePath();
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
            if (mFragment.mListener != null) {
                mFragment.mListener.onRecordStopped();
            }
            final float[] samples = mSampleRecorder.get();
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
            mFragment.sendMessage(new SendMessageHandler() {
                @Nullable
                @Override
                Message.LocalMetadata[] getLocalMetadata(final NewMessage newMessage) {
                    final Message.LocalMetadata[] metadata = new Message.LocalMetadata[1];
                    final MediaPlayer player = MediaPlayer.create(mFragment.getContext(), Uri.parse(recordPath));
                    final Attachment.AudioMetadata metadataItem = new Attachment.AudioMetadata();
                    metadataItem.setDuration(player.getDuration() / 1000f);
                    metadataItem.setSamples(samples);
                    metadata[0] = new Message.LocalMetadata("metadata", JsonSerializer.serialize(metadataItem, Attachment.AudioMetadata.class));
                    player.release();
                    return metadata;
                }

                @Override
                public NewAttachment uploadAttachment(final YepAPI yep, final NewMessage message) throws YepException {
                    final S3UploadToken token = yep.getS3UploadToken(YepAPI.AttachmentKind.MESSAGE);
                    final RestHttpClient client = YepAPIFactory.getHttpClient(yep);
                    try {
                        Utils.uploadToS3(client, token, new File(recordPath));
                    } catch (IOException e) {
                        throw new YepException(e);
                    }
                    return new NewAudioAttachment(token, message.getMetadataValue("metadata", null));
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
                mFragment.mListener.postSetAmplitude(maxAmplitude);
                mFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSampleRecorder.put(maxAmplitude / (float) Short.MAX_VALUE);
                    }
                });
                return System.currentTimeMillis() - callStart;
            }

        }
    }

}
