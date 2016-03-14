package catchla.yep.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import catchla.yep.R;

/**
 * Created by mariotaku on 16/3/14.
 */
public class ChatMediaBottomSheetDialogFragment extends BottomSheetDialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_REQUEST_STORAGE_PERMISSION = 101;
    private GalleryAdapter mGalleryAdapter;
    private RecyclerView mMediaGallery;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getContext();
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.dialog_bottom_sheet_chat_media);
        mGalleryAdapter = new GalleryAdapter(context);
        mMediaGallery = (RecyclerView) dialog.findViewById(R.id.media_gallery);
        assert mMediaGallery != null;
        mMediaGallery.setLayoutManager(new LinearLayoutManager(context));
        mMediaGallery.setAdapter(mGalleryAdapter);
        final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(permissions, REQUEST_REQUEST_STORAGE_PERMISSION);
        return dialog;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_REQUEST_STORAGE_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(0, null, this);
                } else {
                    // TODO show error
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        final Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
        final String sortOrder = Images.Media.DATE_ADDED + " DESC";
        final String[] projection = {Images.Media.DATA};
        return new CursorLoader(getContext(), uri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        mGalleryAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        mGalleryAdapter.setCursor(null);
    }

    class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

        private final LayoutInflater mInflater;
        private Cursor mCursor;

        public GalleryAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public void setCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public GalleryViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new GalleryViewHolder(mInflater.inflate(R.layout.adapter_item_topic_media_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final GalleryViewHolder holder, final int position) {
            mCursor.moveToPosition(position);
            holder.display(mCursor.getString(0));
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }
    }

    class GalleryViewHolder extends RecyclerView.ViewHolder {

        private final View imageView;

        public GalleryViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.media_preview);
        }

        public void display(final String string) {

        }
    }
}
