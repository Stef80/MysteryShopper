package com.example.misteryshopper.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misteryshopper.R;
import com.example.misteryshopper.activity.ShopperListActivity;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper;
import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.utils.camera.CircleTransform;
import com.example.misteryshopper.utils.notification.MessageCreationService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;


public class RecyclerViewConfig {


    private Context context;
    private MyListAdapter shopperAdapter;
    private Intent intent;

    public RecyclerViewConfig(Intent intent) {
        this.intent = intent;
    }

    public void setConfigList(RecyclerView recyclerView, Context context, List shoppers, List<String> keys, OnItemClickListener onItemClickListener) {
        this.context = context;
        shopperAdapter = new MyListAdapter(shoppers, keys, onItemClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(shopperAdapter);
    }


    class ShopperItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView surname;
        private TextView city;
        private String key;
        private ImageView imgProfile;
        private ProgressBar progressBar;
        private Button hireBtn;
        private SharedPrefConfig prefConfig;
        private StoreModel store;
        private OnItemClickListener clickListener;

        public ShopperItemView(@NonNull ViewGroup parent, OnItemClickListener clickListener) {
            super(LayoutInflater.from(context).inflate(R.layout.item_shopper_list, parent, false));
            name = itemView.findViewById(R.id.nameLabel);
            surname = itemView.findViewById(R.id.surname_label);
            city = itemView.findViewById(R.id.cityTxt);
            imgProfile = itemView.findViewById(R.id.imageView);
            hireBtn = itemView.findViewById(R.id.button_confirm);
            progressBar = itemView.findViewById(R.id.progressBarItem);
            prefConfig = new SharedPrefConfig(context);
            this.clickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        public void bind(ShopperModel shopper, String key) {
            String nameStr = shopper.getName().trim().replaceAll(" ","\n");
            String surnameStr = shopper.getSurname().trim().replaceAll(" ","\n");
            name.setText(nameStr);
            surname.setText(surnameStr);
            city.setText(shopper.getCity());
            String imageUri = shopper.getImageUri();
            //
            Log.i("SHOPPER", shopper.getEmail());
            Bundle extras = intent.getExtras();
            if(extras != null){
            store = (StoreModel) extras.get("store");
                Log.i(TAG, "bind: store if: " + store.toString());
              prefConfig.writPrefStore(store);
             }else{
                store = prefConfig.readPrefStore();
                Log.i(TAG, "bind: store else: " + store.toString());
            }
            if (!TextUtils.isEmpty(imageUri)) {
                Picasso.get().load(imageUri).resize(80, 80).transform(new CircleTransform()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imgProfile.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                      Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                      progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                         progressBar.setVisibility(View.VISIBLE);
                    }
                });

            }
            this.key = key;
            hireBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUIHelper.buildDialogHire(context, store, store.getIdEmployer(), shopper.getEmail());
                }
            });
        }


        @Override
        public void onClick(View v) {

            clickListener.onItemClick(this.getBindingAdapterPosition());
        }
    }


    class StoreItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView idShop;
        private TextView city;
        private TextView address;
        private TextView manger;
        private Button searchBtn;
        private TextView employerName;
        private ImageView imgBuilding;
        private String key;
        private OnItemClickListener clickListener;

        public StoreItemView(@NonNull ViewGroup parent, OnItemClickListener listener) {
            super(LayoutInflater.from(context).inflate(R.layout.item_store_list, parent, false));
            idShop = itemView.findViewById(R.id.id_shop);
            city = itemView.findViewById(R.id.city_shop_text);
            manger = itemView.findViewById(R.id.shop_manager_name);
            address = itemView.findViewById(R.id.address_shop_text);
            searchBtn = itemView.findViewById(R.id.button_serch_shop);
            employerName = itemView.findViewById(R.id.employer_name);
            imgBuilding = itemView.findViewById(R.id.image_building);

            this.clickListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(StoreModel storeModel, String key) {
            idShop.setText(storeModel.getIdStore());
            employerName.setText(storeModel.geteName());
            manger.setText(storeModel.getManager());
            city.setText(storeModel.getCity());
            address.setText(storeModel.getAddress());
            String img = storeModel.getImageUri();
            if (!TextUtils.isEmpty(img)) {
                Picasso.get().load(img).fit().into(imgBuilding);
            }
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: storemodel: " + storeModel.toString());
                    Intent search = new Intent(context, ShopperListActivity.class);
                    search.putExtra("store", storeModel);
                    context.startActivity(search);
                }
            });
            this.key = key;
        }


        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: is clicked");
            clickListener.onItemClick(this.getBindingAdapterPosition());
        }
    }


    class HireItemView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView eName;
        private TextView idStore;
        private TextView hAddress;
        private TextView hDate;
        private TextView hFee;
        private TextView confirmLabel;
        private Button btnConfirm;
        private Button btnDecline;
        private DBHelper mDBHelper;
        private OnItemClickListener clickListener;
        private String key;


        public HireItemView(@NonNull ViewGroup parent, OnItemClickListener clickListener) {
            super(LayoutInflater.from(context).inflate(R.layout.item_hiring_list_fragment, parent, false));
            mDBHelper = FirebaseDBHelper.getInstance();
            eName = itemView.findViewById(R.id.hire_name_employer);
            idStore = itemView.findViewById(R.id.hire_id_store);
            hAddress = itemView.findViewById(R.id.hire_address);
            hDate = itemView.findViewById(R.id.hire_date);
            hFee = itemView.findViewById(R.id.hire_fee);
            confirmLabel = itemView.findViewById(R.id.hire_confirm_label);
            btnConfirm = itemView.findViewById(R.id.hire_button_confirm);
            btnDecline = itemView.findViewById(R.id.hire_button_decline);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(HiringModel hiringModel, String key) {
            eName.setText(hiringModel.getEmployerName());
            idStore.setText(hiringModel.getIdStore());
            hFee.setText(String.valueOf(hiringModel.getFee()));
            hAddress.setText(hiringModel.getAddress());
            String date = hiringModel.getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date hireDate = null;
            try {
                hireDate = formatter.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hDate.setText(date);

            String confirm = hiringModel.getAccepted();

            if (confirm == null || confirm.equals("")) {
                confirmLabel.setText(R.string.waiting);
                enableButtons();

            } else if (confirm.equals("declined")) {
                confirmLabel.setText(R.string.declined);
                disableButton();

            } else if (confirm.equals("accepted")) {
                confirmLabel.setText(R.string.accepted);
                disableButton();
            }

            Date nowDate = new Date();
            if (hireDate.before(nowDate)) {
                mDBHelper.setHireDone(hiringModel.getId());
                confirmLabel.setText(R.string.done);
                disableButton();
            }

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefConfig preferences = new SharedPrefConfig(context);
                    ShopperModel shopperModel = (ShopperModel) preferences.readLoggedUser();
                    String name = shopperModel.getName();
                    String surname = shopperModel.getSurname();
                    mDBHelper.getTokenById(hiringModel.getIdEmployer(), new DBHelper.DataStatus() {
                        @Override
                        public void dataIsLoaded(List<?> obj, List<String> keys) {
                            String token = (String) obj.get(0);
                            MessageCreationService.buildMessage(context, token, context.getString(R.string.response_notification),
                                    name + " " + surname, context.getString(R.string.accepted));
                            mDBHelper.setOutcome(hiringModel.getId(), "accepted", (status, key) -> {
                                Toast.makeText(context, context.getString(R.string.accepted), Toast.LENGTH_SHORT).show();
                            });
                            confirmLabel.setText(context.getString(R.string.accepted));
                            disableButton();
                        }
                    });

                }
            });

            btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefConfig preferences = new SharedPrefConfig(context);
                    ShopperModel shopperModel = (ShopperModel) preferences.readLoggedUser();
                    String name = shopperModel.getName();
                    String surname = shopperModel.getSurname();
                    mDBHelper.getTokenById(hiringModel.getIdEmployer(), new DBHelper.DataStatus() {
                        @Override
                        public void dataIsLoaded(List<?> obj, List<String> keys) {
                            String token = (String) obj.get(0);
                            MessageCreationService.buildMessage(context, token, context.getString(R.string.response_notification),
                                    name + " " + surname, context.getString(R.string.declined));
                            mDBHelper.setOutcome(hiringModel.getId(), "declined", (status, key) -> {
                                Toast.makeText(context, context.getString(R.string.declined), Toast.LENGTH_SHORT).show();
                            });
                            confirmLabel.setText(context.getString(R.string.declined));
                            disableButton();
                        }
                    });

                }
            });
            this.key = key;
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(this.getBindingAdapterPosition());
        }

        private void disableButton() {
            btnConfirm.setEnabled(false);
            btnConfirm.setTextColor(Color.WHITE);
            btnDecline.setEnabled(false);
            btnDecline.setTextColor(Color.WHITE);
        }

        private void enableButtons() {
            btnConfirm.setEnabled(true);
            btnConfirm.setTextColor(context.getResources().getColor(R.color.labelAndText));
            btnDecline.setEnabled(true);
            btnDecline.setTextColor(context.getResources().getColor(R.color.labelAndText));
        }
    }

    class MyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int ITEM_HIRE = 2, ITEM_STORE = 1, ITEM_SHOPPER = 0;
        private List<Object> modelList;
        private List<String> keys;
        private OnItemClickListener clickListener;

        public MyListAdapter(List modelList, List<String> keys, OnItemClickListener clickListener) {
            this.modelList = modelList;
            this.keys = keys;
            this.clickListener = clickListener;
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            switch (viewType) {
                case ITEM_SHOPPER:
                    holder = new ShopperItemView(parent, clickListener);
                    break;
                case ITEM_STORE:
                    holder = new StoreItemView(parent, clickListener);
                    break;
                case ITEM_HIRE:
                    holder = new HireItemView(parent,clickListener);
            }
            return holder;
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case ITEM_SHOPPER:
                    ShopperItemView itemView = (ShopperItemView) holder;
                    itemView.bind((ShopperModel) modelList.get(position), keys.get(position));
                    break;
                case ITEM_STORE:
                    StoreItemView itemViewStore = (StoreItemView) holder;
                    itemViewStore.bind((StoreModel) modelList.get(position), keys.get(position));
                    break;
                case ITEM_HIRE:
                    HireItemView itemViewHire = (HireItemView) holder;
                    itemViewHire.bind((HiringModel) modelList.get(position), keys.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }


        public int getItemViewType(int position) {
            if (modelList.get(position) instanceof ShopperModel) {
                return ITEM_SHOPPER;
            } else if (modelList.get(position) instanceof StoreModel) {
                return ITEM_STORE;
            } else if (modelList.get(position) instanceof HiringModel) {
                return ITEM_HIRE;
            }
            return -1;
        }


    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
