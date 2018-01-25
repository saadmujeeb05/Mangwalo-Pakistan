package com.aliyan.mangwalopakistan;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aliyan on 5/1/2017.
 */
public class SingleOrder implements Serializable{
    public String id;
    public ArrayList<Item> items;
    public ArrayList<Long> quantities;
    public boolean paid;
    public String userID;
    public Long cost;

    SingleOrder(){

    }

    SingleOrder(String userID, ArrayList<Item> items,ArrayList<Long> quantities,boolean paid,Long price){
        this.userID = userID;
        this.items = items;
        this.paid = paid;
        this.cost = price;
        this.quantities = quantities;
    }

    public ArrayList<Long> getQuantities() {
        return quantities;
    }

    public void setQuantities(ArrayList<Long> quantities) {
        this.quantities = quantities;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
