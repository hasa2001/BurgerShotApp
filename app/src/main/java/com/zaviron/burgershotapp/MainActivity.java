package com.zaviron.burgershotapp;

import static com.zaviron.burgershotapp.R.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.zaviron.burgershotapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        drawerLayout = findViewById(id.mainDrawerLayout);
        navigationView = findViewById(id.navigationView);
        toolbar = findViewById(id.toolBar);
        user =FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView.setCheckedItem(id.sideNavHome);
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);



        replaceFragment(new HomeFragment());
        binding.bottomNavigation.setBackground(null);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeIconId) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.cart) {
                replaceFragment(new CartFragment());
            } else if (item.getItemId() == R.id.wishlist) {
                replaceFragment(new WishlistFragment());
            } else if (item.getItemId() == R.id.orders) {
                replaceFragment(new OrdersFragment());
            }
            return true;

        });
        // navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setCheckedItem(id.sideNavHome);


        if (user !=null){
            System.out.println("wada");
            View headerView = navigationView.getHeaderView(0);
            ImageView sideNavPic = headerView.findViewById(id.profilePic);
            TextView  username =headerView.findViewById(id.side_nav_username);
            TextView email =headerView.findViewById(id.side_nav_user_email);

            username.setText(user.getDisplayName());
            email.setText(user.getEmail());
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .resize(500, 500)
                    .centerCrop()
                    .into(sideNavPic);




        }

    }

    @Override
    public void setSupportActionBar(@Nullable androidx.appcompat.widget.Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,item.toString());
        int itemId = item.getItemId();
        if (itemId == id.sideNavHome) {
            Log.i(TAG,String.valueOf(item.getGroupId()));
            replaceFragment(new HomeFragment());
        } else if (itemId == id.sideNavLogin) {

            Log.i(TAG,String.valueOf(item.getGroupId()));
            Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();
            //  startActivity(new Intent(MainActivity.this, SignInActivity.class));

        } else if (itemId == id.sideLogout) {
            // startActivity(new Intent(MainActivity.this, SignInActivity.class));
            Toast.makeText(getApplicationContext(), "Log Out", Toast.LENGTH_LONG).show();
            Log.i(TAG,String.valueOf(item.getGroupId())+"Hello");

        }
        return true;
    }

}