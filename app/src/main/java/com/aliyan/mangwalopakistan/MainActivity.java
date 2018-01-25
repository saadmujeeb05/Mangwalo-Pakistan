package com.aliyan.mangwalopakistan;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.app.FragmentManager fragmentManager = getFragmentManager();

    private FirebaseAuth firebaseauth;
    private FirebaseUser user;
    private TextView name;
    private TextView email;
    private CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        firebaseauth = FirebaseAuth.getInstance();
        user = firebaseauth.getCurrentUser();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View header = navView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.name);
        email = (TextView) header.findViewById(R.id.email);
        image = (CircleImageView) header.findViewById(R.id.profile_image);

        loadProfile();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    invalidateOptionsMenu();
                }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadProfile();

                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                invalidateOptionsMenu();
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new Home()).commit();
        }
    }

    private void loadProfile()
    {
        Profile prof = Profile.getCurrentProfile();
        if(prof != null){
            name.setText(prof.getFirstName()+" "+prof.getLastName());
            email.setText("Facebook");
            try {
                URL IM = new URL("https://graph.facebook.com/"+prof.getId()+"/picture?type=normal");
                HttpsURLConnection connection = (HttpsURLConnection) IM.openConnection();
                HttpsURLConnection.setFollowRedirects(HttpsURLConnection.getFollowRedirects());
                connection.setDoInput(true);
                connection.connect();
                Bitmap bitmap= BitmapFactory.decodeStream(connection.getInputStream());
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference(user.getUid());
            email.setText(user.getEmail());
            db.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                    User info = dataSnapshot.getValue(User.class);
                    name.setText(info.firstName + " " + info.lastName);
                    if (info.image == null) {
                        image.setImageResource(R.drawable.add_photo);
                    } else {
                        byte[] decodedString = Base64.decode(info.image, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        image.setImageBitmap(decodedByte);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.logout){
            if(Profile.getCurrentProfile() != null){
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }
            else {
                firebaseauth.signOut();
            }
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new Orders()).commit();
        }
        else if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new Home()).commit();
        }
        else if (id == R.id.nav_my_account) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new MyAccount()).commit();
        }

        else if (id == R.id.nav_shopping_cart) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new ShoppingCart()).commit();
        }
        else if (id == R.id.nav_wishlist) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new WishList()).commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
