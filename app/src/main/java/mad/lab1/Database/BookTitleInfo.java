package mad.lab1.Database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matteo on 28/04/2018.
 */

public class BookTitleInfo implements Parcelable{

    private String title;
    private String isbn;

    public BookTitleInfo(String title, String isbn) {
        this.title = title;
        this.isbn = isbn;
    }

    public BookTitleInfo(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private BookTitleInfo(Parcel in) {
        title = in.readString();
        isbn   = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(isbn);

    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<BookTitleInfo> CREATOR = new Parcelable.Creator<BookTitleInfo>(){
        public BookTitleInfo createFromParcel(Parcel in) {
            return new BookTitleInfo(in);
        }

        public BookTitleInfo[] newArray(int size) {
            return new BookTitleInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
