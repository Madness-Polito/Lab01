package mad.lab1.Fragments;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import mad.lab1.Database.Book;
import mad.lab1.GlideApp;
import mad.lab1.R;

public class AllBooksListAdapter extends RecyclerView.Adapter<AllBooksListAdapter.CardViewHolder> {


    public interface OnBookClicked {
        void onBookClicked(Book b);
    }

    class CardViewHolder extends  RecyclerView.ViewHolder{

        private TextView titleText;
        private TextView authorText;
        private ProgressBar progressBar;
        private ImageView image;
        private CardView card;

        //Getting permanent reference to built cards
        public CardViewHolder(View v){
            super(v);
            card = (CardView) v;
            titleText = v.findViewById(R.id.titleBookTextView);
            authorText = v.findViewById(R.id.authorBookTextView);
            image = v.findViewById(R.id.imageBook);
            progressBar = v.findViewById(R.id.progressBar);
        }


        public void bind(Book b, final OnBookClicked listener){
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calling the interface method, it will start a new activity to show all the info relative to the book
                    listener.onBookClicked(b);
                }
            });
        }

    }

    private ArrayList<Book> allSharedBooks;
    private final OnBookClicked listener;


    public AllBooksListAdapter(ArrayList<Book> allSharedBooks, OnBookClicked listener){
        this.allSharedBooks = allSharedBooks;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return allSharedBooks.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_card_view_all_book, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.titleText.setText(allSharedBooks.get(position).getTitle());
        holder.authorText.setText(allSharedBooks.get(position).getAuthor());

        GlideApp.with(holder.image.getContext())
                .load(allSharedBooks.get(position).getThumbURL())
                .listener(new RequestListener<Drawable>(){
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);
        holder.bind(allSharedBooks.get(position), listener);
    }


}
