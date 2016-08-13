package catchla.yep.view.holder

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.TextView
import catchla.yep.R
import catchla.yep.adapter.TopicsAdapter
import catchla.yep.model.GithubAttachment
import catchla.yep.model.Topic
import catchla.yep.util.ImageLoaderWrapper
import catchla.yep.util.Utils

/**
 * Created by mariotaku on 15/12/9.
 */
class GithubTopicViewHolder(topicsAdapter: TopicsAdapter, itemView: View, context: Context,
                            imageLoader: ImageLoaderWrapper,
                            listener: TopicsAdapter.TopicClickListener?) : TopicViewHolder(topicsAdapter, itemView, context, imageLoader, listener) {

    private val repoName: TextView
    private val repoDescription: TextView

    init {
        repoName = itemView.findViewById(R.id.repo_name) as TextView
        repoDescription = itemView.findViewById(R.id.repo_description) as TextView
        itemView.findViewById(R.id.attachmentView).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.attachmentView -> {
                val attachment = adapter.getTopic(layoutPosition).attachments[0] as GithubAttachment
                Utils.openUri(adapter.context as Activity, Uri.parse(attachment.url))
                return
            }
        }
        super.onClick(v)
    }

    override fun displayTopic(topic: Topic) {
        super.displayTopic(topic)
        val attachment = topic.attachments[0] as GithubAttachment
        repoName.text = attachment.name
        repoDescription.text = attachment.description
    }
}
