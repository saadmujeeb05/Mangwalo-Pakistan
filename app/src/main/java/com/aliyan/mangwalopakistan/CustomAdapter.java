package com.aliyan.mangwalopakistan;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Aliyan on 4/23/2017.
 */
public class CustomAdapter extends ArrayAdapter implements Filterable{

    FirebaseStorage sr = FirebaseStorage.getInstance();
    StorageReference s = sr.getReference().child("ITEM");
    Activity activity;
    ArrayList<Item> items;
    ArrayList<Long> quantities;
    ArrayList<Item> filteredItems;
    ItemHolder holder = new ItemHolder();
    int resource;
    CustomAdapter(Activity activity,int resource,ArrayList<Item> items, ArrayList<Long> quantities){
        super(activity,resource,items);
        this.activity = activity;
        this.items = items;
        this.quantities = quantities;
        this.filteredItems = items;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        try {
            return filteredItems.size();
        }catch(NullPointerException e){
            return 1;
        }
    }
    @Override
    public long getItemId(int position)
    {
        if(items == null)
            return position;
        return items.indexOf(filteredItems.get(position));

    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(resource, parent, false);

        }
        s = sr.getReference().child("ITEM").child(String.valueOf(filteredItems.get(position).getImage())+".jpg");

        switch(resource){
            case R.layout.cart_row:
                holder.t = (TextView) convertView.findViewById(R.id.citemName);
                holder.v = (TextView) convertView.findViewById(R.id.citemPrice);
                holder.c = (TextView) convertView.findViewById(R.id.citemImage);
                holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                holder.less = (Button) convertView.findViewById(R.id.less);
                holder.more = (Button) convertView.findViewById(R.id.more);
                holder.i = (ImageView) convertView.findViewById(R.id.citemPic);
                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout l = (LinearLayout) view.getParent();
                        TextView quan = (TextView) l.findViewById(R.id.quantity);
                        quan.setText(String.valueOf(Integer.parseInt(quan.getText().toString()) + 1));
                    }
                });

                holder.less.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinearLayout l = (LinearLayout) view.getParent();
                        TextView quan = (TextView) l.findViewById(R.id.quantity);
                        if (!quan.getText().toString().equals("1")) {
                            l = (LinearLayout) view.getParent();
                            quan = (TextView) l.findViewById(R.id.quantity);
                            quan.setText(String.valueOf(Integer.parseInt(quan.getText().toString()) - 1));
                        }
                    }
                });

                break;
            case R.layout.order_detail_row:
                holder.t = (TextView) convertView.findViewById(R.id.citemName);
                holder.v = (TextView) convertView.findViewById(R.id.citemPrice);
                holder.c = (TextView) convertView.findViewById(R.id.citemImage);
                holder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                holder.i = (ImageView) convertView.findViewById(R.id.citemPic);
                break;
            case R.layout.itemgrid_row:
                holder.t = (TextView) convertView.findViewById(R.id.itemName);
                holder.v = (TextView) convertView.findViewById(R.id.itemPrice);
                holder.c = (TextView) convertView.findViewById(R.id.itemImage);
                holder.i = (ImageView) convertView.findViewById(R.id.itemPic);
                break;
        }
        Glide
                .with(getContext())
                .using(new FirebaseImageLoader())
                .load(s)
                .into(holder.i);
        holder.t.setText(filteredItems.get(position).getName());
        holder.v.setText("Rs. " + filteredItems.get(position).getPrice().toString());
        holder.c.setText(filteredItems.get(position).getCategory().toString());
        if(quantities != null)
            holder.quantity.setText("x"+quantities.get(position).toString());
        return convertView;
    }

    static class ItemHolder{
        TextView t;
        TextView v;
        TextView c;
        TextView quantity;
        Button less;
        Button more;
        ImageView i;
    }

    @Override
    public android.widget.Filter getFilter(){
        return new android.widget.Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values

                ArrayList<Item> resultData = new ArrayList<Item>();
                if (constraint == null || constraint.equals("All")) {

                    // set the Original result to return
                    results.count = items.size();
                    results.values = items;

                }
                else if(constraint.equals("Fashion") || constraint.equals("Mobile Phone") || constraint.equals("Computer Accessories")){
                    String srch = constraint.toString().toUpperCase();
                    for (int i = 0; i < items.size(); i++) {
                        String data = items.get(i).getCategory();
                        if (data.toUpperCase().equals(srch)) {
                            resultData.add(items.get(i));
                        }
                    }
                    results.count = resultData.size();
                    results.values = resultData;
                }
                else if(constraint != null && constraint.length() > 0) {

                    if (constraint.length() > 0) {
                        constraint = constraint.toString().toUpperCase();
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).getName().toUpperCase().contains(constraint)) {
                                resultData.add(items.get(i));
                            }
                        }
                        results.count = resultData.size();
                        results.values = resultData;
                    }

                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }

        };
    }

}
