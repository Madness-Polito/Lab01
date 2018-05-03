package mad.lab1;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;

import mad.lab1.madFragments.ShowSelectedBookInfo;


public class MainPageMenu extends AppCompatActivity {


    private PagerAdapterFragmentMainMenu adapter;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ValueEventListener bookTitleListener;

    private ArrayList<String> searchableBookTitles;
    private HashMap<String, String> titleToISBN;

    private DatabaseReference dbRef;
    private DatabaseReference dbTitleRef;

    private MaterialSearchView searchView;


    @Override
    protected void onStart() {
        super.onStart();
        loginCheck();                                               //Checking if user logged in or not
        userCreatedCheck();                                         //check if user has filled in all necessary data

    }

    private void userCreatedCheck() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        //user needs to be created!
                        Intent intent = new Intent(MainPageMenu.this, EditProfile.class);
                        MainPageMenu.this.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // download books and store them locally only if did not rotate screen
        if (savedInstanceState == null)
            IsbnDB.getIsbnList(this);

        setContentView(R.layout.activity_main_page_menu);

        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //query contains the title of the book searched
                String isbn = titleToISBN.get(query);
                showBookInfo(isbn);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });



        searchView.setVoiceSearch(false);

        dbTitleRef = FirebaseDatabase.getInstance().getReference().child("titleList");
        searchableBookTitles = new ArrayList<>();
        titleToISBN = new HashMap<>();

        bookTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    BookTitleInfo b = childSnapshot.getValue(BookTitleInfo.class);
                    searchableBookTitles.add(b.getTitle());
                    titleToISBN.put(b.getTitle(), b.getIsbn());
                }

                searchView.setSuggestions(searchableBookTitles.toArray(new String[0]));

                //declare the listener here because the suggestions must be filled for it to work.
                searchView.setOnItemClickListener((adapterView, view, i, l) -> {
                    String query = adapterView.getItemAtPosition(i).toString();
                    String isbn = titleToISBN.get(query);
                    showBookInfo(isbn);
                    searchView.closeSearch();
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        dbTitleRef.addListenerForSingleValueEvent(bookTitleListener);



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



        //I hate this.
        searchView.post(new Runnable() {
            @Override
            public void run() {
                //create your anim here
                searchView.showSearch();
                searchView.closeSearch();
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
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
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

    private void showBookInfo(String isbn) {

        dbRef = FirebaseDatabase.getInstance().getReference().child("isbn").child(isbn);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                IsbnInfo searchedBook = snapshot.getValue(IsbnInfo.class);
                Bundle arg = new Bundle();
                arg.putParcelable("book", searchedBook);
                Intent i = new Intent(getApplicationContext(), ShowSelectedBookInfo.class);
                i.putExtra("argument", (Parcelable) searchedBook);
                startActivity(i);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

}
