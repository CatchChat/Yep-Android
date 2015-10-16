package catchla.yep.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Topic;
import catchla.yep.view.holder.TopicViewHolder;

public class TopicChatActivity extends SwipeBackContentActivity implements Constants {

    private View mTopicFullView;
    private TopicViewHolder mTopicViewHolder;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_chat);
        mTopicViewHolder.setReplyButtonVisible(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        final Topic topic = getIntent().getParcelableExtra(EXTRA_TOPIC);
        displayTopic(topic);
    }

    private void displayTopic(final Topic topic) {
        mTopicViewHolder.displayTopic(topic);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mTopicFullView = findViewById(R.id.topic_full);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTopicViewHolder = new TopicViewHolder(mTopicFullView.findViewById(R.id.item_content),
                this, mImageLoader, null);
    }
}
