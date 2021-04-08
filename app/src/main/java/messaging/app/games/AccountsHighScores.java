package messaging.app.games;

public class AccountsHighScores {
    long mButtonChangeHighScore;
    long mGridReactionHighScore;
    long mPairsHighScore;
    long mPatternHighScore;
    long mStoopTestHighScore;

    String mFullName;
    String mProfileImageURL = null;
    String mProfileImageRotation = null;
    String mUsersUUID;

    public AccountsHighScores() {
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmProfileImageURL() {
        return mProfileImageURL;
    }

    public void setmProfileImageURL(String mProfileImageURL) {
        this.mProfileImageURL = mProfileImageURL;
    }

    public String getmProfileImageRotation() {
        return mProfileImageRotation;
    }

    public void setmProfileImageRotation(String mProfileImageRotation) {
        this.mProfileImageRotation = mProfileImageRotation;
    }

    public long getmButtonChangeHighScore() {
        return mButtonChangeHighScore;
    }

    public void setmButtonChangeHighScore(long mButtonChangeHighScore) {
        this.mButtonChangeHighScore = mButtonChangeHighScore;
    }

    public long getmGridReactionHighScore() {
        return mGridReactionHighScore;
    }

    public void setmGridReactionHighScore(long mGridReactionHighScore) {
        this.mGridReactionHighScore = mGridReactionHighScore;
    }

    public long getmPairsHighScore() {
        return mPairsHighScore;
    }

    public void setmPairsHighScore(long mPairsHighScore) {
        this.mPairsHighScore = mPairsHighScore;
    }

    public long getmPatternHighScore() {
        return mPatternHighScore;
    }

    public void setmPatternHighScore(long mPatternHighScore) {
        this.mPatternHighScore = mPatternHighScore;
    }

    public long getmStoopTestHighScore() {
        return mStoopTestHighScore;
    }

    public void setmStoopTestHighScore(long mStoopTestHighScore) {
        this.mStoopTestHighScore = mStoopTestHighScore;
    }

    public String getmUsersUUID() {
        return mUsersUUID;
    }

    public void setmUsersUUID(String mUsersUUID) {
        this.mUsersUUID = mUsersUUID;
    }
}
