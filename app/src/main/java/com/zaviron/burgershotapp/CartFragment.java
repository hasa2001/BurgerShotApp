package com.zaviron.burgershotapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.adapter.CartAdapter;
import com.zaviron.burgershotapp.adapter.ProductAdapter;
import com.zaviron.burgershotapp.model.Cart;
import com.zaviron.burgershotapp.model.Orders;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


public class CartFragment extends Fragment {

    public static final String TAG = Fragment.class.getName();

    private ArrayList<Cart> cartItems;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    double CartTotalPrice;
    TextView totalPrice;
    private ArrayList<Orders> orders;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        cartItems = new ArrayList<>();
        orders = new ArrayList<>();
        RecyclerView recyclerView = fragment.findViewById(R.id.cartRecyclerView);
        CartAdapter cartAdapter = new CartAdapter(cartItems, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String client_id = user.getUid();
        Log.i(TAG, client_id);
        firestore = FirebaseFirestore.getInstance();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("TotalCartAmount"));

        firestore.collection("cart").whereEqualTo("client_id", client_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change : value.getDocumentChanges()) {
                    Cart cart = change.getDocument().toObject(Cart.class);
                    switch (change.getType()) {
                        case ADDED:
                            cartItems.add(cart);
                        case MODIFIED:
                            break;
                        case REMOVED:
                            cartItems.remove(cart);
                    }
                }

                cartAdapter.notifyDataSetChanged();
            }
        });

        totalPrice = fragment.findViewById(R.id.cartTotalPrice);

//        firestore.collection("cart").whereEqualTo("client_id",client_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                cartItems.clear();
//                for (QueryDocumentSnapshot snapshot : task.getResult()){
//                    Cart cart = snapshot.toObject(Cart.class);
//                    cartItems.add(cart);
//
//
//                }
//
//                cartAdapter.notifyDataSetChanged();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("Error");
//            }
//        });


        fragment.findViewById(R.id.cartBuyNowBtn).setOnClickListener(new View.OnClickListener() {

            boolean shouldStopProcessing = false;

            @Override
            public void onClick(View v) {
//in android recycle view data how to added firestore when fragment button clicked for this button click
                // Orders orders1 =new Orders();
                String order_id = UUID.randomUUID().toString();
                Date date = new Date();
                System.out.println(date);


                firestore.collection("cart").whereEqualTo("client_id", client_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        int total_cart_item_size = task.getResult().getDocuments().size();
                        System.out.println("total_cart_item_size "+total_cart_item_size);
                        final int[] innerCount = {0};
                        int outer_count = 0;
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {

                            Cart cart = snapshot.toObject(Cart.class);

                            firestore.collection("items").whereEqualTo("id", cart.getProduct_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                                    if (task.isSuccessful()) {

                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                            Product product = snapshot.toObject(Product.class);
                                            int product_available_qty = product.getQuantity();
                                            int order_selected_qty = cart.getSelected_qty();
                                            if (product_available_qty < order_selected_qty) {
                                               Toast.makeText(getContext(), product.getName() + "Is not Available", Toast.LENGTH_LONG).show();
                                                // i want stop out loop from this
                                                System.out.println("LOL");
                                                shouldStopProcessing = true;
                                                break;

                                            } else {
                                                orders.add(new Orders(order_id, cart.getCart_id(), cart.getClient_id(), cart.getProduct_id(), cart.getProduct_title(), cart.getProduct_price(), cart.getSelected_qty(), date, totalPrice.getText().toString()));
                                                System.out.println("OKKK");
                                                innerCount[0]++;
                                                System.out.println(innerCount[0]);


                                            }

                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                            if (shouldStopProcessing) {
                                break;
                               // System.out.println(orders.size() + "vamos argentina");
                              //  orders.clear();



                            } else {
                                System.out.println("digatam wada");

                                outer_count++;

                            }

                        }

                        if (!shouldStopProcessing) {
                            System.out.println("inner count " + innerCount[0] + "outer count " + outer_count);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

//                addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                        for (DocumentChange change : value.getDocumentChanges()) {
//                            Cart cart = change.getDocument().toObject(Cart.class);
//                            switch (change.getType()) {
//                                case ADDED:
//                                    cartItems.add(cart.);
//                                case MODIFIED:
//                                    break;
//                                case REMOVED:
//                                    cartItems.remove(cart);
//                            }
//                        }
//
//                        cartAdapter.notifyDataSetChanged();
//                    }
//                });
            }

        });


    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            double cartTotal = intent.getDoubleExtra("totalPrice", 0.00);
            totalPrice.setText(String.valueOf(cartTotal) + "LKR");
        }
    };
}