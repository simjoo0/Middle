package m.i.d.mid;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.internal.AppActionInfo;
import com.kakao.util.KakaoParameterException;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionsSearchListener {

    String filename;

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private List<Polyline> polylinePaths = new ArrayList<>();

    private ViewGroup infoWindow;   // 목적에 맞는 위치 infoWindow Layout
    private ViewGroup infoWindow2;  // 입력받은 위치 infoWindow Layout

    private TextView finalAddressTv;
    private TextView finalNameTv;
    private Button nearInfoBtn;
    private Button saveBtn;

    private LinearLayout directionsSearchResultLinear;
    private TextView inputNameTv;
    private TextView kmTv;
    private TextView durationTv;
    private Button navigateBtn;

    private OnInfoWindowElemTouchListener infoButtonListener1;
    private OnInfoWindowElemTouchListener infoButtonListener2;
    private OnInfoWindowElemTouchListener infoButtonListener3;

    LinearLayout inputMarkerInfoWindowLinear;
    LinearLayout searchLinear;
    LinearLayout SearchAllLinear;
    Button locationPlusBtn;
    Button shareBtn;
    EditText addressEt;

    LinearLayout purposeListLinear;
    ListView purposeListView;

    LinearLayout howToGoLinear;
    ScrollView howToGoScroll;

    ArrayList<ListViewItem> data;   //리스트뷰 아이템 담는 arrayList
    ArrayList<Double> resultList=new ArrayList<Double>(); // 중간좌표

    ArrayList<PurposeNearListItem> purposeNearListItems = new ArrayList<PurposeNearListItem>();
    ArrayList<Double> tempX=new ArrayList<Double>();
    ArrayList<Double> tempY=new ArrayList<Double>();

    ArrayList<Circle> circleArrayList=new ArrayList<Circle>();
    ArrayList<Marker> busMarkerArrayList=new ArrayList<Marker>();

    PurposeNearListAdapter purposeNearListAdapter;

    String searchResultStr="";  //xml 결과를 '/'로 연결한 문자열 '이름/주소/위도/경도' 로 구성된다.
    String markingResultName="";    //리스트뷰 아이템 선택 시 가져오는 이름을 저장할 문자열
    String markingResultLat="";     //리스트뷰 아이템 선택 시 가져오는 위도를 저장할 문자열
    String markingResultLng="";     //리스트뷰 아이템 선택 시 가져오는 경도를 저장할 문자열
    String dong="";

    String purpose_destination_name="";
    String purpose_destination_address="";

    int markerCount=0;
    String meetingPurposeStr="";    //목적
    int peopleCount=0;  //인원수

    String middleNearStr="";
    Marker marker;

    LatLng final_position;  // 중간거리 계산된 위치
    LatLng near_position;   // 중간거리 계산된 위치에서 목적에 맞는 가장가까운 위치

    Circle clicked_list_circle;

    //기록에서 클릭하고 들어왔을 때 지도에 찍어줌
    int dataBaseFlag=0; //클릭하고 들어왔을 때 1로 바뀜
    int dataPeople=0;   //사람수
    double [] x_location;   //사용자의 x좌표의 배열
    double [] y_location;   //사용자의 y좌표의 배열
    String data_meetingPurposeStr="";   //목적
    String data_dong=""; //지역 이름

    //카카오톡 공유하고 나서 다시 앱으로 들어왔을 때 필요한 변수
    Double final_location_x=0.0;    //수학적 중심거리와 가장 가까운 목적에 기반한 장소의 x, y 좌표
    Double final_location_y=0.0;

    Double x_middle_location=0.0;  //수학적 중심거리의 x, y 좌표
    Double y_middle_location=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.purpose_marker_info_window_layout, null);
        this.finalNameTv=(TextView)infoWindow.findViewById(R.id.finalNameTv);
        this.finalAddressTv = (TextView)infoWindow.findViewById(R.id.finalAddressTv);
        this.nearInfoBtn = (Button)infoWindow.findViewById(R.id.nearInfoBtn);
        this.saveBtn = (Button)infoWindow.findViewById(R.id.saveBtn);

        this.infoWindow2 = (ViewGroup)getLayoutInflater().inflate(R.layout.input_marker_info_window_layout,null);
        this.inputNameTv=(TextView)infoWindow2.findViewById(R.id.inputNameTv);
        this.kmTv=(TextView)infoWindow2.findViewById(R.id.kmTv);
        this.durationTv=(TextView)infoWindow2.findViewById(R.id.durationTv);
        this.navigateBtn=(Button)infoWindow2.findViewById(R.id.navigateBtn);
        this.directionsSearchResultLinear=(LinearLayout) infoWindow2.findViewById(R.id.directionsSearchResultLinear);
        inputMarkerInfoWindowLinear=(LinearLayout) infoWindow2.findViewById(R.id.inputMarkerInfoWindowLinear);

        howToGoLinear=(LinearLayout) findViewById(R.id.howToGoLinear);
        howToGoScroll=(ScrollView) findViewById(R.id.howToGoScroll);

        searchLinear = (LinearLayout) findViewById(R.id.SearchLinear);
        SearchAllLinear=(LinearLayout) findViewById(R.id.SearchAllLinear);
        locationPlusBtn=(Button) findViewById(R.id.locationPlusBtn);
        locationPlusBtn.bringToFront();

        shareBtn=(Button)findViewById(R.id.shareBtn);
        addressEt = (EditText) findViewById(R.id.addressEt);

        Intent intent=new Intent(this.getIntent());
        Uri uri=intent.getData();
        if(uri==null){
//            Log.i("여기로 들어옴","디비");
            dataBaseFlag=intent.getIntExtra("dataBaseFlag",0);
            dataPeople=intent.getIntExtra("dataPeople",0);
        }else{  //카톡 공유했을 때 이쪽으로 들어옴
//            Log.i("여기로 들어옴","카톡 공유함");
            dataBaseFlag=Integer.parseInt(uri.getQueryParameter("dataBaseFlag"));// dataBaseFlag=2
        }


        if(dataBaseFlag==0){

//        Toast.makeText(this, meetingPurposeStr+""+peopleCount, Toast.LENGTH_SHORT).show();    //이전 액티비티에서 목적과 인원수 가져오는지 확인
            meetingPurposeStr=intent.getStringExtra("meetingPurPose").toString();
            peopleCount=intent.getIntExtra("peopleCount",0);

        }else{
            shareBtn.setVisibility(View.VISIBLE);

            if(dataBaseFlag==2){   //카톡 공유로 받았을 때
                meetingPurposeStr=uri.getQueryParameter("meetingPurposeStr").toString();
                data_dong=uri.getQueryParameter("data_dong").toString();
                Log.i("동이름",data_dong);
                purpose_destination_name=uri.getQueryParameter("purpose_destination_name");
                Log.i("purpose",purpose_destination_name);
                final_location_x=Double.parseDouble(uri.getQueryParameter("final_location_x"));
                final_location_y=Double.parseDouble(uri.getQueryParameter("final_location_y"));;
                x_middle_location=Double.parseDouble(uri.getQueryParameter("x_middle_location"));
                y_middle_location=Double.parseDouble(uri.getQueryParameter("y_middle_location"));
                dataPeople=Integer.parseInt(uri.getQueryParameter("dataPeople"));
                String share_x_arr=uri.getQueryParameter("x_location");
                String share_y_arr=uri.getQueryParameter("y_location");

                StringTokenizer stk1=new StringTokenizer(share_x_arr,",");
                StringTokenizer stk2=new StringTokenizer(share_y_arr,",");

                x_location=new double[dataPeople];
                y_location=new double[dataPeople];

                for(int i=0;i<dataPeople;i++){
                    x_location[i]=Double.parseDouble(stk1.nextToken());
                    y_location[i]=Double.parseDouble(stk2.nextToken());
                }

            }else if(dataBaseFlag==1){  //기록에서 불러왔을 때
                meetingPurposeStr=intent.getStringExtra("data_meetingPurposeStr");
                data_dong=intent.getStringExtra("data_dong");
                x_location=intent.getDoubleArrayExtra("x_location");
                y_location=intent.getDoubleArrayExtra("y_location");

                purpose_destination_name=intent.getStringExtra("final_location_name");
                final_location_x=intent.getDoubleExtra("final_location_x",0);
                final_location_y=intent.getDoubleExtra("final_location_y",0);

                x_middle_location=intent.getDoubleExtra("x_middle_location",0);
                y_middle_location=intent.getDoubleExtra("y_middle_location",0);
            }

            near_position=new LatLng(final_location_x,final_location_y);
            final_position=new LatLng(x_middle_location,y_middle_location);

            dong=data_dong;

            locationPlusBtn=(Button) findViewById(R.id.locationPlusBtn);
            locationPlusBtn.setVisibility(View.GONE);
            markerCount=dataPeople;
            peopleCount=dataPeople;

            for(int i=0;i<dataPeople;i++){
//                Log.i("x좌표,y좌표:",x_location[i]+","+y_location[i]);
                mMap =((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
                mMap.addMarker(new MarkerOptions().position(new LatLng(x_location[i],y_location[i]))).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.people_marker));
            }
            mMap.addMarker(new MarkerOptions().position(near_position)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_purpose_marker));

            PurposeNearSearch purposeNearSearch=new PurposeNearSearch(dong+" "+meetingPurposeStr);
//                                    Toast.makeText(getApplicationContext(),purposeNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
//                                    ListView purposeListView=(ListView) findViewById(R.id.purposeListView);

            StringTokenizer stk1=new StringTokenizer(purposeNearSearch.getMiddleAddress().toString(),"\n");

            while(stk1.hasMoreTokens()){
                StringTokenizer stk2=new StringTokenizer(stk1.nextToken(),"/");
                while(stk2.hasMoreTokens()){
                    purposeNearListItems.add(new PurposeNearListItem(stk2.nextToken(),stk2.nextToken(),Double.parseDouble(stk2.nextToken()),Double.parseDouble(stk2.nextToken())));
                }
            }

            if(purposeNearListItems.size()!=0) {
                ArrayList<Integer> distIndex = GetNearPosition.calculMinDist(purposeNearListItems, x_middle_location, y_middle_location);

                purpose_destination_name = purposeNearListItems.get(distIndex.get(0)).getName().toString();
                purpose_destination_address = purposeNearListItems.get(distIndex.get(0)).getAddress().toString();
            }


        }
    }

    public void onShareClick(View view){
        final KakaoLink kakaoLink;
        try {
            kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            try {
//                captureScreen();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            int width=300;
            int height=300;

            Log.i("동아",dong);

            String path="http://postfiles1.naver.net/MjAxNjEyMDJfNTAg/MDAxNDgwNjgzNTU1NDc5.C-Hkd_KjBgRyisPVLbkOUBr-dRsMHi0gOszvXEioXy8g.yRXI9SBHlaqoqS6iaf1SYGlJBOS_StoYkFt7j23oKnUg.JPEG.simjoo0/logo.png?type=w3";
            kakaoTalkLinkMessageBuilder.addText("지금 Middle로 약속 장소가 공유되었습니다!");
            kakaoTalkLinkMessageBuilder.addImage(path, width, height);
            if(dataBaseFlag==0){    // 직접 위치 입력했을때

                String share_x_arr="", share_y_arr="";
                for(int i=0;i<tempX.size();i++){
                    if(i==tempX.size()-1){
                        share_x_arr+=tempX.get(i);
                        share_y_arr+=tempY.get(i);
                    }else{
                        share_x_arr+=tempX.get(i)+",";
                        share_y_arr+=tempY.get(i)+",";
                    }
                }

                MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));
                middleNearStr=middleNearSearch.getMiddleAddress().toString();
                String[] tempAddress=middleNearStr.split(" ");

                for(int i=0;i<tempAddress.length;i++){
                    if(tempAddress[i].contains("동")||tempAddress[i].contains("리")||tempAddress[i].contains("읍")||tempAddress[i].contains("면")){
                        dong=tempAddress[i];
                    }
                }


                kakaoTalkLinkMessageBuilder.addAppButton("앱으로 이동", new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()
                        .setExecuteParam("meetingPurposeStr="+meetingPurposeStr+"&data_dong="+dong+"&purpose_destination_name="+purpose_destination_name+
                                "&final_location_x="+near_position.latitude+"&final_location_y="+near_position.longitude+
                        "&x_middle_location="+final_position.latitude+"&y_middle_location="+final_position.longitude+"&dataPeople="+peopleCount+
                        "&dataBaseFlag=2&x_location="+share_x_arr+"&y_location="+share_y_arr).build()).build());

            }else{  //데이터베이스에서 값 가져왔을 때

                String share_x_arr="", share_y_arr="";
                for(int i=0;i<x_location.length;i++){
                    if(i==x_location.length-1){
                        share_x_arr+=x_location[i];
                        share_y_arr+=y_location[i];
                    }else{
                        share_x_arr+=x_location[i]+",";
                        share_y_arr+=y_location[i]+",";
                    }
                }

                kakaoTalkLinkMessageBuilder.addAppButton("앱으로 이동", new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder()
                        .setExecuteParam("meetingPurposeStr="+meetingPurposeStr+"&data_dong="+data_dong+"&purpose_destination_name="+purpose_destination_name+
                                "&final_location_x="+final_location_x+"&final_location_y="+final_location_y+
                                "&x_middle_location="+x_middle_location+"&y_middle_location="+y_middle_location+"&dataPeople="+dataPeople+
                                "&dataBaseFlag=2&x_location="+share_x_arr+"&y_location="+share_y_arr).build()).build());
            }

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder,this);

            shareBtn.setVisibility(View.GONE);

        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }





    }


//    public void captureScreen(){
//        GoogleMap.SnapshotReadyCallback callback=new GoogleMap.SnapshotReadyCallback() {
//            Bitmap bitmap;
//            @Override
//            public void onSnapshotReady(Bitmap snapshot) {
//                bitmap=snapshot;
//                try{
//                    filename=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Camera/MiddleScreen"+System.currentTimeMillis()+".png";
//                    FileOutputStream out =new FileOutputStream(filename);
//                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
//                    out.flush();
//                    out.close();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        mMap.snapshot(callback);
//    }



    @Override
    protected void attachBaseContext(Context newBase) { //Application 클래스 (폰트 적용)
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void onLocationPlus(View view){  //위치 추가 버튼 눌렀을 때 발생하는 method
        locationPlusBtn.setVisibility(View.GONE);

        addressEt.setText(null);
        SearchAllLinear.setVisibility(View.VISIBLE);
        SearchAllLinear.bringToFront();

    }

    public void onSearch(View view){    // 주소로 위치 입력받아서 마커찍는 method

        final String location = addressEt.getText().toString();
        String uriLocation="";
        StringTokenizer stringTokenizer=new StringTokenizer(location," ");
        while(stringTokenizer.hasMoreTokens()){
            if(stringTokenizer.countTokens()>1) {
                uriLocation += stringTokenizer.nextToken()+"+";
            }else{
                uriLocation += stringTokenizer.nextToken();
            }
        }
        final String uriLocationStr=uriLocation;

        final Thread thread=new Thread() {  //Google Place Web Server에 Uri 연결하기 위해 Thread 실행 (UI Thread에서 실행 불가하기 때문)
            public void run() {

                String line = getPlaceInfo(uriLocationStr);

                Bundle bun = new Bundle();
                bun.putString("Place_Info", line);
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ListView listView=(ListView) findViewById(R.id.searchResultLv);
                        listView.setVisibility(View.VISIBLE);
                        data=new ArrayList<ListViewItem>();

                        StringTokenizer stringTokenizer1=new StringTokenizer(searchResultStr,"\n");
                        while(stringTokenizer1.hasMoreTokens()){
                            StringTokenizer stringTokenizer2=new StringTokenizer(stringTokenizer1.nextToken(),"/");
                            data.add(new ListViewItem(stringTokenizer2.nextToken(),stringTokenizer2.nextToken(),stringTokenizer2.nextToken(),stringTokenizer2.nextToken()));
                        }

                        final ListViewAdapter adapter=new ListViewAdapter(MapsActivity.this,R.layout.listview_item,data);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {     // 검색된 결과 리스트에서 리스트아이템 선택 시 해당 리스트 결과로 마커 찍음
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                markerCount++;

                                InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(addressEt.getWindowToken(),0);



                                String str=(String) adapter.getItem(position).toString();
                                StringTokenizer markingStnz=new StringTokenizer(str,",");

                                while(markingStnz.hasMoreTokens()){
                                    markingResultName=markingStnz.nextToken();
                                    markingResultLat=markingStnz.nextToken();
                                    markingResultLng=markingStnz.nextToken();
                                }

                                LatLng latLng = new LatLng(Double.parseDouble(markingResultLat),Double.parseDouble(markingResultLng));  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                mMap.addMarker(new MarkerOptions().position(latLng).title("친구위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.people_marker));
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                                tempX.add(Double.parseDouble(markingResultLat));
                                tempY.add(Double.parseDouble(markingResultLng));

                                data.clear();
                                adapter.notifyDataSetChanged();

                                SearchAllLinear.setVisibility(View.GONE);

                                if(peopleCount==markerCount){
                                    locationPlusBtn.setVisibility(View.GONE);

                                    double[] x=new double[peopleCount];
                                    double[] y=new double[peopleCount];

                                    for(int i=0;i<peopleCount;i++){
                                        x[i]=tempX.get(i);
                                        y[i]=tempY.get(i);
                                    }

                                    GetPosition getPosition=new GetPosition(peopleCount);
                                    resultList=getPosition.mainFunc(x,y);

//                                    Toast.makeText(getApplicationContext(),"결과 값 넘어옴 "+resultList.get(0)+","+resultList.get(1),Toast.LENGTH_LONG).show();

                                    final_position = new LatLng(resultList.get(0),resultList.get(1));  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                    mMap.addMarker(new MarkerOptions().position(final_position).title("중간위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_marker));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));


                                    MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));

//                                    Toast.makeText(getApplicationContext(),middleNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
                                    middleNearStr=middleNearSearch.getMiddleAddress().toString();
//                                    Toast.makeText(getApplicationContext(),middleNearStr,Toast.LENGTH_SHORT).show();
                                    dong="";

                                    String[] tempAddress=middleNearStr.split(" ");

                                    for(int i=0;i<tempAddress.length;i++){
                                        if(tempAddress[i].contains("동")||tempAddress[i].contains("리")||tempAddress[i].contains("읍")||tempAddress[i].contains("면")){
                                            dong=tempAddress[i];
                                        }
                                    }
//                                    Toast.makeText(getApplicationContext(),dong,Toast.LENGTH_SHORT).show();
                                    PurposeNearSearch purposeNearSearch=new PurposeNearSearch(dong+" "+meetingPurposeStr);
//                                    Toast.makeText(getApplicationContext(),purposeNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
//                                    ListView purposeListView=(ListView) findViewById(R.id.purposeListView);

                                    StringTokenizer stk1=new StringTokenizer(purposeNearSearch.getMiddleAddress().toString(),"\n");

                                    while(stk1.hasMoreTokens()){
                                        StringTokenizer stk2=new StringTokenizer(stk1.nextToken(),"/");
                                        while(stk2.hasMoreTokens()){
                                            purposeNearListItems.add(new PurposeNearListItem(stk2.nextToken(),stk2.nextToken(),Double.parseDouble(stk2.nextToken()),Double.parseDouble(stk2.nextToken())));
                                        }
                                    }

                                    if(purposeNearListItems.size()!=0){
                                        ArrayList<Integer> distIndex=GetNearPosition.calculMinDist(purposeNearListItems,resultList.get(0),resultList.get(1));
                                        Toast.makeText(getApplicationContext(), purposeNearListItems.get(distIndex.get(0)).getName() + "이(가) 가장 가깝습니다.", Toast.LENGTH_SHORT).show();

                                        purpose_destination_name=purposeNearListItems.get(distIndex.get(0)).getName().toString();
                                        purpose_destination_address=purposeNearListItems.get(distIndex.get(0)).getAddress().toString();

                                        near_position = new LatLng(purposeNearListItems.get(distIndex.get(0)).getLat(),purposeNearListItems.get(distIndex.get(0)).getLng());  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                        mMap.addMarker(new MarkerOptions().position(near_position).title(purpose_destination_name)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_purpose_marker));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));

                                        double difLat = final_position.latitude - near_position.latitude;
                                        double difLng = final_position.longitude - near_position.longitude;

                                        double zoom = mMap.getCameraPosition().zoom;

                                        double divLat = difLat / (zoom * 2);
                                        double divLng = difLng / (zoom * 2);

                                        LatLng tmpLatOri = near_position;

                                        for(int i = 0; i < (zoom * 2); i++){
                                            LatLng loopLatLng = tmpLatOri;

                                            if(i > 0){
                                                loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.5f), tmpLatOri.longitude + (divLng * 0.5f));
                                            }

                                            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                                    .add(loopLatLng)
                                                    .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                                                    .color(Color.parseColor("#8bd9f2"))
                                                    .width(5f));

                                            tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
                                        }

                                        shareBtn.setVisibility(View.VISIBLE);

                                    }else{
                                        Toast.makeText(getApplicationContext(), "중간 위치에서 가장 가까운 곳이 없습니다.", Toast.LENGTH_SHORT).show();
                                    }



                                }else{
                                    locationPlusBtn.setVisibility(View.VISIBLE);
                                }

                            }

                        });
                    }
                });


            }
        };
        thread.start();



    }

    private String getPlaceInfo(String location){   //Google Place Web Server에 Uri를 통해 접근한 후 결과값을 xml 파일 형식으로 가져오는 method

        StringBuffer buffer=new StringBuffer();
        String encodingLocation = URLEncoder.encode(location); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..
        String queryUrl="https://maps.googleapis.com/maps/api/place/textsearch/xml?query="+location+"&language=ko&key=AIzaSyA6nMZZt07EhgeQgoWYCq0tc0NrOGfdtJM";

        try {
            URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") );  //inputstream 으로부터 xml 입력받기

            String tag;
            xpp.next();

            int eventType= xpp.getEventType();
            while( eventType != XmlPullParser.END_DOCUMENT ){

                switch( eventType ){
                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기
                        if(tag.equals("result")){}
                        else if(tag.equals("name")){
                            xpp.next();
                            buffer.append(xpp.getText()); //name 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");          //구분자
                        }else if(tag.equals("formatted_address")){
                            xpp.next();
                            buffer.append(xpp.getText()); //formatted_address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");          //구분자
                        }else if(tag.equals("lat")){
                            xpp.next();
                            buffer.append(xpp.getText()); //lat 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");          //구분자
                        }else if(tag.equals("lng")){
                            xpp.next();
                            buffer.append(xpp.getText()); //lng 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");          //구분자
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기
                        if(tag.equals("result")){
                            buffer.append("\n"); // 첫번째 검색결과종료
                        }
                        break;
                }
                eventType= xpp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer.toString();
    }

    Handler handler = new Handler() {   // Thread Handler
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            searchResultStr = bun.getString("Place_Info");
//            Log.i("결과",searchResultStr);
        }
    };


    private void setUpMapIfNeeded() {
        if(mMap == null){
            mMap =((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(mMap != null){
                onMapReady(mMap);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ajouUniv = new LatLng(37.2851815, 127.045332);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ajouUniv,14));
        mMap.getUiSettings().setMapToolbarEnabled(false);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);  // My Location 버튼 위치 안옮겨져서 비활성화 시킴. 버튼 새로만들어서 My Location 기능 구현할 것. btn 눌렀을 때 GPS 안켜져있으면 키라는 팝업창 만들 것.
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                if(peopleCount>markerCount){

                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MapsActivity.this);
                    alert_confirm.setMessage("위치를 추가할까요?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'YES'
                                    LatLng longTouchLatLng = new LatLng(latLng.latitude,latLng.longitude);  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                    mMap.addMarker(new MarkerOptions().position(longTouchLatLng).title("친구위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.people_marker));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(longTouchLatLng));

                                    tempX.add(latLng.latitude);
                                    tempY.add(latLng.longitude);

                                    markerCount++;

                                    if(peopleCount==markerCount){
                                        locationPlusBtn.setVisibility(View.GONE);

                                        double[] x=new double[peopleCount];
                                        double[] y=new double[peopleCount];

                                        for(int i=0;i<peopleCount;i++){
                                            x[i]=tempX.get(i);
                                            y[i]=tempY.get(i);
                                        }

                                        GetPosition getPosition=new GetPosition(peopleCount);
                                        resultList=getPosition.mainFunc(x,y);

//                                    Toast.makeText(getApplicationContext(),"결과 값 넘어옴 "+resultList.get(0)+","+resultList.get(1),Toast.LENGTH_LONG).show();

                                        final_position = new LatLng(resultList.get(0),resultList.get(1));  //선택된 리스트 값으로 마커 찍고 카메라 이동
//                                    Log.i("resultList",resultList.get(0)+","+resultList.get(1));
                                        MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));

//                                    Toast.makeText(getApplicationContext(),middleNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
                                        middleNearStr=middleNearSearch.getMiddleAddress().toString();
                                        Log.i("middleNearStr",middleNearStr);
//                                    Toast.makeText(getApplicationContext(),middleNearStr,Toast.LENGTH_SHORT).show();

                                        mMap.addMarker(new MarkerOptions().position(final_position).title("중간위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_marker));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));

                                        String dong="";

                                        String[] tempAddress=middleNearStr.split(" ");

                                        for(int i=0;i<tempAddress.length;i++){
                                            if(tempAddress[i].contains("동")||tempAddress[i].contains("리")||tempAddress[i].contains("읍")||tempAddress[i].contains("면")){
                                                dong=tempAddress[i];
                                            }
                                        }
                                        Log.i("dong",dong);

//                                    Toast.makeText(getApplicationContext(),dong,Toast.LENGTH_SHORT).show();
                                        PurposeNearSearch purposeNearSearch=new PurposeNearSearch(dong+" "+meetingPurposeStr);
//                                    Toast.makeText(getApplicationContext(),purposeNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
//                                    ListView purposeListView=(ListView) findViewById(R.id.purposeListView);
                                        Log.i("purposeNearSearch",purposeNearSearch.getMiddleAddress().toString());

                                        StringTokenizer stk1=new StringTokenizer(purposeNearSearch.getMiddleAddress().toString(),"\n");

                                        while(stk1.hasMoreTokens()){
                                            StringTokenizer stk2=new StringTokenizer(stk1.nextToken(),"/");
                                            while(stk2.hasMoreTokens()){
                                                purposeNearListItems.add(new PurposeNearListItem(stk2.nextToken(),stk2.nextToken(),Double.parseDouble(stk2.nextToken()),Double.parseDouble(stk2.nextToken())));
                                            }
                                        }

                                        if(purposeNearListItems.size()!=0){
                                            ArrayList<Integer> distIndex=GetNearPosition.calculMinDist(purposeNearListItems,resultList.get(0),resultList.get(1));
                                            Toast.makeText(getApplicationContext(), purposeNearListItems.get(distIndex.get(0)).getName() + "이(가) 가장 가깝습니다.", Toast.LENGTH_SHORT).show();

                                            purpose_destination_name=purposeNearListItems.get(distIndex.get(0)).getName().toString();
                                            purpose_destination_address=purposeNearListItems.get(distIndex.get(0)).getAddress().toString();

                                            near_position = new LatLng(purposeNearListItems.get(distIndex.get(0)).getLat(),purposeNearListItems.get(distIndex.get(0)).getLng());  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                            mMap.addMarker(new MarkerOptions().position(near_position).title(purpose_destination_name)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_purpose_marker));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));

                                            double difLat = final_position.latitude - near_position.latitude;
                                            double difLng = final_position.longitude - near_position.longitude;

                                            double zoom = mMap.getCameraPosition().zoom;

                                            double divLat = difLat / (zoom * 2);
                                            double divLng = difLng / (zoom * 2);

                                            LatLng tmpLatOri = near_position;

                                            for(int i = 0; i < (zoom * 2); i++){
                                                LatLng loopLatLng = tmpLatOri;

                                                if(i > 0){
                                                    loopLatLng = new LatLng(tmpLatOri.latitude + (divLat * 0.5f), tmpLatOri.longitude + (divLng * 0.5f));
                                                }

                                                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                                        .add(loopLatLng)
                                                        .add(new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng))
                                                        .color(Color.parseColor("#8bd9f2"))
                                                        .width(5f));

                                                tmpLatOri = new LatLng(tmpLatOri.latitude + divLat, tmpLatOri.longitude + divLng);
                                            }

                                            shareBtn.setVisibility(View.VISIBLE);

                                        }else{
                                            Toast.makeText(getApplicationContext(), "중간 위치에서 가장 가까운 곳이 없습니다.", Toast.LENGTH_SHORT).show();
                                        }




                                    }
                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'No'
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker clickedMarker) {
                marker=clickedMarker;
                if(peopleCount>markerCount){
                    AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MapsActivity.this);
                    alert_confirm.setMessage("위치를 삭제할까요?").setCancelable(false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'YES'
                                    markerCount--;
                                    marker.remove();
                                    for(int i=0;i<tempX.size();i++){
                                        if((tempX.get(i)==marker.getPosition().latitude)&&(tempY.get(i)==marker.getPosition().longitude)){
                                            tempX.remove(i);
                                            tempY.remove(i);
                                        }
                                    }

                                }
                            }).setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 'No'
                                    return;
                                }
                            });
                    AlertDialog alert = alert_confirm.create();
                    alert.show();
                }else{

                    if((marker.getPosition().latitude!=near_position.latitude && marker.getPosition().longitude!=near_position.longitude) && (marker.getPosition().latitude!=final_position.latitude && marker.getPosition().longitude!=final_position.longitude)){
                        saveBtn.setVisibility(View.GONE);
                        directionsSearchResultLinear.setVisibility(View.GONE);
                        howToGoLinear.removeAllViews();
                        howToGoLinear.setVisibility(View.GONE);
                        howToGoScroll.setVisibility(View.GONE);

                        for(Polyline polyline : polylinePaths){
                            polyline.remove();
                        }
                        polylinePaths.clear();

                        for(Marker marker : busMarkerArrayList){
                            marker.remove();
                        }
                        busMarkerArrayList.clear();
                    }



                    mMap.animateCamera(CameraUpdateFactory.newLatLng(clickedMarker.getPosition()));
                    if(marker.getPosition().latitude==near_position.latitude && marker.getPosition().longitude==near_position.longitude){
//                        marker.setSnippet(purpose_destination_address);

                        // Setting custom OnTouchListener which deals with the pressed state
                        // so it shows up
                        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
                        mapWrapperLayout.init(mMap, getPixelsFromDp(getApplicationContext(), 39 + 20));
                        infoButtonListener1 = new OnInfoWindowElemTouchListener(nearInfoBtn){
                            @Override
                            protected void onClickConfirmed(View v, Marker marker) {
                                // Here we can perform some action triggered after clicking the button
                                Toast.makeText(MapsActivity.this, "주변정보버튼 클릭", Toast.LENGTH_SHORT).show();
                                marker.hideInfoWindow();

                                purposeNearListAdapter =new PurposeNearListAdapter(purposeNearListItems,getLayoutInflater());
                                for(int i=0;i<purposeNearListItems.size();i++){
                                    CircleOptions circleOptions = new CircleOptions()
                                            .center(new LatLng(purposeNearListItems.get(i).getLat(),purposeNearListItems.get(i).getLng()))
                                            .radius(40)
                                            .strokeColor(Color.parseColor("#6d6e71"))
                                            .fillColor(Color.parseColor("#6d6e71"));
                                    Circle circle = mMap.addCircle(circleOptions);
                                    circleArrayList.add(circle);
                                    if(i==purposeNearListItems.size()-1){
                                        clicked_list_circle=mMap.addCircle(circleOptions);
                                    }
                                }

                                purposeListView=(ListView) findViewById(R.id.purposeListView);
                                purposeListLinear=(LinearLayout) findViewById(R.id.purposeListLinear);

                                Button purposeListCloseBtn=(Button) findViewById(R.id.purposeListCloseBtn);

                                purposeListView.setAdapter(purposeNearListAdapter);
                                purposeListLinear.setVisibility(View.VISIBLE);
                                purposeListLinear.bringToFront();
                                purposeListView.bringToFront();

                                purposeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        for(int i=0;i<purposeNearListItems.size();i++){
                                            if((purposeNearListItems.get(position).getName()).equals(purposeNearListItems.get(i).getName())){
                                                    clicked_list_circle.setCenter(new LatLng(purposeNearListItems.get(i).getLat(), purposeNearListItems.get(i).getLng()));
                                                    clicked_list_circle.setStrokeColor(Color.parseColor("#8dd6f0"));
                                                    clicked_list_circle.setFillColor(Color.parseColor("#8dd6f0"));
                                            }
                                        }

                                    }
                                });

                                purposeListCloseBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MapsActivity.this);
                                        alert_confirm.setMessage("주변정보가 사라집니다. 닫겠습니까?").setCancelable(false).setPositiveButton("확인",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 'YES'
                                                        clicked_list_circle.remove();
                                                        for(Circle circle : circleArrayList){
                                                            circle.remove();
                                                        }
                                                        circleArrayList.clear();
                                                        purposeListLinear.setVisibility(View.GONE);

                                                    }
                                                }).setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 'No'
                                                        return;
                                                    }
                                                });
                                        AlertDialog alert = alert_confirm.create();
                                        alert.show();
                                    }
                                });
                            }
                        };
                        infoButtonListener2 = new OnInfoWindowElemTouchListener(saveBtn){
                            @Override
                            protected void onClickConfirmed(View v, Marker marker) {
                                // Here we can perform some action triggered after clicking the button
//                                Toast.makeText(MapsActivity.this, "저장버튼 클릭", Toast.LENGTH_SHORT).show();
                                marker.hideInfoWindow();

                                //사용자들이 입력한 좌표들을 스트링에 넣는 작업
                                String people_address="";
                                for(int i=0;i<peopleCount;i++){
                                    people_address+=tempX.get(i)+"/"+tempY.get(i)+",";
                                }

                                MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));
                                middleNearStr=middleNearSearch.getMiddleAddress().toString();
                                String[] tempAddress=middleNearStr.split(" ");

                                for(int i=0;i<tempAddress.length;i++){
                                    if(tempAddress[i].contains("동")||tempAddress[i].contains("리")||tempAddress[i].contains("읍")||tempAddress[i].contains("면")){
                                        dong=tempAddress[i];
                                    }
                                }

                                MySQLiteHandler handler=new MySQLiteHandler(getApplicationContext());
                                handler.insert(people_address,dong+","+purpose_destination_name+","+near_position.latitude+"/"+near_position.longitude, peopleCount,meetingPurposeStr+","+resultList.get(0)+"/"+resultList.get(1));
                                handler.close();

//                                handler.delete("카페");
                                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();

                            }
                        };
                        nearInfoBtn.setOnTouchListener(infoButtonListener1);
                        saveBtn.setOnTouchListener(infoButtonListener2);


                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                // Setting up the infoWindow with current's marker info
                                // We must call this to set the current marker and infoWindow references
                                // to the MapWrapperLayout
                                if(marker.getPosition().latitude==near_position.latitude && marker.getPosition().longitude==near_position.longitude){

                                    finalNameTv.setText(purpose_destination_name);
                                    finalAddressTv.setText(purpose_destination_address);
                                    infoButtonListener1.setMarker(marker);
                                    infoButtonListener2.setMarker(marker);
                                    mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                                    return infoWindow;
                                }else{
                                    return null;
                                }
                            }
                        });


                    }else if(marker.getPosition().latitude!=final_position.latitude && marker.getPosition().longitude != final_position.longitude && marker.getPosition().latitude!=near_position.latitude && marker.getPosition().longitude!=near_position.longitude){
                        final MapWrapperLayout mapWrapperLayout2 = (MapWrapperLayout)findViewById(R.id.map_relative_layout);
                        mapWrapperLayout2.init(mMap, getPixelsFromDp(getApplicationContext(), 39 + 20));

                        infoButtonListener3=new OnInfoWindowElemTouchListener(navigateBtn) {
                            @Override
                            protected void onClickConfirmed(View v, Marker marker) {
                                Log.i("길찾기","클릭");
                                marker.hideInfoWindow();

                                onDirectionsSearchStart();
                                saveBtn.setVisibility(View.VISIBLE);

                                String startLocation=marker.getPosition().latitude+","+marker.getPosition().longitude;
                                String endLocation=near_position.latitude+","+near_position.longitude;
                                DirectionsSearch directionsSearch=new DirectionsSearch(startLocation,endLocation);
//                                Log.i("제발",directionsSearch.getDirections().toString());

                                try {
                                    parseJSON(directionsSearch.getDirections().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };
                        navigateBtn.setOnTouchListener(infoButtonListener3);

                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                // Setting up the infoWindow with current's marker info
                                // We must call this to set the current marker and infoWindow references
                                // to the MapWrapperLayout
                                if(marker.getPosition().latitude!=final_position.latitude && marker.getPosition().longitude != final_position.longitude && marker.getPosition().latitude!=near_position.latitude && marker.getPosition().longitude!=near_position.longitude){
                                    inputNameTv.setText(purpose_destination_name);
                                    infoButtonListener3.setMarker(marker);
                                    mapWrapperLayout2.setMarkerWithInfoWindow(marker, infoWindow2);
                                    return infoWindow2;
                                }else{
                                    return null;
                                }
                            }
                        });

                    }else{
                        marker.setSnippet("추가 위치정보 들어갈 자리");
                    }
                    marker.showInfoWindow();
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        resultList.clear();
        tempX.clear();
        tempY.clear();
        shareBtn.setVisibility(View.GONE);
        purposeNearListItems.clear();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    @Override
    public void onDirectionsSearchStart() {
        progressDialog=ProgressDialog.show(this,"경로를 준비중입니다.","경로를 찾았습니다.",true);

    }

    @Override
    public void onDirectionsSearchSuccess(List<Route> routes) {
        progressDialog.dismiss();
        directionsSearchResultLinear.setVisibility(View.VISIBLE);
        directionsSearchResultLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//        LinearLayout directionsSearchResultLinear=new LinearLayout(getApplicationContext());
//        directionsSearchResultLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        directionsSearchResultLinear.setOrientation(LinearLayout.HORIZONTAL);
//        directionsSearchResultLinear.setGravity(Gravity.CENTER_HORIZONTAL);

        for(Route route : routes){
            kmTv.setText(route.distance.text);
            durationTv.setText(route.duration.text);
            PolylineOptions polylineOptions=new PolylineOptions().geodesic(true).color(Color.parseColor("#A4A4A4")).width(10);
            for(int i=0;i<route.points.size();i++){
                polylineOptions.add(route.points.get(i));
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }

    private void parseJSON (String data) throws JSONException {
        if(data == null)    return;

        howToGoScroll.setVisibility(View.VISIBLE);
        howToGoLinear.setVisibility(View.VISIBLE);

        List<Route> routes=new ArrayList<Route>();
        JSONObject jsonData=new JSONObject(data);
        JSONArray jsonRoutes=jsonData.getJSONArray("routes");
        for(int i=0;i<jsonRoutes.length();i++){
            JSONObject jsonRoute=jsonRoutes.getJSONObject(i);
            Route route=new Route();

            JSONObject overview_polylineJson= jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            JSONArray jsonSteps = jsonLeg.getJSONArray("steps");
//            Log.i("jsonSteps",""+jsonSteps.length());
            for(int k=0;k<jsonSteps.length();k++){
//                Log.i("확인",jsonSteps.getJSONObject(k).getString("html_instructions"));
                LinearLayout stepLinear=new LinearLayout(MapsActivity.this);
                stepLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                stepLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                stepLinear.setOrientation(LinearLayout.VERTICAL);

                TextView html_instructionsTv=new TextView(MapsActivity.this);
                html_instructionsTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                html_instructionsTv.setTextSize(15);
                html_instructionsTv.setTypeface(null, Typeface.BOLD);
                html_instructionsTv.setText(jsonSteps.getJSONObject(k).getString("html_instructions"));

                LinearLayout stepDetailLinear=new LinearLayout(MapsActivity.this);
                stepDetailLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                stepDetailLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                stepDetailLinear.setOrientation(LinearLayout.HORIZONTAL);

                TextView distanceTv=new TextView(MapsActivity.this);
                distanceTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                distanceTv.setTextSize(15);
                distanceTv.setText(jsonSteps.getJSONObject(k).getJSONObject("distance").getString("text")+" ");

                TextView durationTv=new TextView(MapsActivity.this);
                durationTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                durationTv.setTextSize(15);
                durationTv.setText(jsonSteps.getJSONObject(k).getJSONObject("duration").getString("text")+" ");

                TextView travel_modeTv=new TextView(MapsActivity.this);
                travel_modeTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                travel_modeTv.setTextSize(15);
                if(jsonSteps.getJSONObject(k).getString("travel_mode").equals("WALKING")){
                    travel_modeTv.setText("걷기");
                }else{
                    travel_modeTv.setText("대중교통 이용");
                }

                stepLinear.addView(html_instructionsTv);
                stepDetailLinear.addView(distanceTv);
                stepDetailLinear.addView(durationTv);
                stepDetailLinear.addView(travel_modeTv);
                howToGoLinear.addView(stepLinear);
                howToGoLinear.addView(stepDetailLinear);

                if(!jsonSteps.getJSONObject(k).isNull("transit_details")){
//                    Log.i("확인", ""+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getString("short_name"));
                    Double busDepartLat=jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lat");
                    Double busDepartLng=jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lng");

                    Double busArrivalLat=jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lat");
                    Double busArrivalLng=jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lng");

                    busMarkerArrayList.add(mMap.addMarker(new MarkerOptions().position(new LatLng(busDepartLat,busDepartLng))));
                    busMarkerArrayList.add(mMap.addMarker(new MarkerOptions().position(new LatLng(busArrivalLat,busArrivalLng))));
                    for(Marker marker:busMarkerArrayList){
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_stop_marker));
                    }

                    LinearLayout transitLinear=new LinearLayout(MapsActivity.this);
                    transitLinear.setLayoutParams(new LinearLayout.LayoutParams(1000,LinearLayout.LayoutParams.WRAP_CONTENT));
                    transitLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                    transitLinear.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout transitHeadLinear=new LinearLayout(MapsActivity.this);
                    transitHeadLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,70));
                    transitHeadLinear.setOrientation(LinearLayout.HORIZONTAL);

                    TextView short_nameTv=new TextView(MapsActivity.this);
                    short_nameTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    short_nameTv.setTextSize(15);
                    short_nameTv.setTextColor(Color.parseColor(jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getString("color")));
                    short_nameTv.setText(""+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getString("short_name"));

                    TextView vehicle_name_Tv=new TextView(MapsActivity.this);
                    vehicle_name_Tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    vehicle_name_Tv.setTextSize(15);
                    vehicle_name_Tv.setText(" "+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("line").getJSONObject("vehicle").getString("name"));

                    transitHeadLinear.addView(short_nameTv);
                    transitHeadLinear.addView(vehicle_name_Tv);

                    LinearLayout transitDirectionLinear=new LinearLayout(MapsActivity.this);
                    transitDirectionLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    transitDirectionLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                    transitDirectionLinear.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout transitDepartLinear=new LinearLayout(MapsActivity.this);
                        transitDepartLinear.setLayoutParams(new LinearLayout.LayoutParams(400,LinearLayout.LayoutParams.WRAP_CONTENT));
                        transitDepartLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                        transitDepartLinear.setOrientation(LinearLayout.VERTICAL);

                        TextView departure_stop_name_Tv=new TextView(MapsActivity.this);
                        departure_stop_name_Tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        departure_stop_name_Tv.setTextSize(15);
                        departure_stop_name_Tv.setText(jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"));

                    Log.i("출발정류소이름",""+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"));


                        TextView departure_time_Tv=new TextView(MapsActivity.this);
                        departure_time_Tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        departure_time_Tv.setTextSize(15);
                        departure_time_Tv.setText("출발 : "+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_time").getString("text"));

                    Log.i("출발정류소시간",""+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("departure_time").getString("text"));

                        transitDepartLinear.addView(departure_time_Tv);
                        transitDepartLinear.addView(departure_stop_name_Tv);


                    LinearLayout transitArrivalLinear=new LinearLayout(MapsActivity.this);
                    transitArrivalLinear.setLayoutParams(new LinearLayout.LayoutParams(400,LinearLayout.LayoutParams.WRAP_CONTENT));
                    transitArrivalLinear.setGravity(Gravity.CENTER_HORIZONTAL);
                    transitArrivalLinear.setOrientation(LinearLayout.VERTICAL);

                        TextView arrival_stop_name_Tv=new TextView(MapsActivity.this);
                        arrival_stop_name_Tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        arrival_stop_name_Tv.setTextSize(15);
                        arrival_stop_name_Tv.setText(jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"));

                        TextView arrival_time_Tv=new TextView(MapsActivity.this);
                        arrival_time_Tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        arrival_time_Tv.setTextSize(15);
                        arrival_time_Tv.setText("도착 : "+jsonSteps.getJSONObject(k).getJSONObject("transit_details").getJSONObject("arrival_time").getString("text"));

                        transitArrivalLinear.addView(arrival_time_Tv);
                        transitArrivalLinear.addView(arrival_stop_name_Tv);

                    TextView lineTv=new TextView(MapsActivity.this);
                    lineTv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    lineTv.setGravity(Gravity.CENTER_VERTICAL);
                    lineTv.setTextSize(15);
                    lineTv.setText("   →   ");

                    transitDirectionLinear.addView(transitDepartLinear);
                    transitDirectionLinear.addView(lineTv);
                    transitDirectionLinear.addView(transitArrivalLinear);

                    transitLinear.addView(transitHeadLinear);
                    transitLinear.addView(transitDirectionLinear);

                    howToGoLinear.addView(transitLinear);
                }




                if(k!=(jsonSteps.length()-1)){
                    ImageView imageView=new ImageView(MapsActivity.this);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(70,70));
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon0));
                    howToGoLinear.addView(imageView);
                }
                howToGoScroll.bringToFront();
                howToGoLinear.bringToFront();
//
//                stepLinear.addView(stepDetailLinear);

//                inputMarkerInfoWindowLinear.addView(stepLinear);

            }


            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));


            routes.add(route);
            Log.i("parseJSON","여기까지");

        }

        onDirectionsSearchSuccess(routes);

    }

    private List<LatLng> decodePolyLine(final String poly){
        int len=poly.length();
        int index=0;
        List<LatLng> decoded=new ArrayList<LatLng>();
        int lat=0;
        int lng=0;

        while(index<len){
            int b;
            int shift=0;
            int result=0;
            do{
                b=poly.charAt(index++)-63;
                result |= (b&0x1f)<<shift;
                shift+=5;

            }while(b>=0x20);
            int dlat=((result&1)!=0 ? ~(result>>1) : (result>>1));
            lat+= dlat;

            shift=0;
            result=0;
            do{
                b=poly.charAt(index++)-63;
                result |= (b&0x1f)<<shift;
                shift+=5;

            }while(b>=0x20);
            int dlng=((result&1)!=0 ? ~(result>>1):(result>>1));
            lng+=dlng;

            decoded.add(new LatLng(lat/100000d,lng/100000d));
        }
        return decoded;
    }

}
