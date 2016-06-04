package catchla.yep.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore.Images
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import catchla.yep.R
import catchla.yep.adapter.BaseRecyclerViewAdapter

/**
 * Created by mariotaku on 16/3/14.
 */
class ChatMediaBottomSheetDialogFragment : BottomSheetDialogFragment(), LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private var mGalleryAdapter: GalleryAdapter? = null
    private var mMediaGallery: RecyclerView? = null
    private var mGalleryButton: View? = null
    private var mLocationButton: View? = null
    private var mCancelButton: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.dialog_bottom_sheet_chat_media, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMediaGallery = view!!.findViewById(R.id.media_gallery) as RecyclerView
        mGalleryButton = view.findViewById(R.id.gallery)
        mLocationButton = view.findViewById(R.id.location)
        mCancelButton = view.findViewById(R.id.cancel)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context = context
        mGalleryAdapter = GalleryAdapter(this, context)
        val layout = LinearLayoutManager(context)
        layout.orientation = LinearLayoutManager.HORIZONTAL
        mMediaGallery!!.layoutManager = layout
        mMediaGallery!!.adapter = mGalleryAdapter

        mGalleryButton!!.setOnClickListener(this)
        mLocationButton!!.setOnClickListener(this)
        mCancelButton!!.setOnClickListener { dismiss() }

        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permissions, REQUEST_REQUEST_STORAGE_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_REQUEST_STORAGE_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loaderManager.initLoader(0, null, this)
                } else {
                    // TODO show error
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val uri = Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = Images.Media.DATE_ADDED + " DESC"
        val projection = arrayOf(Images.Media._ID, Images.Media.DATA)
        return CursorLoader(context, uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        mGalleryAdapter!!.setCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mGalleryAdapter!!.setCursor(null)
    }

    override fun onClick(v: View) {
        notifyButtonClicked(v.id)
    }

    private fun notifyButtonClicked(id: Int) {
        val callback = callback
        callback?.onButtonClick(id)
        dismiss()
    }

    private fun notifyCameraClick() {
        val callback = callback
        callback?.onCameraClick()
        dismiss()
    }

    private fun notifyMediaClicked(id: Long, data: String) {
        val callback = callback
        callback?.onMediaClick(id, data)
        dismiss()
    }

    val callback: Callback?
        get() {
            val tf = targetFragment
            if (tf is Callback) return tf
            val pf = parentFragment
            if (pf is Callback) return pf
            val host = host
            if (host is Callback) return host
            return null
        }

    interface Callback {
        fun onButtonClick(id: Int)

        fun onCameraClick()

        fun onMediaClick(id: Long, data: String)
    }

    internal class GalleryAdapter(private val mFragment: ChatMediaBottomSheetDialogFragment, context: Context) : BaseRecyclerViewAdapter<RecyclerView.ViewHolder>(context) {
        private val mInflater: LayoutInflater
        private var mCursor: Cursor? = null

        init {
            mInflater = LayoutInflater.from(context)
        }

        fun setCursor(cursor: Cursor?) {
            mCursor = cursor
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                VIEW_TYPE_CAMERA_ENTRY -> {
                    return CameraEntryViewHolder(this, mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false))
                }
                VIEW_TYPE_MEDIA_ITEM -> {
                    return GalleryViewHolder(this, mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false))
                }
            }
            throw UnsupportedOperationException()
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0) return VIEW_TYPE_CAMERA_ENTRY
            return VIEW_TYPE_MEDIA_ITEM
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                VIEW_TYPE_CAMERA_ENTRY -> {
                }
                VIEW_TYPE_MEDIA_ITEM -> {
                    mCursor!!.moveToPosition(position - 1)
                    (holder as GalleryViewHolder).display(mCursor!!.getLong(0))
                }
            }
        }

        override fun getItemCount(): Int {
            if (mCursor == null) return 1
            return mCursor!!.count + 1
        }

        fun notifyCameraClick() {
            mFragment.notifyCameraClick()
        }

        fun notifyGalleryItemClick(position: Int) {
            if (mCursor == null) return
            if (mCursor!!.moveToPosition(position - 1)) {
                mFragment.notifyMediaClicked(mCursor!!.getLong(0), mCursor!!.getString(1))
            }
        }

        companion object {

            private val VIEW_TYPE_CAMERA_ENTRY = 1
            private val VIEW_TYPE_MEDIA_ITEM = 2
        }
    }

    internal class GalleryViewHolder(private val adapter: GalleryAdapter, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageView: ImageView

        init {
            itemView.setOnClickListener(this)
            itemView.findViewById(R.id.media_remove).visibility = View.GONE
            imageView = itemView.findViewById(R.id.media_preview) as ImageView
        }

        fun display(id: Long) {
            adapter.imageLoader.displayImage("media-thumb://" + id, imageView)
        }


        override fun onClick(v: View) {
            adapter.notifyGalleryItemClick(layoutPosition)
        }
    }

    internal class CameraEntryViewHolder(
            private val adapter: GalleryAdapter,
            itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imageView: ImageView

        init {
            itemView.setOnClickListener(this)
            itemView.findViewById(R.id.media_remove).visibility = View.GONE
            imageView = itemView.findViewById(R.id.media_preview) as ImageView
            imageView.setImageResource(R.drawable.ic_pick_source_camera)
        }

        fun display(id: Long) {
            adapter.imageLoader.displayImage("media-thumb://" + id, imageView)
        }

        override fun onClick(v: View) {
            adapter.notifyCameraClick()
        }
    }

    companion object {
        private val REQUEST_REQUEST_STORAGE_PERMISSION = 101
    }
}
