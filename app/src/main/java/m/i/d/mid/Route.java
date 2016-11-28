package m.i.d.mid;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by jooyoung on 2016-11-28.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;

}
