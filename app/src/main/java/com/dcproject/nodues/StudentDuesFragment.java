package com.dcproject.nodues;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dcproject.nodues._MainActivity.GMailSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentDuesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "roll_number";
    private static final String ARG_PARAM2 = "department";

    // TODO: Rename and change types of parameters
    private String rollno;
    private String department;
    View view;
    TextView rollnoShow;
    ListView duesView;
    Button approve, reject;

    String msg;

    ArrayList<String> duelist = new ArrayList<String>();
    ArrayList<String> reslist = new ArrayList<String>();


    ProgressDialog progressDialog;
    public static final String TAG = "StudentDuesFragment";


    //private OnFragmentInteractionListener mListener;

    public StudentDuesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static StudentDuesFragment newInstance(String param1, String param2) {
        StudentDuesFragment fragment = new StudentDuesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rollno = getArguments().getString(ARG_PARAM1);
            department = getArguments().getString(ARG_PARAM2);
        }
    }*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(rollno);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"In StudentDueFragment");
        view= inflater.inflate(R.layout.fragment_student_dues, container, false);
        progressDialog=new ProgressDialog(getActivity());

        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.head).setVisibility(View.GONE);
        view.findViewById(R.id.tv_nodue).setVisibility(View.GONE);
        view.findViewById(R.id.dues_list).setVisibility(View.GONE);
        view.findViewById(R.id.approve_button).setVisibility(View.GONE);
        view.findViewById(R.id.reject_button).setVisibility(View.GONE);

        if (getArguments() != null) {
            rollno = getArguments().getString(ARG_PARAM1);
            department = getArguments().getString(ARG_PARAM2);
        }
        rollnoShow=(TextView)view.findViewById(R.id.student_rollno);
        approve=(Button) view.findViewById(R.id.approve_button);
        reject=(Button)view.findViewById(R.id.reject_button);
        duesView = (ListView) view.findViewById(R.id.dues_list);
        rollnoShow.setText(rollno);

        displayDuesList();

        //Listeners for accept/reject buttons
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveRequest();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectRequest();
            }
        });


        return view;
    }


    public void approveRequest(){
        Log.d(TAG,"In Approve Listener");
        DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        DatabaseReference request=db.child("Request").child(department).child(rollno);
        DatabaseReference stu=db.child("Students").child(rollno).child("email");
        request.setValue("approved");
        Toast.makeText(getActivity(), "Approved Succesfully", Toast.LENGTH_LONG ).show();
        Log.d(TAG,"Approved" );

        stu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getValue().toString());
                sendEmail(dataSnapshot.getValue().toString(),"NoDues NITC",department+" has approved your request for Nodue");
                //Toast.makeText(getActivity(),"sdfsdfdf",Toast.LENGTH_LONG).show();
                getFragmentManager().popBackStackImmediate ();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void rejectRequest(){
        final DatabaseReference db= FirebaseDatabase.getInstance().getReference();
        DatabaseReference request=db.child("Request").child(department).child(rollno);
        DatabaseReference stu=db.child("Students").child(rollno).child("email");
        request.setValue("rejected");
        Toast.makeText(getActivity(), "Rejected Succesfully", Toast.LENGTH_LONG ).show();
        Log.d(TAG,"Rejected" );

        stu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.getValue().toString());
                final String toemail=dataSnapshot.getValue().toString();

                msg=department+" has rejected your request for Nodue\n";
                msg=msg+"You have Dues remaining to pay\n";
                DatabaseReference dues=db.child("Dues").child(department).child(rollno);
                dues.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot due:dataSnapshot.getChildren()) {
                            Dues d;
                            d=due.getValue(Dues.class);
                            msg = msg + d.getRemaining() + "     " +d.getreason()+"\n";
                        }
                        Log.d(TAG,msg.toString());
                        sendEmail(toemail,"NoDues NITC", msg);
                        //Toast.makeText(getActivity(),"sdfsdfdf",Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStackImmediate ();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void displayDuesList(){
        DatabaseReference db=FirebaseDatabase.getInstance().getReference();
        DatabaseReference studentdues= db.child("Dues").child(department).child(rollno);
        studentdues.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                duelist.clear();
                reslist.clear();
                for (DataSnapshot duesnap : dataSnapshot.getChildren()) {
                    Dues due = duesnap.getValue(Dues.class);
                    if (due != null && due.getRemaining() > 0) {
                        duelist.add(due.getRemaining().toString());
                        reslist.add(due.getreason());
                    }

                }
                CustomAdapter listadapter = new CustomAdapter(getActivity(), duelist, reslist);
                duesView.setAdapter(listadapter);
                if(!duelist.isEmpty()){
                    view.findViewById(R.id.head).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_nodue).setVisibility(View.GONE);
                    view.findViewById(R.id.dues_list).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.approve_button).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.reject_button).setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
                else{
                    view.findViewById(R.id.head).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_nodue).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.dues_list).setVisibility(View.GONE);
                    view.findViewById(R.id.approve_button).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.reject_button).setVisibility(View.VISIBLE);
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void sendEmail(final String to, final String subject, final String message) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("noduesnitc@gmail.com","nitcalicut");
                    sender.sendMail(subject, message, "noduesnitc@gmail.com", to);
                    Log.w("sendEmail","Email successfully sent!");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(),"Email successfully sent!", Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (final Exception e) {
                    Log.e("sendEmail", e.getMessage(), e);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getActivity(),"Email not successfully!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

        }).start();
    }
}
