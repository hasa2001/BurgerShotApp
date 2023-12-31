package com.zaviron.burgershotapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.zaviron.burgershotapp.R;
import com.zaviron.burgershotapp.model.Cart;

import java.util.ArrayList;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Cart> carts;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    public WishListAdapter(Context context, ArrayList<Cart> carts) {
        this.context = context;
        this.carts = carts;
        this.storage = FirebaseStorage.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_item_view, parent, false);
        return new WishListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder holder, int position) {

        Cart cart = carts.get(position);
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        holder.productTitle.setText(cart.getProduct_title().toString());
        holder.ProductPrice.setText(cart.getProduct_price().toString());
        holder.ProductQty.setText(String.valueOf(cart.getSelected_qty()));

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
                firestore.collection("wishlist").whereEqualTo("cart_id", cart.getCart_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        carts.remove(position);
                                        notifyDataSetChanged();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.toString());
                                    }
                                });
                            }
                            WishListAdapter.this.notifyDataSetChanged();
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
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
