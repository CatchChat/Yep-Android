package com.nostra13.universalimageloader.core;

/**
 * Created by mariotaku on 15/12/14.
 */
public class DisplayImageOptionsAccessor {
    public static void setSyncLoading(final DisplayImageOptions.Builder builder, boolean isSyncLoading) {
        builder.syncLoading(isSyncLoading);
    }
}
