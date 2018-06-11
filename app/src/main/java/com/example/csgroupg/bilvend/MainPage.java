package com.example.csgroupg.bilvend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;

/**
 * MainPage Class
 * @author Bilvend
 * @version 11.05.2018
 */
public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    //parameters
    private User user;
    private ImageView profilePic;
    private TextView username;
    private TextView email;
    private ArrayList<Advertisement> ads;
    private ArrayList<Advertisement> finalAds;
    private ListView mListView;
    private String category;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * This method starts the activity
     * @param savedInstanceState the bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("User");
        category = intent.getStringExtra("Category");
        if(category == null)
        {
            category = "Category";
        }
        FloatingActionButton floatingActionButton = findViewById(R.id.publish);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        finalAds = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshMain);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is us
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
                public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getAds();
            }
        }
        );

        profilePic = header.findViewById(R.id.navDrawerProfilePhoto);
        username = header.findViewById(R.id.navDrawerUsername);
        email = header.findViewById(R.id.navDrawerEmail);
        ads = new ArrayList<>();
        assert user != null;
        mListView = findViewById(R.id.ads);
        if (user != null) {
            navigationView.getMenu().findItem(R.id.login).setTitle(R.string.log_out);
            navigationView.getMenu().findItem(R.id.login).setIcon(R.drawable.ic_action_logout);
            setProfileDetails();
        }
        profilePic.setOnClickListener(new View.OnClickListener() {

            /**
             *
             * @param v the view
             */
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainPage.this, MyProfileActivity.class);
                intent1.putExtra("User", user);
                startActivity(intent1);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), AdPublishingPage.class);
                    intent1.putExtra("User", user);
                    startActivity(intent1);
                }

            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void setProfileDetails() {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("Photos/" + user.getUsername()+".jpg");
        mStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if ( uri != null)
                    Picasso.get().load(uri).into(profilePic);
            }
        });
        username.setText(user.getName() + " " + user.getSurname());
        email.setText(user.getMail());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        final ArrayList<String> titles = new ArrayList<>();
        final ArrayList<String> descriptions = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Advertisements").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren())
                {
                    String title = ds.child("Title").getValue(String.class);
                    String description = ds.child("Description").getValue(String.class);
                    if ( title != null)
                    {
                        titles.add(title);
                        descriptions.add(description);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getMenuInflater().inflate(R.menu.main_page, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                    return true;
                }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (!newText.equals("")) {
                    ads = new ArrayList<>();
                    FirebaseDatabase.getInstance().getReference().child("Advertisements").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String title = ds.child("Title").getValue(String.class);
                                String description = ds.child("Description").getValue(String.class);
                                String image = ds.child("Image").getValue(String.class);
                                if (title != null) {
                                    int ID = Integer.parseInt(ds.child("ID").getValue(String.class));
                                    if (containsIgnoreCase(title,newText)) {
                                        ads.add(new Item(title, description, image, ID));
                                    } else if (containsIgnoreCase(description,newText)) {
                                        ads.add(new Item(title, description, image, ID));
                                    }
                                    else if (newText.substring(0,1).equals("#") && containsIgnoreCase(ID +"", newText.substring(1)))
                                    {
                                        ads.add(new Item(title, description, image, ID));
                                    }
                                }
                            }
                            CustomAdapter adapter = new CustomAdapter();
                            mListView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                    getAds();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.categories) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        if (id == R.id.inbox) {
//            if (user == null) {
//                Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getApplicationContext(),"Coming soon!", Toast.LENGTH_SHORT).show();
//            }
//
//        }
         if (id == R.id.myPosts) {
            if (user == null) {
                Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 = new Intent(getApplicationContext(), MyPosts.class);
                intent1.putExtra("User", user);
                startActivity(intent1);
            }
        } else if (id == R.id.favorites) {
            if (user == null) {
                Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 = new Intent(getApplicationContext(), Favourites.class);
                intent1.putExtra("User", user);
                startActivity(intent1);
            }
        } else if (id == R.id.profile) {
            if (user == null) {
                Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(new Intent(getApplicationContext(), MyProfileActivity.class));
                intent.putExtra("User", user);
                startActivity(intent);
            }
        } else if (id == R.id.categories) {
            if (user == null) {
                Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent1 = new Intent(getApplicationContext(), Categories.class);
                intent1.putExtra("User", user);
                startActivity(intent1);
            }

        } else if (id == R.id.login) {
            if (item.getTitle().equals(R.string.log_out)) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            } else {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        getAds();
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ads.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.customlayout, null);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( user != null) {
                        Intent intent = new Intent(getApplicationContext(), AdPage.class);
                        intent.putExtra("ID", finalAds.get(position).getID() + "");
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You have to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            TextView mTextView = view.findViewById(R.id.adDescription);
            TextView mTextView2 = view.findViewById(R.id.adTitle);
            if(ads.get(position).getDescription().length() > 50)
            {
                mTextView.setText(ads.get(position).getDescription().substring(0,50) + "...");
            }
            else
                mTextView.setText(ads.get(position).getDescription());

            mTextView2.setText( ads.get(position).getTitle());

            ImageView imageView = view.findViewById(R.id.customPageImage);
            Picasso.get().load(ads.get(position).getAdvertisementImage()).fit().into(imageView);
            return view;
        }
    }
    private void getAds()
    {
        ads = new ArrayList<>();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Advertisements");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ( category.equals("Category")) {
                        String title = ds.child("Title").getValue(String.class);
                        String description = ds.child("Description").getValue(String.class);
                        String image = ds.child("Image").getValue(String.class);
                        if (title != null) {
                            int ID = Integer.parseInt(ds.child("ID").getValue(String.class));
                            ads.add(new Item(title, description, image, ID));
                        }
                    }
                    else
                    {
                        if ( ds.child("Category").getValue(String.class) != null )
                        {
                            if (ds.child("Category").getValue(String.class).equals(category)) {
                                String title = ds.child("Title").getValue(String.class);
                                String description = ds.child("Description").getValue(String.class);
                                String image = ds.child("Image").getValue(String.class);
                                int ID = Integer.parseInt(ds.child("ID").getValue(String.class));
                                ads.add(new Item(title, description, image, ID));
                            }
                        }
                    }
                }
                Collections.reverse(ads);
                if ( ads.size() >= 10) {
                    for (int i = 0; i < 10; i++) {
                        finalAds.add(ads.get(i));
                    }
                }
                else {
                    for (int i = 0; i < ads.size(); i++) {
                        finalAds.add(ads.get(i));
                    }
                }
                CustomAdapter adapter = new CustomAdapter();
                mListView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }
    private boolean containsIgnoreCase(String str, String searchStr)     {
        if(str == null || searchStr == null) return false;

        final int length = searchStr.length();
        if (length == 0)
            return true;

        for (int i = str.length() - length; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, length))
                return true;
        }
        return false;
    }
}