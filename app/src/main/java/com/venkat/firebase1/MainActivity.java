package com.venkat.firebase1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseref;
    private  ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;


    private Uri fileUri;
    private Button Choose,Upload,fetch;
    private TextView Textfilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Choose = (Button)findViewById(R.id.buttonChoose);
        Upload = (Button)findViewById(R.id.buttonUpload);
        fetch = (Button)findViewById(R.id.fetch);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        Textfilename = (TextView)findViewById(R.id.Filename);



        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,MyRecyclerViewActivity.class));
            }
        });

        Choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    selectFile();
                }
                else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri!=null)
                {
                    uploadfile(fileUri);
                }
                else{
                    Toast.makeText(MainActivity.this,"Select a File",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadfile(Uri fileUri) {

        final String filename = System.currentTimeMillis()+"";

        mStorageRef = firebaseStorage.getReference();
        mDatabaseref = firebaseDatabase.getReference();

        mStorageRef.child("Uploads").child(filename).putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url = taskSnapshot.getStorage().getDownloadUrl().toString();

                        mDatabaseref.child(filename).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(MainActivity.this,"File Uploaded Successfully",Toast.LENGTH_SHORT).show();

                            else
                                {
                                    Toast.makeText(MainActivity.this,"File Upload not Successful",Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this,"File Upload not Successful",Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                //Progress bar
                progressBar.setVisibility(View.VISIBLE);
              int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

              progressBar.setProgress(currentProgress);

              if (currentProgress>=100)
              {
                  Textfilename.setText("Choose File");
                  progressBar.setVisibility(View.INVISIBLE);

              }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode ==9 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            selectFile();
        }
        else{
            Toast.makeText(MainActivity.this,"Please Provide Permission",Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {


        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,69);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69 && resultCode == RESULT_OK && data != null){

            fileUri = data.getData(); //get the URI of the selected File
            String filename = data.getData().getLastPathSegment();


            Textfilename.setText("File Name: "+data.getData().getLastPathSegment());

        }

        else
        {
            Toast.makeText(MainActivity.this," Please Select a File",Toast.LENGTH_SHORT).show();
        }
    }
}
