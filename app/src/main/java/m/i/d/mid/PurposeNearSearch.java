package m.i.d.mid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jooyoung on 2016-11-12.
 */

public class PurposeNearSearch {

    String middleResultStr="";
    String result="";
    String location="";
    Thread thread;
    String line="";

    public PurposeNearSearch() {
    }



    public PurposeNearSearch(String address){
        location=address;
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                line = getPurposePlaceInfo(location);
                result=line;

            }
        });
        thread.start();
        try {
            thread.join();
            Bundle bun = new Bundle();
            bun.putString("Middle_Purpose", line);
            Message msg = handler.obtainMessage();
            msg.setData(bun);
            handler.sendMessage(msg);

            getMiddleAddress();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        setMiddleAddress(middleResultStr);

    }


    private String getPurposePlaceInfo(String location){   //Google Place Web Server에 Uri를 통해 접근한 후 결과값을 xml 파일 형식으로 가져오는 method

        StringBuffer buffer=new StringBuffer();
        String encodingLocation = URLEncoder.encode(location); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..
        String queryUrl="https://maps.googleapis.com/maps/api/place/textsearch/xml?query="+encodingLocation+"&language=ko&key=AIzaSyATuGbXS38O_z_01vxGLISIapsO2QWhUq4";

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
                        if(tag.equals("name")) {
                            xpp.next();
                            buffer.append(xpp.getText()); //name 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");
                        }else if(tag.equals("formatted_address")){
                            xpp.next();
                            buffer.append(xpp.getText()); //formatted_address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("/");
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
            middleResultStr = bun.getString("Middle_Purpose");
//            setMiddle(bun.getString("Middle_Near"));

        }
    };




//    public void setMiddleAddress(String str){
//        result=str;
//    }


    public String getMiddleAddress(){
//        Log.i("하이",result);
        return result;
    }

}
