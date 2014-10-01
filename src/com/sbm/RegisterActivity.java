package com.sbm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity implements View.OnClickListener{

    static final String TAG = "REGISTER";

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirmation;
    private Button buttonSubmit;
    public static String firstname = null;
    public static String lastname = null;
    public static String username = null;
    public static String password = null;
    public static String passwordconfirmation = null;
    public SharedPreferences preferences;
    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        context = this;

        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPasswordConfirmation = (EditText) findViewById(R.id.editTextConfirmPassword);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.buttonSubmit:
//                firstname = editTextFirstName.getText().toString();
//                lastname = editTextLastName.getText().toString();
//                username = editTextUsername.getText().toString();
//                password = editTextPassword.getText().toString();
//                passwordconfirmation = editTextPasswordConfirmation.getText().toString();
//                Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT);
//                break;
//        }
    	if (v.getId() == R.id.buttonSubmit) {
            firstname = editTextFirstName.getText().toString();
            lastname = editTextLastName.getText().toString();
            username = editTextUsername.getText().toString();
            password = editTextPassword.getText().toString();
            passwordconfirmation = editTextPasswordConfirmation.getText().toString();
            Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();
    	}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
