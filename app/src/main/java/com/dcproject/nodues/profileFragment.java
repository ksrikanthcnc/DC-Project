package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static java.sql.Types.NULL;


public class profileFragment extends Fragment {

    FirebaseUser user;
    DatabaseReference db=FirebaseDatabase.getInstance().getReference();

    Student studata;
    String rollno;
    ImageView edit_btn;
    EditText branch_et,programme_et,year_et,phone_et,name_et;
    ProgressDialog progressDialog;

    public static final String TAG = "profileFragment";


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG,"In profileFragment");
        view=inflater.inflate(R.layout.fragment_profile, container, false);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);


        final TextView branch_tv=(TextView)view.findViewById(R.id.tv_branch);
        final TextView email_tv=(TextView)view.findViewById(R.id.tv_email);
        final TextView mobile_tv=(TextView)view.findViewById(R.id.tv_mobile);
        final TextView name_tv=(TextView)view.findViewById(R.id.tv_name);
        final TextView programme_tv=(TextView)view.findViewById(R.id.tv_programme);
        final TextView roll_tv=(TextView)view.findViewById(R.id.tv_rollno);
        final TextView year_tv=(TextView)view.findViewById(R.id.tv_year);

        edit_btn=(ImageView)view.findViewById(R.id.edit);

        user= FirebaseAuth.getInstance().getCurrentUser();
        final Query student = db.child("Students").orderByChild("email").equalTo(user.getEmail());

        student.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    Log.d(TAG,"Student Exists");
                    for(DataSnapshot stu : dataSnapshot.getChildren()){

                        Log.d(TAG,"Roll no"+stu.getKey());
                        rollno=stu.getKey();
                        studata=stu.getValue(Student.class);

                        Log.d(TAG,"Name"+studata.getname());
                        Log.d(TAG,"branch"+studata.getbranch());
                        Log.d(TAG,"email"+studata.getemail());

                        name_tv.setText(studata.getname());
                        roll_tv.setText(stu.getKey());
                        branch_tv.setText(studata.getbranch());
                        programme_tv.setText(studata.getprogramme());
                        year_tv.setText(studata.getyear());
                        mobile_tv.setText(studata.getPhone());
                        email_tv.setText(studata.getemail());
                        break;
                    }
                    progressDialog.dismiss();
                }
                else {
                    Log.d(TAG,"Student doesn't Exists");
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "User Data not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Edit profile button");
                view.findViewById(R.id.profile_layout).setVisibility(View.GONE);
                view.findViewById(R.id.details_view).setVisibility(View.GONE);
                view.findViewById(R.id.edit_profile).setVisibility(View.VISIBLE);
                view.findViewById(R.id.details_edit).setVisibility(View.VISIBLE);

                TextView roll_tv2;
                branch_et=(EditText)view.findViewById(R.id.et_branch);
                programme_et=(EditText)view.findViewById(R.id.et_programme);
                year_et=(EditText)view.findViewById(R.id.et_year);
                phone_et=(EditText)view.findViewById(R.id.et_mobile);
                name_et=(EditText)view.findViewById(R.id.et_name);
                roll_tv2=(TextView)view.findViewById(R.id.tv_rollno2);

                roll_tv2.setText(rollno);
                branch_et.setText(studata.getbranch());
                programme_et.setText(studata.getprogramme());
                year_et.setText(studata.getyear());
                phone_et.setText(studata.getPhone());
                name_et.setText(studata.getname());

                Button updatebtn=(Button)view.findViewById(R.id.update_btn);
                Button cancelbtn=(Button)view.findViewById(R.id.cancel_btn);

                updatebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"In update Button");
                        progressDialog=new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        if(checkfields()) {
                            DatabaseReference sturef = db.child("Students").child(rollno);
                            Log.d(TAG,sturef.toString());
                            sturef.child("name").setValue(name_et.getText().toString());
                            sturef.child("branch").setValue(branch_et.getText().toString());
                            sturef.child("programme").setValue(programme_et.getText().toString());
                            sturef.child("year").setValue(year_et.getText().toString());
                            sturef.child("phone").setValue(phone_et.getText().toString());

                        }
                        else {
                            Toast.makeText(getActivity(),"Please fill all fields",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();

                    }
                });

                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,"In cancel Button");
                        view.findViewById(R.id.profile_layout).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.details_view).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.edit_profile).setVisibility(View.GONE);
                        view.findViewById(R.id.details_edit).setVisibility(View.GONE);
                    }
                });


            }
        });


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Profile");
    }

    public boolean checkfields(){

        if(branch_et.getText().toString().isEmpty()) {
            return false;
        }
        if(programme_et.getText().toString().isEmpty()){
            return false;
        }
        if(year_et.getText().toString().isEmpty()){
            return false;
        }
        if(phone_et.getText().toString().isEmpty()) {
            return false;
        }
        if(name_et.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }
}
