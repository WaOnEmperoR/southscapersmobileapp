package id.co.reich.mockupsouthscape.view;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Event;

@Layout(R.layout.event_ahead_more_item_view)
public class ItemViewEventAhead {
    @View(R.id.txt_event_title)
    private TextView tv_event_name;

    @View(R.id.txt_event_type)
    private TextView tv_event_type;

    @View(R.id.txt_time_event)
    private TextView tv_event_time;

    @View(R.id.txt_place_event)
    private TextView tv_event_place;

    private Event mEvent;
    private Context mContext;

    public ItemViewEventAhead(Context context, Event event) {
        mContext = context;
        mEvent = event;
    }

    @Resolve
    private void onResolved() {
        SimpleDateFormat preFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        tv_event_name.setText(mEvent.getEventName());
        tv_event_type.setText(mEvent.getType_event_name());
        tv_event_place.setText(mEvent.getEventPlace());

        try {
            Date date_start=preFormatter.parse(mEvent.getEventStart());
            Date date_finish=preFormatter.parse(mEvent.getEventFinish());

            String start_date = postFormatter.format(date_start);
            String finish_date = postFormatter.format(date_finish);

            tv_event_time.setText(start_date + " s.d. " + finish_date);
        } catch (ParseException e) {
            Log.e("ItemView", e.getMessage());
        }
    }
}
