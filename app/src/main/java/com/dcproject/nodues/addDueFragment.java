package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class addDueFragment extends Fragment {
    EditText etRollno,etDue,etReason,etmonth;
    Button updateBtn;

    String rollNo;
    String dueAmount;
    String reason;
    String month;
    String email,department;
    int dues;

    FirebaseAuth firebaseAuth;
    FirebaseUser User;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    ProgressDialog progressDialog;
    public static String TAG = "addDueFragment";


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG,"In addDueFragment");
        view =inflater.inflate(R.layout.fragment_adddue, container, false);
        progressDialog=new ProgressDialog(getActivity());


        etRollno=(EditText)view.findViewById(R.id.rollno);
        etDue=(EditText)view.findViewById(R.id.dueamount);
        etReason=(EditText)view.findViewById(R.id.reason);
        //etmonth=(EditText)view.findViewById(R.id.month);
        month=null;
        Spinner spinner = (Spinner) view.findViewById(R.id.sp_month);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] months= getResources().getStringArray(R.array.month);
                month=months[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                month=null;
            }
        });


        updateBtn=(Button)view.findViewById(R.id.addduebtn);

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Toast.makeText(getActivity(),"Please Login in to continue",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getActivity(), com.dcproject.nodues.LoginActivity.class);
            startActivity(intent);
        }

        User=firebaseAuth.getCurrentUser();
        //email=User.getEmail().toString();

        db= FirebaseDatabase.getInstance().getReference();
        //retrive department name


        DatabaseReference dept=db.child("Departments");
        Query deptquery = dept.orderByChild("email").equalTo(User.getEmail());
        Log.d(TAG,"in getDeptId "+ User.getEmail() +deptquery.toString());
        deptquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data" +dataSnapshot.toString());
                for(DataSnapshot dept1 : dataSnapshot.getChildren()){
                    Log.d(TAG, dept1.toString());
                    department = dept1.getKey();
                    break;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"database error in getdept id");
            }
        });



        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                if(checkfields())
                    adddue();

                progressDialog.dismiss();
            }
        });


        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Add Dues");
    }


    public boolean checkfields(){

        Log.d(TAG,"In Checkfields");
        rollNo=etRollno.getText().toString().trim();
        dueAmount=etDue.getText().toString().trim();
        reason=etReason.getText().toString().trim();
        //month=etmonth.getText().toString().trim();

        if(rollNo.isEmpty()){
            Toast.makeText(getActivity(), "Enter a student Roll No", Toast.LENGTH_LONG).show();
            return false;
        }
        if(dueAmount.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a Due amount", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            dues=Integer.parseInt(dueAmount);
        if(reason.isEmpty()){
            Toast.makeText(getActivity(), "Enter Due reason", Toast.LENGTH_LONG).show();
            return false;
        }
        if(month.isEmpty()){
            Toast.makeText(getActivity(),"Enter month",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void adddue(){
        Log.d(TAG,"In add due to database");
        DatabaseReference dbDues=db.child("Dues");
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String now = ISO_8601_FORMAT.format(new Date());
        dbDues.child(department).child(rollNo).push().setValue(new Dues(reason,dues,now,month));
        //etRollno.setText("");
        etRollno.getText().clear();
        etReason.setText("");
        etDue.setText("");
        //etmonth.setText("");
        month=null;
        Toast.makeText(getActivity(),"Due Added",Toast.LENGTH_LONG).show();
        Log.d(TAG,"Due updated");
    }
    
}
