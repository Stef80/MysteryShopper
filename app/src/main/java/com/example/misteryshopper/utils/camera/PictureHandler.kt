package com.example.misteryshopper.utils.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper.Companion.instance
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale

object PictureHandler {
    private const val SHOPPER = "Shopper"
    private const val STORE = "Store"
    private var imageFilePath: String? = null
    private var imageUri: Uri? = null
    private val mDBHelper = instance
    const val REQUEST_IMAGE_CAPTURE: Int = 1

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getPicture(context: Activity) {
        var imageFile: File? = null
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            try {
                imageFile = createImageFile(context.getApplicationContext())
            } catch (e: IOException) {
                e.printStackTrace()
            }

            imageUri = FileProvider.getUriForFile(
                context,
                "com.example.misteryshopper.provider",
                imageFile!!
            )
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    fun uploadImage(
        id: String,
        view: ImageView?,
        bar: ProgressBar?,
        flag: String,
        context: Context
    ) {
      /*  if (flag == SHOPPER) {
            mDBHelper.addImageToUserById(id, imageUri!!, context, object : DataStatus {
                override fun dataIsLoaded(obj: MutableList<*>?, keys: MutableList<String?>?) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.loading_success),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            if (view != null) Picasso.get().load(imageUri).transform(CircleTransform(true))
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                        view.setVisibility(View.VISIBLE)
                        view.setImageBitmap(bitmap)
                        bar.setVisibility(View.GONE)
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        bar.setVisibility(View.GONE)
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        bar.setVisibility(View.VISIBLE)
                    }
                })
        } else if (flag == STORE) {
            mDBHelper.addImageToStoreById(id, imageUri!!, context, object : DataStatus {
                override fun dataIsLoaded(obj: MutableList<*>?, keys: MutableList<String?>?) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.loading_success),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun uploaImageWithoutShow(id: String, flag: String, context: Context) {
        uploadImage(id, null, null, flag, context)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp =
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        imageFilePath = image.getAbsolutePath()
        return image
    }
}
