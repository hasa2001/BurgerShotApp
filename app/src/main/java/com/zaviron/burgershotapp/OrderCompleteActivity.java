package com.zaviron.burgershotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class OrderCompleteActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private String channelId ="Info";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel= new NotificationChannel(channelId,"INFO",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            channel.setDescription("This is Information Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0,1000,0,1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);

        }
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
            notification();
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

    private void notification() {

        Intent intent =new Intent(OrderCompleteActivity.this, OrdersFragment.class);

        PendingIntent pendingIntent =PendingIntent
                .getActivity(OrderCompleteActivity.this,0,intent,PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);

        Notification notification=new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.burger_shottt_logo)
                .setContentTitle("BurgerShot Notification")
                .setContentText("Order Completed Check Orders")
                .setColor(Color.YELLOW)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(1,notification);

    }
}