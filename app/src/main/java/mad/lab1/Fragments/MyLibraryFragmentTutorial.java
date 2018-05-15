package mad.lab1.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.amlcurran.showcaseview.ShowcaseView;

import mad.lab1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyLibraryFragmentTutorial extends Fragment {

    private FloatingActionButton fab;

    public MyLibraryFragmentTutorial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_library_fragment_layout, container, false);

        fab = view.findViewById(R.id.addBookToShareActionButton);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        new ShowcaseView.Builder(getActivity())
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(new ViewTarget(fab))
                .setContentTitle("add a new book")
                .setContentText("press this button to add a new book on your library")
                /*
                .setShowcaseEventListener(new SimpleShowcaseEventListener(){
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id., new MyLibraryFragment()) // groupID to be removed , call MyLibraryFragmentTutorial
                                .commit();

                    }

                })
                */
                .build();
    }

}
