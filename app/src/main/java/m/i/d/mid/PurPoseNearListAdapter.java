package m.i.d.mid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jooyoung on 2016-11-12.
 */

public class PurposeNearListAdapter extends BaseAdapter {
    ArrayList<PurposeNearListItem> datas;
    LayoutInflater inflater;

    public PurposeNearListAdapter(ArrayList<PurposeNearListItem> datas, LayoutInflater inflater) {
        this.datas = datas;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            //null이라면 재활용할 View가 없으므로 새로운 객체 생성

            //View의 모양은 res폴더>>layout폴더>>list.xml 파일로 객체를 생성

            //xml로 선언된 레이아웃(layout)파일을 객체로 부풀려주는 LayoutInflater 객체 활용.

            convertView = inflater.inflate(R.layout.purpose_listview_item, null);
            TextView purposeName = (TextView) convertView.findViewById(R.id.purposeListItemTv);
            purposeName.setText(datas.get(position).getName());
        }
        return convertView;
    }
}
