package mad.lab1.Database;

import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {


    private String chatID;          //This id has to be populated using getKey() on the datasnapshot
    private String otherUser;
    private int newMexNumber;
    private String idLastMex;

    //Empty constructor to let firebase serialize this clase
    public Chat(){
        this("", 0, "");
    }

    public Chat(Parcel in){
        this(in.readString(), in.readInt(), in.readString());
    }


    public Chat(String otherUser, int newMexNumber, String idLastMex){
        //this.chatID = chatID;
        this.otherUser = otherUser;
        this.newMexNumber = newMexNumber;
        this.idLastMex = idLastMex;
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

    public int getNewMexNumber(){return this.newMexNumber;}

    public void setNewMexNumber( int newMexNumber){
        this.newMexNumber = newMexNumber;
    }

    public String getIdLastMex(){return this.idLastMex;}

    public void setIdLastMex(String idLastMex){
        this.idLastMex = idLastMex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(this.chatID);
        dest.writeString(this.otherUser);
        dest.writeInt(newMexNumber);
        dest.writeString(idLastMex);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Chat createFromParcel(Parcel in){
            return new Chat(in);
        }

        public Chat[] newArray(int size){
            return new Chat[size];
        }
    };

}
