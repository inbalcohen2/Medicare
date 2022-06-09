package com.example.pillreminder.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.example.pillreminder.Constants
import com.example.pillreminder.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PillDetailActivity : AppCompatActivity() {

    private var clickButton : FrameLayout?=null
    private var textMessageWhenTheTimeToTakeThePill:TextView? =null
    private var testBluetooth : EditText? = null
    private var imagePill : AppCompatImageView? =null
    private var tv_namePill : TextView? =null
    private var tv_descriptionPill : TextView? =null//    private var flag :Boolean = false

    companion object{
        private const val CAMERA_PERMISSION_CODE=1
        private const val CAMERA=2
        private const val GALLERY=3
        private const val IMAGE_DIRECTORY="PillImage" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pill_detail)
        var tollBar : Toolbar = findViewById(R.id.toolbar_pill_detail)
        menageToolBar(tollBar)
        clickButton= findViewById(R.id.click_fl)
        testBluetooth =findViewById(R.id.test)
        textMessageWhenTheTimeToTakeThePill=findViewById(R.id.date_pill_take_text)
        imagePill = findViewById(R.id.iv_place_image_detail)
        tv_namePill =findViewById(R.id.name_pill_detail)
        tv_descriptionPill = findViewById(R.id.description_pill_detail)

        setDetail()
        ifOpenTheBoxAndTakeThePill()
    }

    private fun setDetail() {
        tv_namePill?.text=intent.getStringExtra(Constants.PILL_NAME)
        tv_descriptionPill?.text=intent.getStringExtra(Constants.PILL_DESCRIPTION)
        imagePill?.setImageURI(Uri.parse(intent.getStringExtra(Constants.PILL_IMAGE)))
        imagePill?.scaleType=ImageView.ScaleType.CENTER_CROP

    }


    private fun ifOpenTheBoxAndTakeThePill() {
        GlobalScope.launch {
            val flag =async { getResultFromBluetooth()}.await()
            if(flag) {
                runOnUiThread {
                    clickButton?.setBackgroundResource(R.drawable.item_click_green_take_pill_ripple)
                    textMessageWhenTheTimeToTakeThePill?.text = "Please press the green button to take a picture of the medicine"
                    clickButton?.setOnClickListener {
                        PhotoFromCamera()
                    }
                }
            }
        }
    }



    // sagy and inbal function
    private fun getResultFromBluetooth() :Boolean {
        //get the signal from the box ,if the customer take out the pill then change the openBoxPill flag to true:
        //this is example for while whiting for get back TRUE from edit text
        while (!testBluetooth?.text.toString().equals("TRUE"));


        return true

    }



    //dolev function

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA){
            // this is the pic for dolev task
            val pic : Bitmap =data!!.extras!!.get("data") as Bitmap
            //imagePill?.setImageBitmap(pic)



        }


    }



        private fun PhotoFromCamera(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

        ).withListener(object : MultiplePermissionsListener {
            override fun  onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted()){
                    val CameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(CameraIntent, CAMERA)
                }


            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
            {
                showRationalDialogForPermission()

            }
        }).onSameThread().check()





    }


    private fun showRationalDialogForPermission(){
        AlertDialog.Builder(this).setMessage("You turned off permission required for this feature. It can be enabled under the Applications setting ")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_->try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName,null)
                intent.data=uri
                startActivity(intent)

            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }

            }.setNegativeButton("Cancel"){
                    dialogInterface,_->dialogInterface.dismiss()

            }.show()

    }
    private fun menageToolBar(tollBar: Toolbar) {
        setSupportActionBar(tollBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tollBar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}