package catchla.yep.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.squareup.otto.Bus

/**
 * Created by mariotaku on 16/8/11.
 */
class BusHandler(
        val bus: Bus
) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message?) {
        val obj = msg?.obj ?: return
        bus.post(obj)
    }

    fun postHandler(event: Any) {
        sendMessage(obtainMessage(0, event))
    }

}
