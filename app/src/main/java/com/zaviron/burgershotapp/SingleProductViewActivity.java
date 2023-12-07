package com.zaviron.burgershotapp;

import static java.security.AccessController.getContext;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zaviron.burgershotapp.databinding.ActivityMainBinding;
import com.zaviron.burgershotapp.model.Cart;
import com.zaviron.burgershotapp.model.Orders;
import com.zaviron.burgershotapp.model.Product;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class SingleProductViewActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button buyNow, AddItem, RemoveItem;
    private ImageView imageView;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private TextView product_added_qty;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private int cart_qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);
        Intent intent = getIntent();
        if (intent != null) {
            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            String name = intent.getStringExtra("product_name");
            String qty = intent.getStringExtra("product_qty");
            String description = intent.getStringExtra("product_description");
            String price = intent.getStringExtra("product_price");
            String category = intent.getStringExtra("product_category");
            String image = intent.getStringExtra("product_image");
            String product_id = intent.getStringExtra("product_id");

            // System.out.println(id + " " + name + " " + qty + " " + description + " " + price + " " + category + image);
            TextView textViewTitle = findViewById(R.id.productTitle);
            TextView textViewQty = findViewById(R.id.availableQty);
            TextView textViewDescription = findViewById(R.id.description_details);
            TextView textViewPrice = findViewById(R.id.product_price);
            TextView selected_qty = findViewById(R.id.QtyView);
            //   TextView textViewPrice = findViewById(R.id.productTitle);


            ImageView imageView = findViewById(R.id.singleProductImageView);
            textViewTitle.setText(name.toString());
            textViewQty.setText(String.valueOf(qty));
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
            user = FirebaseAuth.getInstance().getCurrentUser();


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
                    if (user!=null){
                        int select_quantity = Integer.parseInt(selected_qty.getText().toString());

                        String cart_id = UUID.randomUUID().toString();
                        String user_id = user.getUid();


                        firestore.collection("cart").whereEqualTo("product_id", product_id).whereEqualTo("client_id", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        Cart cart = new Cart(cart_id, user_id, product_id, select_quantity, name, price);
                                        firestore.collection("cart").add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Product is already added to the cart", Toast.LENGTH_LONG).show();
                                    }


                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }else {
                        startActivity(new Intent(SingleProductViewActivity.this, SignInActivity.class));
                        Toast.makeText(SingleProductViewActivity.this,"Please Sign In First",Toast.LENGTH_LONG).show();
                    }
                    }

            });

            findViewById(R.id.addTowishList).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (user != null) {
                        int select_quantity = Integer.parseInt(selected_qty.getText().toString());

                        String cart_id = UUID.randomUUID().toString();
                        String user_id = user.getUid();

                        firestore.collection("wishlist").whereEqualTo("product_id", product_id).whereEqualTo("client_id", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        Cart cart = new Cart(cart_id, user_id, product_id, select_quantity, name, price);
                                        firestore.collection("wishlist").add(cart).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext(), "Product Added to wishlist Successfully", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                    System.out.println("product already exists");
                                } else {

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("product failed");
                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(), "Please sign in first", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    }

                }
            });
            findViewById(R.id.buy).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {


                        firestore.collection("items").whereEqualTo("id", product_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            int available_total_product_qty;
                            String document_id;

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    available_total_product_qty = 0;
                                    document_id = "";

                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        //how to get document id
                                        Product product = snapshot.toObject(Product.class);
                                        document_id = snapshot.getId();

                                        available_total_product_qty = product.getQuantity();
                                    }
                                }

                                int selected_product_qty = Integer.parseInt((String) selected_qty.getText());

                                if (available_total_product_qty <= 0) {


                                    Toast.makeText(getApplicationContext(), available_total_product_qty + "product is out of stock" + selected_product_qty, Toast.LENGTH_LONG).show();
                                } else if (selected_product_qty > available_total_product_qty) {
                                    Toast.makeText(getApplicationContext(), available_total_product_qty + "quantity not valid" + selected_product_qty, Toast.LENGTH_LONG).show();

                                } else {
                                    int remaining_total_product_qty = available_total_product_qty - selected_product_qty;
                                    System.out.println(remaining_total_product_qty + "remaining");


                                    firestore.collection("items").document(document_id).update("quantity", remaining_total_product_qty).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                                            System.out.println("success");
                                            double total_price = selected_product_qty * Double.parseDouble(price);
                                            String order_id = UUID.randomUUID().toString();
                                            Date date = new Date();

                                            Orders orders = new Orders(order_id, order_id, currentUser.getUid(), product_id, name, price, selected_product_qty, date, String.valueOf(total_price));
                                            //  addOrders(order_id, orders);
                                            // System.out.println(currentUser.getDisplayName()+" "+order_id+" "+date+" "+total_price);


                                            firestore.collection("orders").document(order_id).set(orders).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Intent intent_order = new Intent(SingleProductViewActivity.this, OrderCompleteActivity.class);
                                                    intent_order.putExtra("user_name", currentUser.getDisplayName());
                                                    intent_order.putExtra("order_id", order_id);
                                                    intent_order.putExtra("date", String.valueOf(date));
                                                    intent_order.putExtra("price", String.valueOf(total_price));

                                                    startActivity(intent_order);
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println("Error");
                                        }
                                    });

                                }
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Please Sign In First", Toast.LENGTH_LONG).show();
                    }


                }
            });


        }


    }

    private void addOrders(String order_id, Orders orders) {


    }

}