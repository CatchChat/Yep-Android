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
import catchla.yep.model.DiscoverQuery;
import catchla.yep.model.PagedUsers;
import catchla.yep.model.Paging;
import catchla.yep.model.TaskResponse;
import catchla.yep.model.User;
import catchla.yep.util.Utils;
import catchla.yep.util.YepAPI;
import catchla.yep.util.YepAPIFactory;
import catchla.yep.util.YepException;

/**
 * Created by mariotaku on 15/5/27.
 */
public class DiscoverLoader extends AsyncTaskLoader<TaskResponse<List<User>>> implements Constants {

    private final Account mAccount;
    private final DiscoverQuery mQuery;
    private final boolean mReadCache, mWriteCache;

    public DiscoverLoader(Context context, Account account, DiscoverQuery query, boolean readCache, boolean writeCache) {
        super(context);
        mAccount = account;
        mQuery = query;
        mReadCache = readCache;
        mWriteCache = writeCache;
    }

    @Override
    public TaskResponse<List<User>> loadInBackground() {
        if (mReadCache) {
            final TaskResponse<List<User>> data = getCached();
            if (data != null) return data;
        }
        final Context context = getContext();
        final YepAPI yep = YepAPIFactory.getInstance(context, mAccount);
        try {
            final Paging paging = new Paging();
            int page = 1;
            final List<User> list = new ArrayList<>();
            PagedUsers users;
            while ((users = yep.getDiscover(mQuery, paging)).size() > 0) {
                list.addAll(users);
                paging.page(++page);
                if (users.getCount() < users.getPerPage()) break;
            }
            if (mWriteCache) {
                saveCached(list);
            }
            return TaskResponse.getInstance(list);
        } catch (YepException e) {
            return TaskResponse.getInstance(e);
        }
    }

    private void saveCached(final List<User> list) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(getCachedDiscoveryFile());
            LoganSquare.serialize(list, os, User.class);
            os.flush();
        } catch (IOException e) {
            Log.w(LOGTAG, e);
        } finally {
            Utils.closeSilently(os);
        }
    }

    private TaskResponse<List<User>> getCached() {
        FileInputStream is = null;
        try {
            final File file = getCachedDiscoveryFile();
            if (!file.exists() || file.length() == 0) throw new FileNotFoundException();
            is = new FileInputStream(file);
            final List<User> data = LoganSquare.parseList(is, User.class);
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
        return new File(getContext().getCacheDir(), String.format("discovery_cache_%s.json",
                URLEncoder.encode(mAccount.name, "UTF-8")));
    }


}
