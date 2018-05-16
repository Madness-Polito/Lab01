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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mad.lab1.Database.ChatInfo;
import mad.lab1.Database.UserInfo;
import mad.lab1.Fragments.AllBooksFragment;
import mad.lab1.Fragments.AllBooksListAdapter;
import mad.lab1.Map.MapsActivity;
import mad.lab1.R;
import mad.lab1.User.Authentication;

public class ChatActivity extends AppCompatActivity {

    private final String SERVER_KEY = "AAAAYKIaOw0:APA91bFrX3UYbXFsB40mpDvg3Na-bbdPSeWtLCvkGUJJjG-nAs6oJKVSTZnTCAU4LR1yGLDT4uXYnhMlliJgRcZ2tlo-90lhLj6iGqneVv5AkSGc9NPVaNKNpTRYB1ZURuqLVjmWY9BT";

    private String chatId; // id of the current chat
    private String user2;  // id of the other user
    private UserInfo user2Profile;
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
        getOtherUserProfile(chat);
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

                //send a notification to user2
                sendPost(user2, msg.getText().toString());

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


        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getOtherUserProfile(ChatInfo c){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference user2Ref = db.getReference().child("users").child(c.getOtherUser());
        user2Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 user2Profile = dataSnapshot.getValue(UserInfo.class);
                 toolbar.setTitle(user2Profile.getName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

    public void sendPost(String user, String msg) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    JSONObject jsonParam2 =  new JSONObject();
                    JSONObject jsonParam3 = new JSONObject();
                    jsonParam2.put("body", msg);
                    jsonParam2.put("title", "New Message");
                    jsonParam.put("notification", jsonParam2);
                    jsonParam.put("to", "/topics/" + user);

                    /*jsonParam3.put("body", msg);
                    jsonParam3.put("title", "testTitle");
                    jsonParam2.put("topic", user);
                    jsonParam2.put("notification", jsonParam3);
                    jsonParam.put("message", jsonParam2);*/


                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
