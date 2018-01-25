package com.aliyan.mangwalopakistan;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Saad Mujeeb on 24/3/2017.
 */

public class Home extends Fragment {
    ArrayList<Item> items = new ArrayList<>();
    ListView lv;
    ProgressBar pb;
    View myView;
    CustomAdapter itemAdapter;
    Spinner staticSpinner;
    SearchView searchView;
    MenuItem item;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.home,container,false);
        lv = (ListView) myView.findViewById(R.id.itemList);
        pb = (ProgressBar) myView.findViewById(R.id.progress);
        itemAdapter = new CustomAdapter(getActivity(), R.layout.itemgrid_row, items,null);


        staticSpinner = (Spinner) myView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.categories,
                        android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                        String text = staticSpinner.getSelectedItem().toString();
                                                        itemAdapter.getFilter().filter(text);
                                                        itemAdapter.notifyDataSetChanged();

                                                        if(item!=null){
                                                            searchView = (SearchView) item.getActionView();
                                                            searchView.setQuery("", false);
                                                            searchView.clearFocus();
                                                            searchView.setIconified(true);
                                                            item.collapseActionView();
                                                        }
                                                    }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("ITEM");
        dbRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                        Item i = child.getValue(Item.class);
                        items.add(i);
                }
                pb.setVisibility(View.GONE);
                lv.setAdapter(itemAdapter);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });



        return myView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search, menu);
        item = menu.findItem(R.id.menuSearch);

        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    String text = staticSpinner.getSelectedItem().toString();
                    itemAdapter.getFilter().filter(text);
                    itemAdapter.notifyDataSetChanged();
                } else
                    itemAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

}
