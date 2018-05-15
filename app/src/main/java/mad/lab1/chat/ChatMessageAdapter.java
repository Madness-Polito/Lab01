package mad.lab1.chat;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.Card;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mad.lab1.ChatActivity;
import mad.lab1.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    //TODO: change with data from firebase
    private List<ChatMessage> msgList;
    private Context context;

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder{

        TextView name, text, time;
        CardView card;

        public ChatMessageViewHolder(View v){
            super(v);
            card = (CardView) v;
            name = v.findViewById(R.id.message_user);
            text = v.findViewById(R.id.message_text);
            time = v.findViewById(R.id.message_time);
        }

    }

    //Constructor receives data from the activity
    //TODO: change with data from firebase
    public ChatMessageAdapter(List<ChatMessage> msgList, Context context){
        this.msgList = msgList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        //Setting data in the cardview
        ChatMessage msg = msgList.get(position);
        holder.name.setText(msg.getUser());
        holder.text.setText(msg.getText());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String time  = dateFormat.format(msg.getTime());
        holder.time.setText(time);
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //If there are some view in the list to be initialized instead of recycled, this method is invoked to
        //link a viewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        //Instantiatin a viewHolder and passing the newly created view.
        //It can then acquire reference to all elements inside of v
        ChatMessageViewHolder c = new ChatMessageViewHolder(v);
        return c;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
