package messaging.app.messages.ViewingMessages;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageData implements Parcelable {
    String timeStamp;
    String fileExtension = null;
    int mediaMessageRotation;
    int deviceOrientationMode;
    String mediaMessageUrl;
    String textMessage = "";
    String fullName;
    boolean unopened;

    public MessageData() {
    }

    public MessageData(String timeStamp, String fileExtension, int mediaMessageRotation, int deviceOrientationMode, String mediaMessageUrl, String textMessage, String fullName, boolean unopened) {
        this.timeStamp = timeStamp;
        this.fileExtension = fileExtension;
        this.mediaMessageRotation = mediaMessageRotation;
        this.deviceOrientationMode = deviceOrientationMode;
        this.mediaMessageUrl = mediaMessageUrl;
        if(textMessage.length() <= 0){
            this.textMessage = "";
        }
        else{
            this.textMessage = textMessage;
        }
        this.unopened = unopened;
        this.fullName = fullName;
    }

    protected MessageData(Parcel in) {
        timeStamp = in.readString();
        fileExtension = in.readString();
        mediaMessageRotation = in.readInt();
        deviceOrientationMode = in.readInt();
        mediaMessageUrl = in.readString();
        textMessage = in.readString();
        fullName = in.readString();
        unopened = in.readByte() != 0;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getMediaMessageRotation() {
        return mediaMessageRotation;
    }

    public void setMediaMessageRotation(int mediaMessageRotation) {
        this.mediaMessageRotation = mediaMessageRotation;
    }

    public int getDeviceOrientationMode() {
        return deviceOrientationMode;
    }

    public void setDeviceOrientationMode(int deviceOrientationMode) {
        this.deviceOrientationMode = deviceOrientationMode;
    }

    public String getMediaMessageUrl() {
        return mediaMessageUrl;
    }

    public void setMediaMessageUrl(String mediaMessageUrl) {
        this.mediaMessageUrl = mediaMessageUrl;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public boolean isUnopened() {
        return unopened;
    }

    public void setUnopened(boolean unopened) {
        this.unopened = unopened;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(timeStamp);
        dest.writeString(fileExtension);
        dest.writeInt(mediaMessageRotation);
        dest.writeInt(deviceOrientationMode);
        dest.writeString(mediaMessageUrl);
        dest.writeString(textMessage);
        dest.writeString(fullName);
        dest.writeByte((byte) (unopened ? 1 : 0));
    }
}
