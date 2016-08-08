package catchla.yep.activity.iface

import java.util.*

/**
 * Created by mariotaku on 16/8/8.
 */
interface IBaseActivity {

    fun executeAfterFragmentResumed(action: (IBaseActivity) -> Unit)

    class ActionHelper(private val activity: IBaseActivity) {

        private var fragmentResumed: Boolean = false
        private val actionQueue = LinkedList<(IBaseActivity) -> Unit>()

        fun dispatchOnPause() {
            fragmentResumed = false
        }

        fun dispatchOnResumeFragments() {
            fragmentResumed = true
            executePending()
        }


        private fun executePending() {
            if (!fragmentResumed) return
            var action: ((IBaseActivity) -> Unit)?
            do {
                action = actionQueue.poll()
                action?.invoke(activity)
            } while (action != null)
        }

        fun executeAfterFragmentResumed(action: (IBaseActivity) -> Unit) {
            actionQueue.add(action)
            executePending()
        }
    }
}

