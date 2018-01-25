package com.aliyan.mangwalopakistan;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registration2 extends AppCompatActivity {

    private EditText firstName;
    private EditText LastName;
    private EditText address;
    private EditText phone;
    private CircleImageView photo;
    private Button CreateAccountBtn;
    private Bitmap bmp;
    private String encoded;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    boolean loadImage;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        loadImage = true;
        type = getIntent().getStringExtra("type");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if (user == null && Profile.getCurrentProfile() == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }



        databaseReference = FirebaseDatabase.getInstance().getReference();

        firstName = (EditText) findViewById(R.id.firstName);
        LastName = (EditText) findViewById(R.id.lastName);
        address = (EditText) findViewById(R.id.address);
        phone = (EditText) findViewById(R.id.phoneNumber);
        photo = (CircleImageView) findViewById(R.id.userImage);
        CreateAccountBtn = (Button) findViewById(R.id.CreateAccountBtn);

        if(type.equals("facebook")){
            loadImage = false;
            firstName.setText(Profile.getCurrentProfile().getFirstName());
            LastName.setText(Profile.getCurrentProfile().getLastName());
            firstName.setEnabled(false);
            LastName.setEnabled(false);
            firstName.setClickable(false);
            CreateAccountBtn.setText("REGISTER");
            LastName.setClickable(false);
            Glide.with(getApplicationContext())
                    .load(Profile.getCurrentProfile().getProfilePictureUri(100,100))
                    .asBitmap().into(photo);
            photo.setClickable(false);
            photo.setEnabled(false);


        }

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loadImage) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 3);
                }
            }
        });
    }


    private void saveUser() {
        String firstN = firstName.getText().toString().trim();
        String lastN = LastName.getText().toString().trim();
        String add = address.getText().toString().trim();
        String number = phone.getText().toString().trim();

        View focusView = null;

        if (TextUtils.isEmpty(firstN)) {
            firstName.setError("Please Enter First Name");
            focusView = firstName;
            focusView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(lastN)) {
            LastName.setError("Please Enter Last Name");
            focusView = LastName;
            focusView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(add)) {
            address.setError("Please Enter Address");
            focusView = address;
            focusView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(number)) {
            phone.setError("Please Enter Phone Number");
            focusView = phone;
            focusView.requestFocus();
            return;
        }

        User info = new User(firstN, lastN, add, number, encoded);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        if(type.equals("facebook")){
            databaseReference.child(Profile.getCurrentProfile().getId()).setValue(info);
        }
        else {
            databaseReference.child(user.getUid()).setValue(info);
        }
        Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(Profile.getCurrentProfile()!=null){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user.delete();
        firebaseAuth.signOut();
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bmp = BitmapFactory.decodeFile(picturePath);
            bmp = Bitmap.createScaledBitmap(bmp, 200, 200, false);

            photo.setImageBitmap(bmp);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

    }

}