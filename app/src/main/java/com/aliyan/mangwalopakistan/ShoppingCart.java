package com.aliyan.mangwalopakistan;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Saad Mujeeb on 24/3/2017.
 */

public class ShoppingCart extends Fragment {

    DatabaseHelper dbHelper;
    ArrayList<Item> items = new ArrayList<>();
    String cart = "";
    int totalCost = 0;
    ProgressBar pb;
    ListView lv;
    Button order;
    CustomAdapter itemAdapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    View myView;
    SearchView searchView;
    String userType;
    ImageView empty;
    TextView emptyText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView =inflater.inflate(R.layout.shopping_cart,container,false);
        itemAdapter = new CustomAdapter(getActivity(), R.layout.cart_row, items,null);
        lv = (ListView) myView.findViewById(R.id.itemListCart);
        lv.setClickable(true);
        pb = (ProgressBar) myView.findViewById(R.id.progressCart);
        empty = (ImageView) myView.findViewById(R.id.empty);
        emptyText = (TextView) myView.findViewById(R.id.emptyText);
        dbHelper = new DatabaseHelper(getActivity());
        userType = UserType.getType();
        order = (Button) myView.findViewById(R.id.PlaceOrder);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        ArrayList<Long> quantities = new ArrayList<>();
                        for(int i =0; i < lv.getAdapter().getCount(); i++){
                            View v = getViewByPosition(i,lv);
                            TextView quantity = (TextView) v.findViewById(R.id.quantity);
                            quantities.add(Long.parseLong(quantity.getText().toString()));
                            String userID = userType;
                            String name = items.get(i).getName();
                            int rowsDeleted = getActivity().getContentResolver().delete(CartProvider.CONTENT_URL,CartProvider.COL_1 + "=? and " + CartProvider.COL_2 + "=?",new String[]{userID,name});
                        }
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Orders");

                        SingleOrder order = new SingleOrder(userType,items,quantities,false,Long.parseLong(String.valueOf(totalCost)));
                        dbRef.push().setValue(order);
                        Toast.makeText(getActivity(),"Order Placed",Toast.LENGTH_SHORT).show();
                        load();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalCost = 0;
                cart = "";
                for (int i =0 ; i < lv.getAdapter().getCount(); i++) {
                    View v = getViewByPosition(i,lv);
                    TextView name = (TextView) v.findViewById(R.id.citemName);
                   TextView tv = (TextView) v.findViewById(R.id.quantity);
                   TextView tv2 = (TextView) v.findViewById(R.id.citemPrice);
                    cart += name.getText().toString() + " ( x" + tv.getText().toString() + " )" + "\n";
                    String cost = tv2.getText().subSequence(4,tv2.getText().length()).toString();
                   totalCost += Integer.parseInt(tv.getText().toString()) * Integer.parseInt(cost);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you confirm the purchase of the following items? \n" + cart + "\n" + "Total cost = Rs." + String.valueOf(totalCost)).setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ProductDetails.class);
                int pos = (int)itemAdapter.getItemId(position);
                Item i = (Item) items.get(pos);
                intent.putExtra("item",i);
                startActivity(intent);
            }
        });
        return myView;
    }


    @Override
    public void onResume() {
        cart = "";
        load();
        super.onResume();
    }

    private void load()
    {
        items.clear();
        Cursor res = dbHelper.getAllData(userType);
        while (res.moveToNext()) {
            Item i = new Item();
            i.setName(res.getString(1));
            i.setPrice(res.getLong(2));
            i.setCategory(res.getString(3));
            i.setImage(res.getLong(4));
            items.add(i);
        }
        pb.setVisibility(View.GONE);
        lv.setAdapter(itemAdapter);
        Button placeOrder = (Button) myView.findViewById(R.id.PlaceOrder);
        if(lv.getAdapter().getCount() == 0){
            lv.setVisibility(View.GONE);
            placeOrder.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            lv.setVisibility(View.VISIBLE);
            placeOrder.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);

        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(ShoppingCart.this).attach(ShoppingCart.this).commit();
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } else
                    itemAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

}
