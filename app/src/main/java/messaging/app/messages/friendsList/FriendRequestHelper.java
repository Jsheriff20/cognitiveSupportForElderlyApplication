package messaging.app.messages.friendsList;

public class FriendRequestHelper {
    private String username;
    private String relationship;

    public FriendRequestHelper(String username, String relationship) {
        this.username = username;
        this.relationship = relationship;
    }

    public FriendRequestHelper() {
    }

    public String getUsername() {
        return username;
    }

    public String getRelationship() {
        return relationship;
    }
}
