package mad.lab1;

import mad.lab1.Database.Book;
import mad.lab1.Database.BookIdInfo;
import mad.lab1.Database.UserInfo;

public class BookUser {

    private BookIdInfo book;
    private UserInfo user;
    private String bookID;

    public BookUser (UserInfo u, BookIdInfo b, String bookID){
        this.user = u;
        this.book = b;
        this.bookID = bookID;
    }

    public BookIdInfo getBook() {
        return book;
    }

    public void setBook(BookIdInfo book) {
        this.book = book;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
