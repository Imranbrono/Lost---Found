package com.example.lost_found;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner,registeruser,textView;
    private EditText editTextFirstName,editTextLastName,editTextAddress,editTextAge,editTextEmail,editTextPassword,editTextConfirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner=(TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registeruser = (Button)findViewById(R.id.registeruser);
        registeruser.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.firstname);
        editTextLastName = (EditText) findViewById(R.id.lastname);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmpassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registeruser:
                registeruser();
                break;
        }
    }

    private void registeruser() {

        String email= editTextEmail.getText().toString().trim();
        String password= editTextPassword.getText().toString().trim();
        String confirmpassword= editTextConfirmPassword.getText().toString().trim();
        String firstname= editTextFirstName.getText().toString().trim();
        String lastname= editTextLastName.getText().toString().trim();
        String address= editTextAddress.getText().toString().trim();
        String age= editTextAge.getText().toString().trim();

        if (firstname.isEmpty()){
            editTextFirstName.setError("First Name is Required!");
            editTextFirstName.requestFocus();
            return;
        }
        if (lastname.isEmpty()){
            editTextLastName.setError("Last Name is Required!");
            editTextLastName.requestFocus();
            return;
        }
        if (age.isEmpty()){
            editTextAge.setError("Age is Require!");
            editTextAge.requestFocus();
            return;
        }
        if (email.isEmpty()){
            editTextEmail.setError("E-Mail is Compulsory!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid Email!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextPassword.setError("Password is Required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length()<8){
            editTextPassword.setError("Password lenght should be 8 characters!");
            editTextPassword.requestFocus();
            return;
        }
        if (confirmpassword.isEmpty()){
            editTextConfirmPassword.setError("Confirm Password is Required!");
            editTextConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmpassword)){
            editTextConfirmPassword.setError("Both Password fields must be identic");
            editTextConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User User = new User(firstname,lastname,address,age,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(User).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this,"User has been registered successfully", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        //redirect to login layout!
                                    }
                                    else {
                                        Toast.makeText(RegisterUser.this, "Failed to register try again!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(RegisterUser.this, "*** Failed to register try again! ***",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        /**
         I have uses Realtime database here to store above value
         All Firebase Realtime Database data is stored as JSON objects.
         You can think of the database as a cloud-hosted JSON tree.
         Unlike a SQL database, there are no tables or records.
         When you add data to the JSON tree, it becomes a node in the existing JSON structure with an associated key.
         **/

    }
}