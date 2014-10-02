package com.sbm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static com.sbm.Global.*;

public class RegisterActivity extends Activity implements View.OnClickListener, DataReceiver {

    private Context context;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirmation;
    private Button buttonSubmit;

    private String[] params = new String[5];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = this;

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextRegisterEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = (EditText) findViewById(R.id.editTextConfirmPassword);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

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
    public void receive(ServerResponse response) {
        if (response != null) {
            if (response.getStatusCode() == HTTP_CREATED) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            } else {
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
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
