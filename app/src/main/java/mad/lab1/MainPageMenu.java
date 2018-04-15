package mad.lab1;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class MainPageMenu extends AppCompatActivity {


    private PagerAdapterFragmentMainMenu adapter;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private Toolbar toolbar;


    @Override
    protected void onStart() {
        super.onStart();
        loginCheck();                                               //Checking if user logged in or not

    }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Authentication.RC_SIGN_IN && resultCode == RESULT_CANCELED){
            //If user pressed back button, closing app
            finish();
        }else if(requestCode == Authentication.RC_SIGN_IN && resultCode == RESULT_OK){
            //User logged in, to be used in case of profile related info in the main page
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile_toolbar_button:
                Intent intent = new Intent(getApplicationContext(), ShowProfile.class);
                startActivity(intent);
                break;
            case R.id.logout_toolbar_button:
                //TODO: CREATE LOGOUT SYSTEM
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void initialization(){
        viewPager = findViewById(R.id.view_pager_main_menu);
        fragmentManager = getSupportFragmentManager();              //Used to handle fragments
        tabLayout = findViewById(R.id.tab_layout_main_menu);
        adapter = new PagerAdapterFragmentMainMenu(fragmentManager);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);                    //Linking tablayout with viewpager, animated bar
        setIcons();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void setIcons(){
        TabLayout.Tab tabCall = tabLayout.getTabAt(0);
        tabCall.setIcon(R.drawable.all_books_tab_icon);

        tabCall = tabLayout.getTabAt(1);
        tabCall.setIcon(R.drawable.my_library_tab_icon);

        tabCall = tabLayout.getTabAt(2);
        tabCall.setIcon(R.drawable.borrowed_books_tab_icon);

    }

    private void loginCheck(){

        if (!Authentication.checkSession()) {
            //User not logged In
            Authentication.signIn(this);
        }
    }
}
