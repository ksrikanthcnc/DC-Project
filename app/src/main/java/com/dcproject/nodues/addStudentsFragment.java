package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PublicKey;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class addStudentsFragment extends Fragment {

    public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    FirebaseAuth firebaseAuth;
    FirebaseUser User;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    public String URL="https://docs.google.com/forms/u/1/d/e/1FAIpQLSfVZab7FSR9-U-iUU2lf2siPpvOIRpCsHhBsmcWsrhBsS91Yw/formResponse";
    public String namekey="entry.1158065776";
    public String emailkey="entry.863335537";
    public String passkey="entry.917818393";
    public String branchkey="entry.1956818872";
    public String programmekey="entry.268186543";
    public String yearkey="entry.414828874";

    EditText email, password,year,name_et,pgm_et ;
    Button SignUp,Back ;
    String EmailHolder,PasswordHolder,YearHolder,nameHolder,pgmHolder;
    ProgressDialog progressDialog;
    Boolean EditTextStatus ;

    public static String TAG = "addStudentsFragment";


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG,"In addStudentsFragment");
        view =inflater.inflate(R.layout.fragment_addstudents, container, false);


        email = (EditText)view.findViewById(R.id.EditText_User_EmailID);
        password = (EditText)view.findViewById(R.id.EditText_User_Password);
        year=(EditText)view.findViewById(R.id.year);
        SignUp = (Button)view.findViewById(R.id.Button_SignUp);
        name_et=(EditText)view.findViewById(R.id.name);
        pgm_et=(EditText)view.findViewById(R.id.programme);

        firebaseAuth = FirebaseAuth.getInstance();
        User=firebaseAuth.getCurrentUser();
        //AuthCredential credential = EmailAuthProvider.getCredential(User.getEmail(),);

        progressDialog = new ProgressDialog(getActivity());

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckEditTextIsEmptyOrNot()){
                    //UserRegistrationFunction();
                    PostDataTask postDataTask = new PostDataTask();

                    //execute asynctask
                    postDataTask.execute(URL,EmailHolder,PasswordHolder,YearHolder,nameHolder,pgmHolder);

                }
                else {
                    Toast.makeText(getActivity(), "Please fill all form fields.", Toast.LENGTH_LONG).show();
                }
            }
        });


        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Add Students");
    }
    public boolean CheckEditTextIsEmptyOrNot(){

        // Getting name and email from EditText and save into string variables.
        EmailHolder = email.getText().toString().trim();
        PasswordHolder = password.getText().toString().trim();
        YearHolder=year.getText().toString().trim();
        nameHolder=name_et.getText().toString().trim();
        pgmHolder=pgm_et.getText().toString().trim();


        if(TextUtils.isEmpty(EmailHolder)||!android.util.Patterns.EMAIL_ADDRESS.matcher(EmailHolder).matches() ){
            Toast.makeText(getActivity(), "Enter a valid Email", Toast.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(PasswordHolder)){
            Toast.makeText(getActivity(), "Enter a Password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true ;
    }

    private class PostDataTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... contactData) {
            Boolean result = true;
            String url = contactData[0];
            String email = contactData[1];
            String password = contactData[2];
            String year = contactData[3];
            String name = contactData[4];
            String pgm = contactData[5];
            String postBody="";

            try {
                //all values must be URL encoded to make sure that special characters like & | ",etc.
                //do not cause problems
                postBody = emailkey+"=" + URLEncoder.encode(email,"UTF-8") +
                        "&" + passkey + "=" + URLEncoder.encode(password,"UTF-8") +
                        "&" + yearkey + "=" + URLEncoder.encode(year,"UTF-8")+
                        "&" + namekey + "=" + URLEncoder.encode(name,"UTF-8") +
                        "&" + programmekey + "=" + URLEncoder.encode(pgm,"UTF-8") ;
            } catch (UnsupportedEncodingException ex) {
                result=false;
            }

            /*
            //If you want to use HttpRequest class from http://stackoverflow.com/a/2253280/1261816
            try {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.sendPost(url, postBody);
		}catch (Exception exception){
			result = false;
		}
            */

            try{
                //Create OkHttpClient for sending request
                OkHttpClient client = new OkHttpClient();
                //Create the request body with the help of Media Type
                RequestBody body = RequestBody.create(FORM_DATA_TYPE, postBody);
                okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(body).build();
                //Send the request
                okhttp3.Response response = client.newCall(request).execute();
            }catch (IOException exception){
                result=false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            //Print Success or failure message accordingly
            Toast.makeText(getActivity(),result?"Message successfully sent!":"There was some error in sending message. Please try again after some time.",Toast.LENGTH_LONG).show();
        }

    }

    // Creating UserRegistrationFunction
    public void UserRegistrationFunction(){

        progressDialog.setMessage("Please Wait,Registering");
        progressDialog.show();



        // Creating createUserWithEmailAndPassword method and pass email and password inside it.
        firebaseAuth.createUserWithEmailAndPassword(EmailHolder, PasswordHolder).
                addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Checking if user is registered successfully.
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Student added Successfully",Toast.LENGTH_LONG).show();
                            //firebaseAuth.signOut();
                        }else{
                            Toast.makeText(getActivity(),"Something Went Wrong.",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
}
