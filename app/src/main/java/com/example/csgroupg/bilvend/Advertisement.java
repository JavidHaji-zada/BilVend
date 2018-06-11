package com.example.csgroupg.bilvend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;

/**
 * Advertisement Class
 * @author Bilvend
 * @version 11.05.2018
 */
public abstract class Advertisement {
    abstract String getAdvertisementImage();

    private final String[] CATEGORIES = {"Categories", "Books", "Notes","Household","Electronic devices", "Roommate","Others"};
    //properties
    private String title;
    private String uploadDate;
    private String description;
    private String publisher;
    private int ID;
    private String categoryType;

    //constructors
    Advertisement(String title, String publisher, int ID, int categoryType, String description, String uploadDate) {
        this.title = title;
        this.publisher = publisher;
        this.ID = ID;
        this.categoryType = CATEGORIES[categoryType];
        this.description = description;
        this.uploadDate = uploadDate;
    }
    Advertisement (String title, String description,int id )
    {
        this.title = title;
        this.description = description;
        this.ID = id;
    }
    public String getPublisher() {
        return publisher;
    }

    public void setPrice(String price) {
        getReference().child("Price").setValue(price);
    }

    public void setCategoryType(String categoryType) {
        getReference().child("Category").setValue(categoryType);
    }

    public int getID() {
        return ID;
    }

    public void setTitle( String title) {
        getReference().child("Title").setValue(title);
    }

    public String getTitle() {
        return title;
    }


    public String getUploadDate() {
        return uploadDate;
    }

    public void setDescription( String description) {
        getReference().child("Description").setValue(description);
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public DatabaseReference getReference()
    {
        return FirebaseDatabase.getInstance().getReference().child("Advertisements").child(ID+"");
    }
}