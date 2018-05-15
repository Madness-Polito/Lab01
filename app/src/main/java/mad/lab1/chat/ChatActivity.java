package mad.lab1.chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;

import mad.lab1.Database.ChatInfo;
import mad.lab1.Fragments.AllBooksFragment;
import mad.lab1.Fragments.AllBooksListAdapter;
import mad.lab1.Map.MapsActivity;
import mad.lab1.R;
import mad.lab1.User.Authentication;

public class ChatActivity extends AppCompatActivity {

    private String chatId; // id of the current chat
    private String user2;  // id of the other user
    private String user2Name;
    private List<ChatMessage> msgList = new ArrayList<>();
    private RecyclerView cardViewList;
    private LinearLayoutManager layoutManager;
    private ChatMessageAdapter adapter;
    private String user1;
    private Toolbar toolbar;
    private ChatInfo chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        System.out.println("--------------> ChatActivity onCreate");

        FloatingActionButton fab = findViewById(R.id.chatActivitySendButton);
        cardViewList = findViewById(R.id.messageListRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cardViewList.setLayoutManager(layoutManager);
        cardViewList.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatMessageAdapter(msgList, this);
        cardViewList.setAdapter(adapter);

        // get intent data
        Intent intent = getIntent();
        Bundle arg = intent.getBundleExtra("chatInfo");
        chat = arg.getParcelable("chat");
        user2  = chat.getOtherUser();

        // generate chatId
        user1 = Authentication.getCurrentUser().getUid();
        chatId = Chat.getChatId(user1, user2);

        // send message when send button pressed
        fab.setOnClickListener((View v) -> {

                EditText input = findViewById(R.id.chatActivityEditText);

                // Read the input field and push a new ChatMessage to Firebase
                ChatMessage msg = new ChatMessage(input.getText().toString(), Authentication.getCurrentUser().getDisplayName());
                Chat.postMessage(msg, chatId, user2);

                // Clear the input
                input.setText("");
            }
        );

        // check if chat already exists
        checkChat();


        // load all messages
        getMessages();


        toolBarInitialization(chat);


    }

    private void checkChat(){

        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    System.out.println("Such chat does not exist");
                    Chat.setChatInfos(user1, user2);
                }
            }
            @Override
            public void onCancelled(DatabaseError dbError) {
            }
        };

        String chatId = Chat.getChatId(user1, user2);
        Chat.getChatInfo(user1, chatId).addListenerForSingleValueEvent(valueListener);
    }

    // reads all the messages from the chat and prints them to screen
    private void getMessages(){

        Context c = this;

        ChildEventListener msgListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new comment has been added, add it to the displayed list
                ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                msgList.add(msg);
                adapter.notifyItemInserted(msgList.indexOf(msg));

                // modify last viewed message of user
                Chat.updateLastReadMsg(user1, chatId, dataSnapshot.getKey());

                // decrease # of new msgs if msg from another user
                if (!msg.getUid().equals(Authentication.getCurrentUser().getUid()))
                    Chat.decreaseNewMsgCount(user1, chatId);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
               /* Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               /* Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...*/
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
              /*  Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(c, "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        };

        Chat.getMessages(msgListener, chatId);
    }


    private void toolBarInitialization(ChatInfo c){
        toolbar = findViewById(R.id.activityChatToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_32dp);
        getOtherUserName(chat);
        toolbar.setTitle(user2Name);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getOtherUserName(ChatInfo c){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference().child("users").child(c.getOtherUser()).child("name");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user2Name = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
