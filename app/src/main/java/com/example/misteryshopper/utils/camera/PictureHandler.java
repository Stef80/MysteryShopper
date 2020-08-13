package com.example.misteryshopper.utils.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.misteryshopper.R;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PictureHandler {
    private static final String SHOPPER = "Shopper" ;
    private static final String STORE = "Store" ;
    private static String imageFilePath;
    private static  Uri imageUri ;
    private static DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    public static final int REQUEST_IMAGE_CAPTURE = 1 ;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void getPicture(Activity context){
        File imageFile = null;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            try {
                imageFile = createImageFile(context.getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageUri = FileProvider.getUriForFile(context, "com.example.misteryshopper.provider", imageFile);
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void uploadImage(String id, ImageView view, ProgressBar bar, String flag, Context context){
        if(flag.equals(SHOPPER)) {
            mDBHelper.addImageToUserById(id, imageUri, context, new DBHelper.DataStatus() {
                @Override
                public void dataIsLoaded(List<?> obj, List<String> keys) {
                    Toast.makeText(context, context.getString(R.string.loading_success), Toast.LENGTH_LONG).show();
                }
            });
            if (view != null)
                Picasso.get().load(imageUri).fit().transform(new CircleTransform(true)).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        view.setVisibility(View.VISIBLE);
                        view.setImageBitmap(bitmap);
                        bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                         Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                          bar.setVisibility(View.VISIBLE);
                    }
                });
        }else if(flag.equals(STORE)){
            mDBHelper.addImageToStoreById(id, imageUri, context, new DBHelper.DataStatus() {
                @Override
                public void dataIsLoaded(List<?> obj, List<String> keys) {
                    Toast.makeText(context, context.getString(R.string.loading_success), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void uploaImageWithoutShow(String id, String flag , Context context){
        uploadImage(id,null,null,flag,context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static File  createImageFile(Context context) throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }


}
