package com.example.admin.adminoperations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Delete extends AppCompatActivity {
    ListView yourListView;
    List<Item> items;
    ListAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        yourListView = (ListView) findViewById(R.id.listViewID);
        yourListView.setLongClickable(true);
        new JSONParse(this).execute();
        yourListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                final Item selected = items.get(pos);

                AlertDialog.Builder dg =  new AlertDialog.Builder(Delete.this)
                        .setTitle("Confirm to delete")
                        .setMessage("Do you really want to delete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(Delete.this, " deleting "+selected.id, Toast.LENGTH_SHORT).show();

                                new MyTask1(selected.id).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                items.remove(selected);
                                customAdapter.notifyDataSetChanged();

                            }})
                        .setNegativeButton(android.R.string.no, null);
                 dg.show();


                return true;
            }
        });



    }



     class MyTask1 extends AsyncTask<Void, Void, String> {

        int c_id;
        MyTask1(int c){

            c_id = c;
        }


         @Override
         protected void onPreExecute() {
             super.onPreExecute();


         }
        @Override
        protected String doInBackground(Void... params) {


            URL url = null;

            try {

                url = new URL("http://192.168.1.6:8888/internship/mobile/Myapp/delete&"+c_id);
                HttpURLConnection client = null;
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                int responseCode = client.getResponseCode();
                System.out.println("\n Sending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);
                InputStreamReader myInput = new InputStreamReader(client.getInputStream());
                BufferedReader in = new BufferedReader(myInput);
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();



            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

        }
    }

    class JSONParse extends AsyncTask<String, String, JSONArray> {
        private ProgressDialog pDialog;
        private Context cx;

        JSONParse(Context cx){
            this.cx = cx;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser jParser = new JSONParser();


            JSONArray json = jParser.getJSONFromUrl("http://192.168.2.26:8080/internship/mobile/main/getcategories");
            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            try {
               items = new ArrayList<>();

                for(int i=0;i<json.length(); i++){
                    JSONObject o = json.getJSONObject(i);
                    items.add(new Item(o.getString("image"),
                            o.getString("category"),
                            o.getString("description"),
                            o.getInt("ID")
                    ));
                }

                customAdapter = new ListAdapter(cx, R.layout.row,items);
                yourListView.setAdapter(customAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}




class ListAdapter1 extends ArrayAdapter<Item> {

    public ListAdapter1(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private List<Item> items;

    public ListAdapter1(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView im = null;
        TextView content = null;
        TextView des = null;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row, null);

            content = (TextView) v.findViewById(R.id.tvbdate);
            des = (TextView) v.findViewById(R.id.tvcity);
            im = (ImageView) v.findViewById(R.id.ic);
        }

        Item p = items.get(position);

        if (p != null) {

            content.setText(p.content);
            des.setText(p.desc);




            new DownloadImageTask(im)
                    .execute("http://192.168.2.26:8080/internship/mobile/files/"+p.image);





        }



        return v;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null){
                int nh = (int) ( result.getHeight() * (512.0 / result.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(result, 512, nh, true);

                bmImage.setImageBitmap(scaled);
            }
        }
    }
}