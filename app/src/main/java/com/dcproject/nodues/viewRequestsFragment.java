package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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



public class viewRequestsFragment extends Fragment {

    FirebaseAuth firebaseauth;
    FirebaseUser user;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ListView requests;
    public ArrayList<String> students= new ArrayList<String>();
    public String department;

    ProgressDialog progressDialog;

    public static String TAG = "viewRequestsFragment";


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG,"In viewRequestsFragment");
        view =inflater.inflate(R.layout.fragment_viewrequests, container, false);

        progressDialog=new ProgressDialog(getActivity());

        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.tv_noreq).setVisibility(View.VISIBLE);
        view.findViewById(R.id.requests_list).setVisibility(View.GONE);

        firebaseauth=FirebaseAuth.getInstance();
        user=firebaseauth.getCurrentUser();
        requests=(ListView)view.findViewById(R.id.requests_list);

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
                if(dataSnapshot.hasChildren()) {
                    view.findViewById(R.id.tv_noreq).setVisibility(View.GONE);
                    view.findViewById(R.id.requests_list).setVisibility(View.VISIBLE);
                    for (DataSnapshot student : dataSnapshot.getChildren()) {
                        if (student.getValue().toString().equals("pending")) {
                            students.add(student.getKey());
                        }
                    }
                    ListIterator list = students.listIterator();
                    while (list.hasNext()) {
                        String rollno = (String) list.next();
                        Log.d(TAG, "roll no " + rollno);
                    }
                    displayList();
                }
                else {
                    view.findViewById(R.id.tv_noreq).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.requests_list).setVisibility(View.GONE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displayList(){
        Log.d(TAG,"In displayList");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, students);
        requests.setAdapter(adapter);

        //Setting Itemclick listeners
        AdapterView.OnItemClickListener rollnoClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "In click listener " +position);
                StudentDuesFragment fragment= StudentDuesFragment.newInstance(students.get(position), department);
                //replacing the fragment
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }
        };
        requests.setOnItemClickListener(rollnoClickListener);
    }
}
