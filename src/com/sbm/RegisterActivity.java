package com.sbm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static com.sbm.Global.*;

public class RegisterActivity extends Activity implements View.OnClickListener, DataReceiver {

    private Context context;
    private SharedPreferences preferences;

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirmation;

    private String[] params = new String[5];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextRegisterEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = (EditText) findViewById(R.id.editTextConfirmPassword);
        Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                params[0] = editTextFirstName.getText().toString();
                params[1] = editTextLastName.getText().toString();
                params[2] = editTextEmail.getText().toString();
                params[3] = editTextPassword.getText().toString();
                params[4] = editTextPasswordConfirmation.getText().toString();
                RegisterSession session = new RegisterSession(context);
                session.delegate = (DataReceiver) context;
                session.execute(params);
                break;
        }
    }

    @Override
    public void receive(ServerResponse response) throws JSONException {
        if (response != null) {
            if (response.getStatusCode() == HTTP_CREATED) {
                JSONObject userObject = new JSONObject(response.getMessage()).getJSONObject(USER_OBJECT);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(CURRENT_USER_ID, userObject.getLong(USER_ID));
                editor.putString(CURRENT_USER_EMAIL, userObject.getString(EMAIL));
                editor.commit();

                startActivity(new Intent(context, MainActivity.class));
                finish();
            } else {
                JSONObject errorJSON = new JSONObject(response.getMessage());
                JSONArray messagesArray = errorJSON.getJSONArray(ERROR_MESSAGES);
                String errorMessages = "";
                for (int i = 0; i < messagesArray.length(); i++)
                    errorMessages += messagesArray.getString(i) + "\n";
                Toast.makeText(context, errorMessages, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class RegisterSession extends AsyncTask<String, Integer, ServerResponse> {
        private final Context RegisterSessionContext;
        private ProgressDialog dialog;
        public DataReceiver delegate;

        public RegisterSession(Context context) {
            RegisterSessionContext = context;
            dialog = new ProgressDialog(RegisterSessionContext);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Signing up..");
            this.dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(String... params) {
            ServerResponse serverResponse = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(REGISTER_URL);

            List<NameValuePair> value = new LinkedList<NameValuePair>();
            value.add(new BasicNameValuePair(FIRST_NAME, params[0]));
            value.add(new BasicNameValuePair(LAST_NAME, params[1]));
            value.add(new BasicNameValuePair(EMAIL, params[2]));
            value.add(new BasicNameValuePair(PASSWORD, params[3]));
            value.add(new BasicNameValuePair(PASSWORD_CONFIRMATION, params[4]));

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

}
