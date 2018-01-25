package com.aliyan.mangwalopakistan;

/**
 * Created by Saad Mujeeb on 3/5/2017.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DownloadService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String FILENAME = "filename";
    public static final String URL = "url";
    File output;

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        String fileName = intent.getStringExtra(FILENAME);
        String url = intent.getStringExtra(URL);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef = storageRef.child("INVOICES");
        storageRef = storageRef.child("Name.pdf");

        String root = Environment.getExternalStorageDirectory().toString();


        try {
            output = File.createTempFile("Name",".pdf",new File(root+"/MangwaloPakistan"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        storageRef.getFile(output).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                sendNotification(getApplicationContext());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(),exception.toString(),Toast.LENGTH_SHORT).show();
            }
        });
            result = Activity.RESULT_OK;
    }



        private void sendNotification(Context context) {
        Notification notification = new Notification.Builder(context).
                setTicker("Mangwalo Pakistan").
                setContentTitle("Invoice").
                setContentText("Invoice download complete").
                setSmallIcon(R.drawable.cart_white).getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        try {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notificationUri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

