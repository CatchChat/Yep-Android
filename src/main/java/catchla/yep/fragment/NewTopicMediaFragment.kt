package catchla.yep.fragment

import android.support.annotation.WorkerThread

import catchla.yep.model.NewTopic
import catchla.yep.model.YepException
import catchla.yep.util.YepAPI

/**
 * Created by mariotaku on 16/1/3.
 */
abstract class NewTopicMediaFragment : BaseFragment() {
    abstract fun hasMedia(): Boolean

    abstract fun saveDraft(): Boolean

    @WorkerThread
    @Throws(YepException::class)
    abstract fun uploadMedia(yep: YepAPI, newTopic: NewTopic)

    abstract fun clearDraft()
}
