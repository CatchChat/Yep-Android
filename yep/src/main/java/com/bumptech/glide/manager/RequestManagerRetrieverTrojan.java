package com.bumptech.glide.manager;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.bumptech.glide.RequestManager;

/**
 * Created by mariotaku on 15/9/19.
 */
public class RequestManagerRetrieverTrojan {

    public static RequestManager supportFragmentGet(final Context context, final FragmentManager fm) {
        RequestManagerRetriever retriever = RequestManagerRetriever.get();
        return retriever.supportFragmentGet(context, fm);
    }

}
