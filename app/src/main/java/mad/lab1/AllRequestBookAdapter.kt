package mad.lab1

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

class AllRequestBookAdapter (val d : ArrayList<String>, val c : Context):RecyclerView.Adapter<AllRequestBookAdapter.AllRequestBookViewHolder>(){

    //TODO change with real value taken from firebase
    private var users : ArrayList<String> = d
    private lateinit var data : ArrayList<String>

    class AllRequestBookViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        var nameTextView : TextView = view.findViewById(R.id.requestProfileName)
        var cityTextView : TextView = view.findViewById(R.id.requestProfileCity)
        var profileImage : de.hdodenhof.circleimageview.CircleImageView = view.findViewById(R.id.requestProfileImage)
        var cardView : CardView = view.findViewById(R.id.requestBookCard)
    }


    override fun onBindViewHolder(holder: AllRequestBookViewHolder, position: Int) {

        holder.nameTextView.setText(users.get(position))
        holder.nameTextView.setText(data.get(0))
        holder.cardView.setOnClickListener { Toast.makeText(c, "prova", Toast.LENGTH_SHORT).show() }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRequestBookViewHolder {

        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.element_card_view_all_requests, parent, false)
        return AllRequestBookViewHolder(v)

    }

    override fun getItemCount(): Int {
        return users.size
    }

}