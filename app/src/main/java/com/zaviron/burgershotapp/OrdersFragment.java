package com.zaviron.burgershotapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.adapter.OrdersAdapter;
import com.zaviron.burgershotapp.model.Orders;

import java.util.ArrayList;


public class OrdersFragment extends Fragment {

    private ArrayList<Orders> orders;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    public static final String TAG = OrdersFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
        orders = new ArrayList<>();
        RecyclerView recyclerView = fragment.findViewById(R.id.order_recycler_view);
        OrdersAdapter ordersAdapter = new OrdersAdapter(orders, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(ordersAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();
        Log.i(TAG, user_id);
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("orders").whereEqualTo("client_id", user_id).addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change : value.getDocumentChanges()) {
                    Orders order = change.getDocument().toObject(Orders.class);
                    System.out.println(order.getOrder_id() + " " + order.getProduct_price() + " " + order.getProduct_id());
                    switch (change.getType()) {
                        case ADDED:
                            orders.add(order);
                        case MODIFIED:
                            break;
                        case REMOVED:
                            orders.remove(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();
            }
        });

    }
}