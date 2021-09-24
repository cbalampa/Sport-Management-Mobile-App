package com.example.project2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class LoginActivity extends AppCompatActivity {
    public EditText et_email;
    private EditText et_password;
    private String email, password;
    private Button buttonLogin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Εμφάνιση Toolbar
        Toolbar toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Εμφάνιση του BackButton στο tool bar
        getSupportActionBar().setTitle("Log In"); // Θέτω ως τίτλο του toolbar το Log In

        init(); // // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο activity_login.xml

        // Ελέγχω αν έχουν συμπληρωθεί όλα τα πεδία ώστε να ενεργοποιήσω το Login button
        et_email.addTextChangedListener(sumbitTextWatcher);
        et_password.addTextChangedListener(sumbitTextWatcher);

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί buttonLogin | Log In
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();
            } // End of onClick method
        }); // End of setOnClickListener method
    } // End of onCreate

    // Εκτελείται όταν κάνω κλικ στο back button του navigation bar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.slide_up_out);
        finish();
    }

    // Εκτελείται όταν κάνω κλικ στο arrow button του tool bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    // Disable Button When EditExt is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getValuesFromTextView(); // Παίρνω τις τιμές που έχουν τα TextViews στο activity_main.xml και τα αντιστοιχώ στις μεταβλητές
            buttonLogin.setEnabled(!email.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }; // End of TextWatcher

    private void login() {
        getValuesFromTextView(); // Παίρνω τις τιμές που έχουν τα TextViews στο activity_main.xml και τα αντιστοιχώ στις μεταβλητές

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", email);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Prevent activity from opening multiple times
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Incorrect email or password", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    // Παίρνω τις τιμές που έχουν τα TextViews στο activity_main.xml και τα αντιστοιχώ στις μεταβλητές
    public void getValuesFromTextView() {
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
    }

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο activity_login.xml
    private void init() {
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        buttonLogin = findViewById(R.id.buttonLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

} // End of class