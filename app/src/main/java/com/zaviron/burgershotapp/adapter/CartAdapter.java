package com.zaviron.burgershotapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.zaviron.burgershotapp.R;
import com.zaviron.burgershotapp.model.Cart;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<Cart> carts;
    private FirebaseStorage storage;
    private Context context;
    private double totalCartPrice;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;


    public CartAdapter(ArrayList<Cart> carts, Context context) {
        this.carts = carts;
        this.storage = FirebaseStorage.getInstance();
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item_view, parent, false);
        return new ViewHolder(view);
    }

    private void updateTotalPrice() {
        totalCartPrice = 0;
        for (Cart cart : carts) {
            totalCartPrice += Double.parseDouble(cart.getProduct_price()) * (double) cart.getSelected_qty();
        }
        Intent intent = new Intent("TotalCartAmount");
        intent.putExtra("totalPrice", totalCartPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {

        Cart cart = carts.get(position);
        updateTotalPrice();
        //  totalCartPrice =totalCartPrice+ Double.parseDouble(carts.get(position).getProduct_price()) * (double) carts.get(position).getSelected_qty();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        holder.productTitle.setText(cart.getProduct_title().toString());
        holder.ProductPrice.setText(cart.getProduct_price().toString());
        holder.ProductQty.setText(String.valueOf(cart.getSelected_qty()));


//
//        Intent intent = new Intent("TotalCartAmount");
//        intent.putExtra("totalPrice",totalCartPrice);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


        storage.getReference("product-images/" + cart.getProduct_id()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .resize(500, 500)
                        .centerCrop()
                        .into(holder.productImage);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("ImageDownload Failed");
            }
        });

        holder.ProductDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//remove item from firestore  and  curerrent recycler view too
                firestore.collection("cart").whereEqualTo("cart_id", cart.getCart_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        carts.remove(position);
                                        notifyDataSetChanged();
                                        updateTotalPrice();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.toString());
                                    }
                                });
                            }
                            CartAdapter.this.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle, ProductQty, ProductPrice;
        ImageView productImage, ProductDeleteImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.cartItemTitle);
            ProductQty = itemView.findViewById(R.id.cartItemQty);
            ProductPrice = itemView.findViewById(R.id.cartItemPrice);
            productImage = itemView.findViewById(R.id.cartItemImage);
            ProductDeleteImage = itemView.findViewById(R.id.cartItemRemove);

        }

    }
}
