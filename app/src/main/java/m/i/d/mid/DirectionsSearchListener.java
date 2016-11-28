package m.i.d.mid;

import java.util.List;

/**
 * Created by jooyoung on 2016-11-28.
 */

public interface DirectionsSearchListener {
    void onDirectionsSearchStart();
    void onDirectionsSearchSuccess(List<Route> route);
}
