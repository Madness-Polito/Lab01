package mad.lab1.chat;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import mad.lab1.GlideApp;
import mad.lab1.R;
import mad.lab1.User.Authentication;

public class ChatMessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT     = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SYSTEM   = 3;
    private static final String DATE_FORMAT = "dd/MM/YY HH:mm";

    private List<ChatMessage> msgList;
    private Context context;

    //Constructor receives data from the activity
    public ChatMessageAdapter(List<ChatMessage> msgList, Context context){
        this.msgList = msgList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessage msg =  msgList.get(position);

        System.out.println("-------->getItemViewType user " + msg.getUid() + " text " + msg.getText());

        // If the current user is the sender of the message
        if (msg.getUid().equals(Authentication.getCurrentUser().getUid()))
            return VIEW_TYPE_MESSAGE_SENT;
        // If some other user sent the message
        else if (!msg.getUid().equals(""))
            return VIEW_TYPE_MESSAGE_RECEIVED;
        // system message
        else
            return VIEW_TYPE_MESSAGE_SYSTEM;
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_SYSTEM){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_system, parent, false);
            return new SystemMessageHolder(view);
        }

        return null;
    }


    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg =  msgList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(msg);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(msg);
                break;
            case VIEW_TYPE_MESSAGE_SYSTEM:
                ((SystemMessageHolder) holder).bind(msg);
                break;
        }
    }

    private class SystemMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;//, timeText;

        SystemMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
        }

        void bind(ChatMessage msg) {
            messageText.setText(msg.getText());
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;//, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            //timeText    = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage msg) {
            messageText.setText(msg.getText());

            // Format the stored timestamp into a readable String using method.
            Long time = msg.getTime();
            DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            //timeText.setText(formatter.format(time));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            System.out.println("------------> ReceivedMessageViewHolder");

            messageText  =  itemView.findViewById(R.id.text_message_body);
            nameText     =  itemView.findViewById(R.id.text_message_name);
            profileImage =  itemView.findViewById(R.id.image_message_profile);
        }

        void bind(ChatMessage msg) {

            System.out.println("------------> ReceivedMessageViewHolder.bind()");

            messageText.setText(msg.getText());

            // show only text if prev msg was from the same person
            if (msg.getUid().equals(msg.getPrevMsgUid())){
                nameText.setVisibility(View.GONE);
                profileImage.setVisibility(View.GONE);
            }
            // otherwise show full msg
            else {
                nameText.setText(msg.getUser());

                // set profile pic
                StorageReference profilePicRef = FirebaseStorage.getInstance()
                        .getReference("userPics")
                        .child(msg.getUid());
                GlideApp.with(context)
                        .load(profilePicRef)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImage);
            }

        }
    }
}

