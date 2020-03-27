package com.example.sharonsimon.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.sharonsimon.Activities.MainActivity;
import com.example.sharonsimon.Classes.Highlight;
import com.example.sharonsimon.Fragments.UpdateTodaysTasksFragment;
import com.example.sharonsimon.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UploadHighlightToFirebaseService extends Service {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private NotificationManager notificationManager;
    private static final AtomicInteger c = new AtomicInteger(0);
    public static boolean isCanceled;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        isCanceled = intent.getBooleanExtra("isCanceled",false);

        if(!isCanceled) {
            final Highlight highlight = (Highlight) intent.getSerializableExtra("highlight");
            final Uri videoUri = intent.getParcelableExtra("videoUri");
            final int notificationID = getNotificationID();

            if (notificationManager == null)
                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Upload Video")
                    .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                    .setContentTitle("מעלה סרטון")
                    .setContentText("קן: " + highlight.getKenName() + " | " + "משימה: " + highlight.getTaskDesc())
                    .setProgress(100, 0, false)
                    .setOngoing(true);


            Intent cancelDownloadIntent = new Intent(this, UploadHighlightToFirebaseService.class);
            cancelDownloadIntent.putExtra("isCanceled", true);
            PendingIntent cancelDownloadPendingIntent = PendingIntent.getService(this,
                    0,
                    cancelDownloadIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            builder.addAction(R.drawable.ic_cancel_black_24dp, "Cancel", cancelDownloadPendingIntent);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Upload Video", "Upload Video", NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(null, null);
                notificationManager.createNotificationChannel(channel);
            }

            startForeground(notificationID, builder.build());

            final String path = "videos/highlights/" + highlight.getKenName() + "_" + highlight.getTaskDesc();
            final StorageReference ref = storageReference.child(path);

            final UploadTask uploadTask = ref.putFile(videoUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    builder.setProgress(0, 0, false)
                            .setContentText("הסרטון עלה בהצלחה").setOngoing(false);
                    notificationManager.notify(notificationID, builder.build());

                    storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            String videoUrl = "";
                            try {
                                videoUrl = (new URL(uri.toString())).toString();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            highlight.setVideoURL(videoUrl);
                            databaseReference.child("highlights").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<Highlight> highlights = new ArrayList<>();
                                    if (dataSnapshot.exists()) {
                                        GenericTypeIndicator<ArrayList<Highlight>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Highlight>>() {
                                        };
                                        highlights = dataSnapshot.getValue(genericTypeIndicator);
                                    }
                                    for (Highlight highlightFromDatabase : highlights) {
                                        if (highlightFromDatabase.isSameHighlight(highlight)) {
                                            highlightFromDatabase.setVideoURL(highlight.getVideoURL());
                                            break;
                                        }
                                    }
                                    databaseReference.child("highlights").setValue(highlights).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent highlightUploadedIntent = new Intent("sharon_simon.highlight_uploaded_action");
                                            highlightUploadedIntent.putExtra("highlight", highlight);
                                            LocalBroadcastManager.getInstance(UploadHighlightToFirebaseService.this).sendBroadcast(highlightUploadedIntent);
                                            //stopForeground(false);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            stopForeground(false);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    stopForeground(false);
                                }
                            });
                        }
                    });

                    stopForeground(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uploadTask", "failure");
                    e.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("test",isCanceled + "");
                    if (isCanceled) uploadTask.cancel();
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    builder.setProgress(100, (int) progress, false);
                    notificationManager.notify(notificationID, builder.build());
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        databaseReference.child("highlights").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Highlight> highlights = new ArrayList<>();
                                if (dataSnapshot.exists()) {
                                    GenericTypeIndicator<ArrayList<Highlight>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Highlight>>() {
                                    };
                                    highlights = dataSnapshot.getValue(genericTypeIndicator);
                                }
                                for (Highlight highlight1 : highlights) {
                                    if (highlight.isSameHighlight(highlight1)) {
                                        highlights.remove(highlight1);
                                    }
                                }
                                databaseReference.child("highlights").setValue(highlights);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent highlightUploadedIntent = new Intent("sharon_simon.highlight_uploaded_action");
                        highlightUploadedIntent.putExtra("highlightUploadCanceled", highlight);
                        LocalBroadcastManager.getInstance(UploadHighlightToFirebaseService.this).sendBroadcast(highlightUploadedIntent);
                        builder.setProgress(0, 0, false)
                                .setContentText("העלאה בוטלה").setOngoing(false);
                        notificationManager.notify(notificationID, builder.build());
                        stopForeground(false);
                        isCanceled = false;
                    }
                });
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static int getNotificationID(){
        return c.incrementAndGet();
    }
}
