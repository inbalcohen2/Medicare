package com.example.pillreminder.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.pillreminder.R
import com.example.pillreminder.adapters.pillAdapter
import com.example.pillreminder.database.DataBaseHandler
import com.example.pillreminder.models.PillModel

class MainActivity : AppCompatActivity() {
    private var addPill : View?=null
    private var textNoRecycle : TextView? =null
    private var recyclerView : RecyclerView? =null

    companion object{
        var ADD_PILL_ACTIVITY =1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val animationView: LottieAnimationView = findViewById(R.id.vv)
//        animationView.setAnimation("blue_pill.json")
//        animationView.playAnimation()
        textNoRecycle = findViewById(R.id.text_no_recycle)
        recyclerView =findViewById(R.id.recycle_pills)
        addPill =findViewById(R.id.add_pill)
        getPillsFromDatabase()

//        recyclerView?.adapter {
//           val intent =Intent(this,PillDetailActivity::class.java)
//            startActivity(intent)
//
//        }


        addPill?.setOnClickListener {
            val intent = Intent(this, addPills::class.java)
            startActivityForResult(intent,ADD_PILL_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== ADD_PILL_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK) {
                getPillsFromDatabase()
            }
        }else{
            Log.e("Activity","Cancelled or Back pressed")
        }

    }

    private fun setPillRecycle(list : ArrayList<PillModel>){

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)

        val placeAdapter = pillAdapter(this,list)
        recyclerView?.adapter =placeAdapter


    }

    private fun getPillsFromDatabase(){

        val databaseHandler= DataBaseHandler(this)

        val pillList=databaseHandler.getPillList()

        if(pillList.size>0){
            recyclerView?.visibility =View.VISIBLE
            textNoRecycle?.visibility = View.GONE
            setPillRecycle(pillList)
        }
        else{
            recyclerView?.visibility =View.GONE
            textNoRecycle?.visibility = View.VISIBLE

        }

    }


}