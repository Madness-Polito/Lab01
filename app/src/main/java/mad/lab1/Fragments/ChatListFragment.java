package mad.lab1.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mad.lab1.R;

public class ChatListFragment extends Fragment {

    private RecyclerView chatListRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ChatListAdapter adapter;


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

        ArrayList<String> data = new ArrayList<>();
        data.add("prova");
        data.add("ciao");
        data.add("daniele");



        //Populating the adapter
        adapter = new ChatListAdapter(data);


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
}
