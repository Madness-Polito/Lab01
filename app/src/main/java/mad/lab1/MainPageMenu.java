package mad.lab1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

                if (position == 3) {
                    tabLayout.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                } else {
                    if (!getSupportActionBar().isShowing()) {
                        getSupportActionBar().show();
                    }
                    if (tabLayout.getVisibility() == View.GONE) {
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

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast;
        CharSequence text;

            if (requestCode == Authentication.RC_SIGN_IN && resultCode == RESULT_CANCELED) {
                //If user pressed back button, closing app
                finish();
            } else if (requestCode == Authentication.RC_SIGN_IN && resultCode == RESULT_OK) {
                //User logged in, to be used in case of profile related info in the main page
            }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.profile_toolbar_button:
                Intent intent = new Intent(getApplicationContext(), ShowProfile.class);
                startActivity(intent);
                break;
            case R.id.logout_toolbar_button:
                Authentication.signOut(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void initialization() {
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

    private void setIcons() {
        TabLayout.Tab tabCall = tabLayout.getTabAt(0);
        tabCall.setIcon(R.drawable.all_books_tab_icon);

        tabCall = tabLayout.getTabAt(1);
        tabCall.setIcon(R.drawable.my_library_tab_icon);

        tabCall = tabLayout.getTabAt(2);
        tabCall.setIcon(R.drawable.borrowed_books_tab_icon);

    }

    private void loginCheck() {

        // if user is logged in download his data
        if (Authentication.checkSession()){

            // no user data stored locally: download it
            if (!LocalDB.isProfileSaved(this)){

                UsersDB.getCurrentUser(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);

                        // user not yet created
                        if (userInfo == null) {

                            // get details of user from auth
                            FirebaseUser fbUser = Authentication.getCurrentUser();
                            userInfo = new UserInfo(fbUser.getUid(),
                                                    fbUser.getDisplayName(),
                                                    fbUser.getEmail(),
                                                    fbUser.getPhoneNumber());

                            // save user data both to db and locally
                            UsersDB.setUser(userInfo);
                        }

                        // save data locally
                        LocalDB.putUserInfo(getApplicationContext(), userInfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            // download profile pic if not locally stored
            if (!LocalDB.isProfilePicSaved(this))
                StorageDB.getProfilePic(this);
        }
        else
            Authentication.signIn(this);
    }
}
