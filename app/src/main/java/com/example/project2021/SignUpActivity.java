package com.example.project2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2021.database.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUpActivity extends AppCompatActivity {
    private EditText et_username, et_email, et_password, et_password_repeat;
    private FirebaseFirestore firebaseFirestore;
    private String var_username, var_email, var_password, var_password_repeat;
    private Button buttonLogin;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Εμφάνιση Toolbar
        Toolbar toolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Εμφάνιση του BackButton στο tool bar
        getSupportActionBar().setTitle("Sign Up"); // Θέτω ως τίτλο του toolbar το Sign up

        init(); // Καλώ την μέθοδο init για να αρχικοποιήσω τις μεταβλητές μου

        // Ελέγχω αν έχουν συμπληρωθεί όλα τα πεδία ώστε να ενεργοποιήσω το Sign up button
        et_username.addTextChangedListener(sumbitTextWatcher);
        et_email.addTextChangedListener(sumbitTextWatcher);
        et_password.addTextChangedListener(sumbitTextWatcher);
        et_password_repeat.addTextChangedListener(sumbitTextWatcher);

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί buttonSignUp | Sing Up
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (var_password.equals(var_password_repeat)) {
                    registerUser(); // Καλώ την μέθοδο registerUser για να δημιουργήσω νέο λογαριασμό χρήστη
                } else {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }
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

    // Δημιουργία λογαριασμού χρήστη
    private void registerUser() {
        getValuesFromTextView();

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        DocumentReference docRef = firebaseFirestore.collection("Users").document(var_email);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Email already in use.", Toast.LENGTH_LONG).show();
                } else {
                    Users user = new Users();
                    try {
                        user.setUsername(var_username);
                        user.setEmail(var_email);
                        user.setPassword(var_password);

                        firebaseFirestore.collection("Users")
                                .document(var_email)
                                .set(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notification();

                                        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword());

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.putExtra("email", var_email);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Prevent activity from opening multiple times
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } // End of if document.exists
            } else {
                Toast.makeText(SignUpActivity.this, "get failed with " + task.getException(), Toast.LENGTH_LONG).show();
            } // End of isSuccessful
        });
    } // End of registerUser

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
        }
    }

    // Disable Button When EditExt is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getValuesFromTextView();

            // Ενεργοποίηση sign up όταν το μήκος του password είναι μεγαλύτερο ή ίσο του 6
            if (var_password.length() >= 6 && var_password_repeat.length() >= 6)
                buttonLogin.setEnabled(!var_username.isEmpty() && !var_email.isEmpty() && !var_password.isEmpty() && !var_password_repeat.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }; // End of TextWatcher

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

    // Αρχικοποιήση μεταβλητών | Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο activity_sign_up.xml
    private void init() {
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_pass);
        et_password_repeat = findViewById(R.id.et_pass_conf);

        buttonLogin = findViewById(R.id.buttonSignup);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    // Στέλνω notification μήνυμα στον χρήστη μετά την επιτυχή δημιουργία λογαριασμού
    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentText("Code Sphere")
                .setSmallIcon(R.drawable.ic_email_24)
                .setAutoCancel(true)
                .setContentText("Your account has been created successfully!");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

    // Παίρνω τις τιμές που έχουν τα TextViews στο activity_sign_up.xml και τα αντιστοιχώ στις μεταβλητές
    public void getValuesFromTextView() {
        var_username = et_username.getText().toString().trim();
        var_email = et_email.getText().toString().trim();
        var_password = et_password.getText().toString().trim();
        var_password_repeat = et_password_repeat.getText().toString().trim();
    }

    // Καθαρίζω τα EditTexts πεδία του activity ώστε να είναι έτοιμα για την επόμενη δημιουργία λογαριασμού χρήστη
    public void resetTextViews() {
        et_username.setText("");
        et_email.setText("");
        et_password.setText("");
        et_password_repeat.setText("");
    }

}