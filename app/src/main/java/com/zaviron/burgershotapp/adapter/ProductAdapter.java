package com.zaviron.burgershotapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.zaviron.burgershotapp.R;
import com.zaviron.burgershotapp.SingleProductViewActivity;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> products;
    private FirebaseStorage storage;
    private Context context;
    private Uri product_uri;

    public ProductAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        this.storage =FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_item_view, parent, false);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.textTitle.setText(product.getName());
        holder.textPrice.setText(product.getPrice());
        storage.getReference("product-images/"+product.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .resize(500,500)
                        .centerCrop()
                        .into(holder.image);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("ImageDownload Failed");
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(context, SingleProductViewActivity.class);


                intent.putExtra("product_id",product.getId());
                intent.putExtra("product_name",product.getName());
                intent.putExtra("product_qty",String.valueOf(product.getQuantity()));
                intent.putExtra("product_description",product.getDescription());
                intent.putExtra("product_price",product.getPrice());
                intent.putExtra("product_category",product.getCategory());
               intent.putExtra("product_image",product.getImage());
                context.startActivity(intent);





            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle ,textPrice;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle =itemView.findViewById(R.id.itemName);
            textPrice =itemView.findViewById(R.id.itemPrice);
            image =itemView.findViewById(R.id.imageView);

        }
    }
}
