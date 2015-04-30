/*
 * Copyright (c) 2015. Catch Inc,
 */

package catchla.yep.adapter.iface;

/**
 * Created by mariotaku on 15/4/16.
 */
public interface ILoadMoreSupportAdapter {
    int ITEM_VIEW_TYPE_LOAD_INDICATOR = 0;

    boolean isLoadMoreIndicatorVisible();

    void setLoadMoreIndicatorVisible(boolean enabled);

    boolean isLoadMoreSupported();

    void setLoadMoreSupported(boolean supported);
}
