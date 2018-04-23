package mad.lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

/**
 * Created by Matteo on 17/04/2018.
 */

public class Book implements Parcelable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private String status;
    private String condition;
    private String publisher;
    private String pubYear;
    private String description;
    private String thumbURL;

    public Book(String bookId, String isbn, String title, String author, String status, String condition, String publisher, String pubYear, String description,String thumbURL) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
        this.condition = condition;
        this.publisher = publisher;
        this.pubYear = pubYear;
        this.description = description;
        this.thumbURL = thumbURL;
    }

    public Book(Parcel in){
        this.bookId = in.readString();
        this.isbn = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.status = in.readString();
        this.condition = in.readString();
        this.publisher = in.readString();
        this.pubYear = in.readString();
        this.description = in.readString();
        this.thumbURL = in.readString();
    }
    public Book(){
        this("", "", "", "", "", "", "", "", "", "");
    }

    public String getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public String getCondition() {
        return condition;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubYear() {
        return pubYear;
    }

    public String getDescription() {
        return description;
    }


    public void setThumbURL(String thumbURL){
        this.thumbURL = thumbURL;
    }

    public String getThumbURL() {
        return thumbURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookId);
        dest.writeString(this.isbn);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.status);
        dest.writeString(this.condition);
        dest.writeString(this.publisher);
        dest.writeString(this.pubYear);
        dest.writeString(this.description);
        dest.writeString(this.thumbURL);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Book createFromParcel(Parcel in){
            return new Book(in);
        }

        public Book[] newArray(int size){
            return new Book[size];
        }
    };
}
