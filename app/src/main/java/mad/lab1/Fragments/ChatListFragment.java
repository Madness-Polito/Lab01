package mad.lab1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

import mad.lab1.Database.Book;
import mad.lab1.Database.ChatInfo;
import mad.lab1.Database.UserInfo;
import mad.lab1.Database.UsersDB;
import mad.lab1.R;

public class ChatListFragment extends Fragment {

    private RecyclerView chatListRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatListAdapter adapter;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;
    private ArrayList<ChatInfo> chatInfoList;
    private FirebaseUser user;
    private ChildEventListener chatListListener;


    public static ChatListFragment newInstance(int page, String title){
        ChatListFragment fragment = new ChatListFragment();
        Bundle arg = new Bundle();
        arg.putString("title", title);
        arg.putInt("page", page);
        fragment.setArguments(arg);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        chatDownload();



        //Populating the adapter
        adapter = new ChatListAdapter(chatInfoList, getContext());


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Just got the tab view to be populated with the data of the chat list
        //Adding the right layout
        //Passing false, the rendering is handled by the ViewPager
        View v = inflater.inflate(R.layout.chat_list_fragment_layout, container, false);

        //Get reference to the recycler view
        chatListRecyclerView = v.findViewById(R.id.chatListRecyclerView);

        //Creating a layout manager to handle the Recyclerview
        linearLayoutManager = new LinearLayoutManager(v.getContext());
        chatListRecyclerView.setLayoutManager(linearLayoutManager);

        //Setting the adapter
        chatListRecyclerView.setAdapter(adapter);



        return v;

    }



    @Override
    public void onPause() {
        super.onPause();
        //Remove childEventListener
        if(dbRef != null) {
            dbRef.removeEventListener(chatListListener);
            int size = chatInfoList.size();
            chatInfoList.clear();
            adapter.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //add childEventListener
        if(chatListListener != null) {
            dbRef.addChildEventListener(chatListListener);
        }

    }

    private void chatDownload(){

        chatInfoList = new ArrayList<>();

        //Getting database reference to download chats to be listed
        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference().child("chatInfo").child(user.getUid()).child("chatInfoList");

        chatListListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //A new chat has been created
                ChatInfo c = dataSnapshot.getValue(ChatInfo.class);     //Retrieving new chat
                c.setChatID(dataSnapshot.getKey());

                //Add chat from dataSnapshot to chatlist
                chatInfoList.add(c);

                //Notify adapter that it has to render a new CardView
                adapter.notifyItemInserted(chatInfoList.indexOf(c));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }


}

