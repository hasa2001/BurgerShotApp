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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.adapter.CartAdapter;
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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


                @Override
                public void onClick(View v) {

                    String order_id = UUID.randomUUID().toString();
                    Date date = new Date();
                    String user_id = user.getUid();


                    firestore.collection("cart").whereEqualTo("client_id", client_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            final double[] total_cart_price = {0};


                            int total_cart_item_size = task.getResult().getDocuments().size();
                            int[] currentIteration = {0};

                            for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                Cart cart = snapshot.toObject(Cart.class);

                                firestore.collection("items").whereEqualTo("id", cart.getProduct_id()).whereGreaterThanOrEqualTo("quantity", cart.getSelected_qty()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    String document_id;

                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //  int total_cart_item_size = task.getResult().getDocuments().size();
                                            //  int currentIteration = 0;
                                            document_id = "";
                                            for (QueryDocumentSnapshot snapshots : task.getResult()) {
                                                ///   currentIteration++;
                                                Product product = snapshots.toObject(Product.class);
                                                int product_available_qty = product.getQuantity();
                                                int order_selected_qty = cart.getSelected_qty();
                                                int product_updated_quantity = product_available_qty - order_selected_qty;
                                                document_id = snapshot.getId();
                                                updateProductQty(document_id, product_updated_quantity);
                                                double product_total_price = (double) order_selected_qty * Double.parseDouble(product.getPrice());
                                                total_cart_price[0] += product_total_price;
                                                Orders productOrders = new Orders(order_id, cart.getCart_id(), user_id, product.getId(), product.getName(), product.getPrice(), order_selected_qty, date, String.valueOf(product_total_price));
                                                createOrder(order_id, productOrders);
                                                deleteCartItems(document_id);


                                            }
                                            currentIteration[0]++;
                                            if (currentIteration[0] == total_cart_item_size) {
                                                // This block will be executed when the loop is over
                                                // You can perform any actions you need after the loop here
                                                System.out.println(total_cart_price[0] + "outer");
                                                //  currentIteration[0]=0;
                                                Intent intent_order = new Intent(getContext(), OrderCompleteActivity.class);
                                                intent_order.putExtra("user_name", user.getDisplayName());
                                                intent_order.putExtra("order_id", order_id);
                                                intent_order.putExtra("date", String.valueOf(date));
                                                intent_order.putExtra("price", String.valueOf(total_cart_price[0]));

                                                startActivity(intent_order);
                                            }

                                        }


                                    }
                                });


                            }
//                        if (currentIteration[0] == total_cart_item_size) {
//                            // This block will be executed when the loop is over
//                            // You can perform any actions you need after the loop here
//                            System.out.println(total_cart_price[0]+"outer");
//                        }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }

            });
        }else {
            startActivity(new Intent(getContext(), SignInActivity.class));
            Toast.makeText(getContext(),"Please Sign In First",Toast.LENGTH_LONG).show();
        }


    }

    private void deleteCartItems(String document_id) {
        firestore.collection("cart").document(document_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
           Log.i(TAG,"delete Success");
            }
        });
    }

    private void createOrder(String orderId, Orders orders) {
        firestore.collection("orders").document(orderId).set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "Order Adding Success");

            }
        });

    }

    private void updateProductQty(String document_id, int productUpdatedQuantity) {
        firestore.collection("items").document(document_id).update("quantity", productUpdatedQuantity).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.i(TAG, "Product Updating Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "Failed");
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