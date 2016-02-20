package catchla.yep.view.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import catchla.yep.R;
import catchla.yep.adapter.TopicsAdapter;
import catchla.yep.model.GithubAttachment;
import catchla.yep.model.Topic;
import catchla.yep.util.ImageLoaderWrapper;

/**
 * Created by mariotaku on 15/12/9.
 */
public class GithubTopicViewHolder extends TopicViewHolder {

    private final TextView repoName, repoDescription;

    public GithubTopicViewHolder(final TopicsAdapter topicsAdapter, final View itemView, final Context context,
                                 final ImageLoaderWrapper imageLoader,
                                 final TopicsAdapter.TopicClickListener listener) {
        super(topicsAdapter, itemView, context, imageLoader, listener);
        repoName = (TextView) itemView.findViewById(R.id.repo_name);
        repoDescription = (TextView) itemView.findViewById(R.id.repo_description);
        itemView.findViewById(R.id.attachment_view).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.attachment_view: {
                GithubAttachment attachment = (GithubAttachment) adapter.getTopic(getLayoutPosition()).getAttachments().get(0);
                adapter.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(attachment.getUrl())));
                return;
            }
        }
        super.onClick(v);
    }

    @Override
    public void displayTopic(final Topic topic) {
        super.displayTopic(topic);
        GithubAttachment attachment = (GithubAttachment) topic.getAttachments().get(0);
        repoName.setText(attachment.getName());
        repoDescription.setText(attachment.getDescription());
    }
}
