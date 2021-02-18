package messaging.app.messages.friendsList;

import java.util.Map;

public class friendRequestHelper {
    private String username;
    private String relationship;

    public friendRequestHelper(String username, String relationship) {
        this.username = username;
        this.relationship = relationship;
    }

    public friendRequestHelper() {
    }

    public String getUsername() {
        return username;
    }

    public String getRelationship() {
        return relationship;
    }
}
