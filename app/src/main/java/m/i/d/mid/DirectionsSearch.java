package m.i.d.mid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jooyoung on 2016-11-27.
 */

// LatLng ajouUniv = new LatLng(37.2851815, 127.045332);
// https://maps.googleapis.com/maps/api/directions/json?origin=Brooklyn&destination=Queens&mode=transit&transit_mode=bus|subway&language=ko&key=AIzaSyAN4KbpaZ1tos1szgJHTdjvoUDArBYsLng

public class DirectionsSearch {

    String directionsStr="";
    String result="";
    String startLocation="";
    String endLocation="";
    Thread thread;
    String line="";

    public DirectionsSearch() {
    }



    public DirectionsSearch(String start,String end){
        this.startLocation=start;
        this.endLocation=end;
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                line = getDirectionsSearch(startLocation,endLocation);
                result=line;

            }
        });
        thread.start();
        try {
            thread.join();
            Bundle bun = new Bundle();
            bun.putString("Directions", line);
            Message msg = handler.obtainMessage();
            msg.setData(bun);
            handler.sendMessage(msg);

            getDirections();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        setMiddleAddress(middleResultStr);

    }


    private String getDirectionsSearch(String start, String end){   //Google Place Web Server에 Uri를 통해 접근한 후 결과값을 xml 파일 형식으로 가져오는 method
// LatLng ajouUniv = new LatLng(37.2851815, 127.045332);
// https://maps.googleapis.com/maps/api/directions/json?origin=37.2851815,127.045332&destination=37.291460,127.044116&mode=transit&transit_mode=bus|subway&language=ko&key=AIzaSyAN4KbpaZ1tos1szgJHTdjvoUDArBYsLng
        String line = null;
        String page = "";
        String queryUrl="https://maps.googleapis.com/maps/api/directions/json?origin="+start+"&destination="+end+"&mode=transit&transit_mode=bus|subway&language=ko&key=AIzaSyAN4KbpaZ1tos1szgJHTdjvoUDArBYsLng";

        try {
            URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream();  //url위치로 입력스트림 연결
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "utf-8"));



            while ((line = bufferedReader.readLine()) != null) {
                page += line;
            }




//            JSONObject json = new JSONObject(page);
//            JSONArray jsonArray = json.getJSONArray("dataSend");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return page;
    }

    Handler handler = new Handler() {   // Thread Handler
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            directionsStr = bun.getString("Directions");
//            setMiddle(bun.getString("Middle_Near"));

        }
    };




    public String getDirections(){
//        Log.i("하이",result);
        return result;
    }


}
