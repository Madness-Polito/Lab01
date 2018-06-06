package mad.lab1.review

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_review_pic.view.*
import kotlinx.android.synthetic.main.review.view.*
import mad.lab1.GlideApp
import mad.lab1.R
import mad.lab1.User.Authentication
import android.os.Environment.DIRECTORY_PICTURES
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ReviewActivity : AppCompatActivity() {

    // constants
    private val TITLE : String = "title"
    private val BODY  : String = "body"
    private val NUM_STARS : String = "numStars"

    private var uid : String? = null
    private var title : EditText? = null
    private var body  : EditText? = null
    private var ratingBar : RatingBar? = null
    private var okBtn : FloatingActionButton? = null
    private var picBtn : FloatingActionButton? = null
    private var rvPic : RecyclerView? = null
    private var mCropImageUri: Uri? = null
    private val picUris: ArrayList<Uri> = ArrayList()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)
        uid = intent.getStringExtra("uid") // uid of user we want to give a review

        // identify layout objects
        title = findViewById(R.id.title)
        body  = findViewById(R.id.body)
        ratingBar = findViewById(R.id.ratingBar)
        okBtn = findViewById(R.id.btn_ok)
        picBtn = findViewById(R.id.picBtn)
        rvPic = findViewById(R.id.recyclerViewPic)

        // set adapter for recyclerview
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd  = true
        rvPic?.layoutManager = mLayoutManager
        rvPic?.adapter = PicAdapter(picUris, this)

        // set listener for picBtn
        picBtn?.setOnClickListener{
            CropImage.startPickImageActivity(this)
        }

        // define okBtn action when pressed
        okBtn?.setOnClickListener{

            val reviewRef = FirebaseDatabase.getInstance()
                                            .reference
                                            .child("reviews")
                                            .child(uid!!)

            // 1) add stars to user's starCount & and 1 to reviewCount
            reviewRef.child("totStarCount")
                    .runTransaction(object : Transaction.Handler{
                        override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        }

                        override fun doTransaction(mutableData: MutableData): Transaction.Result {

                            var totStarCount: Float? = mutableData.getValue(Float::class.javaObjectType)

                            if (totStarCount == null){
                                mutableData.value = ratingBar!!.rating
                                return Transaction.success(mutableData)
                            }

                            totStarCount += ratingBar!!.rating

                            mutableData.value = totStarCount
                            return Transaction.success(mutableData)
                        }
                    })

            reviewRef.child("reviewCount")
                    .runTransaction(object : Transaction.Handler{
                        override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        }

                        override fun doTransaction(mutableData: MutableData): Transaction.Result {

                            var reviewCount : Int? = mutableData.getValue(Int::class.javaObjectType)

                            if (reviewCount == null){
                                mutableData.value = 1
                                return Transaction.success(mutableData)
                            }

                            reviewCount++

                            mutableData.value = reviewCount
                            return Transaction.success(mutableData)
                        }
                    })


            // prepare list of image names to put into review
            val picNames : ArrayList<String> = ArrayList()
            for (uri : Uri in picUris)
                picNames.add(uri.lastPathSegment)

            // 2) add review to his path
            val review = Review(Authentication.getCurrentUser().uid,
                                Authentication.getCurrentUser().displayName!!,
                                ratingBar!!.rating,
                                title!!.text.toString(),
                                body!!.text.toString(),
                                picNames)
            reviewRef.child("reviewList")
                    .push()
                    .setValue(review)

            // load pictures
            val picReviewRef = storage.reference
                    .child("reviews")

            for (uri : Uri in picUris){
                picReviewRef.child(uri.lastPathSegment)
                        .putFile(uri)// TODO add onfailurelistener and onsuccesslistener
            }

            // finish activity
            finish()
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        System.out.println("""onActivityResult""")
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
            } else {
                // no permissions required or already granted, can start crop image activity
                startCropImageActivity(imageUri)
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                picUris.add(resultUri)
                val index : Int = picUris.indexOf(resultUri)
                rvPic?.adapter?.notifyItemInserted(index)
                rvPic?.scrollToPosition(index)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                error.printStackTrace()
            }

        }
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .start(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        // save form data
        outState?.putString(TITLE, title!!.text.toString())
        outState?.putString(BODY, body!!.text.toString())
        outState?.putFloat(NUM_STARS, ratingBar!!.rating)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        // restore form data
        title!!.setText(savedInstanceState!!.getString(TITLE))
        body!!.setText(savedInstanceState.getString(BODY))
        ratingBar!!.rating = savedInstanceState.getFloat(NUM_STARS)
    }

    // adapter for pictures recyclerview
    class PicAdapter(private val picUris: ArrayList<Uri>, val context: Context)
        : RecyclerView.Adapter<PicAdapter.ViewHolder>() {

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                                  .inflate(R.layout.item_review_pic, parent, false)
            return ViewHolder(v, context)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: PicAdapter.ViewHolder, position: Int) {
            holder.bindItems(picUris[position])
        }

        //this method is giving the size of the list
        override fun getItemCount(): Int {
            return picUris.size
        }

        //the class is holding the list view
        class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

            fun bindItems(picUri: Uri) {
                itemView.pic.setImageURI(picUri)
                itemView.progressBar.visibility = View.GONE
            }
        }
    }

}