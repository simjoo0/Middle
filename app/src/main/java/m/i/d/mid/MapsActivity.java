package m.i.d.mid;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LinearLayout searchLinear;
    LinearLayout SearchAllLinear;
    Button locationPlusBtn;
    EditText addressEt;

    ArrayList<ListViewItem> data;   //리스트뷰 아이템 담는 arrayList
    ArrayList<Double> resultList=new ArrayList<Double>(); // 중간좌표

    ArrayList<PurposeNearListItem> purposeNearListItems = new ArrayList<PurposeNearListItem>();

    String searchResultStr="";  //xml 결과를 '/'로 연결한 문자열 '이름/주소/위도/경도' 로 구성된다.
    String markingResultName="";    //리스트뷰 아이템 선택 시 가져오는 이름을 저장할 문자열
    String markingResultLat="";     //리스트뷰 아이템 선택 시 가져오는 위도를 저장할 문자열
    String markingResultLng="";     //리스트뷰 아이템 선택 시 가져오는 경도를 저장할 문자열

    int markerCount=0;
    String meetingPurposeStr="";    //목적
    int peopleCount=0;  //인원수

    String middleNearStr="";

    ArrayList<Double> tempX=new ArrayList<Double>();
    ArrayList<Double> tempY=new ArrayList<Double>();

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent=new Intent(this.getIntent());
        meetingPurposeStr=intent.getStringExtra("meetingPurPose").toString();
        peopleCount=intent.getIntExtra("peopleCount",0);

//        Toast.makeText(this, meetingPurposeStr+""+peopleCount, Toast.LENGTH_SHORT).show();    //이전 액티비티에서 목적과 인원수 가져오는지 확인



        searchLinear = (LinearLayout) findViewById(R.id.SearchLinear);
        SearchAllLinear=(LinearLayout) findViewById(R.id.SearchAllLinear);
        locationPlusBtn=(Button) findViewById(R.id.locationPlusBtn);
        locationPlusBtn.bringToFront();

        addressEt = (EditText) findViewById(R.id.addressEt);


    }


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

                                    LatLng final_position = new LatLng(resultList.get(0),resultList.get(1));  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                    mMap.addMarker(new MarkerOptions().position(final_position).title("중간위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_marker));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));


                                    MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));

//                                    Toast.makeText(getApplicationContext(),middleNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
                                    middleNearStr=middleNearSearch.getMiddleAddress().toString();
//                                    Toast.makeText(getApplicationContext(),middleNearStr,Toast.LENGTH_SHORT).show();
                                    String dong="";

                                    String[] tempAddress=middleNearStr.split(" ");

                                    for(int i=0;i<tempAddress.length;i++){
                                        if(tempAddress[i].contains("동")||tempAddress[i].contains("리")||tempAddress[i].contains("읍")||tempAddress[i].contains("면")){
                                            dong=tempAddress[i];
                                        }
                                    }
//                                    Toast.makeText(getApplicationContext(),dong,Toast.LENGTH_SHORT).show();
                                    PurposeNearSearch purposeNearSearch=new PurposeNearSearch(dong+" "+meetingPurposeStr);
//                                    Toast.makeText(getApplicationContext(),purposeNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
                                    ListView purposeListView=(ListView) findViewById(R.id.purposeListView);

                                    StringTokenizer stk1=new StringTokenizer(purposeNearSearch.getMiddleAddress().toString(),"\n");

                                    while(stk1.hasMoreTokens()){
                                        StringTokenizer stk2=new StringTokenizer(stk1.nextToken(),"/");
                                        while(stk2.hasMoreTokens()){
                                            purposeNearListItems.add(new PurposeNearListItem(stk2.nextToken(),Double.parseDouble(stk2.nextToken()),Double.parseDouble(stk2.nextToken())));
                                        }
                                    }
//                                    PurposeNearListAdapter purposeNearListAdapter =new PurposeNearListAdapter(purposeNearListItems,getLayoutInflater());
//                                    purposeListView.setAdapter(purposeNearListAdapter);
//                                    purposeListView.setVisibility(View.VISIBLE);
//                                    purposeListView.bringToFront();

                                    if(purposeNearListItems.size()!=0){
                                        ArrayList<Integer> distIndex=GetNearPosition.calculMinDist(purposeNearListItems,resultList.get(0),resultList.get(1));
                                        Toast.makeText(getApplicationContext(), purposeNearListItems.get(distIndex.get(0)).getName() + "이(가) 가장 가깝습니다.", Toast.LENGTH_SHORT).show();

                                        LatLng near_position = new LatLng(purposeNearListItems.get(distIndex.get(0)).getLat(),purposeNearListItems.get(distIndex.get(0)).getLng());  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                        mMap.addMarker(new MarkerOptions().position(near_position).title("최종위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_purpose_marker));
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

                                    LatLng final_position = new LatLng(resultList.get(0),resultList.get(1));  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                    mMap.addMarker(new MarkerOptions().position(final_position).title("중간위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_marker));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(final_position));
                                    Log.i("resultList",resultList.get(0)+","+resultList.get(1));
                                    MiddleNearSearch middleNearSearch=new MiddleNearSearch(resultList.get(0),resultList.get(1));

//                                    Toast.makeText(getApplicationContext(),middleNearSearch.getMiddleAddress(),Toast.LENGTH_SHORT).show();
                                    middleNearStr=middleNearSearch.getMiddleAddress().toString();
                                    Log.i("middleNearStr",middleNearStr);
//                                    Toast.makeText(getApplicationContext(),middleNearStr,Toast.LENGTH_SHORT).show();
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
                                    ListView purposeListView=(ListView) findViewById(R.id.purposeListView);
                                    Log.i("purposeNearSearch",purposeNearSearch.getMiddleAddress().toString());

                                    StringTokenizer stk1=new StringTokenizer(purposeNearSearch.getMiddleAddress().toString(),"\n");

                                    while(stk1.hasMoreTokens()){
                                        StringTokenizer stk2=new StringTokenizer(stk1.nextToken(),"/");
                                        while(stk2.hasMoreTokens()){
                                            purposeNearListItems.add(new PurposeNearListItem(stk2.nextToken(),Double.parseDouble(stk2.nextToken()),Double.parseDouble(stk2.nextToken())));
                                        }
                                    }
//                                    PurposeNearListAdapter purposeNearListAdapter =new PurposeNearListAdapter(purposeNearListItems,getLayoutInflater());
//                                    purposeListView.setAdapter(purposeNearListAdapter);
//                                    purposeListView.setVisibility(View.VISIBLE);
//                                    purposeListView.bringToFront();

                                    if(purposeNearListItems.size()!=0){
                                        ArrayList<Integer> distIndex=GetNearPosition.calculMinDist(purposeNearListItems,resultList.get(0),resultList.get(1));
                                        Toast.makeText(getApplicationContext(), purposeNearListItems.get(distIndex.get(0)).getName() + "이(가) 가장 가깝습니다.", Toast.LENGTH_SHORT).show();

                                        LatLng near_position = new LatLng(purposeNearListItems.get(distIndex.get(0)).getLat(),purposeNearListItems.get(distIndex.get(0)).getLng());  //선택된 리스트 값으로 마커 찍고 카메라 이동
                                        mMap.addMarker(new MarkerOptions().position(near_position).title("최종위치")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.middle_purpose_marker));
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
                    marker.setSnippet("추가 위치정보 들어갈 자리");
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
    }
}
