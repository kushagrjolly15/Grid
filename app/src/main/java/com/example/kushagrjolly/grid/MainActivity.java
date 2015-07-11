package com.example.kushagrjolly.grid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


public class MainActivity extends Activity {
    ProgressDialog prgDialog;
    private static String SOAP_ACTION1 = "http://pack1/gridtable";
    private static String NAMESPACE = "http://pack1/";
    private static String METHOD_NAME1 = "gridtable";
    private static String URL = "http://172.16.6.55:8080/hello/grid?wsdl";
    private ArrayList<String> resp;

    String[] numbers = new String[] {

            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    private Context context=this;
    String x;
    int flag=1;
    protected GridView gv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        resp= new ArrayList<>();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        Log.d("asynctask","strated");
        loadgrid();

    }

    public void loadgrid() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {
                Log.d("asynctask","pre execute");
            };

            @Override
            protected String doInBackground(Void... params) {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME1);

                //Declare the version of the SOAP request
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                Log.d("asynctask","doinbackground");
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                //this is the actual part that will call the webservice
                try {
                    androidHttpTransport.call(SOAP_ACTION1, envelope);
                } catch (Exception e)  {
                    e.printStackTrace();
                }
                try {


                    // Get the SoapResult from the envelope body.
                    SoapObject result = (SoapObject)envelope.bodyIn;

                    if(result != null)
                    {
                        Log.d("count", String.valueOf(result.getPropertyCount()));

                        //Get the first property and change the label text
                        for(int i=0;i<result.getPropertyCount();i++) {

                            resp.add(result.getProperty(i).toString());
                            Log.d("rsult", resp.get(i));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {

                prgDialog.hide();
                loadgridview();
                //launchActivity(true);
            }
        }.execute(null, null, null);
    }

    private void loadgridview() {
        final GridView gridView = (GridView) findViewById(R.id.gridview1);

        // Create adapter to set value for grid view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, resp);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                gv = (GridView) parent;
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);



                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        x = String.valueOf(userInput.getText());
                                        resp.set(position,x);
                                        gv.invalidateViews();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        flag = 2;

                                    }

                                });

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                /*if (flag == 1) {
                    // Log.d("x", x);
                    ((TextView) v).setText(x);
                }*/
            }
        });

    }

}
