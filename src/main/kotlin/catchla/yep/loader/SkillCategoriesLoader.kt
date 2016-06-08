package catchla.yep.loader

import android.accounts.Account
import android.content.Context
import android.support.v4.content.AsyncTaskLoader
import android.util.Log
import catchla.yep.Constants
import catchla.yep.model.Paging
import catchla.yep.model.SkillCategory
import catchla.yep.model.TaskResponse
import catchla.yep.model.YepException
import catchla.yep.util.Utils
import catchla.yep.util.YepAPIFactory
import com.bluelinelabs.logansquare.LoganSquare
import java.io.*
import java.net.URLEncoder
import java.util.*

/**
 * Created by mariotaku on 15/5/27.
 */
class SkillCategoriesLoader(context: Context, private val mAccount: Account, private val mReadCache: Boolean, private val mWriteCache: Boolean) : AsyncTaskLoader<TaskResponse<List<SkillCategory>>>(context), Constants {

    override fun loadInBackground(): TaskResponse<List<SkillCategory>> {
        if (mReadCache) {
            val data = cached
            if (data != null) return data
        }
        val context = context
        val yep = YepAPIFactory.getInstance(context, mAccount)
        try {
            val paging = Paging()
            val page = 1
            val list = ArrayList<SkillCategory>()
            val users = yep.getSkillCategories()
            list.addAll(users)
            if (mWriteCache) {
                saveCached(list)
            }
            return TaskResponse(list)
        } catch (e: YepException) {
            return TaskResponse(exception = e)
        }

    }

    private fun saveCached(list: List<SkillCategory>) {
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(cachedDiscoveryFile)
            LoganSquare.serialize(list, os, SkillCategory::class.java)
            os.flush()
        } catch (e: IOException) {
            Log.w(Constants.LOGTAG, e)
        } finally {
            Utils.closeSilently(os)
        }
    }

    private val cached: TaskResponse<List<SkillCategory>>?
        get() {
            var s: FileInputStream? = null
            try {
                val file = cachedDiscoveryFile
                if (!file.exists() || file.length() == 0L) throw FileNotFoundException()
                s = FileInputStream(file)
                val data = LoganSquare.parseList(s, SkillCategory::class.java)
                if (data != null) return TaskResponse(data)
            } catch (ignored: IOException) {
            } finally {
                Utils.closeSilently(s)
            }
            return null
        }

    override fun onStartLoading() {
        forceLoad()
    }

    private val cachedDiscoveryFile: File
        @Throws(IOException::class)
        get() = File(context.cacheDir, String.format("skill_categories_cache_%s.json",
                URLEncoder.encode(mAccount.name, "UTF-8")))


}
