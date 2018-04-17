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

import java.util.ArrayList;

import mad.lab1.R;

public class AllBooksListAdapter extends RecyclerView.Adapter<AllBooksListAdapter.CardViewHolder> {


    public interface OnBookClicked {
        void onBookClicked(String value);       //TODO: SET BOOKS INSTEAD OF STRING
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

        //TODO: SET BOOKS INSTEAD OF STRING
        public void bind(String value, final OnBookClicked listener){
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Calling the interface method, it will start a new activity to show all the info relative to the book
                    listener.onBookClicked(value);
                }
            });
        }

    }

    private ArrayList<String> value;
    private final OnBookClicked listener;

    //TODO: IT WILL RECEIVE THE LIST OF BOOK ITEMS TO DISPLAY
    public AllBooksListAdapter(ArrayList<String> value, OnBookClicked listener){
        this.value = value;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return value.size();
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
        holder.titleText.setText(value.get(position));
        holder.authorText.setText(value.get(position));
        //The view holder gets the listener
        holder.bind(value.get(position), listener);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
