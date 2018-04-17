package mad.lab1.madFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mad.lab1.R;

public class ShowSelelctedBookInfoDialogFragment extends DialogFragment {


    private String value;

    //TODO: CHANGE STRING TO THE BOOK ITEM TO BE DISPLAYED
    //Static method used to get an instance of this fragment
    public static ShowSelelctedBookInfoDialogFragment newInstance(String value){
        ShowSelelctedBookInfoDialogFragment fragment = new ShowSelelctedBookInfoDialogFragment();
        Bundle b = new Bundle();
        b.putString("book", value);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.book_info_dialog_layout, container, false);
        TextView text = v.findViewById(R.id.bookInfoDialogTextView);
        text.setText(value);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        value = getArguments().getString("book");
    }
}
