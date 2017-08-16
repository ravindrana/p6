package santhosh.healthpredictor.com.data;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import santhosh.healthpredictor.com.R;
import santhosh.healthpredictor.com.application.MyApplication;

/**
 * Created by AUS8KOR on 8/14/2017.
 */

public class MyFireBaseDB {
    private static MyFireBaseDB mDB;
    //Firebase DB
    private FirebaseDatabase mFirebaseDB;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public static MyFireBaseDB getInstance() {
        if(mDB == null){
            synchronized (MyFireBaseDB.class) {
                if(mDB == null){
                    mDB= new MyFireBaseDB();
                }
            }
        }
        return mDB;
    }

    private MyFireBaseDB() {
    }

    public void init(Context context){
        mContext = context;
        //init fore
        if(mFirebaseDB == null) {
            mFirebaseDB = FirebaseDatabase.getInstance();
        }
        //set app title
        mFirebaseDB.getReference("app_title").setValue(mContext.getString(R.string.app_name));
    }

    public void saveUserProfile(UserProfile userProfile, final OnCallback callback){
        //get unique id from firebase auth
        String userId = ((MyApplication)mContext.getApplicationContext()).getFirebaseUser().getUid();
        DatabaseReference userDB  = mFirebaseDB.getReference("users").child(userId);
        /*if(userDB == null) {
            userDB.setValue(userProfile);
        }*/
        userDB.setValue(userProfile);
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onSuccess("success");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
    /**
     * Callback for caller
     */
    public interface OnCallback{
        void onSuccess(String response);
    };
}
