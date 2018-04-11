package mad.lab1.madFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mad.lab1.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO: TRANSFER PROFILE ACTIVITY ON A FRAGMENT

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Used by adapter to get an instance of this type of fragment. Possible to pass arguments via a bundle
    public static ProfileFragment newInstance(int page, String title){
        ProfileFragment fragment = new ProfileFragment();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putInt("page", page);
        fragment.setArguments(arg);
        return fragment;
    }
}
