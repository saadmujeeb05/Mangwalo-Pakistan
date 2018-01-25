package com.aliyan.mangwalopakistan;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.net.Uri;
import android.sax.StartElementListener;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProductDetails extends AppCompatActivity {

    Item item;
    FirebaseStorage sr = FirebaseStorage.getInstance();
    StorageReference s = sr.getReference().child("ITEM");
    DatabaseHelper dbHelper;
    ImageView itemPic;
    TextView itemName;
    TextView itemCategory;
    TextView itemPrice;
    Button cartBtn;
    Button wishlistBtn;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        dbHelper = new DatabaseHelper(this);

        userType = UserType.getType();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        item = (Item) getIntent().getSerializableExtra("item");
        itemPic = (ImageView) findViewById(R.id.itemPic);
        itemName = (TextView) findViewById(R.id.itemName);
        itemCategory = (TextView) findViewById(R.id.itemCategory);
        itemPrice = (TextView) findViewById(R.id.itemPrice);
        cartBtn = (Button) findViewById(R.id.cartBtn);
        wishlistBtn = (Button) findViewById(R.id.wishlistBtn);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("WishList");
        dbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                        if (child.getKey().contains(userType + item.getName())) {
                            wishlistBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tootadil_white, 0, 0, 0);
                            wishlistBtn.setText("Remove from Wish List");
                            return;
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });


            if (dbHelper.checkForCart(userType, item.getName())) {
                cartBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping_cart_remove_white, 0, 0, 0);
                cartBtn.setText("Remove from Cart");
            }

        s = sr.getReference().child("ITEM").child(String.valueOf(item.getImage())+".jpg");
        Glide
                .with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(s)
                .into(itemPic);


        itemName.setText(item.getName());
        itemCategory.setText(item.getCategory());
        itemPrice.setText("Rs. " + item.getPrice());

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("WishList");
                if(wishlistBtn.getText().toString().equals("Add to Wish List")) {
                    Item i = new Item(item.getName(), item.getPrice(), item.getCategory(), item.getImage());
                    databaseReference.child(userType + item.getName()).setValue(i);
                    Toast.makeText(getApplicationContext(), "Item Added to Wish List", Toast.LENGTH_SHORT).show();
                    wishlistBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tootadil_white, 0, 0, 0);
                    wishlistBtn.setText("Remove from Wish List");
                }
                else if(wishlistBtn.getText().toString().equals("Remove from Wish List"))
                {
                    databaseReference.child(userType+item.getName()).removeValue();
                    Toast.makeText(getApplication(),"Item Removed from Wish List",Toast.LENGTH_SHORT).show();
                    wishlistBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wishlist_white, 0, 0, 0);
                    wishlistBtn.setText("Add to Wish List");
                }
            }
        });


        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartBtn.getText().toString().equals("Add to Cart")) {
                    String userID = userType;
                    String name = item.getName();
                    Long price = item.getPrice();
                    String category = item.getCategory();
                    Long imageRef = item.getImage();
                    ContentValues values = new ContentValues();
                    values.put(CartProvider.Name,name);
                    values.put(CartProvider.User,userID);
                    values.put(CartProvider.Price,price);
                    values.put(CartProvider.Category,category);
                    values.put(CartProvider.ImageRef,imageRef);
                    if(getContentResolver().insert(CartProvider.CONTENT_URL,values) != null) {
                        Toast.makeText(getApplicationContext(), "Item Added to the Cart", Toast.LENGTH_SHORT).show();
                        cartBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping_cart_remove_white, 0, 0, 0);
                        cartBtn.setText("Remove from Cart");
                    }
                }
                else if(cartBtn.getText().toString().equals("Remove from Cart"))
                {
                    String userID = userType;
                    String name = item.getName();

                    if (getContentResolver().delete(CartProvider.CONTENT_URL,CartProvider.COL_1 + "=? and " + CartProvider.COL_2 + "=?",new String[]{userID,name}) == 1 ){
                        Toast.makeText(getApplicationContext(), "Item Removed from Cart", Toast.LENGTH_SHORT).show();
                        cartBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shopping_cart_add_white, 0, 0, 0);
                        cartBtn.setText("Add to Cart");
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
