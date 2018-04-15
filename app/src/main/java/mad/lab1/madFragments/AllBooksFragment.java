package mad.lab1.madFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import mad.lab1.R;

public class AllBooksFragment extends Fragment {

    FloatingActionButton fab;
    ListView lsv_Books;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflating the layout for the fragment
        View v = inflater.inflate(R.layout.all_books_fragment_layout, container, false);

        lsv_Books = v.findViewById(R.id.allBookListView);
        fab = v.findViewById(R.id.addBookToShareActionButton);

        fab.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        IntentIntegrator.forSupportFragment(AllBooksFragment.this)
                                .setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
                                .setOrientationLocked(false)
                                .setBeepEnabled(false)
                                .initiateScan();
                    }
                }
            );

        //TODO: CREATE METHODS A ACTIONS FOR THIS FRAGMENT

        //Returning the view to viewPager
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String barcode = result.getContents();
        //TODO: Get book info and upload to firebase / fill in the listview
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
