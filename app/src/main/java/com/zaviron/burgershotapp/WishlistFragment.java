package com.zaviron.burgershotapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.adapter.CartAdapter;
import com.zaviron.burgershotapp.adapter.ProductAdapter;
import com.zaviron.burgershotapp.adapter.WishListAdapter;
import com.zaviron.burgershotapp.model.Cart;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;
import java.util.Map;


public class WishlistFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;
    private ArrayList<Product>products;
    private ArrayList<Cart> carts;
    private WishListAdapter wishListAdapter;
    public static final String TAG = WishlistFragment.class.getName();



 //   @Override

//    public void onStart() {
//        super.onStart();
//        user =FirebaseAuth.getInstance().getCurrentUser();
//        if (user==null){
//          startActivity(new Intent(getContext(), SignInActivity.class));
//        }
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        user =FirebaseAuth.getInstance().getCurrentUser();
        if (user !=null){
            carts =new ArrayList<>();
            RecyclerView itemView = fragment.findViewById(R.id.wishlistRecycleView);

            //  cartAdapter =new CartAdapter(carts,getContext());
            wishListAdapter =new WishListAdapter(getContext(), carts);

            LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());

            itemView.setLayoutManager(linearLayoutManager);

            itemView.setAdapter(wishListAdapter);

            firestore.collection("wishlist").whereEqualTo("client_id", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    for (DocumentChange change : value.getDocumentChanges()) {
                        Cart cart = change.getDocument().toObject(Cart.class);
                        //     System.out.println(cart.getProduct_id());
                        switch (change.getType()) {
                            case ADDED:
                                carts.add(cart);
                            case MODIFIED:
                                break;
                            case REMOVED:
                                carts.remove(cart);
                        }
                    }

                    wishListAdapter.notifyDataSetChanged();
                }
            });
        }else {
            startActivity(new Intent(getContext(), SignInActivity.class));
            Toast.makeText(getContext(),"Please Sign In First",Toast.LENGTH_LONG).show();
        }





//                for (QueryDocumentSnapshot document : task.getResult()){
//                    String name = document.getString("name");
//                    String category = document.getString("category");
//                    String description = document.getString("description");
//                    Log.i(TAG,name);

                //}




    }
}

