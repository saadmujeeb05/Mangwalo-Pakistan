package com.aliyan.mangwalopakistan;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Saad Mujeeb on 24/3/2017.
 */

public class WishList extends Fragment {

    View myView;
    ProgressBar pb;
    ListView lv;
    CustomAdapter itemAdapter;
    ArrayList<Item> items = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String userType;
    ImageView empty;
    TextView emptyText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView =inflater.inflate(R.layout.wishlist,container,false);
        userType=UserType.getType();

        itemAdapter = new CustomAdapter(getActivity(), R.layout.itemgrid_row, items,null);
        lv = (ListView) myView.findViewById(R.id.itemWishList);
        pb = (ProgressBar) myView.findViewById(R.id.progressWishList);
        empty = (ImageView) myView.findViewById(R.id.empty);
        emptyText = (TextView) myView.findViewById(R.id.emptyText);

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
        load();
        super.onResume();
    }


    private void load() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("WishList");
        items.clear();
        dbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    if (child.getKey().contains(userType)) {
                        Item i = child.getValue(Item.class);
                        items.add(i);
                    }
                }
                pb.setVisibility(View.GONE);
                if(items.size() == 0){
                    lv.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                }
                else
                {
                    lv.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    emptyText.setVisibility(View.GONE);
                    lv.setAdapter(itemAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(WishList.this).attach(WishList.this).commit();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } else
                    itemAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
