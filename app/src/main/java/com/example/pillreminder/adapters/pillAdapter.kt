package com.example.pillreminder.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.Constants
import com.example.pillreminder.R
import com.example.pillreminder.activity.PillDetailActivity
import com.example.pillreminder.models.PillModel
import de.hdodenhof.circleimageview.CircleImageView

open class pillAdapter(
    private val context: Context
    ,private val list: ArrayList<PillModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return viewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_pill_list, parent, false
            )

        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pill = list[position]
        if (holder is viewHolder) {
            holder.itemView.findViewById<TextView>(R.id.rc_pill_name).text = pill.name
            holder.itemView.findViewById<TextView>(R.id.rc_pill_description).text = pill.description
            holder.itemView.findViewById<CircleImageView>(R.id.profile_image)
                .setImageURI(Uri.parse(pill.image))

            holder.itemView.setOnClickListener {
                val intent =Intent(context,PillDetailActivity::class.java)
                intent.putExtra(Constants.PILL_NAME,pill.name)
                intent.putExtra(Constants.PILL_DESCRIPTION,pill.description)
                intent.putExtra(Constants.PILL_IMAGE,pill.image)
                startActivity(context,intent,null)

//
            }



        }


    }




    override fun getItemCount(): Int {
        return list.size
    }


    private class viewHolder(view: View) : RecyclerView.ViewHolder(view)


    }
