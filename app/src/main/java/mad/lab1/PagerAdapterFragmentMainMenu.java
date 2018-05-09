package mad.lab1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mad.lab1.Fragments.AllBooksFragment;
import mad.lab1.Fragments.BorrowedBooksFragment;
import mad.lab1.Fragments.ChatListFragment;
import mad.lab1.Fragments.MyLibraryFragment;

public class PagerAdapterFragmentMainMenu extends FragmentPagerAdapter {


    //Constructor, it needs to pass a fragment manager to the superclass
    public PagerAdapterFragmentMainMenu(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    //TODO: to be modified if more fragments are needed
    public static final int PAGE_NUM = 4;

    //This method will return a fragment. It is call by the ViewPager when it needs to change page
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return AllBooksFragment.newInstance(0, "All Books");
            case 1:
                return MyLibraryFragment.newInstance(1, "My Library");
            case 2:
                return BorrowedBooksFragment.newInstance(2, "Borrowed Books");
            case 3:
                return ChatListFragment.newInstance(2, "Chat List");

        }

        return null;
    }



    @Override
    public int getCount() {
        return PAGE_NUM;
    }
}
