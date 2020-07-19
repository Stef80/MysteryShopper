package com.example.misteryshopper.datbase;

import android.content.Context;
import android.net.Uri;


import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.models.User;

import java.util.List;

public interface DBHelper {


    public void readShoppers(DataStatus status);

    public void register(final User model, String email, String password, Context context, final DataStatus status);

    public void login(String user, String password, final Context context,final DataStatus status);

    public void signOut(Context context);

    public void getShopperByMail(String mail, DataStatus callback);

    public void getEmployerByMail(String mail, DataStatus callback);

    public Object getCurrentUser();

    public void  getUserById(String UId,DataStatus status);

    public String getIdCurrentUser();

    public void getRole(String uId,DataStatus status);

    public void readStoreOfSpecificUser(String UId, DataStatus status);

    public void updateUsers(User model, String id, Context context, DataStatus status);

    public void addStoreOfSpecificId(StoreModel model, DataStatus status);

    void addTokenToUser(User user, Context context);

    void getTokenByMail(String mail, DataStatus status);

    void getTokenById(String id,DataStatus status);

    void addHiringModel(HiringModel model, DataStatus dataStatus);

    void setOutcome(String hId,String outcome,DataStatus status);

    void getHireByMail(String mail, DataStatus status);

    void setHireDone(String id);

    void addImageToUserById(String id, Uri imageUri,Context context, DataStatus status);

    void addImageToStoreById(String id,Uri imageUri,Context context, DataStatus status);


    public interface DataStatus {
        void dataIsLoaded(List<? extends Object> obj, List<String> keys);

    }

}
