package m.i.d.mid;

/**
 * Created by jooyoung on 2016-10-05.
 */

public class ListViewItem {
    private String searchResultName;
    private String searchResultAddress;
    private String searchResultLat;
    private String searchResultLng;

    public String getSearchResultName(){return searchResultName;}
    public String getSearchResultAddress(){return searchResultAddress;}
    public String getSearchResultLat(){return searchResultLat;}
    public String getSearchResultLng(){return searchResultLng;}

    public ListViewItem(String name, String address, String lat, String lng){
        this.searchResultName=name;
        this.searchResultAddress=address;
        this.searchResultLat=lat;
        this.searchResultLng=lng;
    }


}
