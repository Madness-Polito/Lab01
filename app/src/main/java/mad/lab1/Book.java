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
    private String encodedThumbnail;

    public Book(String bookId, String isbn, String title, String author, String status, String condition, String publisher, String pubYear, String encodedThumbnail) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
        this.condition = condition;
        this.publisher = publisher;
        this.pubYear = pubYear;
        this.encodedThumbnail = encodedThumbnail;
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
        this.encodedThumbnail = in.readString();
    }
    public Book(){
        this("", "", "", "", "", "", "", "", "");
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

    public void setEncodedThumbnail(String encodedThumbnail){
        this.encodedThumbnail = encodedThumbnail;
    }

    public Bitmap getDecodedThumbnail() {
        if(encodedThumbnail != null) {
            return decodeToBitmap(encodedThumbnail);
        }else{
            return null;
        }
    }

    private Bitmap decodeToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
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
        dest.writeString(this.encodedThumbnail);
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
