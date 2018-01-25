package com.aliyan.mangwalopakistan;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Saad Mujeeb on 24/3/2017.
 */

public class Orders extends Fragment {

    View myView;
    String t = "";
    ArrayList<SingleOrder> orders = new ArrayList<>();
    ArrayList<SingleOrder> ordersReceived = new ArrayList<>();
    ArrayList<String> ordersInfo = new ArrayList<String>();
    ArrayList<String> ordersReceivedInfo = new ArrayList<String>();
    ArrayAdapter<String> orderAdapter;
    ListView listView;
    TextView tv;
    Spinner staticSpinner;
    String userType;
    ProgressBar pb;
    boolean rec;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView =inflater.inflate(R.layout.orders,container,false);
        rec = false;

        pb = (ProgressBar) myView.findViewById(R.id.progressOrders);
        listView = (ListView) myView.findViewById(R.id.itemListOrders);
        tv = (TextView) myView.findViewById(R.id.textn);

        userType=UserType.getType();

        staticSpinner = (Spinner) myView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(getActivity(), R.array.orderType,
                        android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        staticSpinner.setAdapter(staticAdapter);
        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String text = staticSpinner.getSelectedItem().toString();
                if(text.equals("Orders Pending")) {
                    orderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ordersInfo);
                    rec = false;
                }

                else if (text.equals("Orders Received")) {
                    orderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ordersReceivedInfo);
                    rec = true;
                }


                listView.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),orderDetails.class);
                int pos = (int)orderAdapter.getItemId(position);
                SingleOrder singleOrder = null;
                if(rec)
                     singleOrder = (SingleOrder) ordersReceived.get(pos);
                else
                     singleOrder = (SingleOrder) orders.get(pos);

                intent.putExtra("single order",singleOrder);
                intent.putExtra(("rec"),rec);
                startActivity(intent);
            }
        });
        load();

        return myView;
    }

    @Override
    public void onResume() {
        pb.setVisibility(View.GONE);
        super.onResume();
    }

    private void load() {
        t = "";
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Orders");
        dbref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    SingleOrder e;
                    e = child.getValue(SingleOrder.class);
                    if(e.userID.equals(userType)){
                        addItem(e,child);
                    }
                }
                if(getActivity()!=null) {
                    orderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, ordersInfo);
                    listView.setAdapter(orderAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void addItem(SingleOrder e, DataSnapshot child){
        e.id = child.getKey();
        if(e.isPaid())
            ordersReceived.add(e);
        else
            orders.add(e);
        t += "Order id: " + e.id + "\n";
        for(int i = 0; i < e.items.size(); i++){
            t += e.items.get(i).getName() + " x" + e.quantities.get(i) + "\n";
        }
        t += "Total Cost: Rs. " + e.cost + "\n\n";

        if(e.isPaid())
            ordersReceivedInfo.add(t);
        else
            ordersInfo.add(t);

        t = "";
    }
}
