package mad.lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by Matteo on 17/04/2018.
 */

public class Book {
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

    public Bitmap getDecodedThumbnail() {
        return decodeToBitmap(encodedThumbnail);
    }

    private Bitmap decodeToBitmap(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
