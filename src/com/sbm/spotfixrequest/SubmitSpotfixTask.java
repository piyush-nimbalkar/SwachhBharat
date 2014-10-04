package com.sbm.spotfixrequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.sbm.DataReceiver;
import com.sbm.Global;
import com.sbm.ServerResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class SubmitSpotfixTask extends AsyncTask<String, Integer, ServerResponse> {

    private ProgressDialog dialog;
    public DataReceiver delegate;

    public SubmitSpotfixTask(Context context) {
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog.setMessage("Submitting request...");
        this.dialog.show();
    }

    @Override
    protected ServerResponse doInBackground(String... params) {
        ServerResponse serverResponse = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(Global.CREATE_SPOTFIXES_URL);

        List<NameValuePair> value = new LinkedList<NameValuePair>();
        value.add(new BasicNameValuePair(Global.SPOTFIX_OWNER_ID, params[0]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_TITLE, params[1]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_DESC, params[2]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_ESTIMATED_HOURS, params[3]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_ESTIMATED_PEOPLE, params[4]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_LAT, params[5]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_LONG, params[6]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_FIX_DATE, params[7]));
        value.add(new BasicNameValuePair(Global.SPOTFIX_STATUS, params[8]));

        try {
            post.setEntity(new UrlEncodedFormEntity(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            HttpResponse httpResponse = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String responseString = reader.readLine();
            serverResponse = new ServerResponse(httpResponse.getStatusLine().getStatusCode(), responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    @Override
    protected void onPostExecute(ServerResponse response) {
        super.onPostExecute(response);
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
