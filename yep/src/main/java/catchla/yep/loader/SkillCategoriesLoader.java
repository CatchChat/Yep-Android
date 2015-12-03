package catchla.yep.loader;

import android.accounts.Account;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import catchla.yep.Constants;
import catchla.yep.model.PagedSkillCategories;
import catchla.yep.model.Paging;
import catchla.yep.model.SkillCategory;
import catchla.yep.model.TaskResponse;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.model.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class SkillCategoriesLoader extends AsyncTaskLoader<TaskResponse<List<SkillCategory>>> implements Constants {

    private final Account mAccount;
    private final boolean mReadCache, mWriteCache;

    public SkillCategoriesLoader(Context context, Account account, boolean readCache, boolean writeCache) {
        super(context);
        mAccount = account;
        mReadCache = readCache;
        mWriteCache = writeCache;
    }

    @Override
    public TaskResponse<List<SkillCategory>> loadInBackground() {
        if (mReadCache) {
            final TaskResponse<List<SkillCategory>> data = getCached();
            if (data != null) return data;
        }
        final Context context = getContext();
        final YepAPI yep = YepAPIFactory.getInstance(context, mAccount);
        try {
            final Paging paging = new Paging();
            int page = 1;
            final List<SkillCategory> list = new ArrayList<>();
            PagedSkillCategories users = yep.getSkillCategories();
            list.addAll(users);
            if (mWriteCache) {
                saveCached(list);
            }
            return TaskResponse.getInstance(list);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    private void saveCached(final List<SkillCategory> list) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(getCachedDiscoveryFile());
            LoganSquare.serialize(list, os, SkillCategory.class);
            os.flush();
        } catch (IOException e) {
            Log.w(LOGTAG, e);
        } finally {
            Utils.closeSilently(os);
        }
    }

    private TaskResponse<List<SkillCategory>> getCached() {
        FileInputStream is = null;
        try {
            final File file = getCachedDiscoveryFile();
            if (!file.exists() || file.length() == 0) throw new FileNotFoundException();
            is = new FileInputStream(file);
            final List<SkillCategory> data = LoganSquare.parseList(is, SkillCategory.class);
            if (data != null) return TaskResponse.getInstance(data);
        } catch (IOException ignored) {
        } finally {
            Utils.closeSilently(is);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private File getCachedDiscoveryFile() throws IOException {
        return new File(getContext().getCacheDir(), String.format("skill_categories_cache_%s.json",
                URLEncoder.encode(mAccount.name, "UTF-8")));
    }


}
