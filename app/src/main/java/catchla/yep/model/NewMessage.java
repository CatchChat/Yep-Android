package catchla.yep.model;

import org.mariotaku.restfu.http.SimpleValueMap;

/**
 * Created by mariotaku on 15/6/12.
 */
public class NewMessage extends SimpleValueMap {

    public NewMessage recipientId(String recipientId) {
        put("recipient_id", recipientId);
        return this;
    }

    public NewMessage recipientType(String recipientType) {
        put("recipient_type", recipientType);
        return this;
    }

    public NewMessage mediaType(String mediaType) {
        put("media_type", mediaType);
        return this;
    }

    public NewMessage textContent(String textContent) {
        put("text_content", textContent);
        return this;
    }

}
