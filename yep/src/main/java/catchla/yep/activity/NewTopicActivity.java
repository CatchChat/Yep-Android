package catchla.yep.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;

import catchla.yep.Constants;
import catchla.yep.R;
import catchla.yep.model.NewTopic;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.Topic;
import catchla.yep.util.ParseUtils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/10/13.
 */
public class NewTopicActivity extends SwipeBackContentActivity implements Constants {
    private EditText mEditText;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mEditText = (EditText) findViewById(R.id.edit_text);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send: {
                final NewTopic newTopic = new NewTopic();
                newTopic.body(ParseUtils.parseString(mEditText.getText()));
                final TaskRunnable<NewTopic, TaskResponse<Topic>, NewTopicActivity> taskRunnable = new TaskRunnable<NewTopic, TaskResponse<Topic>, NewTopicActivity>() {
                    @Override
                    public TaskResponse<Topic> doLongOperation(final NewTopic params) throws InterruptedException {
                        final YepAPI yep = YepAPIFactory.getInstance(NewTopicActivity.this, getAccount());
                        try {
                            return TaskResponse.getInstance(yep.postTopic(params.toJson()));
                        } catch (YepException e) {
                            return TaskResponse.getInstance(e);
                        } catch (Throwable t) {
                            Log.wtf(LOGTAG, t);
                            System.exit(0);
                            return null;
                        }
                    }

                    @Override
                    public void callback(final NewTopicActivity handler, final TaskResponse<Topic> response) {
                        if (response.hasData()) {
                            if (!handler.isFinishing()) {
                                handler.finish();
                            }
                            Log.d(LOGTAG, String.valueOf(response.getData()));
                        } else if (response.hasException()) {
                            Log.w(LOGTAG, response.getException());
                        }
                    }
                };
                taskRunnable.setResultHandler(this);
                taskRunnable.setParams(newTopic);
                AsyncManager.runBackgroundTask(taskRunnable);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }
}
