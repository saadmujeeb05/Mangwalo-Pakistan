package com.aliyan.mangwalopakistan;

import java.io.Serializable;

/**
 * Created by Aliyan on 4/22/2017.
 */
public class Item implements Serializable{

    private String Name;
    private Long Price;
    private String Category;
    private Long Image;

    public Item(String name, Long price, String category, Long image) {
        this.Name = name;
        this.Price = price;
        this.Category = category;
        this.Image = image;
    }

    public Item() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        this.Price = price;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public Long getImage() {
        return Image;
    }

    public void setImage(Long image) {
        this.Image = image;
    }
}
