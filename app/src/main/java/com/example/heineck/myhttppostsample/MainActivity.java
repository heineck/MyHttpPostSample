package com.example.heineck.myhttppostsample;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.jar.Attributes;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ProgressDialog progressDialog;

        private static String TAG = "PlaceholderFragment";

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button btnSubmit = (Button)rootView.findViewById(R.id.btnSubmit);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&sensor=true";
                        String params = null;
                        params = URLEncoder.encode("latlng", "UTF-8")
                                + "=" + URLEncoder.encode("40.714224,-73.961452", "UTF-8");
                        params += "&" + URLEncoder.encode("sensor", "UTF-8") + "="
                                + URLEncoder.encode("true", "UTF-8");

//                    params += "&" + URLEncoder.encode("user", "UTF-8")
//                            + "=" + URLEncoder.encode(Login, "UTF-8");
//
//                    params += "&" + URLEncoder.encode("pass", "UTF-8")
//                            + "=" + URLEncoder.encode(Pass, "UTF-8");

                        PostSendTask pst = new PostSendTask();
                        pst.execute(url,params);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            });

            return rootView;
        }

        private class PostSendTask extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                Log.d(TAG, "INIT onPreExecute");

                progressDialog = ProgressDialog.show(getActivity(), "Aguarde", "Carregando...", true, false);

                Log.d(TAG, "END onPreExecute");
            }

            @Override
            protected String doInBackground(String... params) {

                Log.d(TAG, "INIT doInBackground");

                Log.d(TAG, "url: " + params[0]);
                Log.d(TAG, "params: " + params[1]);

                String response = MyRest.performPost(params[0],params[1]);

                Log.d(TAG, "response: " + response);

                Log.d(TAG, "END doInBackground");

                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                Log.d(TAG, "INIT onPostExecute");

                progressDialog.dismiss();

                // the object data is inside a "global" JSONObject
                try {
                    JSONObject data = new JSONObject(s);
                    Log.d(TAG, "status: " + data.getString("status"));

                    JSONArray results = data.getJSONArray("results");
                    JSONObject addrCompObj = results.getJSONObject(0);
                    JSONArray addrComp = addrCompObj.getJSONArray("address_components");
                    JSONObject stateObj = addrComp.getJSONObject(6);
                    String state = stateObj.getString("long_name");
                    Log.d(TAG, "state: " + state);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "END onPostExecute");

            }
        }
    }
}
