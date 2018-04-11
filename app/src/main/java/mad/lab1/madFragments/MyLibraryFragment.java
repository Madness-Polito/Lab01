package mad.lab1.madFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mad.lab1.R;

public class MyLibraryFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_library_fragment_layout, container, false);

        return v;
    }

    //Used by adapter to get an instance of this type of fragment. Possible to pass arguments via a bundle
    public static MyLibraryFragment newInstance(int page, String title){
        MyLibraryFragment fragment = new MyLibraryFragment();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putInt("page", page);
        fragment.setArguments(arg);
        return fragment;
    }
}
