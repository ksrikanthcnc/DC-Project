package com.dcproject.nodues;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class updateDueFragment extends Fragment {

    FirebaseAuth firebaseauth;
    FirebaseUser User;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    EditText amt_et,id_et,roll_et;
    TextView rem_tv,res_tv;
    Button getdue_btn;
    ListView lv_due;

    Dues duedetails;
    String amt,transid,roll,department;
    int amtint;

    ArrayList<String> duelist = new ArrayList<String>();
    ArrayList<String> reslist = new ArrayList<String>();
    ArrayList<String> db_dues = new ArrayList<String>();

    ProgressDialog progressDialog;
    public static String TAG = "updateDueFragment";

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"In updateDueFragment");
        view =inflater.inflate(R.layout.fragment_updatedue, container, false);

        progressDialog=new ProgressDialog(getActivity());
        firebaseauth=FirebaseAuth.getInstance();
        User=firebaseauth.getCurrentUser();

        roll_et=(EditText)view.findViewById(R.id.et_roll);
        getdue_btn=(Button)view.findViewById(R.id.btn_getdue);

        lv_due=(ListView)view.findViewById(R.id.due_lv);

        getdue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Please Wait fetching Dues");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                roll = roll_et.getText().toString();
                if (!roll.isEmpty()) {
                    //get department key
                    roll=roll.toUpperCase();
                    DatabaseReference dept = db.child("Departments");
                    Query deptquery = dept.orderByChild("email").equalTo(User.getEmail());
                    Log.d(TAG, "in getDeptId " + User.getEmail() + deptquery.toString());
                    deptquery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "data" + dataSnapshot.toString());
                            for (DataSnapshot dept1 : dataSnapshot.getChildren()) {
                                Log.d(TAG, dept1.toString());
                                department = dept1.getKey();
                                break;
                            }
                            getduelist();
                            progressDialog.dismiss();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "database error in getdept id");
                            //progressDialog.dismiss();
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(),"Enter a Student id",Toast.LENGTH_LONG).show();
                }

            }
        });
        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Update Due");
    }


    void getduelist(){
        Log.d(TAG,"getting duelist");
        DatabaseReference stu_due=db.child("Dues").child(department).child(roll);

        stu_due.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                duelist.clear();
                reslist.clear();
                if(dataSnapshot.hasChildren()) {
                    view.findViewById(R.id.head).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.empty).setVisibility(View.GONE);
                    view.findViewById(R.id.duelist_ll).setVisibility(View.VISIBLE);
                }
                else{
                    view.findViewById(R.id.head).setVisibility(View.GONE);
                    view.findViewById(R.id.empty).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.duelist_ll).setVisibility(View.GONE);
                }
                for (DataSnapshot due : dataSnapshot.getChildren()) {
                    Log.d(TAG,"dueid"+due.getKey());
                    db_dues.add(due.getKey());
                    duedetails=due.getValue(Dues.class);
                    duelist.add(duedetails.getRemaining().toString());
                    reslist.add(duedetails.getreason());
                }
                Log.d(TAG,"changing layout");
                //ArrayAdapter<String> listadapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,duelist);
                CustomAdapter listadapter=new CustomAdapter(getActivity(),duelist,reslist);
                lv_due.setAdapter(listadapter);

                AdapterView.OnItemClickListener dueClickListener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                        Log.d(TAG, "In click listener " +position);
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View view_ad=inflater.inflate(R.layout.alert_updatedue, null);
                        res_tv=(TextView)view_ad.findViewById(R.id.tv_reason);
                        rem_tv=(TextView)view_ad.findViewById(R.id.tv_remaining);

                        Log.d(TAG,duelist.get(position)+reslist.get(position));
                        res_tv.setText(reslist.get(position));
                        rem_tv.setText(duelist.get(position));

                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setView(view_ad);
                        builder.setTitle("Updatedue");
                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                amt_et=(EditText)view_ad.findViewById(R.id.et_payment);
                                id_et=(EditText)view_ad.findViewById(R.id.et_payid);
                                if(checkfields()){
                                    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                                    String now = ISO_8601_FORMAT.format(new Date());
                                    db.child("Payments").child(department).child(roll).push().setValue(new Payments(transid,amtint,now));

                                    final DatabaseReference db_due=db.child("Dues").child(department).child(roll).child(db_dues.get(position));
                                    db_due.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int rem_due=dataSnapshot.child("remaining").getValue(int.class);
                                            Log.d(TAG,"remainigdue:  "+rem_due);
                                            db_due.child("remaining").setValue(rem_due-amtint);
                                            Toast.makeText(getActivity(),"payment Added",Toast.LENGTH_LONG).show();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                };
                lv_due.setOnItemClickListener(dueClickListener);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "database error in getdept id");
            }
        });
    }

    public boolean checkfields(){
        amt=amt_et.getText().toString();
        transid=id_et.getText().toString();
        if(amt.isEmpty()){
            Toast.makeText(getActivity(),"Enter Payment amount",Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            amtint=Integer.parseInt(amt);
        }
        if(transid.isEmpty()){
            Toast.makeText(getActivity(),"Enter transaction id",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
