package m.i.d.mid;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by jooyoung on 2016-11-10.
 */

public class GetPosition {
    static double result_x=0, result_y=0;

    //가장 가까운 점의 인덱스부터 나열되는 ArrayList
    static ArrayList<Integer> distIndex=new ArrayList<Integer>();
    static ArrayList<Integer> tempIndex=new ArrayList<Integer>();

    static String result="";
    static int num=0;  //인원수

    //입력받은 x좌표 값
    static double x[]=new double[num];
    //입력받은 y좌표 값
    static double y[]=new double[num];

    public GetPosition() {
    }

    public GetPosition(int num) {
        this.num=num;
    }

    public static ArrayList<Double> mainFunc(double[] x2, double[] y2) {

        x=x2;
        y=y2;

        ArrayList<Double> resultList=new ArrayList<Double>();

        if(num==2){
            result_x=(x[0]+x[1])/2;
            result_y=(y[0]+y[1])/2;

        }else if(num==3){
            calculFunc(x,y);

        }else if(num==4){
            distIndex=calculMinDist(num, x, y);
            int maxDist=distIndex.get(distIndex.size()-1);

            double tempX[]=new double[3];
            double tempY[]=new double[3];

            tempX[0]=x[0];
            tempY[0]=y[0];
            for(int i=1;i<3;i++){
                tempX[i]=x[distIndex.get(i-1)];
                tempY[i]=y[distIndex.get(i-1)];
            }
            calculFunc(tempX,tempY);
            result=result_x+","+result_y;

            tempX[0]=x[maxDist];
            tempY[0]=y[maxDist];
            calculFunc(tempX,tempY);
            result+=","+result_x+","+result_y;

            splitNumber(result);

        }else if(num==5){
            distIndex=calculMinDist(num, x, y);
            double tempX[]=new double[3];
            double tempY[]=new double[3];
            tempX[0]=x[0];
            tempY[0]=y[0];

            for(int i=1;i<3;i++){
                tempX[i]=x[distIndex.get(i-1)];
                tempY[i]=y[distIndex.get(i-1)];
            }
            calculFunc(tempX,tempY);
            result=result_x+","+result_y;   //첫번째 세 점


            //두번째 세 점
            double tempX2[]=new double[num-1];
            double tempY2[]=new double[num-1];
            tempX2[0]=x[distIndex.get(2)];
            tempY2[0]=y[distIndex.get(2)];
            tempX2[1]=x[distIndex.get(0)];
            tempY2[1]=y[distIndex.get(0)];
            tempX2[2]=x[distIndex.get(1)];
            tempY2[2]=y[distIndex.get(1)];
            tempX2[3]=x[distIndex.get(3)];
            tempY2[3]=y[distIndex.get(3)];

            tempIndex=calculMinDist(num-1, tempX2, tempY2);
            tempX[0]=tempX2[0];
            tempY[0]=tempY2[0];
            for(int i=1;i<3;i++){
                tempX[i]=x[distIndex.get(i-1)];
                tempY[i]=y[distIndex.get(i-1)];
            }
            calculFunc(tempX,tempY);
            result+=","+result_x+","+result_y;


            //세번째 삼각형
            tempX2[0]=x[distIndex.get(3)];
            tempY2[0]=y[distIndex.get(3)];
            for(int i=1;i<4;i++){
                tempX2[i]=x[distIndex.get(i-1)];
                tempY2[i]=y[distIndex.get(i-1)];
            }
            tempIndex=calculMinDist(num-1, tempX2, tempY2);
            tempX[0]=tempX2[0];
            tempY[0]=tempY2[0];
            for(int i=1;i<3;i++){
                tempX[i]=x[distIndex.get(i-1)];
                tempY[i]=y[distIndex.get(i-1)];
            }
            calculFunc(tempX,tempY);
            result+=","+result_x+","+result_y;
            splitNumber(result);

        }else if(num==6){   //삼각형 두개의 외심 -> 두개 외심의 중점
            distIndex=calculMinDist(num, x, y);
            double tempX[]=new double[3];
            double tempY[]=new double[3];

            tempX[0]=x[0];
            tempY[0]=y[0];

            for(int i=1;i<3;i++){
                tempX[i]=x[distIndex.get(i-1)];
                tempY[i]=y[distIndex.get(i-1)];
            }

            calculFunc(tempX,tempY);
            result=result_x+","+result_y;

            for(int i=0;i<3;i++){
                tempX[i]=x[distIndex.get(i+2)];
                tempY[i]=y[distIndex.get(i+2)];
            }
            calculFunc(tempX,tempY);
            result+=","+result_x+","+result_y;

            splitNumber(result);
        }

        resultList.add(result_x);
        resultList.add(result_y);

        return resultList;
    }



    //한 점에서 다른 점 거리들을 구해서 거리 값 오름차순으로 인덱스 값 입력받은 ArrayList리턴하는 함수
    public static ArrayList<Integer> calculMinDist(int num, double[] x, double[] y){
        ArrayList<Integer> minIndex=new ArrayList<Integer> ();
        ArrayList<Double> distance=new ArrayList<Double>();
        double temp[]=new double[num-1];

        for(int i=1;i<num;i++){
            distance.add((Math.pow((x[0]-x[i]), 2)+Math.pow((y[0]-y[i]), 2)));
            temp[i-1]=(Math.pow((x[0]-x[i]), 2)+Math.pow((y[0]-y[i]), 2));
        }// 하나는 List,하나는 배열 형태로 내용물 똑같이 하나씩 만듦.
        Arrays.sort(temp); // 배열 형태로 만든 것은 오름차순으로 sort함

        for(int i=0;i<num-1;i++){
            for(int k=0;k<num-1;k++){
                if(temp[i]==distance.get(k)){
                    minIndex.add(k+1);
                }
            }
        }

        return minIndex;
    }

    //연립 방정식 계산 함수
    public static void calculFunc(double[] x, double[] y){
        double a1,b1,c1,a2,b2,c2;      //연립방정식의 계수들

        a1=2*x[0]-2*x[1];
        b1=2*y[0]-2*y[1];
        c1=x[0]*x[0]-x[1]*x[1]+y[0]*y[0]-y[1]*y[1];

        a2=2*x[0]-2*x[2];
        b2=2*y[0]-2*y[2];
        c2=x[0]*x[0]-x[2]*x[2]+y[0]*y[0]-y[2]*y[2];

        result_x=((c1*b2)-(b1*c2))/((a1*b2)-(a2*b1));
        result_y=((a1*c2)-(c1*a2))/((a1*b2)-(a2*b1));
    }


    private static void splitNumber(String result) {
        // TODO Auto-generated method stub
        ArrayList<Float> tempList=new ArrayList<Float>();

        StringTokenizer stk=new StringTokenizer(result,",");
        while(stk.hasMoreTokens()){
            tempList.add(Float.parseFloat(stk.nextToken()));
        }

        for(int i=0;i<tempList.size()/2;i+=2){
            result_x+=tempList.get(i);
            result_y+=tempList.get(i+1);
        }

        result_x=result_x/(tempList.size()/2);
        result_y=result_y/(tempList.size()/2);


    }


}
