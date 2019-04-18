package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPassenger extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id,trip_id,photoPath,joinid,seats;
    CurrentTripSession currentTripSession;
    int Id=1;
    ArrayList<String> arrayList=new ArrayList();
    ArrayList<EditText> etList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_passenger);

        etList  = new ArrayList<EditText>();
        TableLayout tbl=(TableLayout)findViewById(R.id.table);


        Log.d("etListetList",""+seats);
        sessionManager=new SessionManager(AddPassenger.this);
        currentTripSession=new CurrentTripSession(AddPassenger.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
        seats=getIntent().getStringExtra("seats");
        photoPath=getIntent().getStringExtra("photoPath");
        joinid=getIntent().getStringExtra("joinid");
        sessionManager.checkLogin();

        Log.d("etListetList",""+seats);
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        if (seats!=null && !(seats.isEmpty()))
        {
            Log.d("etListetList-->",""+seats);

            int j= Integer.parseInt(seats);
            for(int i=1;i<=j;i++){
                Log.d("etListetList--->",""+i);

                TableRow tr = new TableRow(AddPassenger.this);
                TableLayout.LayoutParams tableRowParams=
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
                EditText et=new EditText(AddPassenger.this);

              //  ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                et.setHint("Enter Passenger Name");
              //  et.setLayoutParams(layoutParams);
                et.setId(Id+i);//string+i
                //add et to table row
                tr.addView(et);
                //add table row to table
                tbl.addView(tr, tableRowParams);
                //add edittext to list
                etList.add(et);
            }

        }


        findViewById(R.id.savdata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EditText et : etList) {

                    if (et.getText().toString().equalsIgnoreCase(""))
                    {
                        Toast.makeText(AddPassenger.this, "Please Enter data", Toast.LENGTH_SHORT).show();
                    }
else
                    {
                        arrayList.add(et.getText().toString());
                    }


                }



                startActivity(new Intent(AddPassenger.this,PaymentActivity.class)
                        .putExtra("photoPath",photoPath)
                        .putExtra("joinid",joinid)
                        .putExtra("user_id",user_id)
                        .putExtra("trip_id",trip_id)
                        .putStringArrayListExtra("mypassenger",arrayList)



                );
            }
        });
    }
}
