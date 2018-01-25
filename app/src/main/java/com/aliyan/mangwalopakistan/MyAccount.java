package com.aliyan.mangwalopakistan;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Saad Mujeeb on 24/3/2017.
 */

public class MyAccount extends Fragment {
    EditText firstname;
    EditText lastName;
    TextView email;
    EditText address;
    EditText phone;
    Button edit;
    Button save;
    CircleImageView profileImage;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref;
    User u;
    View myView;
    Bitmap bmp;
    boolean loadImage;

    private String encoded;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.my_account, container, false);

        if(user == null){
           ref = db.getReference(Profile.getCurrentProfile().getId());
        }
        else{
          ref = db.getReference(user.getUid());
        }

        firstname = (EditText) myView.findViewById(R.id.firstName);
        lastName = (EditText) myView.findViewById(R.id.lastName);
        email = (TextView) myView.findViewById(R.id.email);
        address = (EditText) myView.findViewById(R.id.address);
        phone = (EditText) myView.findViewById(R.id.phoneNumber);
        edit = (Button) myView.findViewById(R.id.edit);
        save = (Button) myView.findViewById(R.id.save);
        profileImage = (CircleImageView) myView.findViewById(R.id.profile_image);


        loadImage = false;

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
                firstname.setText(u.firstName);
                lastName.setText(u.lastName);
                if(user != null)
                    email.setText(user.getEmail());
                else
                    email.setText("Connected Via Facebook");
                address.setText(u.address);
                phone.setText(u.phone);
                if(Profile.getCurrentProfile() != null){
                    Glide.with(getActivity())
                            .load(Profile.getCurrentProfile().getProfilePictureUri(100,100))
                            .asBitmap().into(profileImage);
                }
                else {
                    if (u.image == null) {
                        profileImage.setImageResource(R.drawable.add_photo);
                    } else {
                        byte[] decodedString = Base64.decode(u.image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profileImage.setImageBitmap(decodedByte);
                        encoded = u.image;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loadImage) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 3);
                }
            }
        });



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user != null) {
                    firstname.setEnabled(true);
                    lastName.setEnabled(true);
                    profileImage.setEnabled(true);
                    profileImage.setClickable(true);
                    loadImage = true;
                }
                address.setEnabled(true);
                phone.setEnabled(true);
                save.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u.firstName = firstname.getText().toString();
                u.lastName = lastName.getText().toString();
                u.address = address.getText().toString();
                u.phone = phone.getText().toString();
                u.image = encoded;
                ref.setValue(u);

                firstname.setEnabled(false);
                lastName.setEnabled(false);
                address.setEnabled(false);
                phone.setEnabled(false);
                profileImage.setEnabled(false);
                profileImage.setClickable(false);
                save.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                loadImage = false;
            }
        });

        return myView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bmp = BitmapFactory.decodeFile(picturePath);
            bmp = Bitmap.createScaledBitmap(bmp, 200, 200, false);

            profileImage.setImageBitmap(bmp);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //MenuItem item = menu.findItem(R.id.menuSearch);
        //item.setVisible(false);
    }
}
