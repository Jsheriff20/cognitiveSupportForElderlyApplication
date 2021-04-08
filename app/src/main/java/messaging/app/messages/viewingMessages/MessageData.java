package messaging.app.messages.viewingMessages;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageData implements Parcelable {
    String mTimeStamp;
    String mFileExtension = null;
    int mMediaMessageRotation;
    int mDeviceOrientationMode;
    String mMediaMessageUrl;
    String mTextMessage = "";
    String mFullName;
    boolean mUnopened;

    public MessageData() {
    }

    public MessageData(String timeStamp, String fileExtension, int mediaMessageRotation,
                       int deviceOrientationMode, String mediaMessageUrl, String textMessage,
                       String fullName, boolean unopened) {
        this.mTimeStamp = timeStamp;
        this.mFileExtension = fileExtension;
        this.mMediaMessageRotation = mediaMessageRotation;
        this.mDeviceOrientationMode = deviceOrientationMode;
        this.mMediaMessageUrl = mediaMessageUrl;
        if (textMessage.length() <= 0) {
            this.mTextMessage = "";
        } else {
            this.mTextMessage = textMessage;
        }
        this.mUnopened = unopened;
        this.mFullName = fullName;
    }

    protected MessageData(Parcel in) {
        mTimeStamp = in.readString();
        mFileExtension = in.readString();
        mMediaMessageRotation = in.readInt();
        mDeviceOrientationMode = in.readInt();
        mMediaMessageUrl = in.readString();
        mTextMessage = in.readString();
        mFullName = in.readString();
        mUnopened = in.readByte() != 0;
    }

    public static final Creator<MessageData> CREATOR = new Creator<MessageData>() {
        @Override
        public MessageData createFromParcel(Parcel in) {
            return new MessageData(in);
        }

        @Override
        public MessageData[] newArray(int size) {
            return new MessageData[size];
        }
    };

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmFileExtension() {
        return mFileExtension;
    }

    public void setmFileExtension(String mFileExtension) {
        this.mFileExtension = mFileExtension;
    }

    public int getmMediaMessageRotation() {
        return mMediaMessageRotation;
    }

    public void setmMediaMessageRotation(int mMediaMessageRotation) {
        this.mMediaMessageRotation = mMediaMessageRotation;
    }

    public int getmDeviceOrientationMode() {
        return mDeviceOrientationMode;
    }

    public void setmDeviceOrientationMode(int mDeviceOrientationMode) {
        this.mDeviceOrientationMode = mDeviceOrientationMode;
    }

    public String getmMediaMessageUrl() {
        return mMediaMessageUrl;
    }

    public void setmMediaMessageUrl(String mMediaMessageUrl) {
        this.mMediaMessageUrl = mMediaMessageUrl;
    }

    public String getmTextMessage() {
        return mTextMessage;
    }

    public void setmTextMessage(String mTextMessage) {
        this.mTextMessage = mTextMessage;
    }

    public boolean ismUnopened() {
        return mUnopened;
    }

    public void setmUnopened(boolean mUnopened) {
        this.mUnopened = mUnopened;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTimeStamp);
        dest.writeString(mFileExtension);
        dest.writeInt(mMediaMessageRotation);
        dest.writeInt(mDeviceOrientationMode);
        dest.writeString(mMediaMessageUrl);
        dest.writeString(mTextMessage);
        dest.writeString(mFullName);
        dest.writeByte((byte) (mUnopened ? 1 : 0));
    }
}
