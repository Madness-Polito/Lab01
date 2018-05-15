package mad.lab1;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import mad.lab1.User.Authentication;
import mad.lab1.User.ShowProfile;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton sendMessageButton;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialization();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chatActivityShowUserProfile:
                //TODO: show user profile
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_activity_menu, menu);
        return true;
    }

    private void initialization(){

        toolbar = findViewById(R.id.activityChatToolbar);
        //TODO: link username here from firebase
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp);

        //toolbar.setLogo(R.drawable.profile_selected_24dp);
        toolbar.setTitle("Nome Utente");
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        sendMessageButton = findViewById(R.id.chatActivitySendButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get message from inputText
                //Clean inputText
                //Store it on firebase

            }
        });



    }

}




