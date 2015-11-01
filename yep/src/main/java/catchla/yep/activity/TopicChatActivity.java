package catchla.yep.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import org.apache.commons.lang3.StringUtils;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.Topic;
import catchla.yep.util.MenuUtils;
import catchla.yep.util.Utils;
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

        final Topic topic = getTopic();
        displayTopic(topic);
    }

    private Topic getTopic() {
        return getIntent().getParcelableExtra(EXTRA_TOPIC);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final boolean isMyTopic = StringUtils.equals(getTopic().getUser().getId(), Utils.getAccountId(this, getAccount()));
        MenuUtils.setMenuGroupAvailability(menu, R.id.group_menu_my_topic, isMyTopic);
        return super.onPrepareOptionsMenu(menu);
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
