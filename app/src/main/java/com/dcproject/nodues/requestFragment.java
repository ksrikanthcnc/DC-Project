package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;


public class requestFragment extends Fragment {

    Button logout, request ;
    TextView userEmailShow ;
    FirebaseAuth firebaseAuth ;
    FirebaseUser firebaseUser;
    public String rollno;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    requestFragment.MyCustomAdapter dataAdapter = null;
    ArrayList<String> departments = new ArrayList<String>();

    ProgressDialog progressDialog;
    public static final String TAG = "requestFragment";

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_request, container, false);

        Log.d(TAG,"In requestFragment");
        DatabaseReference dept = db.child("Departments");
        dept.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dept1 : dataSnapshot.getChildren()){
                    departments.add(dept1.getKey());
                    //displayListView();
                    //checkButtonClick();
                }
                displayListView();
                checkButtonClick();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Request Tab");
    }

    private void displayListView() {
        //Array list of departments
        ListView lv = (ListView) view.findViewById(R.id.listView1);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ArrayList<Department> DepartmentList = new ArrayList<Department>();

        int i=0;
        for (i=0;i<departments.size();i++)
        {
            Department dep = new Department(departments.get(i),false);
            DepartmentList.add(dep);
        }

        //create an ArrayAdaptar from the String Array
        dataAdapter = new requestFragment.MyCustomAdapter(getActivity(), R.layout.checkbox, DepartmentList);

        ListView listView = (ListView) view.findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Department Department = (Department) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Clicked on Row: " + Department.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Department> {

        private ArrayList<Department> DepartmentList;

        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Department> DepartmentList) {
            super(context, textViewResourceId, DepartmentList);
            this.DepartmentList = new ArrayList<Department>();
            this.DepartmentList.addAll(DepartmentList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            requestFragment.MyCustomAdapter.ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.checkbox, null);

                holder = new requestFragment.MyCustomAdapter.ViewHolder();
                //holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Department Department = (Department) cb.getTag();
                        Toast.makeText(getActivity(), "Clicked on Checkbox: " + cb.getText(), Toast.LENGTH_SHORT).show();
                        Department.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (requestFragment.MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            Department Department = DepartmentList.get(position);

            holder.name.setText(Department.getName());
            holder.name.setChecked(Department.isSelected());
            holder.name.setTag(Department);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) view.findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                //responseText.append("The following were selected...\n");
                firebaseAuth= FirebaseAuth.getInstance();
                FirebaseUser user=firebaseAuth.getCurrentUser();
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                Query student = db.child("Students").orderByChild("email").equalTo(user.getEmail());

                student.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot stu : dataSnapshot.getChildren()){
                            rollno =stu.getKey();
                            break;
                        }
                        sendrequest();
                        Toast.makeText(getActivity(), "Request sent to the selected departments " , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        });


    }

    public void sendrequest(){
        ArrayList<Department> DepartmentList = dataAdapter.DepartmentList;
        String body;
        body = "Request to the following departments is succesful\n";

        for(int i=0;i<DepartmentList.size();i++){
            Department Department = DepartmentList.get(i);
            if(Department.isSelected()){
                body = body + Department.getName() + "\n";
                DatabaseReference request = db.child("Request").child(Department.getName()).child(rollno);
                request.setValue("pending");
            }
        }
        StudentDuesFragment.sendEmail(firebaseAuth.getCurrentUser().getEmail(),"NoDues NITC",body);

    }
}
