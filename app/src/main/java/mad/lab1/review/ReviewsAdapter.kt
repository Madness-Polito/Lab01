package mad.lab1.review
import android.content.Context
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.review.view.*
import mad.lab1.R

class ReviewsAdapter(private val reviews: ArrayList<Review>, val context: Context)
    : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ReviewsAdapter.ViewHolder, position: Int) {
        holder.bindItems(reviews[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return reviews.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(review: Review) {
            itemView.userName.text = review.userName
            itemView.ratingBar.numStars = review.numStars
            itemView.title.text = review.title
            itemView.body.text  = review.body
        }
    }
}