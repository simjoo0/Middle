package m.i.d.mid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

/**
 * Created by jooyoung on 2016-11-05.
 */

public class InfoActivity extends Activity{
    Button questionBtn;
    Button closeBtn,closeBtn2;
    Button mapEnterBtn;
    Button dataBtn;
    LinearLayout questionLinear;
    LinearLayout dataLinear;
    GridView gview;

    RadioGroup radioGroup;
    RadioButton mealRadio;
    RadioButton caffeRadio;
    RadioButton studyRadio;
    RadioButton cultureRadio;


    int peopleCount=0;
    String meetingPurposeStr="";

    ArrayList<DataDto> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        questionLinear=(LinearLayout) findViewById(R.id.questionLinear);
        dataLinear=(LinearLayout) findViewById(R.id.dataLinear);

        questionBtn=(Button) findViewById(R.id.questionBtn);
        closeBtn=(Button) findViewById(R.id.closeBtn);
        closeBtn2=(Button) findViewById(R.id.closeBtn2);
        mapEnterBtn=(Button) findViewById(R.id.mapEnterBtn);
        dataBtn=(Button) findViewById(R.id.dataBtn);

        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        mealRadio=(RadioButton) findViewById(R.id.mealRadio);
        caffeRadio=(RadioButton) findViewById(R.id.caffeRadio);
        studyRadio=(RadioButton) findViewById(R.id.studyRadio);
        cultureRadio=(RadioButton) findViewById(R.id.cultureRadio);

        gview=(GridView)findViewById(R.id.gridView);

        Spinner spinner = (Spinner)findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                peopleCount=Integer.parseInt(parent.getItemAtPosition(position).toString());
//                Toast.makeText(getApplicationContext(),"인원은"+peopleCount,Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBtn.setVisibility(View.GONE);
                questionBtn.setVisibility(View.GONE);
                mapEnterBtn.setVisibility(View.GONE);
                questionLinear.setVisibility(View.VISIBLE);

                questionLinear.bringToFront();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionBtn.setVisibility(View.VISIBLE);
                mapEnterBtn.setVisibility(View.VISIBLE);
                dataBtn.setVisibility(View.VISIBLE);
                questionLinear.setVisibility(View.GONE);
            }
        });

        mapEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mealRadio.getId()==radioGroup.getCheckedRadioButtonId()) meetingPurposeStr=mealRadio.getText().toString();
                if(caffeRadio.getId()==radioGroup.getCheckedRadioButtonId()) meetingPurposeStr=caffeRadio.getText().toString();
                if(studyRadio.getId()==radioGroup.getCheckedRadioButtonId()) meetingPurposeStr=studyRadio.getText().toString();
                if(cultureRadio.getId()==radioGroup.getCheckedRadioButtonId()) meetingPurposeStr=cultureRadio.getText().toString();

                if(peopleCount!=0 && !meetingPurposeStr.equals("")){

                    Intent intent=new Intent(getApplication(), MapsActivity.class);
                    intent.putExtra("peopleCount",peopleCount);
                    intent.putExtra("meetingPurPose",meetingPurposeStr);
                    startActivity(intent); // 로딩이 끝난후 이동할 Activity

                }else{
                    if(peopleCount==0 && !meetingPurposeStr.equals("")){
                        Toast.makeText(InfoActivity.this, "인원 수를 선택하세요", Toast.LENGTH_SHORT).show();
                    }
                    if(peopleCount!=0 && meetingPurposeStr.equals("")){
                        Toast.makeText(InfoActivity.this,"목적을 선택하세요",Toast.LENGTH_SHORT).show();
                    }
                    if(peopleCount==0 && meetingPurposeStr.equals("")){
                        Toast.makeText(InfoActivity.this, "인원 수와 목적을 선택하세요", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        dataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySQLiteHandler handler=new MySQLiteHandler(getApplicationContext());
                //여기서부터 정보 가져와지나 보는코드
                Cursor cursor=handler.select();
                startManagingCursor(cursor);
                if(cursor.getCount()!=0){
                    dataList=new ArrayList<DataDto>();
                    DataDto dataDto;

                    dataBtn.setVisibility(View.GONE);
                    questionBtn.setVisibility(View.GONE);
                    mapEnterBtn.setVisibility(View.GONE);
                    dataLinear.setVisibility(View.VISIBLE);
                    dataLinear.bringToFront();

                    while(cursor.moveToNext()){
                        dataDto=new DataDto();
                        dataDto.setAll_location(cursor.getString(cursor.getColumnIndex("all_location")));  //사용자의 위치가 x1/y1,x2/y2, 이런식으로 되어있음
                        dataDto.setFinal_address(cursor.getString(cursor.getColumnIndex("final_address")));
                        dataDto.setPeopleCount(cursor.getInt(cursor.getColumnIndex("peopleCount")));
                        dataDto.setTransport(cursor.getString(cursor.getColumnIndex("transport")));
                        dataList.add(dataDto);
//                        Toast.makeText(getApplicationContext(), dataDto.getTransport()+" 잘가져와짐!", Toast.LENGTH_SHORT).show();
                    }



                    DataAdapter adapter = new DataAdapter(InfoActivity.this, dataList);
                    gview.setAdapter(adapter);
                    gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String[] tempAll_Location=dataList.get(position).getAll_location().split(",");

                            int size=tempAll_Location.length;
                            double[] x_location=new double[size];
                            double[] y_location=new double[size];
                            for(int i=0;i<size;i++){
                                String[] temp=tempAll_Location[i].split("/");
                                x_location[i]=Double.parseDouble(temp[0].trim());
                                y_location[i]=Double.parseDouble(temp[1].trim());
                            }

                            String [] temp_final=dataList.get(position).getFinal_address().split(",");
                            String [] temp_final_location=temp_final[2].split("/");

                            String [] temp_middle=dataList.get(position).getTransport().split(",");
                            String [] temp_middle_location=temp_middle[1].split("/");


                            Intent intent=new Intent(getApplication(), MapsActivity.class);
                            intent.putExtra("dataBaseFlag",1);
                            intent.putExtra("dataPeople",size);
                            intent.putExtra("x_location",x_location);
                            intent.putExtra("y_location",y_location);

                            //수학된 중간거리 보내줌
                            intent.putExtra("x_middle_location",Double.parseDouble(temp_middle_location[0].trim()));
                            intent.putExtra("y_middle_location",Double.parseDouble(temp_middle_location[1].trim()));

                            intent.putExtra("final_location_name",temp_final[1]);
                            intent.putExtra("final_location_x",Double.parseDouble(temp_final_location[0].trim()));
                            intent.putExtra("final_location_y",Double.parseDouble(temp_final_location[1].trim()));
                            intent.putExtra("data_dong",temp_final[0]);
                            intent.putExtra("data_meetingPurposeStr",temp_middle[0]);
                            startActivity(intent); // 로딩이 끝난후 이동할 Activity

                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"저장되어진 데이터가 없습니다!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        closeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionBtn.setVisibility(View.VISIBLE);
                mapEnterBtn.setVisibility(View.VISIBLE);
                dataLinear.setVisibility(View.GONE);
                dataBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) { //Application 클래스 (폰트 적용)
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
