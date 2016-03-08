package catchla.yep.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * Created by mariotaku on 16/3/8.
 */
@JsonObject
public class ConversationsResponse {
    @JsonField(name = "messages")
    List<Message> messages;
    @JsonField(name = "users")
    List<User> users;
    @JsonField(name = "circles")
    List<Circle> circles;


    @JsonField(name = "current_page")
    int currentPage;
    @JsonField(name = "per_page")
    int perPage;
    @JsonField(name = "count")
    int count;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = users;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(final List<Circle> circles) {
        this.circles = circles;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(final int perPage) {
        this.perPage = perPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }
}
