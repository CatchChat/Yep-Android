package com.bumptech.glide.manager;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.bumptech.glide.RequestManager;

/**
 * Created by mariotaku on 15/9/19.
 */
public class GlideInternal {

    public RequestManager get(Context context, FragmentManager fm) {
        return RequestManagerRetrieverTrojan.supportFragmentGet(context, fm);
    }

}
