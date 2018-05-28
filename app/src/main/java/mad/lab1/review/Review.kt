package mad.lab1.review;

import android.os.Parcel
import android.os.Parcelable

// represents a review of a user
data class Review (val uid: String,      // uid of the user who is writing the review
                   val userName: String, // username of the user who is writing the review
                   val numStars: Int,
                   val title: String = "",
                   val body: String = "") : Parcelable {
//picsUri: List<String>? = null) : Parcelable {

    constructor(p: Parcel) : this(
            p.readString(),
            p.readString(),
            p.readInt(),
            p.readString(),
            p.readString())
            //picsUri  = p.readList(getClassLoader())))

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeString(uid)
        p.writeString(userName)
        p.writeInt(numStars)
        p.writeString(title)
        p.writeString(body)
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