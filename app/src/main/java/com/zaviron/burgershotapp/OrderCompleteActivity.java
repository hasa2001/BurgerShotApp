package com.zaviron.burgershotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class OrderCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
        Intent intent =getIntent();
        if (intent !=null){
            String username =intent.getStringExtra("user_name");
            String order_id =intent.getStringExtra("order_id");
            String order_date_time =intent.getStringExtra("date");
            String total_price =intent.getStringExtra("price");

            TextView order =findViewById(R.id.order_id);
            TextView user =findViewById(R.id.order_username);
            TextView order_date =findViewById(R.id.date);
            TextView price =findViewById(R.id.price);

            order.setText(order_id);
            user.setText(username);
            order_date.setText(order_date_time);
            price.setText(total_price+"LKR");

          //  System.out.println(order_id+" "+username+" "+order_date_time+" "+total_price);


            findViewById(R.id.order_doneBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OrderCompleteActivity.this, MainActivity.class));
                  //  System.out.println(order_id+" "+username+" "+order_date_time+" "+total_price);
                    finish();
                }
            });


        }



    }
}