package catchla.yep.activity;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.List;

import catchla.yep.R;
import catchla.yep.loader.SkillCategoriesLoader;
import catchla.yep.model.SkillCategory;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;

/**
 * Created by mariotaku on 15/6/10.
 */
public class AddSkillActivity extends ContentActivity implements LoaderManager.LoaderCallbacks<TaskResponse<List<SkillCategory>>> {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skill);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<TaskResponse<List<SkillCategory>>> onCreateLoader(final int id, final Bundle args) {
        return new SkillCategoriesLoader(this, Utils.getCurrentAccount(this), false, false);
    }

    @Override
    public void onLoadFinished(final Loader<TaskResponse<List<SkillCategory>>> loader, final TaskResponse<List<SkillCategory>> data) {
        System.identityHashCode(data);
    }

    @Override
    public void onLoaderReset(final Loader<TaskResponse<List<SkillCategory>>> loader) {

    }
}
