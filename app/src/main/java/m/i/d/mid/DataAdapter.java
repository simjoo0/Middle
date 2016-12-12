package m.i.d.mid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
/**
 * Created by jooyoung on 2016-12-01.
 */

public class DataAdapter extends BaseAdapter{

    private TextView textView1, textView2, textView3;
    private Context context;
    private ArrayList<DataDto> list;

    public DataAdapter(Context context, ArrayList<DataDto> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;

        if(convertView==null){
            layout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.data_layout,parent,false);
        }else{
            layout=(LinearLayout) convertView;
        }
        textView1=(TextView) layout.findViewById(R.id.colum1);
        textView2=(TextView) layout.findViewById(R.id.colum2);
        textView3=(TextView) layout.findViewById(R.id.colum3);

        String[] tempAll_Location=list.get(position).getAll_location().split(",");

        int size=tempAll_Location.length;

        double[] x=new double[size];
        double[] y=new double[size];

        for(int i=0;i<size;i++){
            String[] temp=tempAll_Location[i].split("/");
            x[i]=Double.parseDouble(temp[0].trim());
            y[i]=Double.parseDouble(temp[1].trim());
        }
        //사용자의 주소들 스트링에 넣는 작업
        MiddleNearSearch [] middleNearSearch=new MiddleNearSearch[size];
        String people_address="";

        for(int i=0;i<size;i++){
            middleNearSearch[i]=new MiddleNearSearch(x[i],y[i]);

            if(i==size-1){
                people_address+=middleNearSearch[i].getMiddleAddress().toString();
            }else{
                people_address+=middleNearSearch[i].getMiddleAddress().toString()+"\n";
            }
        }

        String[] temp_final=list.get(position).getFinal_address().split(",");

        String final_location=temp_final[0]+"\n"+temp_final[1];
        textView1.setText(list.get(position).getPeopleCount() + "명");
        textView2.setText(people_address);
        textView3.setText(final_location);

        return layout;
    }
}
