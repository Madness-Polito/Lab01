package mad.lab1.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mad.lab1.ChatActivity;
import mad.lab1.Database.Chat;
import mad.lab1.Database.LocalDB;
import mad.lab1.R;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private ArrayList<Chat> chatList;
    private Context context;

    public static class ChatListViewHolder extends RecyclerView.ViewHolder{

        CircleImageView chatUserImage;                    //Book owner's profile picture that we want to borrow
        TextView chatUserName;                      //Book owner's profile name that we want to borrrow
        CardView card;
        CircleImageView chatNewMexCountBackground;
        TextView newMexCount;

        public ChatListViewHolder(View v){
            super(v);
            card = (CardView) v;
            chatUserImage = v.findViewById(R.id.activeChatLayoutProfileImage);
            chatUserName = v.findViewById(R.id.activeChatLayoutProfileName);
            chatNewMexCountBackground = v.findViewById(R.id.activeChatLayoutNewMessageCountBackground);
            newMexCount = v.findViewById(R.id.activeChatLayoutNewMessageCount);

            newMexCount.setVisibility(View.GONE);
            chatNewMexCountBackground.setVisibility(View.GONE);
        }

    }

    //Constructor receives data from the activity

    public ChatListAdapter(ArrayList<Chat> chatList, Context context){

        this.chatList = chatList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

        //Check if the list contains some chat, else go on.
        if(!chatList.isEmpty()){

            Chat c = chatList.get(position);

            //Set the user profile pic
            setOtherUserImageProfile(holder, c);

            if(c.getNewMexNumber() > 0){
                //New message, show the number
                holder.newMexCount.setText(c.getNewMexNumber());
                holder.newMexCount.setVisibility(View.VISIBLE);
                holder.chatNewMexCountBackground.setVisibility(View.VISIBLE);
            }else{
                //No new messages, hide the number
                holder.newMexCount.setVisibility(View.GONE);
                holder.chatNewMexCountBackground.setVisibility(View.GONE);
            }


            //Setting data in the cardview
            holder.chatUserName.setText(chatList.get(position).getOtherUser());
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("chat", c);
                    intent.putExtra("chatInfo", b);
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //If there are some view in the list to be initialized instead of recycled, this method is invoked to
        //link a viewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_chat_list_layout, parent, false);

        //Instantiatin a viewHolder and passing the newly created view.
        //It can then acquire reference to all elements inside of v
        ChatListViewHolder c = new ChatListViewHolder(v);
        return c;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    private void setOtherUserImageProfile(ChatListViewHolder holder, Chat c){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference user2Ref = db.getReference().child("users").child(c.getOtherUser());

        Uri picUri = Uri.parse(LocalDB.getProfilePicPath(context));
        holder.chatUserImage.setImageURI(picUri);

    }

}
