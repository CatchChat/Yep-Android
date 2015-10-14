package catchla.yep.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import catchla.yep.R;

public class TopicChatActivity extends SwipeBackContentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
