package catchla.yep.model;


/**
 * Created by mariotaku on 15/6/3.
 */
public class DribbbleShotImage {

    private String resolution;
    private String url;

    public DribbbleShotImage(final String resolution, final String url) {
        this.resolution = resolution;
        this.url = url;
    }

    public DribbbleShotImage() {
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(final String resolution) {
        this.resolution = resolution;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
}
