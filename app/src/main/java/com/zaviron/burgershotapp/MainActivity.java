package com.zaviron.burgershotapp;

import static com.zaviron.burgershotapp.R.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.splashscreen.SplashScreen;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.zaviron.burgershotapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener, SensorEventListener {

    public static final String TAG = MainActivity.class.getName();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private SensorManager sensorManager;
    private Sensor acceleroMeter;
    private static final float SHAKE_THRESHOLD = 60.0f;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        drawerLayout = findViewById(id.mainDrawerLayout);
        navigationView = findViewById(id.navigationView);
        toolbar = findViewById(id.toolBar);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.BATTERY_LOW");
        MyBroadcastReceiver mbr = new MyBroadcastReceiver();
        Intent intent = registerReceiver(mbr, intentFilter);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            acceleroMeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (acceleroMeter != null) {

            sensorManager.registerListener(MainActivity.this, acceleroMeter, SensorManager.SENSOR_DELAY_UI);
        }

        // setSupportActionBar(toolbar);
        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        //  setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, string.open_nav, string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigationView.setCheckedItem(id.sideNavHome);
                drawerLayout.open();
            }
        });

        //navigationView.setNavigationItemSelectedListener(this);


//        replaceFragment(new HomeFragment());
//        binding.bottomNavigation.setBackground(null);
//
//        binding.bottomNavigation.setOnItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.homeIconId) {
//                replaceFragment(new HomeFragment());
//            } else if (item.getItemId() == R.id.cart) {
//                replaceFragment(new CartFragment());
//            } else if (item.getItemId() == R.id.wishlist) {
//                replaceFragment(new WishlistFragment());
//            } else if (item.getItemId() == R.id.orders) {
//                replaceFragment(new OrdersFragment());
//            }
//            return true;
//
//        });
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(id.bottomNavigation);
        bottomNavigationView.setBackground(null);
        replaceFragment(new HomeFragment());
        navigationView.setCheckedItem(id.sideNavHome);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == id.homeIconId) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == id.cart) {
                replaceFragment(new CartFragment());
            } else if (item.getItemId() == id.wishlist) {
                replaceFragment(new WishlistFragment());
            } else if (item.getItemId() == id.orders) {
                replaceFragment(new OrdersFragment());
            }
            return true;
        });


        Menu menu = navigationView.getMenu();


        if (user != null) {

            menu.findItem(id.sideNavLogin).setVisible(false);
            menu.findItem(id.sideLogout).setVisible(true);
            menu.findItem(id.profile).setVisible(true);
            View headerView = navigationView.getHeaderView(0);
            ImageView sideNavPic = headerView.findViewById(id.profilePic);
            TextView username = headerView.findViewById(id.side_nav_username);
            TextView email = headerView.findViewById(id.side_nav_user_email);


            username.setText(user.getDisplayName());
            email.setText(user.getEmail());
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .resize(500, 500)
                    .centerCrop()
                    .into(sideNavPic);


        }


    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, item.toString());
        int itemId = item.getItemId();
        if (itemId == id.sideNavHome) {
            Log.i(TAG, String.valueOf(item.getGroupId()));
            replaceFragment(new HomeFragment());
        } else if (itemId == id.sideNavLogin) {

            Log.i(TAG, String.valueOf(item.getGroupId()));

            startActivity(new Intent(MainActivity.this, SignInActivity.class));

        } else if (itemId == id.sideLogout) {
            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
            Log.i(TAG, String.valueOf(item.getGroupId()) + "log out");

        } else if (itemId == id.profile) {


        } else if (itemId == id.location) {
            startActivity(new Intent(MainActivity.this, MapActivity.class));

        } else if (itemId == id.contact) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0705136124")));
        }
        return true;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        if (acceleration > SHAKE_THRESHOLD) {

            replaceFragment(new OrdersFragment());
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}