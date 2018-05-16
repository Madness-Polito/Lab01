package mad.lab1.chat;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import mad.lab1.Database.ChatInfo;
import mad.lab1.User.Authentication;

public class Chat {

    private static final String CHATS = "chats";        // path on firebase where we store all the messages of all the chats
    private static final String CHAT_INFO = "chatInfo"; // path on firebase where we store the metadata of the chats of a user
    private static final String CHAT_INFO_LIST = "chatInfoList";
    private static final String LAST_READ_MSG = "lastReadMsg"; // name of field where is stored the key of the last read message
    private static final String NUM_NEW_MSG   = "newMsgCount"; // number of new messages
    private static final String TOT_NUM_NEW_MSG = "totNumNewMsg"; // number of all new messages of all the chats of a given user

    // returns all the messages of a given chat
    public static void getMessages(ChildEventListener listener, String chatId){

        Chat.getReference()
                .child(chatId) // "/chats/chatId"
                .addChildEventListener(listener);
    }

    // sends a chat message
    public static void postMessage(Context c, ChatMessage msg, String chatId, String uid2){

        System.out.println("---------->PostMessage " + msg.getText() + " from " + msg.getUser());

        // add message to list of messages
        Chat.getReference()
                .child(chatId) // i.e. /chats/chatId
                .push()
                .setValue(msg);

        // notify the other user of a new message on the chat
        DatabaseReference newMsgCountRef = Chat.getChatInfo(uid2, chatId)
                                            .child(NUM_NEW_MSG);
        newMsgCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer newMsgCount = mutableData.getValue(Integer.class);

                if (newMsgCount == null)
                    newMsgCount = 0;

                System.out.println("----------->newMsgCount " + newMsgCount);

                newMsgCount++;
                mutableData.setValue(newMsgCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                showTransactionError(c, error, committed, currentData);
            }
        });

        // notify other user of a new message in newMsgCount for all chats
        DatabaseReference totNewMsgCountRef = FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child(CHAT_INFO)
                                                        .child(uid2)
                                                        .child(TOT_NUM_NEW_MSG);
        totNewMsgCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer totNewMsgCount = mutableData.getValue(Integer.class);

                if (totNewMsgCount == null)
                    totNewMsgCount = 0;

                System.out.println("----------->totNewMsgCount " + totNewMsgCount);

                totNewMsgCount++;
                mutableData.setValue(totNewMsgCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                showTransactionError(c, error, committed, currentData);
            }
        });
    }

    // returns the reference to the "/chats" url where chats are stored
    static DatabaseReference getReference(){
        return FirebaseDatabase.getInstance()
                .getReference()
                .child(CHATS);
    }

    // returns the id of a chat given the 2 uid
    public static String getChatId(String uid1, String uid2){

        String chatId;

        if (uid1.compareTo(uid2) >= 0)
            chatId = uid1 + uid2;
        else
            chatId = uid2 + uid1;

        return chatId;
    }

    public static void updateLastReadMsg(String uid, String chatId, String msgKey){

        Chat.getChatInfo(uid, chatId)
                .child(LAST_READ_MSG)
                .setValue(msgKey);
    }

    public static void updateNumNewMsg(String uid, String chatId) {

        DatabaseReference numNewMsgRef = Chat.getChatInfo(uid, chatId)
                .child(NUM_NEW_MSG);

        numNewMsgRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(int.class) == null) {
                    numNewMsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.getValue() == null) {
                                numNewMsgRef.setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    return Transaction.abort();
                }

                int numNewMsg = mutableData.getValue(int.class);
                mutableData.setValue(numNewMsg + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {

            }
        });
    }

    public static void setChatInfos(String uid1, String uid2){

        String chatId = Chat.getChatId(uid1, uid2);

        setChatInfo(uid1, uid2, chatId);
        setChatInfo(uid2, uid1, chatId);
    }

    private static void setChatInfo(String uid1, String uid2, String chatId){

        ChatInfo chatInfo = new ChatInfo(0, uid2);

        Chat.getChatInfo(uid1, chatId)
                .setValue(chatInfo);
    }

    // returns the path to where metadata of a given user and a given chat is stored
    public static DatabaseReference getChatInfo(String uid, String chatId){

        return FirebaseDatabase.getInstance()
                .getReference()
                .child(CHAT_INFO)
                .child(uid)
                .child(CHAT_INFO_LIST)
                .child(chatId);
    }

    public static void decreaseNewMsgCount(Context c, String uid, String chatId){

        // decrease by 1 the msgCount of the chat we are in
        DatabaseReference newMsgCountRef = Chat.getChatInfo(uid, chatId)
                .child(NUM_NEW_MSG);
        newMsgCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer newMsgCount = mutableData.getValue(Integer.class);

                if (newMsgCount == null)
                    return Transaction.success(mutableData);

                System.out.println("----------->newMsgCount " + newMsgCount);

                newMsgCount--;
                mutableData.setValue(newMsgCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                showTransactionError(c, error, committed, currentData);
            }
        });

        // decrease by 1 the totNewMsgCount too
        DatabaseReference totNewMsgCountRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(CHAT_INFO)
                .child(uid)
                .child(TOT_NUM_NEW_MSG);
        totNewMsgCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer totNewMsgCount = mutableData.getValue(Integer.class);

                if (totNewMsgCount == null)
                    return Transaction.success(mutableData);

                System.out.println("----------->totNewMsgCount " + totNewMsgCount);

                totNewMsgCount--;
                mutableData.setValue(totNewMsgCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                showTransactionError(c, error, committed, currentData);
            }
        });


    }


    private static void showTransactionError(Context c, DatabaseError dbErr, boolean committed, DataSnapshot currentData){

        if (dbErr != null)
            Toast.makeText(c, dbErr.getDetails(), Toast.LENGTH_SHORT).show();

        if (!committed)
            Toast.makeText(c, "Transaction not committed", Toast.LENGTH_SHORT).show();

        //Toast.makeText(c, currentData.getValue().toString(), Toast.LENGTH_SHORT).show();
    }

}
