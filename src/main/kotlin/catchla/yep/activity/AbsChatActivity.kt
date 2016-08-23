/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.view.View
import catchla.yep.Constants.EXTRA_ACCOUNT
import catchla.yep.Constants.EXTRA_CONVERSATION
import catchla.yep.IFayeService
import catchla.yep.R
import catchla.yep.extension.Bundle
import catchla.yep.extension.account
import catchla.yep.extension.set
import catchla.yep.fragment.ChatInputBarFragment
import catchla.yep.fragment.ChatListFragment
import catchla.yep.model.Conversation
import catchla.yep.model.Message
import catchla.yep.model.NewMessage
import catchla.yep.service.FayeService
import catchla.yep.util.ThemeUtils
import kotlinx.android.synthetic.main.activity_chat.*

/**
 * Created by mariotaku on 15/4/30.
 */
abstract class AbsChatActivity : SwipeBackContentActivity(), ChatInputBarFragment.Listener, ServiceConnection {

    abstract val conversation: Conversation
    protected var fayeService: IFayeService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        val primaryColor = ThemeUtils.getColorFromAttribute(this, R.attr.colorPrimary, 0)
        actionBar.setBackgroundDrawable(ThemeUtils.getActionBarBackground(primaryColor, true))

        mainContent?.setStatusBarColorDarken(primaryColor)

        val chatListFragment = instantiateChatListFragment()
        val chatInputBarFragment = instantiateChatInputBarFragment()

        chatInputBarFragment.listener = this

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.listContainer, chatListFragment)
        ft.replace(R.id.inputPanel, chatInputBarFragment)
        ft.commit()


    }


    override fun onStart() {
        super.onStart()
        bindService(Intent(this, FayeService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(this)
        super.onStop()
    }

    override fun onMessageSentStarted(newMessage: NewMessage) {
        val f = chatListFragment
        f.scrollToStart()
        f.jumpToLast = true
    }

    override fun onMessageSentFinished(result: Message) {
        val f = chatListFragment
        f.scrollToStart()
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        fayeService = IFayeService.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        fayeService = null
    }


    override fun onRecordStarted() {
        voiceWaveContainer.visibility = View.VISIBLE
        voiceWaveView.startRecording()
    }

    override fun onRecordStopped() {
        voiceWaveContainer.visibility = View.GONE
        voiceWaveView.stopRecording()
    }

    @WorkerThread
    override fun postSetAmplitude(amplitude: Int) {
        runOnUiThread { voiceWaveView.amplitude = amplitude }
    }

    protected abstract fun instantiateChatListFragment(): ChatListFragment

    protected fun instantiateChatInputBarFragment(): ChatInputBarFragment {
        val fragmentArgs = Bundle {
            this[EXTRA_CONVERSATION] = conversation
            this[EXTRA_ACCOUNT] = account
        }
        return Fragment.instantiate(this, ChatInputBarFragment::class.java.name, fragmentArgs) as ChatInputBarFragment
    }

    protected val chatListFragment: ChatListFragment
        get() = supportFragmentManager.findFragmentById(R.id.listContainer) as ChatListFragment

}
