package com.example.misteryshopper.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.misteryshopper.MainActivity;
import com.example.misteryshopper.R;
import com.example.misteryshopper.activity.RegisterEmployerActivity;
import com.example.misteryshopper.activity.RegisterShopperActivity;
import com.example.misteryshopper.activity.StoreListActivity;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.models.EmployerModel;
import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.utils.camera.PictureHandler;
import com.example.misteryshopper.utils.notification.MessageCreationService;

import java.util.List;

public class DialogUIHelper {

    private static DBHelper mDBHelper = FirebaseDBHelper.getInstance();
    private static SharedPrefConfig sharedPref;

    public static void createRegistationDialog(Context context) {
        sharedPref = new SharedPrefConfig(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_main, null);
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        Button retryBtn = dialogView.findViewById(R.id.retry_button);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button regShopBtn = dialogView.findViewById(R.id.reg_shop_button);
        regShopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, RegisterShopperActivity.class));
            }
        });

        Button regEmpBtn = dialogView.findViewById(R.id.reg_empl_button);
        regEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, RegisterEmployerActivity.class));
            }
        });
        dialog.show();

    }


    public static void createStoreDialog(final StoreModel model, Context context) {
           sharedPref = new SharedPrefConfig(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_store, null);
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        EditText idShop = dialogView.findViewById(R.id.id_store_add);
        EditText manager = dialogView.findViewById(R.id.manager_name_store_add);
        EditText city = dialogView.findViewById(R.id.city_store_add);
        EditText address = dialogView.findViewById(R.id.address_store_add);
        Button addImage = dialogView.findViewById(R.id.image_add_dialog);
        Button btnAdd = dialogView.findViewById(R.id.button_store_add);
        Button btnCancel = dialogView.findViewById(R.id.button_store_cancel);

        addImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String storeId = idShop.getText().toString();
                if(TextUtils.isEmpty(storeId))
                    idShop.setError(context.getString(R.string.field_required));
                else {
                    sharedPref.writePrefString("store_id", storeId);
                    StoreListActivity activity = (StoreListActivity) context;
                    PictureHandler.getPicture(activity);
                }
            }
        });
        btnCancel.setOnClickListener(onClick->dialog.dismiss());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idShop.getText().toString();
                String managerStr = manager.getText().toString();
                String cityStr = city.getText().toString();
                String adr = address.getText().toString();
                if(TextUtils.isEmpty(id))
                    idShop.setError(context.getString(R.string.field_required));
                if(TextUtils.isEmpty(managerStr))
                    manager.setError(context.getString(R.string.field_required));
                if(TextUtils.isEmpty(cityStr))
                    city.setError(context.getString(R.string.field_required));
                if(TextUtils.isEmpty(adr))
                    address.setError(context.getString(R.string.field_required));
                EmployerModel user = (EmployerModel) sharedPref.readLoggedUser();
                model.setIdStore(id);
                model.seteName(user.getEmName());
                model.setManager(manager.getText().toString());
                model.setCity(city.getText().toString());
                model.setAddress(address.getText().toString());
                model.setImageUri(new SharedPrefConfig(context).readPrefString("imageUri"));
                Log.i("USERINDIALOG", user.toString());
                model.setIdEmployer(user.getId());
             if(!(TextUtils.isEmpty(id) && TextUtils.isEmpty(managerStr) && TextUtils.isEmpty(cityStr) && TextUtils.isEmpty(adr))) {
                 mDBHelper.addStoreOfSpecificId(model, new DBHelper.DataStatus() {
                     @Override
                     public void dataIsLoaded(List<?> obj, List<String> keys) {
                         if (obj != null) {
                             Toast.makeText(context, context.getString(R.string.shop_added), Toast.LENGTH_LONG).show();
                             dialog.dismiss();
                         } else {
                             Toast.makeText(context, context.getString(R.string.shop_not_added), Toast.LENGTH_LONG).show();
                             idShop.setText("");
                             manager.setText("");
                             city.setText("");
                             address.setText("");
                         }
                     }

                 });
             }
            }
        });
        dialog.show();
    }



    public static void buildDialogHire(Context context,StoreModel model, String idEmployer,String mailShopper){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_hire, null);
        AlertDialog dialog = builder.create();
        dialog.setView(dialogView);
        DatePicker date = dialogView.findViewById(R.id.date_picker_ref);
        TextView storeId = dialogView.findViewById(R.id.store_id_ref);
        TextView shopperMail = dialogView.findViewById(R.id.mail_shopper_ref);
        TextView address = dialogView.findViewById(R.id.address_store_ref);
        EditText fee = dialogView.findViewById(R.id.fee_edtxt_ref);
        Button buttonHire = dialogView.findViewById(R.id.button_hire);
        dialogView.findViewById(R.id.button_cancel_ref).setOnClickListener(x -> dialog.dismiss());

        storeId.setText(model.getIdStore());
        address.setText(model.getCity()+"\n"+model.getAddress());
        shopperMail.setText(mailShopper);
        date.setMinDate(System.currentTimeMillis());

        buttonHire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String dateStr = date.getDayOfMonth()+"/"+
                       (date.getMonth()+1)+"/"+
                date.getYear();
               String feeStr =fee.getText().toString();
               if(TextUtils.isEmpty(feeStr)){
                   fee.setError(context.getString(R.string.field_required));
               }else {
                   Double feeNumber = Double.valueOf(feeStr);
                   String now =  String.valueOf(System.currentTimeMillis());
                   String addressStore = address.getText().toString();
                   HiringModel hiringModel = new HiringModel(now + idEmployer, idEmployer,
                           model.geteName(), mailShopper,addressStore, model.getIdStore(), dateStr, feeNumber);
                   mDBHelper.addHiringModel(hiringModel, new DBHelper.DataStatus() {
                       @Override
                       public void dataIsLoaded(List<?> obj, List<String> keys) {
                           mDBHelper.getTokenByMail(mailShopper, (obj1, keys1) -> {
                               Log.i("DILAOGTOKEN", (String) obj1.get(0));
                               MessageCreationService.buildMessage(context, (String) obj1.get(0),
                                       context.getString(R.string.notification_of_employment), model.getCity() + "\n" + model.getAddress()
                                       , dateStr, String.valueOf(feeNumber), model.geteName(),
                                       idEmployer,hiringModel.getId(),model.getIdStore());
                           });
                           dialog.dismiss();
                       }
                   });
               }
            }
        });

        dialog.show();
    }


    public static void buildDialogResponse(Context context, String name,String outcome){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_response, null);
        AlertDialog dialog = builder.create();

        dialog.setView(dialogView);
        TextView nameView = dialogView.findViewById(R.id.dialog_respnse_message_name);
        TextView outcomeView = dialogView.findViewById(R.id.dialog_response_outcome);
        Button btnOk = dialogView.findViewById(R.id.dialog_ok_button);

        nameView.setText(name);
        outcomeView.setText(outcome);
        btnOk.setOnClickListener(x-> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });
        dialog.show();
    }

}
