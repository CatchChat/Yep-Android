package catchla.yep.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.SecureRandom;

import catchla.yep.R;

/**
 * Created by mariotaku on 16/1/6.
 */
public class TestPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    private TextView mTextView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTextView = (TextView) view.findViewById(R.id.text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_page, container, false);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MyLoader(getContext(), args.getInt("position"));
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        mTextView.setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public static class MyLoader extends AsyncTaskLoader<String> {
        private int position;

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        public MyLoader(Context context, int position) {
            super(context);
            this.position = position;
        }

        @Override
        public String loadInBackground() {
//            SecureRandom random = new SecureRandom();
//            byte[] buf = new byte[1024];
//            for (int i = 0; i < 300; i++) {
//                random.nextBytes(buf);
//            }
            return String.valueOf(position);
        }
    }
}
