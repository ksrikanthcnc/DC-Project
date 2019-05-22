package com.dcproject.nodues;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class statusFragment extends Fragment {

    Button gen_btn;
    FirebaseUser user;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    ArrayList<String> departments = new ArrayList<String>();
    ArrayList<String> status = new ArrayList<String>();
    public static final String TAG = "statusFragment";
    String rollno;
    int dep=0,sts=0;
    ListView status_lv,dep_lv;
    ProgressDialog progressDialog;


    private File pdfFile;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;


    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        Log.d(TAG,"In statusFragment");
        view =inflater.inflate(R.layout.fragment_status, container, false);

        status_lv=(ListView) view.findViewById(R.id.status_list);
        dep_lv=(ListView) view.findViewById(R.id.dep_list);
        gen_btn=(Button)view.findViewById(R.id.generate);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);



        gen_btn.setEnabled(false);

        user= FirebaseAuth.getInstance().getCurrentUser();
        final Query student = db.child("Students").orderByChild("email").equalTo(user.getEmail());


        //get student Id
        student.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    Log.d(TAG,"Student Exists");
                    for(DataSnapshot stu : dataSnapshot.getChildren()){
                        Log.d(TAG,"Roll no"+stu.getKey());
                        rollno=stu.getKey();
                        break;
                    }
                    getstatus();
                }
                else {
                    Log.d(TAG,"Student doesn't Exists");
                    //Toast.makeText(getActivity(), "User Data not available", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return  view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Status");
    }

    public void getstatus(){

        DatabaseReference request=db.child("Request");

        request.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"Getting Departments and status"+dataSnapshot.getChildrenCount());
                departments.clear();dep=0;
                status.clear();sts=0;
                gen_btn.setEnabled(false);
                for (DataSnapshot dept : dataSnapshot.getChildren()) {
                    if (dept.hasChild(rollno)) {
                        status.add(dept.child(rollno).getValue().toString());
                        departments.add(dept.getKey());
                        dep++;
                        if (dept.child(rollno).getValue().equals("approved"))
                            sts++;
                    }
                }
                if(dep!=0){
                    view.findViewById(R.id.stat_lv).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.tv_nor).setVisibility(View.GONE);
                    setlistview();
                    progressDialog.dismiss();
                }
                else {
                    view.findViewById(R.id.stat_lv).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_nor).setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Database Error");
                progressDialog.dismiss();
            }
        });
    }

    public void setlistview(){
        Log.d(TAG,"Setting dept listview");
        ArrayAdapter<String> listadapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,departments);
        dep_lv.setAdapter(listadapter);

        Log.d(TAG,"Setting status listview");
        ArrayAdapter<String> listadapter2 =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,status);
        status_lv.setAdapter(listadapter2);

        if(sts==dep){
            gen_btn.setEnabled(true);
            gen_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        progressDialog.setMessage("Please Wait creating PDF");
                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        createPdfWrapper();
                        progressDialog.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void createPdfWrapper() throws FileNotFoundException,DocumentException{

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }else {
            createPdf();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity()).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", null)
                .create().show();
    }

    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(),"Nodues.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Dues Cleared"));

        document.close();
        previewPdf();

    }

    private void previewPdf() {

        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(getActivity(),"Download a PDF Viewer to see the generated PDF",Toast.LENGTH_SHORT).show();
        }
    }
}
