package com.eleganz.msafiri.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganz.msafiri.FindRideActivity;
import com.eleganz.msafiri.R;
import com.eleganz.msafiri.adapter.FavRoutesAdapter;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.SampleSearchModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

import static android.app.Activity.RESULT_OK;
import static com.eleganz.msafiri.utils.Constant.BASEURL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
    private MarkerOptions options = new MarkerOptions();
    SessionManager sessionManager;
    String user_id;
    GoogleMap mGooglemap;
    LinearLayout findride;
    ProgressBar pickup_progress,destination_progress;
    LinearLayout top;
    String strEditText1, strEditText2;
Button searchbtn;

    RobotoMediumTextView no_of_seats;
    CardView  card2;
    private static final int PLACE_PICKER_REQUEST2 = 1001;

    public MapFragment() {
        // Required empty public constructor
    }
    EditText from_pickup,from_destination;
    //LinearLayout fab,fab2,fab3;
    //MapView mapView;
    SearchBox pickup,destination;
    ArrayList<String> arrayList;;
    ArrayList<SampleSearchModel> sampleSearchModels;;
    ArrayList<SampleSearchModel> arrayList_des;;


    Calendar myCalendar = Calendar.getInstance();

    ArrayList<String>  toarrayList=new ArrayList<>();
    RobotoMediumTextView roboto,date_selected;
    RecyclerView fav_rec;
    private static final String TAG = "DataLog";
    private static final int PLACE_PICKER_REQUEST = 1000;
CardView cardseat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_map, container, false);
        Animation pop_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_anim);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Inflate the layout for this fragment
       //mapView= (MapView) v.findViewById(R.id.map);
       /*fab=  v.findViewById(R.id.fab);
       fab2= v.findViewById(R.id.fab2);
       fab3= v.findViewById(R.id.fab3);*/
        top= v.findViewById(R.id.top);
        findride= v.findViewById(R.id.findride);
        from_pickup= v.findViewById(R.id.from_pickup);
        from_destination= v.findViewById(R.id.from_destination);
        card2= v.findViewById(R.id.card2);
        no_of_seats=v.findViewById(R.id.no_of_seats);
        searchbtn=v.findViewById(R.id.searchbtn);
        cardseat=v.findViewById(R.id.cardseat);
        date_selected=v.findViewById(R.id.date_selected);
        destination_progress=v.findViewById(R.id.destination_progress);
        pickup_progress=v.findViewById(R.id.destination_progress);
        sessionManager=new SessionManager(getActivity());
fav_rec=v.findViewById(R.id.fav_rec);
        sessionManager.checkLogin();

date_selected.setText(""+getCurrentTimeStamp());

        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        Log.d("myid",user_id);
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                        String smonth,sday;
                        if (month<10)
                        {
                            smonth="0"+(month+1);

                        }
                        else
                        {
                            smonth=""+(month+1);

                        }

                        if (dayOfMonth<10)
                        {
                            sday="0"+(dayOfMonth);

                        }
                        else
                        {
                            sday=""+(dayOfMonth);

                        }

                        date_selected.setText(""+year+"-"+smonth+"-"+sday);



                    }
                },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        cardseat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.vehicle_type_dialog);
                final TextView car=dialog.findViewById(R.id.car);
                final TextView van=dialog.findViewById(R.id.van);
                final TextView bike=dialog.findViewById(R.id.bike);
                final TextView four=dialog.findViewById(R.id.four);

                car.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        no_of_seats.setText("1");
                        dialog.dismiss();
                    }
                });


                van.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        no_of_seats.setText("2");

                        dialog.dismiss();
                    }
                });

                bike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        no_of_seats.setText("3");

                        dialog.dismiss();
                    }
                });

                four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        no_of_seats.setText("4");
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        from_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sampleSearchModels!=null) {


                if (sampleSearchModels.size()>0) {
                    SimpleSearchDialogCompat dialog = new SimpleSearchDialogCompat(getActivity(), "Search...",
                            "Select Pickup Location", null, sampleSearchModels,
                            new SearchResultListener<SampleSearchModel>() {
                                @Override
                                public void onSelected(
                                        BaseSearchDialogCompat dialog,
                                        SampleSearchModel item, int position
                                ) {

                                    from_pickup.setText(item.getTitle());
                                    from_destination.setText("");
                                    destination_progress.setVisibility(View.VISIBLE);
                                    getToList("" + item.getTitle());
                                    dialog.dismiss();
                                }
                            }
                    );
                    dialog.show();
                    dialog.getSearchBox().setTypeface(Typeface.SERIF);

                }
                }
                }
        });

        from_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayList_des!=null) {
                    if (arrayList_des.size() > 0) {
                        SimpleSearchDialogCompat dialog = new SimpleSearchDialogCompat(getActivity(), "Search...",
                                "Select Destination Location", null, arrayList_des,
                                new SearchResultListener<SampleSearchModel>() {
                                    @Override
                                    public void onSelected(
                                            BaseSearchDialogCompat dialog,
                                            SampleSearchModel item, int position
                                    ) {

                                        from_destination.setText(item.getTitle());
                                        dialog.dismiss();
                                    }
                                }
                        );
                        dialog.show();
                        dialog.getSearchBox().setTypeface(Typeface.SERIF);

                    }
                }

                }
        });
        pickup= (SearchBox) v.findViewById(R.id.pickup);
        destination= (SearchBox) v.findViewById(R.id.destination);
        pickup.enableVoiceRecognition(this);
        destination.enableVoiceRecognition(this);


        roboto=  v.findViewById(R.id.roboto);
        top.startAnimation(pop_anim);
        findride.startAnimation(pop_anim);

       /* pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), LocationSearch.class);
                startActivityForResult(i, 1);            }
        });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getActivity(), LocationSearch.class);
                startActivityForResult(i, 2);

            }
        });*/
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Toast.makeText(getActivity(), ""+pickup.getSearchText(), Toast.LENGTH_SHORT).show();


                if ((from_pickup.getText().toString().equalsIgnoreCase("")) || (from_destination.getText().toString().equalsIgnoreCase("")))

                {
                    if ((from_pickup.getText().toString().equalsIgnoreCase("")))
                    {
                        Toast.makeText(getActivity(), "Please select Pickup Location", Toast.LENGTH_SHORT).show();
                    }
                    if ((from_destination.getText().toString().equalsIgnoreCase("")))
                    {
                        Toast.makeText(getActivity(), "Please select Destination Location", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    startActivity(new Intent(getActivity(),FindRideActivity.class).putExtra("from_title", from_pickup.getText().toString())
                            .putExtra("to_title", from_destination.getText().toString())
                            .putExtra("get_date",date_selected.getText().toString())
                            .putExtra("seats",no_of_seats.getText().toString()));
                    //getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    Bungee.swipeLeft(getActivity());
                }
            }
        });
       pickup.setHint("Pickup Location");
       destination.setHint("Destination Location");
   TextView textView=pickup.findViewById(R.id.logo);
        textView.setHint("Pickup Location");
        TextView textView2=destination.findViewById(R.id.logo);
        textView2.setHint("Destination Location");
        pickup.showLoading(true);
        pickup_progress.setVisibility(View.VISIBLE);
        getAddress();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        fav_rec.setLayoutManager(layoutManager);
        fav_rec.setAdapter(new FavRoutesAdapter(arrayList, getActivity()));





       /* //search.hideCircularly();
        pickup.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
               Toast.makeText(getActivity(), "Menu click", Toast.LENGTH_LONG).show();
            }

        });

        destination.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(getActivity(), "Menu click", Toast.LENGTH_LONG).show();
            }

        });
        pickup.*/



        pickup.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen

            }

            @Override
            public void onSearchTermChanged(String term) {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
                //Toast.makeText(getActivity(), searchTerm +" Searched", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResultClick(SearchResult result) {

               // Toast.makeText(getActivity(), ""+result, Toast.LENGTH_SHORT).show();
destination.toggleSearch();
                    if (toarrayList.size()>0) {
                        //destination.toggleSearch();



                        destination.setSearchString("");
                        //destination.setLogoTextInt("");

                       /* toarrayList.clear();

                        destination.clearResults();*/
                        //destination.clearSearchable();


                        getToList("" + result);
                      //  Toast.makeText(getActivity(), "if", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        destination.showLoading(true);
                        //destination.setLogoText("No Destination");
                        //Toast.makeText(getActivity(), "else", Toast.LENGTH_SHORT).show();
                        getToList("" + result);

                    }




                //React to a result being clicked
            }

            @Override
            public void onSearchCleared() {
                Log.d("mmmmmm","clear");
                Toast.makeText(getActivity(), "Menu closed", Toast.LENGTH_LONG).show();

                //Called when the clear button is clicked
            }

        });
        destination.setSearchListener(new SearchBox.SearchListener(){

            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen

            }

            @Override
            public void onSearchTermChanged(String term) {
                //React to the search term changing
                //Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {
               // Toast.makeText(getActivity(), searchTerm +" Searched", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
            }

            @Override
            public void onSearchCleared() {
                Log.d("mmmmmm","clear");
                //Called when the clear button is clicked
            }

        });

       /* Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.flyin1);
        fab.startAnimation(anim);
        fab2.startAnimation(anim);
        fab3.startAnimation(anim);*/

        ///mapView.getMapAsync(this);



        return v;
    }
    private ArrayList<SampleSearchModel> createSampleData(){
        ArrayList<SampleSearchModel> items = new ArrayList<>();
        items.add(new SampleSearchModel("First item"));
        items.add(new SampleSearchModel("Second item"));
        items.add(new SampleSearchModel("Third item"));
        items.add(new SampleSearchModel("The ultimate item"));
        items.add(new SampleSearchModel("Last item"));
        items.add(new SampleSearchModel("Lorem ipsum"));
        items.add(new SampleSearchModel("Dolor sit"));
        items.add(new SampleSearchModel("Some random word"));
        items.add(new SampleSearchModel("guess who's back"));
        return items;
    }
    private void getAddress() {
        //arrayList=new ArrayList<>();
        sampleSearchModels=new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getmyAddress(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    //Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();

                    if (jsonObject!=null)
                    {

                        if (jsonObject.getString("status").equalsIgnoreCase("1"))
                        {

                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++) {

                                JSONObject childObject = jsonArray.getJSONObject(i);


                                SampleSearchModel searchModel=new SampleSearchModel(childObject.getString("address"));
                                sampleSearchModels.add(searchModel);

                            }
                            getPickup();
                        }
                        else
                        {
                            getPickup();
                        }
                    }

                    Log.d(TAG, "" + stringBuilder +"--"+sampleSearchModels);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "" + error.getMessage());
                getPickup();
            }
        });


    }
    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    private void getToList(String result) {
        arrayList_des=new ArrayList<>();
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        apiInterface.getallTolist(result, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {


               /* destination.setLogoText("");

                toarrayList.clear();*//*
                destination.clearSearchable();*/

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        destination_progress.setVisibility(View.GONE);
                        from_destination.setHint("Destination");

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++)
                        {

                            JSONObject childObject=jsonArray.getJSONObject(i);

                            SampleSearchModel searchModel=new SampleSearchModel(childObject.getString("to_title"));

                            arrayList_des.add(searchModel);

                        }

                        Log.d(" getallTolist()",""+arrayList_des);


                    }
                    else {
                        destination_progress.setVisibility(View.GONE);
                        from_destination.setHint("No Data");
                        Toast.makeText(getActivity(), ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                destination_progress.setVisibility(View.GONE);
            }
        });
    }

    private void getPickup() {

        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        apiInterface.getAllTripData("",new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                pickup.showLoading(false);
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                  //  Toast.makeText(getActivity(), ""+stringBuilder, Toast.LENGTH_SHORT).show();
                    for(int j=0;j<sampleSearchModels.size();j++) {


                        Log.d(" getPickup()", "000000" + sampleSearchModels.get(j).getTitle());
                    }
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        pickup_progress.setVisibility(View.GONE);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++)
                        {

                            JSONObject childObject=jsonArray.getJSONObject(i);

                           if (contains(sampleSearchModels,childObject.getString("from_title")))

                           {

                           }
                           else {


                               SampleSearchModel searchModel = new SampleSearchModel(childObject.getString("from_title"));
                               sampleSearchModels.add(searchModel);
                           }


                        /*    SampleSearchModel searchModel= new SampleSearchModel(childObject.getString("from_title"));
                            sampleSearchModels.add(searchModel);

*/


                        }
                       /*for(int x = 0; x < arrayList.size(); x++){
                            SearchResult option = new SearchResult(arrayList.get(x), getResources().getDrawable(R.drawable.ic_history));
                            pickup.addSearchable(option);
                        }*/
                        Log.d(" getPickup()",""+stringBuilder);
                        Log.d(" getPickup()",""+arrayList);
                    }
                    else
                    {
                        pickup_progress.setVisibility(View.GONE);
                        /*if (sampleSearchModels.size()>0){
                            for(int x = 0; x < sampleSearchModels.size(); x++){
                                SearchResult option = new SearchResult(arrayList.get(x), getResources().getDrawable(R.drawable.ic_history));
                                pickup.addSearchable(option);
                            }
                            Log.d(" getPickup()else",""+arrayList);
                        }*/


                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(" getPickup()",""+error.getMessage());
               // pickup.setLogoText("No Destination");
                pickup_progress.setVisibility(View.GONE);

                // destination.clearSearchable();
              //  pickup.setHint("No Destination");

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"uioj");


        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            pickup.populateEditText(matches.get(0));


        }






    }
    boolean contains(List<SampleSearchModel> modelList,String title){

        for (SampleSearchModel searchModel:modelList)
        {
            if (searchModel.getTitle().equalsIgnoreCase(title))
            {
                return true;
            }
        }

        return false;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if(mapView != null)
//        {
//            mapView.onCreate(null);
//            mapView.onResume();
//            mapView.getMapAsync(this);
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//
//            MapsInitializer.initialize(getActivity());
//
//
//        boolean success = googleMap.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                        getActivity(), R.raw.style_json));
//
//        if (!success) {
//            Log.e(TAG, "Style parsing failed.");
//        }
//        Log.e("ddddddd", "Style parsing failed.");
//
//            LatLng india=new LatLng(20.5937,78.9629);
//            googleMap.addMarker(new MarkerOptions().position(india).title("INDIA").snippet("MY INDIA").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
//
//
//
//
//        /*
//        CameraPosition lib=CameraPosition.builder().target(new LatLng(20.5937,78.9629)).zoom(16).bearing(0).tilt(45).build();
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lib));*/
//
//
//
//}
}
