package com.sbm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.sbm.model.Spotfix;
import com.sbm.model.SpotfixBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;

import static com.sbm.Global.*;

public class SyncSpotfixesTask extends AsyncTask<String, Integer, ServerResponse> {

    private final static String TAG = "SYNC_TASK";

    private final Context context;
    public DataReceiver delegate;
    private ProgressDialog dialog;

    public SyncSpotfixesTask(Context context) {
        this.context = context;
        dialog = new ProgressDialog(this.context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected ServerResponse doInBackground(String... params) {
        ServerResponse serverResponse = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(GET_SPOTFIXES_URL);

        try {
            HttpResponse httpResponse = client.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String responseString = reader.readLine();
            serverResponse = new ServerResponse(httpResponse.getStatusLine().getStatusCode(), responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    @Override
    protected void onPostExecute(ServerResponse response) {
        super.onPostExecute(response);
        ArrayList<Spotfix> spotfixes = new ArrayList<Spotfix>();
        Spotfix spotfix;
        Log.d(TAG, response.getMessage());
        try {
            JSONObject jsonObject = new JSONObject(response.getMessage());
            JSONArray spotfixesArray = (JSONArray) jsonObject.get("spotfixes");
            JSONObject spotfixObject;

            for (int i = 0; i < spotfixesArray.length(); i++) {
                spotfixObject = spotfixesArray.getJSONObject(i).getJSONObject("spotfix");
                spotfix = SpotfixBuilder.spotfix()
                        .setId(Long.parseLong(spotfixObject.getString("id")))
                        .setOwnerId(Long.parseLong(spotfixObject.getString("owner_id")))
                        .setTitle(spotfixObject.getString("title"))
                        .setDescription(spotfixObject.getString("description"))
                        .setStatus(spotfixObject.getString("status"))
                        .setEstimatedHours(Long.parseLong(spotfixObject.getString("estimated_hours")))
                        .setEstimatedPeople(Long.parseLong(spotfixObject.getString("estimated_people")))
                        .setLatitude(Double.parseDouble(spotfixObject.getString("latitude")))
                        .setLongitude(Double.parseDouble(spotfixObject.getString("longitude")))
                        .setFixDate(spotfixObject.getString("fix_date")).build();
                Log.d(TAG, "Spotfix added: " + spotfix.getTitle());
                spotfixes.add(spotfix);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            delegate.receive(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

}
