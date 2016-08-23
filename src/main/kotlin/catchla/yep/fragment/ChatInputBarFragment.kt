package catchla.yep.fragment

import android.Manifest
import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import catchla.yep.Constants
import catchla.yep.R
import catchla.yep.activity.LocationPickerActivity
import catchla.yep.activity.ThemedImagePickerActivity
import catchla.yep.annotation.AttachableType
import catchla.yep.model.*
import catchla.yep.util.*
import catchla.yep.util.task.SendMessageDelegate
import catchla.yep.util.task.SendMessageTask
import catchla.yep.util.task.sendMessagePromise
import kotlinx.android.synthetic.main.layout_chat_input_panel.*
import nl.komponents.kovenant.ui.successUi
import org.apache.commons.lang3.ArrayUtils
import org.mariotaku.abstask.library.TaskStarter
import org.mariotaku.commons.parcel.ViewUtils
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Input bar component for chat activities
 * Created by mariotaku on 15/11/16.
 */
class ChatInputBarFragment : BaseFragment(), Constants, ChatMediaBottomSheetDialogFragment.Callback {

    companion object {
        val REQUEST_PICK_IMAGE = 101
        val REQUEST_TAKE_PHOTO = 102
        val REQUEST_PICK_LOCATION = 103
        val REQUEST_REQUEST_RECORD_PERMISSION = 104
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attachmentSend.setOnClickListener {
            if (editText.length() > 0) {
                sendTextMessage()
            } else {
                openAttachmentMenu()
            }
        }

        val handler = EditTextEnterHandler.attach(editText, true) { sendTextMessage() }
        handler.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                attachmentSend.setImageResource(if (s.length > 0) R.drawable.ic_action_send else R.drawable.ic_action_attachment)
                if (listener != null) {
                    listener!!.onTypingText()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        voiceToggle.setOnClickListener {
            val showVoice = voiceRecord.visibility != View.VISIBLE
            if (showVoice) {
                val activity = activity
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(voiceRecord.windowToken, 0)
            }
            voiceRecord.visibility = if (showVoice) View.VISIBLE else View.GONE
            editTextContainer.visibility = if (showVoice) View.GONE else View.VISIBLE
        }
        val helper = GestureViewHelper(context)
        helper.setOnGestureListener(VoicePressListener(this))
        voiceRecord.setOnTouchListener { v, event ->
            helper.onTouchEvent(event)
            false
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.layout_chat_input_panel, container, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_REQUEST_RECORD_PERMISSION -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, R.string.record_audio_permission_required, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE, REQUEST_TAKE_PHOTO -> {
                if (resultCode != Activity.RESULT_OK) return
                sendImage(data!!.data)
                return
            }
            REQUEST_PICK_LOCATION -> {
                if (resultCode != Activity.RESULT_OK) return
                val location = data!!.getParcelableExtra<Location>(Constants.EXTRA_LOCATION)
                sendLocation(location)
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendTextMessage() {
        sendMessage(object : SendMessageHandler() {

            override val mediaType: String
                get() = Message.MediaType.TEXT

        })
    }

    private fun openAttachmentMenu() {
        val df = ChatMediaBottomSheetDialogFragment()
        df.setTargetFragment(this, 0)
        df.show(fragmentManager, "pick_media")
    }

    private fun sendLocation(location: Location?) {
        if (location == null) return
        // Show error if location is null
        sendMessage(object : SendMessageHandler() {
            @Throws(YepException::class)
            public override fun uploadAttachment(yep: YepAPI, message: NewMessage): FileAttachment? {
                message.location(location.latitude, location.longitude)
                return null
            }

            override val mediaType: String
                get() = Message.MediaType.LOCATION

        })
    }

    private fun sendImage(imageUri: Uri) {
        sendMessage(object : SendMessageHandler() {
            @Throws(YepException::class)
            public override fun uploadAttachment(yep: YepAPI, message: NewMessage): FileAttachment? {
                val path = imageUri.path
                val mimeType = message.getMetadataValue("mime_type", null)
                val metadata = message.getMetadataValue("metadata", null)
                return yep.uploadAttachment(AttachmentUpload.create(File(path), mimeType,
                        AttachableType.MESSAGE, metadata))
            }

            override fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>? {
                val path = imageUri.path
                val imageMetadata = FileAttachment.ImageMetadata.getImageMetadata(path)
                return arrayOf(
                        Message.LocalMetadata("image", imageUri.toString()),
                        Message.LocalMetadata("mime_type", imageMetadata.mimeType),
                        Message.LocalMetadata("metadata", JsonSerializer.serialize(imageMetadata, FileAttachment.ImageMetadata::class.java))
                )
            }

            override val mediaType: String
                get() = Message.MediaType.IMAGE


        })
    }

    private fun sendMessage(sendMessageHandler: SendMessageHandler) {
        val conversation = this.conversation ?: return
        val newMessage = NewMessage()
        newMessage.textContent(editText.text.toString())
        newMessage.accountId(conversation.accountId)
        newMessage.conversationId(conversation.id)
        newMessage.recipientId(conversation.recipientId)
        newMessage.recipientType(conversation.recipientType)
        newMessage.circle(conversation.circle)
        newMessage.sender(Utils.getAccountUser(context, account))
        newMessage.user(conversation.user)
        newMessage.createdAt(System.currentTimeMillis())
        newMessage.randomId(Utils.generateRandomId(16))
        listener!!.onMessageSentStarted(newMessage)
        sendMessagePromise(context, account, newMessage, object : SendMessageDelegate {
            override val mediaType: String
                get() = sendMessageHandler.mediaType

            @Throws(YepException::class)
            override fun uploadAttachment(yep: YepAPI, newMessage: NewMessage): FileAttachment? {
                return sendMessageHandler.uploadAttachment(yep, newMessage)
            }

            override fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>? {
                return sendMessageHandler.getLocalMetadata(newMessage)
            }
        }).successUi { body ->
            listener?.onMessageSentFinished(body)
        }
        editText.setText("")
    }

    private val conversation: Conversation?
        get() = arguments.getParcelable<Conversation>(Constants.EXTRA_CONVERSATION)

    private val account: Account
        get() = arguments.getParcelable<Account>(Constants.EXTRA_ACCOUNT)

    override fun onButtonClick(id: Int) {
        when (id) {
            R.id.gallery -> {
                startActivityForResult(ThemedImagePickerActivity.withThemed(context).pickImage().build(),
                        REQUEST_PICK_IMAGE)
            }
            R.id.location -> {
                val intent = Intent(context, LocationPickerActivity::class.java)
                intent.putExtra(Constants.EXTRA_ACCOUNT, account)
                startActivityForResult(intent, REQUEST_PICK_LOCATION)
            }
        }
    }

    override fun onCameraClick() {
        startActivityForResult(ThemedImagePickerActivity.withThemed(context).takePhoto().build(),
                REQUEST_TAKE_PHOTO)
    }

    override fun onMediaClick(id: Long, data: String) {
        sendImage(Uri.parse("file://" + data))
    }

    internal abstract class SendMessageHandler {
        @Throws(YepException::class)
        internal open fun uploadAttachment(yep: YepAPI, message: NewMessage): FileAttachment? {
            return null
        }

        internal abstract val mediaType: String

        internal open fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>? {
            return null
        }
    }

    interface Listener {

        fun onRecordStarted()

        @WorkerThread
        fun postSetAmplitude(amplitude: Int)

        fun onRecordStopped()

        fun onMessageSentFinished(result: Message)

        fun onMessageSentStarted(newMessage: NewMessage)

        fun onTypingText()
    }

    var listener: Listener? = null

    internal class SampleRecorder {

        private val mSamplesList = ArrayList<Float>()

        fun start() {
            mSamplesList.clear()
        }

        fun get(): FloatArray {
            val size = mSamplesList.size
            val rawSamplesArray = ArrayUtils.toPrimitive(mSamplesList.toTypedArray())
            val idealSampleSize = 20
            if (size < idealSampleSize) {
                return rawSamplesArray
            }
            val gap = size / idealSampleSize
            val result = FloatArray(idealSampleSize)
            for (i in 0 until idealSampleSize) {
                result[i] = MathUtils.avg(rawSamplesArray, i * gap, (i + 1) * gap - 1)
            }
            return result
        }

        fun put(maxAmplitude: Float) {
            mSamplesList.add(maxAmplitude / java.lang.Short.MAX_VALUE.toFloat())
        }
    }

    private class VoicePressListener(private val fragment: ChatInputBarFragment) : GestureDetector.SimpleOnGestureListener(), GestureViewHelper.OnUpListener, GestureViewHelper.OnCancelListener {
        private var recorder: MediaRecorder? = null
        private var timerTask: RecordMetersThread? = null
        private var currentRecordPath: String? = null
        private val sampleRecorder: SampleRecorder

        init {
            sampleRecorder = SampleRecorder()
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDown(e: MotionEvent): Boolean {
            fragment.voiceRecord.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            if (ContextCompat.checkSelfPermission(fragment.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
                fragment.requestPermissions(permissions, ChatInputBarFragment.REQUEST_REQUEST_RECORD_PERMISSION)
                return false
            }
            return startRecording()
        }

        @RequiresPermission(Manifest.permission.RECORD_AUDIO)
        private fun startRecording(): Boolean {
            val recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setOutputFile(recordFilePath)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                recorder.prepare()
            } catch (ioe: IOException) {
                return false
            }
            currentRecordPath = recordFilePath
            recorder.start()
            recorder.maxAmplitude
            val task = RecordMetersThread(this)
            timerTask = task
            task.start()
            this.recorder = recorder
            fragment.listener?.onRecordStarted()
            fragment.voiceRecord.setText(R.string.release_to_send)
            sampleRecorder.start()
            return true
        }

        private val recordFilePath: String
            get() = File(fragment.context.cacheDir, "record_" + System.currentTimeMillis()).absolutePath

        override fun onUp(event: MotionEvent) {
            stopRecording(!ViewUtils.hitView(event.rawX, event.rawY, fragment.voiceRecord))
        }

        override fun onCancel(event: MotionEvent) {
            stopRecording(true)
        }

        private fun stopRecording(cancel: Boolean) {
            fragment.listener?.onRecordStopped()
            fragment.voiceRecord.setText(R.string.ptt_hint)
            val samples = sampleRecorder.get()
            val recorder = this.recorder ?: return
            recorder.stop()
            recorder.release()
            this.recorder = null
            val task = timerTask
            task?.cancel()
            timerTask = null
            val recordPath = currentRecordPath
            if (cancel) {
                if (recordPath != null) {
                    val file = File(recordPath)
                    file.delete()
                }
                return
            }
            fragment.sendMessage(object : SendMessageHandler() {
                override fun getLocalMetadata(newMessage: NewMessage): Array<Message.LocalMetadata>? {
                    val player = MediaPlayer.create(fragment.context, Uri.parse(recordPath))
                    val metadataItem = FileAttachment.AudioMetadata()
                    metadataItem.duration = player.duration / 1000f
                    metadataItem.samples = samples
                    try {
                        return arrayOf(Message.LocalMetadata("metadata",
                                JsonSerializer.serialize(metadataItem, FileAttachment.AudioMetadata::class.java)))
                    } finally {
                        player.release()
                    }
                }

                @Throws(YepException::class)
                public override fun uploadAttachment(yep: YepAPI, message: NewMessage): FileAttachment? {
                    val file = File(recordPath!!)
                    return yep.uploadAttachment(AttachmentUpload.create(file, "audio/mp4",
                            AttachableType.MESSAGE, message.getMetadataValue("metadata", null)))
                }

                override val mediaType: String
                    get() = Message.MediaType.AUDIO
            })

        }


        private class RecordMetersThread(val listener: VoicePressListener) : Thread() {
            private val cancelled = AtomicBoolean()
            val INTERVAL = 16L

            fun cancel() {
                cancelled.set(true)
            }

            override fun run() {
                while (!cancelled.get()) {
                    try {
                        updateView()
                        Thread.sleep(Math.max(0, INTERVAL))
                    } catch (ignored: Exception) {
                    }

                }
            }

            private fun updateView(): Long {
                val callStart = System.currentTimeMillis()
                if (cancelled.get()) return System.currentTimeMillis() - callStart
                val recorder = listener.recorder
                if (recorder == null) {
                    cancel()
                    return System.currentTimeMillis() - callStart
                }
                val maxAmplitude = recorder.maxAmplitude
                listener.fragment.listener!!.postSetAmplitude(maxAmplitude)
                listener.fragment.activity.runOnUiThread { listener.sampleRecorder.put(maxAmplitude / Short.MAX_VALUE.toFloat()) }
                return System.currentTimeMillis() - callStart
            }

        }
    }

}
