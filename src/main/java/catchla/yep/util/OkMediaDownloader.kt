package catchla.yep.util

import catchla.yep.model.CacheMetadata
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.mariotaku.mediaviewer.library.CacheDownloadLoader
import org.mariotaku.mediaviewer.library.MediaDownloader
import java.io.IOException
import java.io.InputStream

/**
 * Created by mariotaku on 16/1/20.
 */
class OkMediaDownloader(private val client: OkHttpClient) : MediaDownloader {

    @Throws(IOException::class)
    override fun get(url: String, extra: Any?): CacheDownloadLoader.DownloadResult {
        val builder = Request.Builder()
        builder.url(url)
        val response = client.newCall(builder.build()).execute()
        if (!response.isSuccessful) {
            response.body().close()
            throw IOException("Unable to get " + url + ": " + response.code())
        }
        val body = response.body()
        val metadata = CacheMetadata()
        metadata.contentType = body.contentType().toString()
        return OkDownloadResult(body, metadata)
    }


    data class OkDownloadResult(
            private val body: ResponseBody,
            private val metadata: CacheMetadata?
    ) : CacheDownloadLoader.DownloadResult {

        @Throws(IOException::class)
        override fun close() {
            body.close()
        }

        @Throws(IOException::class)
        override fun getLength(): Long {
            return body.contentLength()
        }

        @Throws(IOException::class)
        override fun getStream(): InputStream {
            return body.byteStream()
        }

        override fun getExtra(): ByteArray? {
            if (metadata == null) return null
            val serialize = JsonSerializer.serialize(metadata, CacheMetadata::class.java) ?: return null
            return serialize.toByteArray()
        }

        protected fun finalize() {
            close()
        }
    }
}
