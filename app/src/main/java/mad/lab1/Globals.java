package mad.lab1;

import android.app.Application;
import android.graphics.Bitmap;

public class Globals extends Application {

    private boolean profileSet;
    private String name;
    private String mail;
    private String bio;
    private Bitmap bmp;
    private String phone;
    private String dateOfBirth;

    public void setDateOfBirth(String birthDate) { this.dateOfBirth = birthDate; }

    public String getDateOfBirth(){ return this.dateOfBirth; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPhone(){ return this.phone; }

    public boolean isProfileSet() {
        return profileSet;
    }

    public void setProfileSet(boolean profileSet) {
        this.profileSet = profileSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
