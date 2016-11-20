package m.i.d.mid;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jooyoung on 2016-11-12.
 */

public class GetNearPosition {
        static ArrayList<PurposeNearListItem> purposeNearListItem=new ArrayList<PurposeNearListItem>();

        //한 점에서 다른 점 거리들을 구해서 거리 값 오름차순으로 인덱스 값 입력받은 ArrayList리턴하는 함수
        public static ArrayList<Integer> calculMinDist(ArrayList<PurposeNearListItem> list, double middleX, double middleY){
            purposeNearListItem=list;

            double [] x=new double [purposeNearListItem.size()+1];
            double [] y=new double [purposeNearListItem.size()+1];
            x[0]=middleX;    //기준점이 될 곳에 수학적 중심거리의 x, y값 넣어주기
            y[0]=middleY;

            for(int i=1;i<purposeNearListItem.size()+1;i++){
                x[i]=purposeNearListItem.get(i-1).getLat();
                y[i]=purposeNearListItem.get(i-1).getLng();
            }

            int num=x.length;

            ArrayList<Integer> minIndex=new ArrayList<Integer> ();
            ArrayList<Double> distance=new ArrayList<Double>();
            double temp[]=new double[num-1];

            for(int i=1;i<num;i++){
                distance.add((Math.pow((x[0]-x[i]), 2)+Math.pow((y[0]-y[i]), 2)));
//            Log.i("거리", (Math.pow((x[0] - x[i]), 2) + Math.pow((y[0] - y[i]), 2)) + "");
                temp[i-1]=(Math.pow((x[0]-x[i]), 2)+Math.pow((y[0]-y[i]), 2));
            }// 하나는 List,하나는 배열 형태로 내용물 똑같이 하나씩 만듦.
            Arrays.sort(temp); // 배열 형태로 만든 것은 오름차순으로 sort함
            for(int i=0;i<temp.length;i++){
                Log.i("순서", temp[i] + "");
            }
            for(int i=0;i<num-1;i++){
                for(int k=0;k<num-1;k++){
                    if(temp[i]==distance.get(k)){
                        minIndex.add(k);
                    }
                }
            }

            return minIndex;
        }

    }
