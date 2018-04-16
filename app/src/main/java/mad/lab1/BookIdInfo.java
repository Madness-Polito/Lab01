package mad.lab1;

import android.os.Parcel;
import android.os.Parcelable;

// represents the book info stored in firebase
public class BookIdInfo implements Parcelable{


    private String uid;
    private String isbn;
    private String title;
    private String author;
    private String status;
    private String condition;
    private String publisher;
    private String pubYear;


    BookIdInfo(){}

    public BookIdInfo(String uid, String isbn, String title, String author, String status, String condition, String publisher, String pubYear) {
        this.uid = uid;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.status = status;
        this.condition = condition;
        this.publisher = publisher;
        this.pubYear = pubYear;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    // In constructor you will read the variables from Parcel. Make sure to read them in the same sequence in which you have written them in Parcel.
    private BookIdInfo(Parcel in) {

        uid  = in.readString();
        isbn = in.readString();
        title  = in.readString();
        author = in.readString();
        status  = in.readString();
        condition   = in.readString();
        publisher   = in.readString();
        pubYear = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(isbn);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(status);
        dest.writeString(condition);
        dest.writeString(publisher);
        dest.writeString(pubYear);
    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<BookIdInfo> CREATOR = new Parcelable.Creator<BookIdInfo>(){
        public BookIdInfo createFromParcel(Parcel in) {
            return new BookIdInfo(in);
        }

        public BookIdInfo[] newArray(int size) {
            return new BookIdInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
