package m.i.d.mid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by jooyoung on 2016-11-05.
 */

public class InfoActivity extends Activity{
    Button questionBtn;
    Button closeBtn;
    Button mapEnterBtn;
    LinearLayout questionLinear;

    RadioGroup radioGroup;
    RadioButton mealRadio;
    RadioButton caffeRadio;
    RadioButton studyRadio;
    RadioButton cultureRadio;


    int peopleCount=0;
    String meetingPurposeStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        questionLinear=(LinearLayout) findViewById(R.id.questionLinear);

        questionBtn=(Button) findViewById(R.id.questionBtn);
        closeBtn=(Button) findViewById(R.id.closeBtn);
        mapEnterBtn=(Button) findViewById(R.id.mapEnterBtn);

        radioGroup=(RadioGroup) findViewById(R.id.radioGroup);
        mealRadio=(RadioButton) findViewById(R.id.mealRadio);
        caffeRadio=(RadioButton) findViewById(R.id.caffeRadio);
        studyRadio=(RadioButton) findViewById(R.id.studyRadio);
        cultureRadio=(RadioButton) findViewById(R.id.cultureRadio);

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

                    InfoActivity.this.finish();
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

    }

    @Override
    protected void attachBaseContext(Context newBase) { //Application 클래스 (폰트 적용)
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
