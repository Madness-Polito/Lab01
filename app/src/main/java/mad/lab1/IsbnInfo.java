package mad.lab1;

import android.os.Parcel;
import android.os.Parcelable;

// represents the book isbn info stored in firebase
public class IsbnInfo implements Parcelable{

    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String pubYear;
    private String description;
    private String thumbURL;

    IsbnInfo(){}

    public IsbnInfo(String isbn, String title, String author, String publisher, String pubYear, String description, String encodedThumbnail) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.pubYear = pubYear;
        this.description = description;
        this.thumbURL = encodedThumbnail;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    // In constructor you will read the variables from Parcel. Make sure to read them in the same sequence in which you have written them in Parcel.
    private IsbnInfo(Parcel in) {
        isbn   = in.readString();
        title = in.readString();
        author  = in.readString();
        publisher = in.readString();
        pubYear = in.readString();
        description  = in.readString();
        thumbURL = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(isbn);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(publisher);
        dest.writeString(pubYear);
        dest.writeString(description);
        dest.writeString(thumbURL);
    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<IsbnInfo> CREATOR = new Parcelable.Creator<IsbnInfo>(){
        public IsbnInfo createFromParcel(Parcel in) {
            return new IsbnInfo(in);
        }

        public IsbnInfo[] newArray(int size) {
            return new IsbnInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
