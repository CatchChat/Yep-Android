package catchla.yep.activity;

import android.os.Bundle;
import android.view.Menu;

import catchla.yep.R;

/**
 * Created by mariotaku on 15/10/10.
 */
public class FeedbackActivity extends SwipeBackContentActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }
}
