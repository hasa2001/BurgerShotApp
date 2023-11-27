package com.zaviron.burgershotapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zaviron.burgershotapp.databinding.ActivityMainBinding;
import com.zaviron.burgershotapp.model.Cart;

import java.util.UUID;

public class SingleProductViewActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button buyNow, AddItem, RemoveItem;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private TextView product_added_qty;

    private FirebaseUser user;

    private int cart_qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);
        Intent intent = getIntent();
        if (intent != null) {
            firestore = FirebaseFirestore.getInstance();
            String name = intent.getStringExtra("product_name");
            String qty = intent.getStringExtra("product_qty");
            String description = intent.getStringExtra("product_description");
            String price = intent.getStringExtra("product_price");
            String category = intent.getStringExtra("product_category");
            String image = intent.getStringExtra("product_image");

            // System.out.println(id + " " + name + " " + qty + " " + description + " " + price + " " + category + image);
            TextView textViewTitle = findViewById(R.id.productTitle);
            TextView textViewQty = findViewById(R.id.availableQty);
            TextView textViewDescription = findViewById(R.id.description_details);
            TextView textViewPrice = findViewById(R.id.product_price);
            TextView selected_qty = findViewById(R.id.QtyView);
            //   TextView textViewPrice = findViewById(R.id.productTitle);


            ImageView imageView = findViewById(R.id.singleProductImageView);
            textViewTitle.setText(name.toString());
            textViewQty.setText(qty.toString());
            textViewPrice.setText(price.toString() + "LKR");
            textViewDescription.setText(description.toString());


//            available_stock=findViewById(R.id.availableQty);
//            product_description=findViewById(R.id.description_details);
//            product_price =findViewById(R.id.product_price);
//
//            title.setText("name");
//            available_stock.setText(qty);
//            product_description.setText(description);
//            product_price.setText(price+"LKR");

            storage = FirebaseStorage.getInstance();


            storage.getReference("product-images/" + image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get()
                            .load(uri)
                            .fit()
                            .centerCrop()
                            .into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "ImageLoading Failed", Toast.LENGTH_LONG).show();
                }
            });

            findViewById(R.id.addQtyPlusBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cart_qty < Integer.parseInt(qty)) {

                        cart_qty++;
                        selected_qty.setText(String.valueOf(cart_qty));
                    }
                }
            });

            findViewById(R.id.removeQty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cart_qty > 1) {
                        cart_qty--;
                        selected_qty.setText(String.valueOf(cart_qty));

                    }
                }
            });
            findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int select_quantity = Integer.parseInt(selected_qty.getText().toString());
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    String cart_id = UUID.randomUUID().toString();
                    String user_id = user.getUid();
                    String product_id = intent.getStringExtra("product_id");
                    Cart cart = new Cart(cart_id,user_id, product_id, select_quantity,name,price);

                    firestore .collection("cart").add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Product Added to cart Successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }
    }


}