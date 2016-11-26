package m.i.d.mid;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by jooyoung on 2016-11-25.
 * 인포윈도우 덮는 클래스
 */

public class MapWrapperLayout extends RelativeLayout {

    private GoogleMap map;
    private int bottomOffsetPixels;
    private Marker marker;
    private View infoWindow;


    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = false;

        if (marker != null && marker.isInfoWindowShown() && map != null && infoWindow != null) {

            Point point = map.getProjection().toScreenLocation(marker.getPosition());

            // Make a copy of the MotionEvent and adjust it's location
            // so it is relative to the infoWindow left top corner
            MotionEvent copyEv = MotionEvent.obtain(ev);
            copyEv.offsetLocation(
                    -point.x + (infoWindow.getWidth() / 2),
                    -point.y + infoWindow.getHeight() + bottomOffsetPixels);

            // Dispatch the adjusted MotionEvent to the infoWindow
            ret = infoWindow.dispatchTouchEvent(copyEv);
        }
        // If the infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return ret || super.dispatchTouchEvent(ev);
    }
}
