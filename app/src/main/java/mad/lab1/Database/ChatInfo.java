package mad.lab1.Database;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatInfo implements Parcelable {


    private String chatID;          //This id has to be populated using getKey() on the datasnapshot
    private String otherUser;
    private int newMsgCount;
    //private String idLastMex;

    //Empty constructor to let firebase serialize this clase
    public ChatInfo(){
        this(0, "");
    }

    public ChatInfo(Parcel in){
        this(in.readInt(), in.readString());
    }


    public ChatInfo(int newMsgCount, String otherUser){
        //this.chatID = chatID;
        this.otherUser = otherUser;
        this.newMsgCount = newMsgCount;
        //this.idLastMex = idLastMex;
    }



    public String getChatID(){
        return this.chatID;
    }

    public void setChatID(String chatID){ this.chatID = chatID;}

    public String getOtherUser(){
        return this.otherUser;
    }

    public void setOtherUser(String otherUser){
        this.otherUser = otherUser;
    }

    public int getNewMsgCount(){return this.newMsgCount;}

    public void setNewMsgCount( int newMsgCount){
        this.newMsgCount = newMsgCount;
    }

    //public String getIdLastMex(){return this.idLastMex;}

    //public void setIdLastMex(String idLastMex){
        //this.idLastMex = idLastMex;
    //}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(this.chatID);
        dest.writeInt(this.newMsgCount);
        dest.writeString(this.otherUser);

        //dest.writeString(this.idLastMex);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public ChatInfo createFromParcel(Parcel in){
            return new ChatInfo(in);
        }

        public ChatInfo[] newArray(int size){
            return new ChatInfo[size];
        }
    };

}
