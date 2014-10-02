package com.sbm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.sbm.Global.*;

public class LoginActivity extends Activity implements View.OnClickListener, DataReceiver {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private String[] params = new String[2];
    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        editTextUsername = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Button buttonSignIn = (Button) findViewById(R.id.buttonLogin);
        TextView createAccountLink = (TextView) findViewById(R.id.createAccountLink);

        buttonSignIn.setOnClickListener(this);
        createAccountLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                params[0] = editTextUsername.getText().toString();
                params[1] = editTextPassword.getText().toString();
                LoginSession mySession = new LoginSession(context);
                mySession.delegate = (DataReceiver) context;
                mySession.execute(params);
                break;
            case R.id.createAccountLink:
                Intent i = new Intent(context, RegisterActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void receive(ServerResponse response) {
        if (response != null) {
            if (response.getStatusCode() == HTTP_SUCCESS) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            } else {
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class LoginSession extends AsyncTask<String, Integer, ServerResponse> {
        private final Context LoginSessionContext;
        private ProgressDialog dialog;
        public DataReceiver delegate;

        public LoginSession(Context context) {
            LoginSessionContext = context;
            dialog = new ProgressDialog(LoginSessionContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Signing in..");
            this.dialog.show();
        }

        @Override
        protected ServerResponse doInBackground(String... params) {
            ServerResponse serverResponse = null;

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(LOGIN_URL);

            List<NameValuePair> value = new LinkedList<NameValuePair>();
            value.add(new BasicNameValuePair(EMAIL, params[0]));
            value.add(new BasicNameValuePair(PASSWORD, params[1]));

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
            delegate.receive(response);
            if (dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }

}
