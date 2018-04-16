package mad.lab1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

// represents the user info stored in firebase
public class UserInfo implements Parcelable{

    private String uid;
    private String name;
    private String mail;
    private String phone;
    private String city;
    private String dob;
    private String bio;

    UserInfo(){}

    UserInfo(String uid, String name, String mail, String phone){
        this.uid   = uid;
        this.name  = name;
        this.mail  = mail;
        this.phone = phone;
    }

    UserInfo(String uid, String name, String mail, String phone, String city, String dob, String bio){
        this.uid   = uid;
        this.name  = name;
        this.mail  = mail;
        this.phone = phone;
        this.city  = city;
        this.dob   = dob;
        this.bio   = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // In constructor you will read the variables from Parcel. Make sure to read them in the same sequence in which you have written them in Parcel.
    private UserInfo(Parcel in) {
        uid   = in.readString();
        name  = in.readString();
        mail  = in.readString();
        phone = in.readString();
        city  = in.readString();
        dob   = in.readString();
        bio   = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeString(phone);
        dest.writeString(city);
        dest.writeString(dob);
        dest.writeString(bio);
    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>(){
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}