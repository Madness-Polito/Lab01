package mad.lab1;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;

import mad.lab1.Database.Book;
import mad.lab1.Database.BookTitleInfo;
import mad.lab1.Database.LocalDB;
import mad.lab1.Database.StorageDB;
import mad.lab1.Database.UserInfo;
import mad.lab1.Database.UsersDB;
import mad.lab1.Fragments.ShowSelectedBookInfo;
import mad.lab1.Notifications.Constants;
import mad.lab1.User.Authentication;
import mad.lab1.User.EditProfile;
import mad.lab1.User.ShowProfile;


public class MainPageMenu extends AppCompatActivity {


    private PagerAdapterFragmentMainMenu adapter;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ValueEventListener bookTitleListener;

    private ArrayList<String> searchableAllBookTitles;
    private ArrayList<String> searchableBorrowedBookTitles;
    private ArrayList<String> searchableLibraryBookTitles;
    private HashMap<String, String> titleToISBN;

    private DatabaseReference dbRef;
    private DatabaseReference dbTitleRef;

    private MaterialSearchView searchView;




    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MARKER", "onStart()");
        /*
        Log.d("MARKER", "onStart()");
        String tutorialKey = "tutorialCheck";
        Boolean firstTime = getPreferences(MODE_PRIVATE).getBoolean(tutorialKey, true);
        if (firstTime) {
            runTutorial(); // here you do what you want to do - an activity tutorial in my case
            getPreferences(MODE_PRIVATE).edit().putBoolean(tutorialKey, false).apply();
        }
        */

        //runTutorial();
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
        if(!isFirstStart) {
            loginCheck();                                               //Checking if user logged in or not
            userCreatedCheck();                                         //check if user has filled in all necessary data
        }

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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MARKER", "onCreate()");
        setContentView(R.layout.activity_main_page_menu);

        searchView = findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //query contains the title of the book searched
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

        searchableAllBookTitles = new ArrayList<>();
        searchableBorrowedBookTitles = new ArrayList<>();
        searchableLibraryBookTitles = new ArrayList<>();
        titleToISBN = new HashMap<>();

        //create the lists for the different searchable titles
        createAllBooksSearchList();
        createBorrowedBooksSearchList();
        createLibraryBooksSearchList();

        initialization();                                           //Initialization and getting views references

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            String[] titles = {getApplicationContext().getString(R.string.AllBooks),
                    getApplicationContext().getString(R.string.Library),
                    getApplicationContext().getString(R.string.BorrowedBooks),
                    "Chat List"};


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //change what books can be searched
                if(position == 0){
                    //all books
                    searchView.setSuggestions(searchableAllBookTitles.toArray(new String[0]));

                    //declare the listener here because the suggestions must be filled for it to work.
                    searchView.setOnItemClickListener((adapterView, view, i, l) -> {
                        String query = adapterView.getItemAtPosition(i).toString();
                        String isbn = titleToISBN.get(query);
                        showBookInfo(isbn);
                        searchView.closeSearch();
                    });

                }else if(position == 1){
                    //my library
                    searchView.setSuggestions(searchableLibraryBookTitles.toArray(new String[0]));

                    //declare the listener here because the suggestions must be filled for it to work.
                    searchView.setOnItemClickListener((adapterView, view, i, l) -> {
                        String query = adapterView.getItemAtPosition(i).toString();
                        String isbn = titleToISBN.get(query);
                        showBookInfo(isbn);
                        searchView.closeSearch();
                    });

                }else if(position == 2){
                    //borrowed books
                    searchView.setSuggestions(searchableBorrowedBookTitles.toArray(new String[0]));

                    //declare the listener here because the suggestions must be filled for it to work.
                    searchView.setOnItemClickListener((adapterView, view, i, l) -> {
                        String query = adapterView.getItemAtPosition(i).toString();
                        String isbn = titleToISBN.get(query);
                        showBookInfo(isbn);
                        searchView.closeSearch();
                    });

                }else{
                    //chats
                }

                toolbar.setTitle(titles[position]);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        //I hate this. Fixes layout on first run
        searchView.post(new Runnable() {
            @Override
            public void run() {
                //create your anim here
                searchView.showSearch();
                searchView.closeSearch();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //subscribe to firebase notification channel
        if(user != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
        }

        if(getIntent().getAction() != null) {
            //this has been opened from a notification
            viewPager.setCurrentItem(1);
        }



    }

    private void createAllBooksSearchList() {
        dbTitleRef = FirebaseDatabase.getInstance().getReference().child("titleList");

        bookTitleListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                    BookTitleInfo b = childSnapshot.getValue(BookTitleInfo.class);
                    searchableAllBookTitles.add(b.getTitle());
                    titleToISBN.put(b.getTitle(), b.getIsbn());
                }

                searchView.setSuggestions(searchableAllBookTitles.toArray(new String[0]));

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


    }

    private void createBorrowedBooksSearchList(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            dbTitleRef = FirebaseDatabase.getInstance().getReference().child("borrowedBooks").child(user.getUid());

            bookTitleListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        //BookTitleInfo b = childSnapshot.getValue(BookTitleInfo.class);
                        //searchableAllBookTitles.add(b.getTitle());
                        //titleToISBN.put(b.getTitle(), b.getIsbn());
                        Book b = childSnapshot.getValue(Book.class);
                        searchableBorrowedBookTitles.add(b.getTitle());

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbTitleRef.addListenerForSingleValueEvent(bookTitleListener);
        }
    }

    private void createLibraryBooksSearchList(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            dbTitleRef = FirebaseDatabase.getInstance().getReference().child("bookList").child(user.getUid());

            bookTitleListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        //BookTitleInfo b = childSnapshot.getValue(BookTitleInfo.class);
                        //searchableAllBookTitles.add(b.getTitle());
                        //titleToISBN.put(b.getTitle(), b.getIsbn());
                        Book b = childSnapshot.getValue(Book.class);
                        searchableLibraryBookTitles.add(b.getTitle());

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            dbTitleRef.addListenerForSingleValueEvent(bookTitleListener);
        }
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());
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
        toolbar.setTitle(getApplicationContext().getString(R.string.AllBooks));
        setSupportActionBar(toolbar);


    }

    private void setIcons() {
        TabLayout.Tab tabCall = tabLayout.getTabAt(0);
        tabCall.setIcon(R.drawable.all_books_tab_icon);

        tabCall = tabLayout.getTabAt(1);
        tabCall.setIcon(R.drawable.my_library_tab_icon);

        tabCall = tabLayout.getTabAt(2);
        tabCall.setIcon(R.drawable.borrowed_books_tab_icon);

        tabCall = tabLayout.getTabAt(3);
        tabCall.setIcon(R.drawable.chat_list_tab_icon);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MARKER", "onResume()");

        if(viewPager.getCurrentItem() == 3){
            //clear all chat notifications, we are on the chat page
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.NOTIFICATION_TAG);
        }

        runTutorial();
    }

    private void runTutorial(){
        /////////////////////////////////////////

        //  Initialize SharedPreferences
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());


        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        //  If the activity has never started before...
        if (isFirstStart) {


            //  Launch app intro
            final Intent i = new Intent(getBaseContext(), IntroTutorialActivity.class);

            startActivity(i);
            //  Make a new preferences editor
            SharedPreferences.Editor e = getPrefs.edit();

            //  Edit preference to make it false because we don't want this to run again
            e.putBoolean("firstStart", false);

            //  Apply changes
            e.apply();

            //Log.d("MARKER", "upd value "+getPrefs.getBoolean("firstStart", false));// true before
        }
        //else
            //Log.d("MARKER", "it's not first time app runs");
        ////////////////////////////////////////////////////
    }

    private void loginCheck() {

        Log.d("MARKER", "loginCheck()");
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
                Book searchedBook = snapshot.getValue(Book.class);
                Bundle arg = new Bundle();
                arg.putParcelable("book", searchedBook);
                Intent i = new Intent(getApplicationContext(), ShowSelectedBookInfo.class);
                i.putExtra("argument", searchedBook);
                startActivity(i);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }



}
