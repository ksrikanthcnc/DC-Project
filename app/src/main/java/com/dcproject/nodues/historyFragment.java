package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.ListIterator;


public class historyFragment extends Fragment {

    FirebaseAuth firebaseauth;
    FirebaseUser user;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ListView requests;
    public ArrayList<String> students= new ArrayList<String>();
    public ArrayList<String> status = new ArrayList<String>();
    public String department;

    ProgressDialog progressDialog;

    public static String TAG = "historyFragment";


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG,"In viewRequestsFragment");
        view =inflater.inflate(R.layout.fragment_history, container, false);

        progressDialog=new ProgressDialog(getActivity());

        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.tv_nohis).setVisibility(View.GONE);
        view.findViewById(R.id.head).setVisibility(View.GONE);

        firebaseauth=FirebaseAuth.getInstance();
        user=firebaseauth.getCurrentUser();
        requests=(ListView)view.findViewById(R.id.lv_history);

        final DatabaseReference request=db.child("Request");
        DatabaseReference dept=db.child("Departments");

        Log.d(TAG,"calling getDeptId");
        Query deptquery = dept.orderByChild("email").equalTo(user.getEmail());

        Log.d(TAG,"in getDeptId "+ user.getEmail() +deptquery.toString());
        deptquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "data" +dataSnapshot.toString());
                for(DataSnapshot dept1 : dataSnapshot.getChildren()){
                    Log.d(TAG, dept1.toString());
                    department = dept1.getKey();
                    getAllRequests(request.child(department));
                    break;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"database error in getdept id");
            }
        });

        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("View Requests");
    }

    // Add all requests into the list 'students'
    public void getAllRequests(DatabaseReference request){

        request.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                students.clear();
                status.clear();
                for (DataSnapshot student : dataSnapshot.getChildren()) {
                    if (student.getValue().toString().equals("approved")||student.getValue().toString().equals("rejected")) {
                        students.add(student.getKey());
                        status.add(student.getValue().toString());
                    }
                }
                ListIterator list = students.listIterator();
                while (list.hasNext()) {
                    String rollno = (String) list.next();
                    Log.d(TAG, "roll no " + rollno);
                }
                CustomAdapter listadapter = new CustomAdapter(getActivity(), students, status);
                requests.setAdapter(listadapter);
                if(!students.isEmpty()) {
                    view.findViewById(R.id.tv_nohis).setVisibility(View.GONE);
                    view.findViewById(R.id.head).setVisibility(View.VISIBLE);
                }
                else {
                    view.findViewById(R.id.tv_nohis).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.head).setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
