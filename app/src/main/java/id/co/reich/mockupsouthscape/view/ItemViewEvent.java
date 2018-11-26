package id.co.reich.mockupsouthscape.view;

import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.model.InfiniteFeedInfo;
import id.co.reich.mockupsouthscape.pojo.Event;

@Layout(R.layout.event_more_item_view)
public class ItemViewEvent {
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

    public ItemViewEvent(Context context, Event event) {
        mContext = context;
        mEvent = event;
    }

    @Resolve
    private void onResolved() {
        tv_event_name.setText(mEvent.getEventName());
        tv_event_type.setText(mEvent.getType_event_name());
        tv_event_place.setText(mEvent.getEventPlace());
        tv_event_time.setText(mEvent.getEventStart() + " - " + mEvent.getEventFinish());
    }
}
