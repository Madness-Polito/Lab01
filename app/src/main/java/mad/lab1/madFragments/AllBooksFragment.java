package mad.lab1.madFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mad.lab1.R;

public class AllBooksFragment extends Fragment {

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the fragment
        View v = inflater.inflate(R.layout.all_books_fragment_layout, container, false);

        //TODO: add onClickListener to add a book to share
        fab = v.findViewById(R.id.addBookToShareActionButton);

        //TODO: CREATE METHODS A ACTIONS FOR THIS FRAGMENT

        //Returning the view to viewPager
        return v;
    }


    //Used by adapter to get an instance of this type of fragment. Possible to pass arguments via a bundle
    public static AllBooksFragment newInstance(int page, String title){
        AllBooksFragment fragment = new AllBooksFragment();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putInt("page", page);
        fragment.setArguments(arg);
        return fragment;
    }

}
