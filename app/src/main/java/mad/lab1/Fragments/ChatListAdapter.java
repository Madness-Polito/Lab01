package mad.lab1.Fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.Card;

import java.util.ArrayList;
import java.util.List;

import mad.lab1.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    //TODO: change with data from firebase
    private ArrayList<String> data;

    public static class ChatListViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView text;
        CardView card;

        public ChatListViewHolder(View v){
            super(v);
            card = (CardView) v;
            image = v.findViewById(R.id.activeChatLayoutProfileImage);
            text = v.findViewById(R.id.activeChatLayoutProfileName);
        }

    }

    //Constructor receives data from the activity
    //TODO: change with data from firebase
    public ChatListAdapter(ArrayList<String> data){
        this.data = data;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        //Setting data in the cardview
        holder.text.setText(data.get(position));
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //If there are some view in the list to be initialized instead of recycled, this method is invoked to
        //link a viewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_chat_layout, parent, false);

        //Instantiatin a viewHolder and passing the newly created view.
        //It can then acquire reference to all elements inside of v
        ChatListViewHolder c = new ChatListViewHolder(v);
        return c;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
