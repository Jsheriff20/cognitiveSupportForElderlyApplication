package messaging.app.games;

public class AccountsHighScores {
    long buttonChangeHighScore;
    long gridReactionHighScore;
    long pairsHighScore;
    long patternHighScore;
    long stoopTestHighScore;

    String fullName;
    String profileImageURL = null;
    String profileImageRotation = null;
    String usersUUID;

    public AccountsHighScores() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getProfileImageRotation() {
        return profileImageRotation;
    }

    public void setProfileImageRotation(String profileImageRotation) {
        this.profileImageRotation = profileImageRotation;
    }

    public long getButtonChangeHighScore() {
        return buttonChangeHighScore;
    }

    public void setButtonChangeHighScore(long buttonChangeHighScore) {
        this.buttonChangeHighScore = buttonChangeHighScore;
    }

    public long getGridReactionHighScore() {
        return gridReactionHighScore;
    }

    public void setGridReactionHighScore(long gridReactionHighScore) {
        this.gridReactionHighScore = gridReactionHighScore;
    }

    public long getPairsHighScore() {
        return pairsHighScore;
    }

    public void setPairsHighScore(long pairsHighScore) {
        this.pairsHighScore = pairsHighScore;
    }

    public long getPatternHighScore() {
        return patternHighScore;
    }

    public void setPatternHighScore(long patternHighScore) {
        this.patternHighScore = patternHighScore;
    }

    public long getStoopTestHighScore() {
        return stoopTestHighScore;
    }

    public void setStoopTestHighScore(long stoopTestHighScore) {
        this.stoopTestHighScore = stoopTestHighScore;
    }

    public String getUsersUUID() {
        return usersUUID;
    }

    public void setUsersUUID(String usersUUID) {
        this.usersUUID = usersUUID;
    }
}
