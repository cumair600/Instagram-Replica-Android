package com.example.cykablyat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    EditText edtUsername, edtName, edtEmail, edtPassword, edtPhone;
    Button btnRegister;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        MobileAds.initialize(this,"ca-app-pub-1362709198139070/3546979410");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        edtUsername = findViewById(R.id.edtUsername);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(Signup.this);
                pd.setMessage("Please Wait...");
                pd.show();

                String u = edtUsername.getText().toString();
                String n = edtName.getText().toString();
                String e = edtEmail.getText().toString();
                String p = edtPassword.getText().toString();
                String ph = edtPhone.getText().toString();

                if(TextUtils.isEmpty(u) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p) || TextUtils.isEmpty(n) || TextUtils.isEmpty(ph)) {
                    Toast.makeText(Signup.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else if(p.length() < 6) {
                    Toast.makeText(Signup.this, "Password length must have 6 characters at least!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else
                {
                    register(u,n,e,p,ph);
                }
            }
        });

    }

    private void register(final String u, final String n, final String e, final String p, final String ph)
    {
        auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(Signup.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser fu = auth.getCurrentUser();
                    String userid = fu.getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", u.toLowerCase());
                    hashMap.put("name", n);
                    hashMap.put("bio", "");
                    hashMap.put("phone",ph);
                    hashMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/cykablyat-6272e.appspot.com/o/Profile_avatar_placeholder_large.png?alt=media&token=8d5949e2-2be3-49c0-b67e-6575afd59ae8");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Intent intent = new Intent(Signup.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else
                    {
                        pd.dismiss();
                        Toast.makeText(Signup.this,"You cannot register with this email or password!",Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}
