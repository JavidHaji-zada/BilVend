package com.example.csgroupg.bilvend;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Date;

/**
 * Item Class
 * @author BilVend
 * @version 11.05.2018
 */
public class Item extends Advertisement {

    //parameters
    int price;
    private String description;
    private String advertisementImage;
    private StorageReference mStorageReference;

    //constructors
    Item(String title, String publisher, int ID, int categoryType, String description, String uploadDate, int price, String  advertisementImage) {

        super( title, publisher, ID, categoryType, description, uploadDate);
        setPrice(price);
        mStorageReference = FirebaseStorage.getInstance().getReference().child("Advertisements/" + getID() + "/" + getTitle() + ".jpg");
        this.advertisementImage = advertisementImage;
    }
    Item (String title, String description, String  advertisementImage, int id )
    {
        super(title, description, id);
        this.advertisementImage = advertisementImage;

    }

    /**
     * This method sets the price of the item
     * @param price the price
     */
    public void setPrice( int price) {
        this.price = price;
    }

    /**
     * This method returns the price of the item
     * @return
     */
    public int getPrice() {
        return price;
    }

    /**
     * This method returns the advertisementImage as string
     * @return the advertisementImage as string
     */
    public String getAdvertisementImage()
    {
        return advertisementImage;
    }

    /**
     * This method sets the advertisementImage
     * @param uri the advertisementImage as uri
     */
    public void setAdvertisementImage(Uri uri)
    {
        advertisementImage = uri.toString();
        mStorageReference.putFile(uri);
    }

}
