package catchla.yep.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.mariotaku.abstask.library.AbstractTask;
import org.mariotaku.abstask.library.TaskStarter;

import catchla.yep.R;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.YepException;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;

/**
 * Created by mariotaku on 15/10/10.
 */
public class FeedbackActivity extends SwipeBackContentActivity {

    private EditText mEditFeedback;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mEditFeedback = (EditText) findViewById(R.id.edit_feedback);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send: {
                final Editable text = mEditFeedback.getText();
                if (TextUtils.isEmpty(text)) return true;
                sendFeedback(String.valueOf(text));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendFeedback(final String content) {
        final AbstractTask<String, TaskResponse<Boolean>, FeedbackActivity> task
                = new AbstractTask<String, TaskResponse<Boolean>, FeedbackActivity>() {

            @Override
            public TaskResponse<Boolean> doLongOperation(final String params) {
                final YepAPI yepAPI = YepAPIFactory.getInstance(FeedbackActivity.this, getAccount());
                final String deviceInfo = getResources().getConfiguration().toString();
                try {
                    yepAPI.postFeedback(params, deviceInfo);
                    return TaskResponse.getInstance(true);
                } catch (YepException e) {
                    return TaskResponse.getInstance(e);
                }
            }

            @Override
            public void afterExecute(final FeedbackActivity handler, final TaskResponse<Boolean> result) {
                handler.finish();
            }
        };
        task.setParams(content);
        task.setResultHandler(this);
        TaskStarter.execute(task);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }
}
