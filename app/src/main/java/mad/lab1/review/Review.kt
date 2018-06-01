package mad.lab1.review;

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

// represents a review of a user
data class Review (var uid: String,      // uid of the user who is writing the review
                   var userName: String, // username of the user who is writing the review
                   var numStars: Float,
                   var title: String = "",
                   var body: String = "",
                   var picNames : List<String>? = null) : Parcelable {
//picsUri: List<String>? = null) : Parcelable {

    constructor() : this("",
                         "",
                         0f)

    constructor(p: Parcel) : this(){
        uid = p.readString()
        userName = p.readString()
        numStars = p.readFloat()
        title = p.readString()
        body = p.readString()
        p.readStringList(picNames)
    }

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeString(uid)
        p.writeString(userName)
        p.writeFloat(numStars)
        p.writeString(title)
        p.writeString(body)
        p.writeStringList(picNames)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}