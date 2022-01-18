package com.example.ward63kota;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Submit extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText complainttext;
    Button submitbutton;
    String fullName, email, phone, complaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        complainttext=findViewById(R.id.submitbox);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    fullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
//                    pName.setText(fullName);
                    email = documentSnapshot.getString("emailAddress");
                    //pEmail.setText(documentSnapshot.getString("emailAddress"));
                    phone = fAuth.getCurrentUser().getPhoneNumber();
//                    pPhone.setText(fAuth.getCurrentUser().getPhoneNumber());
                }
            }
        });
        //email = documentSnapshot.getString("emailAddress");

        submitbutton=findViewById(R.id.submitbutton);
        submitbutton.setOnClickListener(this);
    }

        public void addItemToSheet() {
            complaint = complainttext.getText().toString();
            final ProgressDialog loading = ProgressDialog.show(this, "Sending complaint", "Please wait");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxwP1y8LbD0-Op_oq3DBWqVLoIyvPITh5xVb1XlYYDjZRXQrtP6XoL6gF7oIFjuY4GeIQ/exec",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            Toast.makeText(Submit.this, response, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    } ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("action", "addItem");
                    params.put("name", fullName);
                    params.put("contact", phone);
                    params.put("email", email);
                    params.put("issue", complaint);

                    return params;
                }
            };

            int socketTimeOut = 50000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }
    @Override
    public void onClick(View v){
        if(v==submitbutton){
            addItemToSheet();
        }
    }
}