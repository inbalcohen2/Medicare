package com.example.pillreminder.activity

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import com.example.pillreminder.R
import com.example.pillreminder.database.DataBaseHandler
import com.example.pillreminder.models.PillModel
import com.example.pillreminder.saveTimeData
import com.example.pillreminder.models.timePill
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class addPills : AppCompatActivity() , View.OnClickListener {

    companion object{
        private const val CAMERA_PERMISSION_CODE=1
        private const val CAMERA=2
        private const val GALLERY=3
        private const val IMAGE_DIRECTORY="PillImage"

    }


    private var Pilltime: timePill?=null
    private var timeClick : TimePicker? =null
    private var  savePillAdd : Button?= null
    private var tv_imagePill : TextView? =null
    private var imagePill : AppCompatImageView? =null
    private var tv_namePill : TextView? =null
    private var tv_descriptionPill : TextView? =null

    private var latitude: Double=0.0
    private var longitude: Double=0.0
    private var saveImageToInternalStorage : Uri? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pills)
        var tollBar : Toolbar = findViewById(R.id.toolbar_add_pill)
        menageToolBar(tollBar)


        timeClick = findViewById(R.id.datePicker)
        savePillAdd = findViewById(R.id.btn_save)
        tv_imagePill =findViewById(R.id.tv_add_image)
        imagePill = findViewById(R.id.vvv)
        tv_namePill =findViewById(R.id.pill_name_text)
        tv_descriptionPill = findViewById(R.id.pill_description_text)

        savePillAdd?.setOnClickListener(this)
        tv_imagePill?.setOnClickListener(this)



    }




    fun setTime(hour:Int ,minute:Int){
        val saveDataTime= saveTimeData(applicationContext)
        saveDataTime.setAlarm(hour,minute)
    }

    private fun menageToolBar(tollBar: Toolbar) {
        setSupportActionBar(tollBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tollBar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.btn_save ->{
                when{
                tv_namePill?.text.isNullOrEmpty()->{
                        Toast.makeText(this, "Please enter the name pill", Toast.LENGTH_SHORT).show()
                    }
                    tv_descriptionPill?.text.isNullOrEmpty()->{
                        Toast.makeText(this, "Please enter the description pill", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage==null ->{
                        Toast.makeText(this, "Please select an image ", Toast.LENGTH_SHORT).show()
                    }
                    else->{
                        val pillModle = PillModel(
                            0,
                            tv_namePill?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            tv_descriptionPill?.text.toString(),
                            "10.12",
                            "10:00",
                            0.0,
                            0.0)


                        val databaseHandler=DataBaseHandler(this)
                        val longStore = databaseHandler.addPill(pillModle)


                        if(longStore>0){
//                            Toast.makeText(this, "Good you add a new pill ", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                        }

                       // Toast.makeText(this,"${timeClick?.hour}:${timeClick?.minute} ",Toast.LENGTH_SHORT).show()
                        setTime(timeClick!!.hour,timeClick!!.minute)
                        finish()
                    }
                }
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from Gallery", "Capture photo from camera")

                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> PhotoFromCamera()
//                        1 -> Toast.makeText(this, "coming soon...", Toast.LENGTH_SHORT).show()
                    }


                }.show()



            }




        }






    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                if(data!=null){
                    val URI =data.data
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,URI)
                       saveImageToInternalStorage=saveImageRoInternalStorage(selectedImageBitmap)

                        Log.e("Save image ","path :: $saveImageToInternalStorage")

                        imagePill?.setImageBitmap(selectedImageBitmap)

                    }catch (e:IOException){e.printStackTrace()}
                }
            }
            else if (requestCode == CAMERA){
                val pic : Bitmap =data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage=saveImageRoInternalStorage(pic)
                Log.e("Save image from camera ","path :: $saveImageToInternalStorage")
                imagePill?.setImageBitmap(pic)



            }
        }


    }
    private fun saveImageRoInternalStorage(bitmap: Bitmap):Uri{
        val wrapper =ContextWrapper(applicationContext)
        var file =wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()

        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)


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
            override fun  onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
            {
                showRationalDialogForPermission()

            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun  onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)

                Toast.makeText(this@addPills,"Now you can select an image from GALLERY",Toast.LENGTH_SHORT).show()
            }


            }
            override fun  onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
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

    }
