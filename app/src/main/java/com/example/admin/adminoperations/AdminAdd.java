package com.example.admin.adminoperations;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AdminAdd extends AppCompatActivity {
EditText category,description;
Button uploadbtn,submitbtn;
String cate,desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        category=findViewById(R.id.et1);
        description=findViewById(R.id.et2);

        uploadbtn=findViewById(R.id.btn2);
        submitbtn = findViewById(R.id.btn);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 42);

            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cate=category.getText().toString();
                desc=description.getText().toString();
                new DownloadFilesTask(uri).execute();

            }
        });
    }

    Uri uri = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {


        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {


            if (resultData != null) {
                uri = resultData.getData();
                System.out.println(" UUU "+uri.toString());


            }
        }
    }


    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {

        Uri sourceFileUri = null;

        DownloadFilesTask(Uri uri) {
            sourceFileUri = uri;
        }


        protected Long doInBackground(URL... urls) {
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File sourceFile = new File(sourceFileUri.toString());

            if (false) {


                Log.e("uploadFile", "Source File not exist");

                return 0L;

            } else {
                try {


                    InputStream fileInputStream = getContentResolver().openInputStream(sourceFileUri);
                    URL url = new URL("http://192.168.2.6:8888/internship/mobile/files/upload");


                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("file", sourceFileUri.getPath());

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + sourceFile.getName() + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);


                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];


                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }


                    dos.writeBytes(lineEnd);

                    String key = "category";
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: text/plain" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(cate);
                    dos.writeBytes(lineEnd);


                    key = "description";
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: text/plain" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(desc);
                    dos.writeBytes(lineEnd);


                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": ");



                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {


                    ex.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(AdminAdd.this, "MalformedURLException",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {


                    e.printStackTrace();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(AdminAdd.this, "Got Exception : see logcat ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Upload file to server", "Exception : "
                            + e.getMessage(), e);
                }

                return 200L;

            }


        }


    }}