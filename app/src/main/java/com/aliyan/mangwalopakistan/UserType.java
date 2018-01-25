package com.aliyan.mangwalopakistan;

import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Aliyan on 5/4/2017.
 */
public class UserType {



    public static String getType(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return Profile.getCurrentProfile().getId();
        }
        else{
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    public static String getEmail(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return "Facebook";
        }
        else{
            return FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
    }


}
