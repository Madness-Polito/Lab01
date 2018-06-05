package mad.lab1.review
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.review.view.*
import mad.lab1.R
import mad.lab1.R.id.imageView
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.bumptech.glide.Glide
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import android.net.Uri
import android.support.annotation.Nullable
import android.support.v4.widget.CircularProgressDrawable
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_review_pic.view.*
import mad.lab1.GlideApp
import mad.lab1.R.id.imageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase




class ReviewsAdapter(private val reviews: ArrayList<Review>, val context: Context)
    : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.review, parent, false)
        return ViewHolder(v, context)
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
    class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(review: Review) {

            val rv : RecyclerView = itemView.reviewPicList
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = ReviewsAdapter.ReviewImagesAdapter(review.picNames!!, context)

            itemView.userName.text = review.userName
            itemView.ratingBar.rating = review.numStars
            itemView.title.text = review.title
            itemView.body.text  = review.body

            // load profile pic
            val picRef : StorageReference = FirebaseStorage.getInstance()
                                                           .getReference("userPics")
                                                           .child(review.uid)
            GlideApp.with(context)
                    .load(picRef)
                    .into(itemView.profilePic)
        }
    }

    // adapter to show the images of the current review
    class ReviewImagesAdapter(private val picNames: List<String>, val context: Context)
        : RecyclerView.Adapter<ReviewsAdapter.ReviewImagesAdapter.ViewHolder>() {

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsAdapter.ReviewImagesAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                                  .inflate(R.layout.item_review_pic, parent, false)
            return ViewHolder(v, context)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: ReviewsAdapter.ReviewImagesAdapter.ViewHolder, position: Int) {
            holder.bindItems(picNames[position])
        }

        //this method is giving the size of the list
        override fun getItemCount(): Int {
            return picNames.size
        }

        //the class is holding the list view
        class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(picName: String) {

                // load current review pic
                val picRef : StorageReference = FirebaseStorage.getInstance()
                        .getReference("reviews")
                        .child(picName)

                GlideApp.with(context)
                        .load(picRef)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                itemView.progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                itemView.progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(itemView.pic)
            }
        }
    }
}

