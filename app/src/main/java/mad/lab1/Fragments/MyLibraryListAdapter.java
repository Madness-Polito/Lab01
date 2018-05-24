package mad.lab1.Fragments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import mad.lab1.Database.Book;
import mad.lab1.R;

public class MyLibraryListAdapter extends RecyclerView.Adapter<MyLibraryListAdapter.MyLibraryListViewHolder> {


    //TODO: change this value if needed based on the database
    private final int BOOK_FREE = 0;
    private final int BOOK_PENDING = 1;
    private final int BOOK_BOOKED = 2;
    private Context context;
    private int status;

    public static class MyLibraryListViewHolder extends RecyclerView.ViewHolder{


        private TextView titleText;
        private TextView authorText;
        private ImageView image;
        private CardView card;

        //Constructor
        public MyLibraryListViewHolder(View v){
            super(v);
            card = (CardView) v;
            titleText = v.findViewById(R.id.titleBookTextViewMyLibrary);
            authorText = v.findViewById(R.id.authorBookTextViewMyLibrary);
            image = v.findViewById(R.id.imageBookMyLibrary);
        }

    }


    private ArrayList<Book> allSharedBooks;

    //Constructor
    public MyLibraryListAdapter(ArrayList<Book> allSharedBooks, Context context){
        this.allSharedBooks = allSharedBooks;
        this.context = context;
        status = getBookStatus();
    }

    @NonNull
    @Override
    public MyLibraryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_card_view_my_library, parent, false);
        MyLibraryListViewHolder holder = new MyLibraryListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyLibraryListViewHolder holder, int position) {
        holder.titleText.setText(allSharedBooks.get(position).getTitle());
        holder.authorText.setText(allSharedBooks.get(position).getAuthor());
        //holder.image.setImageBitmap(allSharedBooks.get(position).getDecodedThumbnail());
        Glide.with(holder.image.getContext())
                .load(allSharedBooks.get(position).getThumbURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.my_library_selected_24dp)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(holder.image);

        /*
        * Listener on card based on the status of the book
        * FREE && counter > 0
        *   Open list of user
        * PENDING
        *   Dialog, possible to remove request
        * BOOKED
        *   Write a review of the user or modify an existing one
        *
        * */

        //It is better to keep getBookStatus call separated from the onClick, to avoid cumbersome operations during event handling
        status = getBookStatus();
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: based on the book status, it will trigger different actions
                switch(status){
                    case BOOK_FREE:
                        Toast.makeText(context, "Free book", Toast.LENGTH_SHORT).show();
                        break;
                    case BOOK_PENDING:
                        break;
                    case BOOK_BOOKED:
                        break;
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return allSharedBooks.size();
    }


    private int getBookStatus(){
        //TODO this function should retrieve the status from firebase and return the corresponding constant
        return BOOK_FREE;
    }
}
