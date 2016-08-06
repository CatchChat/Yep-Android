package catchla.yep.view

import android.content.Context
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.util.AttributeSet
import android.widget.FrameLayout

import catchla.yep.model.TaskResponse
import catchla.yep.model.YepException

/**
 * Created by mariotaku on 16/8/3.
 */
abstract class ProviderWidgetContainer<T> : FrameLayout {
    private var task: AsyncTask<Any, Any, TaskResponse<T>>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var attachedToWindow: Boolean = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
        startTask()
    }

    fun startTask() {
        if (task != null || !ready || !attachedToWindow) return
        task = object : AsyncTask<Any, Any, TaskResponse<T>>() {

            override fun doInBackground(vararg objects: Any): TaskResponse<T> {
                try {
                    return TaskResponse(doRequest(), null)
                } catch (e: YepException) {
                    return TaskResponse(null, e)
                }

            }

            override fun onPreExecute() {
                preRequest()
            }

            override fun onPostExecute(result: TaskResponse<T>) {
                displayData(result)
            }
        }.execute()
    }

    override fun onDetachedFromWindow() {
        if (task != null && task!!.status == AsyncTask.Status.RUNNING) {
            task!!.cancel(true)
            task = null
        }
        attachedToWindow = false
        super.onDetachedFromWindow()
    }

    @UiThread
    protected abstract fun displayData(result: TaskResponse<T>)

    @UiThread
    protected abstract fun preRequest()

    protected open val ready: Boolean
        get() = true

    @WorkerThread
    @Throws(YepException::class)
    protected abstract fun doRequest(): T
}
