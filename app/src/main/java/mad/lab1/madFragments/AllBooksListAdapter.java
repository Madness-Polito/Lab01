package mad.lab1.madFragments;

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

import java.util.List;

import mad.lab1.Book;
import mad.lab1.IsbnInfo;
import mad.lab1.R;

public class AllBooksListAdapter extends RecyclerView.Adapter<AllBooksListAdapter.CardViewHolder> {


    public interface OnBookClicked {
        void onBookClicked(IsbnInfo isbn);       //TODO: SET BOOKS INSTEAD OF STRING
    }

    class CardViewHolder extends  RecyclerView.ViewHolder{

        private TextView titleText;
        private TextView authorText;
        private ImageView image;
        private CardView card;

        //Getting permanent reference to built cards
        public CardViewHolder(View v){
            super(v);
            card = (CardView) v;
            titleText = v.findViewById(R.id.titleBookTextView);
            authorText = v.findViewById(R.id.authorBookTextView);
            image = v.findViewById(R.id.imageBook);
        }


        public void bind(IsbnInfo isbn, final OnBookClicked listener){
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calling the interface method, it will start a new activity to show all the info relative to the book
                    listener.onBookClicked(isbn);
                }
            });
        }

    }

    private List<IsbnInfo> isbnList;
    private final OnBookClicked listener;

    //TODO: IT WILL RECEIVE THE LIST OF BOOK ITEMS TO DISPLAY
    public AllBooksListAdapter(List<IsbnInfo> isbnList, OnBookClicked listener){
        this.isbnList = isbnList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        if(isbnList == null)
            return 0;
        return isbnList.size();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_card_view, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(v);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.titleText.setText(isbnList.get(position).getTitle());
        holder.authorText.setText(isbnList.get(position).getAuthor());
        //holder.image.setImageBitmap(allSharedBooks.get(position).getDecodedThumbnail());
        Glide.with(holder.image.getContext())
                .load(isbnList.get(position).getThumbURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.my_library_selected_24dp)
                        .centerCrop()
                        .dontAnimate()
                        .dontTransform())
                .into(holder.image);
        //The view holder gets the listener
        holder.bind(isbnList.get(position), listener);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}