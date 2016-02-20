package catchla.yep.model;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mariotaku on 15/7/2.
 */
public class ContactUpload {

    private ArrayList<ContactItem> items = new ArrayList<>();

    public void add(String name, String number) {
        items.add(new ContactItem(name, number));
    }


    @Override
    public String toString() {
        try {
            return LoganSquare.serialize(items, ContactItem.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonObject
    public static class ContactItem {
        @JsonField(name = "name")
        String name;
        @JsonField(name = "number")
        String number;

        public ContactItem(final String name, final String number) {
            this.name = name;
            this.number = number;
        }

        public ContactItem() {
        }
    }

}
