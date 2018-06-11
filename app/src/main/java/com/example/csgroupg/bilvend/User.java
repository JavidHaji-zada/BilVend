package com.example.csgroupg.bilvend;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple user class for BilVend
 * @author Bilvend
 * @version 11.05.2018
 */
public class User implements Parcelable{


    //parameters
    private String name;
    private String surname;
    private String email;
    private String number;
    private String username;
    private String Uid;

    //constructors
    User(String name, String surname, String username, String email, String number, String UId){
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.number = number;
        this.Uid = UId;
    }

    private User(Parcel in) {
        this.name = in.readString();
        this.surname = in.readString();
        this.email = in.readString();
        this.number = in.readString();
        this.username = in.readString();
        this.Uid = in.readString();
    }

    /**
     *
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * This method gets reference from Firebase
     * @return instance of DatabaseReference
     */
    public DatabaseReference getReference()
    {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * This method returns the email of the user
     * @return email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method sets the email of the user
     * @param email the email of the user
     */
    public void setEmail(String email) {
        getReference().child("Users").child(Uid).child("Email").setValue(email);
    }

    /**
     * This method returns id of the user
     * @return user id
     */
    public String getUid()
    {
        return Uid;
    }

    /**
     * This method sets the name of the user
     * @param name the name of the user
     */
    public void setName( String name){
        getReference().child("Users").child(Uid).child("Name").setValue(name);
    }

    /**
     * This method returns the tname of the user
     * @return name of the user
     */
    public String getName(){
        return name;
    }

    /**
     * This method returns the surname of the user
     * @return surname of the user
     */
    public String getSurname(){
        return surname;
    }

    /**
     * This method sets the surname of the user
     * @param surname the surname of the user
     */
    public void setSurname(String surname){
        getReference().child("Users").child(Uid).child("Surname").setValue(surname);
    }

    /**
     * This method returns the email of the user
     * @return email of the user
     */
    public String getMail(){
        return email;
    }

    /**
     * This method returns the number of user
     * @return number of the user
     */
    public String getNumber(){
        if(number == null)
            return null;
        return number;
    }

    /**
     * This method sets the number of the user
     * @param num the number of the user
     */
    public void setNumber(String num)
    {
        getReference().child("Users").child(Uid).child("Numbers").setValue(num);
    }

    /**
     * This method returns the username of the user
     * @return username of the user
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * This method publishes an advertisement
     * @param ad the advertisement
     * @param ID the unique ID
     */
    public void publishAdvertisement(Item ad,int ID)
    {
        DatabaseReference mReference;
        Map<String, java.io.Serializable> newPost;

        mReference =  FirebaseDatabase.getInstance().getReference().child("Advertisements").child((ad.getID() +""));
        newPost = new HashMap<>();
        newPost.put("Title", ad.getTitle());
        newPost.put("Publisher",ad.getPublisher());
        newPost.put("Description", ad.getDescription());
        newPost.put("Price", ad.getPrice() + "");
        newPost.put("Category", ad.getCategoryType());
        newPost.put("Upload date", ad.getUploadDate());
        newPost.put("ID", ad.getID() + "");
        newPost.put("Image", ad.getAdvertisementImage());
        newPost.put("Contact phone", number);
        newPost.put("Publisher's mail", email);
        mReference.setValue( newPost);
        mReference = FirebaseDatabase.getInstance().getReference().child("Advertisements").child("Total number of advertisements");
        mReference.setValue(ID);

        mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Published advertisements:").child(ad.getTitle());
        mReference.setValue(ad.getID() + "");
    }

    /**
     * This method returns the details of the user
     * @return
     */
    @Override
    public String toString() {
        return "name: " + name + "surname: " + surname + "mail: " + email + "number: " + number + "username: " + username;
    }

    /**
     * This method returns 0 when it is called
     * @return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * This method writes the informations to parcel
     * @param dest parcel
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.surname);
        dest.writeString(this.email);
        dest.writeString(this.number);
        dest.writeString(this.username);
        dest.writeString(this.Uid);
    }

    /**
     * This method removes the advertisement from Firebase
     * @param neededId the unique id of the advertisement
     * @param title the title of the advertisement
     */
    public void removeAdvertisement(final String neededId, String title) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Advertisements").child(neededId).removeValue();
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Advertisements").child(neededId);
        storage.delete();
        reference.child("Users").child(getUid()).child("Published advertisements:").child(title).removeValue();
    }

    /**
     * This method enables to edit the advertisement
     * @param title the title of the advertisement
     * @param description the description of the advertisement
     * @param price the price of the advertisement
     * @param neededId the unique id of the advertisement
     * @param uri the uri of the image
     */
    public void editAdvertisement(String title, String description, String price, final String neededId, Uri uri)
    {
        DatabaseReference mReference;
        mReference =  FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededId).child("Title");
        if ( !title.equals(""))
            mReference.setValue(title);
        mReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(getUid()).child("Description");
        if ( !description.equals(""))
            mReference.setValue(description);
        mReference =  FirebaseDatabase.getInstance().getReference().child("Users").child(getUid()).child("Price");
        if ( !price.equals(""))
            mReference.setValue(price);
        if ( uri != null)
            FirebaseDatabase.getInstance().getReference().child("Advertisements").child(neededId).child("Image").setValue(uri.toString());
    }
}