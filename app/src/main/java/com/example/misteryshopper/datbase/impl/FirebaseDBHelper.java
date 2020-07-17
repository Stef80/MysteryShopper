package com.example.misteryshopper.datbase.impl;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.misteryshopper.R;
import com.example.misteryshopper.datbase.DBHelper;
import com.example.misteryshopper.models.EmployerModel;
import com.example.misteryshopper.models.HiringModel;
import com.example.misteryshopper.models.ShopperModel;
import com.example.misteryshopper.models.StoreModel;
import com.example.misteryshopper.models.User;
import com.example.misteryshopper.utils.DialogUIHelper;
import com.example.misteryshopper.utils.SharedPrefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDBHelper implements DBHelper {

    private static final String STORE = "Store";
    private static final String HIRE = "Hire";
    private final String USER = "User";
    private final String EMPLOYER = "Employer";
    private final String SHOPPER = "Shopper";
    private final String EMAIL = "email";


    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseInstanceId mFirebaseId;
    private FirebaseStorage mStorage;
    public static DBHelper mDbHelper;


    private FirebaseDBHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseId = FirebaseInstanceId.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }


    public static DBHelper getInstance() {
        if (mDbHelper == null) {
            mDbHelper = new FirebaseDBHelper();
        }
        return mDbHelper;
    }


    @Override
    public void readShoppers(final DataStatus dataStatus) {
        List<ShopperModel> list = new ArrayList<>();
        Query query = mDatabase.getReference(USER).orderByChild("role").equalTo(SHOPPER);
        doQuery(query, ShopperModel.class, list, dataStatus);
    }


    @Override
    public void register(final User model, String email, String password, Context context, final DataStatus status) {
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, context.getString(R.string.registration_failed), Toast.LENGTH_LONG).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    String UId = mAuth.getCurrentUser().getUid();
                    model.setId(UId);
                    if (model instanceof EmployerModel) model.setRole(EMPLOYER);
                    if (model instanceof ShopperModel) model.setRole(SHOPPER);
                    if (task.isSuccessful()) {
                        updateUsers(model, UId, context, status);
                    } else {
                        Toast.makeText(context, context.getString(R.string.registration_failed), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    @Override
    public void login(String userMail, String password, final Context context, DataStatus status) {
        if (!TextUtils.isEmpty(userMail) && !TextUtils.isEmpty(password))
            mAuth.signInWithEmailAndPassword(userMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final String uId = task.getResult().getUser().getUid();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.login_success));
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRole(uId, new DataStatus() {
                                    @Override
                                    public void dataIsLoaded(List<?> obj, List<String> keys) {
                                        String role = (String) obj.get(0);
                                        if (role.equals(SHOPPER)) {
                                            getShopperByMail(userMail, status);
                                        } else {
                                            getEmployerByMail(userMail, status);
                                        }
                                    }

                                });
                            }
                        });
                        builder.show();
                    } else {
                        DialogUIHelper.createRegistationDialog(context);
                    }
                }
            });
    }


    @Override
    public void signOut(Context context) {
        new SharedPrefConfig(context).cancelData();
        mAuth.signOut();
    }


    @Override
    public void getShopperByMail(String mail, DataStatus status) {
        List<ShopperModel> shopListUpdate = new ArrayList<>();
        Query query = mDatabase.getReference(USER).orderByChild(EMAIL).equalTo(mail);
        doQuery(query, ShopperModel.class, shopListUpdate, status);
    }


    @Override
    public void getEmployerByMail(String mail, DataStatus status) {
        List<EmployerModel> ListUpdate = new ArrayList<>();
        Query query = mDatabase.getReference(USER).orderByChild(EMAIL).equalTo(mail);
        doQuery(query, EmployerModel.class, ListUpdate, status);
    }


    @Override
    public Object getCurrentUser() {
        return mAuth.getCurrentUser();
    }


    @Override
    public void getUserById(String UId, DataStatus status) {
        List<User> userList = new ArrayList<>();
        mReference = mDatabase.getReference(USER);
        Query query = mReference.orderByChild("id").equalTo(UId);
        doQuery(query, User.class, userList, status);
    }


    @Override
    public void getRole(String uId, DataStatus status) {
        List<String> roleList = new ArrayList<>();
        Query query = mDatabase.getReference(USER).orderByChild("id").equalTo(uId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roleList.clear();
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    User user = node.getValue(User.class);
                    String role = user.getRole();
                    roleList.add(role);
                }
                status.dataIsLoaded(roleList, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("ERROR", databaseError.toString());
            }
        });
    }


    @Override
    public void readStoreOfSpecificUser(String UId, DataStatus status) {
        List<StoreModel> storeList = new ArrayList<>();
        mReference = mDatabase.getReference(STORE);
        Query query = mReference.orderByChild("idEmployer").equalTo(UId);
        doQuery(query, StoreModel.class, storeList, status);
    }


    @Override
    public void addStoreOfSpecificId(StoreModel model, DataStatus status) {
        mReference = mDatabase.getReference(STORE);
        if (!model.getIdStore().equals(""))
            mReference.child(model.getIdStore()).setValue(model)
                    .addOnFailureListener(x -> status.dataIsLoaded(null, null))
                    .addOnSuccessListener(x -> status.dataIsLoaded(new ArrayList<StoreModel>(1), null));
        else
            status.dataIsLoaded(null, null);
    }


    @Override
    public void addTokenToUser(User user, Context context) {
        mFirebaseId.getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    Log.i("TOKEN", token);
                    mDatabase.getReference(USER).child(user.getId()).child("token").setValue(token);
                }
            }
        });
    }

    @Override
    public void getTokenByMail(String mail, DataStatus status) {
        Log.i("MAILDB", mail);
        List<String> tokens = new ArrayList<>();
        mDatabase.getReference().child(USER).orderByChild("email").equalTo(mail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    User user = node.getValue(User.class);
                    tokens.add(user.getToken());
                    Log.i("TOKENS", tokens.toString());
                }
                status.dataIsLoaded(tokens, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getTokenById(String id, DataStatus status) {
        List<String> tokens = new ArrayList<>();
        mDatabase.getReference().child(USER).orderByChild("id").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    User user = node.getValue(User.class);
                    tokens.add(user.getToken());
                    Log.i("TOKENS", tokens.toString());
                }
                status.dataIsLoaded(tokens, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void addHiringModel(HiringModel model, DataStatus dataStatus) {
        mDatabase.getReference(HIRE).child(model.getId()).setValue(model)
                .addOnCompleteListener(x -> dataStatus.dataIsLoaded(null, null));
    }

    @Override
    public void setOutcome(String hId, String outcome, DataStatus status) {
        mDatabase.getReference(HIRE).child(hId).child("accepted").setValue(outcome)
                .addOnCompleteListener(x -> status.dataIsLoaded(null, null));
    }

    @Override
    public void getHireByMail(String mail, DataStatus status) {
        List<HiringModel> list = new ArrayList<>();
        Query query = mDatabase.getReference(HIRE).orderByChild("mailShopper").equalTo(mail);
        doQuery(query, HiringModel.class, list, status);
    }

    @Override
    public void setHireDone(String id) {
        mDatabase.getReference(HIRE).child(id).child("done").setValue(true);
    }

    @Override
    public void addImageToUserById(String id, Uri imageUri, Context context, DataStatus status) {
        mStorage.getReference("uploads").child(System.currentTimeMillis() + "."
                + getFileExtension(imageUri, context)).putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mDatabase.getReference(USER).child(id).child("imageUri").setValue(uri.toString());
                                status.dataIsLoaded(null,null);
                            }
                        });
                    }
                });
    }

    @Override
    public void updateUsers(User model, String UId, Context context, DataStatus status) {
        mReference = mDatabase.getReference(USER);
        mReference.child(UId).setValue(model).addOnSuccessListener(aVoid ->
                status.dataIsLoaded(null, null)
        );
    }


    private void doQuery(Query query, Class myClass, List listUpdate, DataStatus status) {
        List<String> keys = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUpdate.clear();
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    keys.add(node.getKey());
                    listUpdate.add(node.getValue(myClass));
                }

                Log.i("QUERYLIST", listUpdate.toString());
                status.dataIsLoaded(listUpdate, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("ERROR", databaseError.toString());
            }
        });

    }

    private String getFileExtension(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


}