package mad.lab1;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainPageMenu extends AppCompatActivity {


    private PagerAdapterFragmentMainMenu adapter;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_menu);

        initialization();                                           //Initialization and getting views references

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 3){
                    tabLayout.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                }
                else{
                    if(!getSupportActionBar().isShowing()){
                        getSupportActionBar().show();
                    }
                    if(tabLayout.getVisibility() == View.GONE){
                        tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



    private void initialization(){
        viewPager = findViewById(R.id.view_pager_main_menu);
        fragmentManager = getSupportFragmentManager();              //Used to handle fragments
        tabLayout = findViewById(R.id.tab_layout_main_menu);
        adapter = new PagerAdapterFragmentMainMenu(fragmentManager);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);                    //Linking tablayout with viewpager, animated bar
        setIcons();

    }

    private void setIcons(){
        TabLayout.Tab tabCall = tabLayout.getTabAt(0);
        tabCall.setIcon(R.drawable.all_books_tab_icon);

        tabCall = tabLayout.getTabAt(1);
        tabCall.setIcon(R.drawable.my_library_tab_icon);

        tabCall = tabLayout.getTabAt(2);
        tabCall.setIcon(R.drawable.borrowed_books_tab_icon);

        tabCall = tabLayout.getTabAt(3);
        tabCall.setIcon(R.drawable.profile_tab_icon);

    }
}
