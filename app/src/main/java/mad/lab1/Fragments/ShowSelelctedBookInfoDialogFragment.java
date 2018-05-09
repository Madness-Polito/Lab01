package mad.lab1.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mad.lab1.Database.Book;
import mad.lab1.R;

public class ShowSelelctedBookInfoDialogFragment extends DialogFragment {


    private Book book;

    //Static method used to get an instance of this fragment
    public static ShowSelelctedBookInfoDialogFragment newInstance(Book b){
        ShowSelelctedBookInfoDialogFragment fragment = new ShowSelelctedBookInfoDialogFragment();
        Bundle argument = new Bundle();
        argument.putParcelable("book", b);
        fragment.setArguments(argument);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.book_info_dialog_layout, container, false);
        TextView text = v.findViewById(R.id.showBookInfoTitle);

        //TODO:POPULATE THE VIEW
        text.setText(book.getTitle());

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        book = getArguments().getParcelable("book");
    }
}
