package m.i.d.mid;

/**
 * Created by jooyoung on 2016-11-12.
 */

public class PurposeNearListItem {
    String name;
    String address;
    Double lat;
    Double lng;

    public PurposeNearListItem(String name, String address, Double lat, Double lng) {
        this.name = name;
        this.address=address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
