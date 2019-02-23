package com.example.admin.adminoperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Restaurant extends AppCompatActivity {
    ListView yourListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

         yourListView = (ListView) findViewById(R.id.listViewID);

         new JSONParse(this).execute();

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


            JSONArray json = jParser.getJSONFromUrl("http://192.168.2.26:8080/internship/mobile/main/getcategoriesres");
            return json;
        }

        @Override
        protected void onPostExecute(JSONArray json) {

            try {
                List<Item> items = new ArrayList<>();

                for(int i=0;i<json.length(); i++){
                    JSONObject o = json.getJSONObject(i);
                    items.add(new Item(o.getString("image"),
                            o.getString("category"),
                            o.getString("description"),
                            o.getInt("ID")
                    ));
                }

                ListAdapter customAdapter = new ListAdapter(cx, R.layout.row,items);
                yourListView.setAdapter(customAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}



class Item implements Serializable {

    String image; String content; String desc;
    Integer id = 0;
    Item(String image, String content, String desc,int id){
        this.image = image;
        this.content = content;
        this.desc = desc;
        this.id = id;
    }





}

class ListAdapter extends ArrayAdapter<Item> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    private List<Item> items;

    public ListAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView im = null;
        TextView content = null;
        TextView des = null;

        if (true) {

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
                new DownloadImageTask(im, "http://192.168.1.6:8888/internship/Myapp/files/"+p.image).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        }



        return v;

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        String iu;
        public DownloadImageTask(ImageView bmImage, String u) {
            this.bmImage = bmImage;
            iu = u;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = iu;
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