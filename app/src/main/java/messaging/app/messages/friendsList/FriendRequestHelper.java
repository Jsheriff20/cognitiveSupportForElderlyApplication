package messaging.app.messages.friendsList;

public class FriendRequestHelper {
    private String mUsername;
    private String mRelationship;

    public FriendRequestHelper(String username, String relationship) {
        this.mUsername = username;
        this.mRelationship = relationship;
    }

    public FriendRequestHelper() {
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmRelationship() {
        return mRelationship;
    }
}
