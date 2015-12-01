package catchla.yep.model;

import android.support.v4.util.ArrayMap;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

import catchla.yep.util.JsonSerializer;

/**
 * Created by mariotaku on 15/7/2.
 */
public class ContactUpload extends ArrayMap<String, String> {

    private ArrayList<ContactItem> items = new ArrayList<>();

    public void add(String name, String number) {
        items.add(new ContactItem(name, number));
    }

    @Override
    public boolean has(final String s) {
        return "contacts".equals(s);
    }

    @Override
    public String get(final String s) {
        if (!"contacts".equals(s)) return null;
        return JsonSerializer.serialize(items, ContactItem.class);
    }

    @Override
    public String[] keys() {
        return new String[]{"contacts"};
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
