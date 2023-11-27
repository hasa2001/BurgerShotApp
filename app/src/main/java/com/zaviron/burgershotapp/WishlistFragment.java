package com.zaviron.burgershotapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.model.Cart;

import java.util.Map;


public class WishlistFragment extends Fragment {

private  FirebaseFirestore firestore;
public  static final String TAG =WishlistFragment.class.getName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore =FirebaseFirestore.getInstance();
        firestore.collection("cart").whereEqualTo("client_id","RUG9eaZ8rohuoFIouJpyzh0pZq32").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()){

                for (QueryDocumentSnapshot snapshot: task.getResult()){
                    System.out.println("LLLLLLLLL");
                }
            }else {
                System.out.println("FFFFFFF");
            }

//                for (QueryDocumentSnapshot document : task.getResult()){
//                    String name = document.getString("name");
//                    String category = document.getString("category");
//                    String description = document.getString("description");
//                    Log.i(TAG,name);

                //}


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error");
            }
        });
    }
}

