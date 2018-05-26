package mad.lab1;

import mad.lab1.Database.Book;
import mad.lab1.Database.BookIdInfo;
import mad.lab1.Database.UserInfo;

public class BookUser {

    private BookIdInfo book;
    private UserInfo user;

    public BookUser (UserInfo u, BookIdInfo b){
        this.user = u;
        this.book = b;
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
}
