package mad.lab1.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import mad.lab1.User.Authentication;

public class ChatMessage implements Parcelable{

    private String text;
    private String user;
    private String uid;
    private long time;

    public ChatMessage(String text, String user) {
        this.text = text;
        this.user = user;
        this.uid  = Authentication.getCurrentUser().getUid();

        // Initialize to current time
        time = new Date().getTime();
    }

    public ChatMessage(String text, String user, String uid) {
        this.text = text;
        this.user = user;
        this.uid  = uid;

        // Initialize to current time
        time = new Date().getTime();
    }

    public ChatMessage(){

    }

    protected ChatMessage(Parcel in) {
        text = in.readString();
        user = in.readString();
        time = in.readLong();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String messageText) {
        this.text = messageText;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String messageUser) {
        this.user = messageUser;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long messageTime) {
        this.time = messageTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(user);
        dest.writeLong(time);
    }
}