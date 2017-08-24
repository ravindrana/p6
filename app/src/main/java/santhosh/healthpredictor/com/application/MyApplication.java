package santhosh.healthpredictor.com.application;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by AUS8KOR on 8/14/2017.
 */

public class MyApplication extends Application {
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public FirebaseUser getFirebaseUser(){
        return mFirebaseUser;
    }

    public void setFirebaseUser(FirebaseUser mFirebaseUser) {
        this.mFirebaseUser = mFirebaseUser;
    }

    public FirebaseAuth getFirebaseAuth(){
        if(mFirebaseAuth == null){
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }

}
