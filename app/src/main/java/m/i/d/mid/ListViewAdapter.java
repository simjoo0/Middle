package m.i.d.mid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jooyoung on 2016-10-05.
 */

public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ListViewItem> data;
    private int layout;

    public ListViewAdapter(Context context, int layout, ArrayList<ListViewItem> data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).getSearchResultName()+","+data.get(position).getSearchResultLat()+","+data.get(position).getSearchResultLng();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);
        }

        ListViewItem listViewItem=data.get(position);
        TextView searchResultNameTv=(TextView) convertView.findViewById(R.id.searchResultNameTv);
        searchResultNameTv.setText(listViewItem.getSearchResultName());
        TextView searchResultAddressTv=(TextView) convertView.findViewById(R.id.searchResultAddressTv);
        searchResultAddressTv.setText(listViewItem.getSearchResultAddress());
        return convertView;
    }
}
