package com.aliyan.mangwalopakistan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Registration extends Activity {

    private EditText email;
    private EditText password;
    private EditText confirm;
    private TextView login;
    private Button register;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        register = (Button) findViewById(R.id.nextBtn);
        login = (TextView) findViewById(R.id.login);
        progress = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirmPassword);

        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void authenticate() {
        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();
        String c = confirm.getText().toString().trim();

        View focusView = null;

        if(TextUtils.isEmpty(e)){
            email.setError("Please enter Email");
            focusView = email;
            focusView.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(p)){
            password.setError("Please enter Password");
            focusView = password;
            focusView.requestFocus();
            return;
        }

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(!pattern.matcher(e).matches()){
            email.setError("Invalid Email Address");
            focusView = email;
            focusView.requestFocus();
            return;
        }

        if(p.length() < 6){
            password.setError("Password must be at least 6 characters");
            focusView = password;
            focusView.requestFocus();
            return;
        }

        if(!p.equals(c)){
            confirm.setError("Passwords do not match");
            focusView = confirm;
            focusView.requestFocus();
            return;
        }

        progress.setMessage("Authenticating Please Wait...");
        progress.show();
        firebaseAuth.createUserWithEmailAndPassword(e,p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Intent intent = new Intent(getApplication(),Registration2.class);
                            intent.putExtra("type","email");
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplication(),"Authentication failed, check your connection settings and type a unique Email Address.",Toast.LENGTH_SHORT).show();
                        }
                        progress.dismiss();
                    }
                });

    }

}


