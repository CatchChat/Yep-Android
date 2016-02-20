package catchla.yep.model;

import java.util.ArrayList;

/**
 * Created by mariotaku on 15/5/27.
 */
public class ResponseList<T> extends ArrayList<T> {

    int currentPage;
    int perPage;
    int count;

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getCount() {
        return count;
    }

    void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    void setPerPage(final int perPage) {
        this.perPage = perPage;
    }

    void setCount(final int count) {
        this.count = count;
    }
}
