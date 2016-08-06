package catchla.yep.view

import android.accounts.Account
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import catchla.yep.R
import catchla.yep.model.*
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.YepAPIFactory
import catchla.yep.util.dagger.GeneralComponentHelper
import kotlinx.android.synthetic.main.provider_widget_topics.view.*
import javax.inject.Inject

/**
 * Created by mariotaku on 16/8/3.
 */
class TopicsWidgetContainer : ProviderWidgetContainer<ResponseList<Topic>> {
    @Inject
    lateinit var imageLoader: ImageLoaderWrapper

    var account: Account? = null

    var user: User? = null

    constructor(context: Context) : super(context) {
        GeneralComponentHelper.build(context).inject(this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        GeneralComponentHelper.build(context).inject(this)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        GeneralComponentHelper.build(context).inject(this)
    }

    override fun displayData(result: TaskResponse<ResponseList<Topic>>) {
        widgetContent.visibility = View.VISIBLE
        loadProgress.visibility = View.GONE
        if (result.data != null) {
            val views = arrayOf(mediaPreview0, mediaPreview1, mediaPreview2, mediaPreview3)
            views.forEachIndexed { index, view ->
                if (index < result.data.size) {
                    val topic = result.data[index]
                    val image = topic.attachments.firstOrNull() as? FileAttachment
                    if (image != null) {
                        imageLoader.displayProviderPreviewImage(image.file.url, view)
                        view.scaleType = ImageView.ScaleType.CENTER_CROP
                    } else {
                        view.setImageResource(R.drawable.ic_feed_placeholder_text)
                        view.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    }
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.GONE
                }
            }
        }
    }

    override fun preRequest() {
        widgetContent.visibility = View.GONE
        loadProgress.visibility = View.VISIBLE
    }

    override val ready: Boolean
        get() = account != null && user != null

    @Throws(YepException::class)
    override fun doRequest(): ResponseList<Topic> {
        val yep = YepAPIFactory.getInstance(context, account)
        val paging = Paging()
        paging.perPage(4)
        return yep.getTopics(user!!.id, paging)
    }
}
