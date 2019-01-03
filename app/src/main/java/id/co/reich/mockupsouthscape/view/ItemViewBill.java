package id.co.reich.mockupsouthscape.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.LongClick;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import id.co.reich.mockupsouthscape.PaymentDetailActivity;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Bill;

@Layout(R.layout.bill_more_item_view)
public class ItemViewBill {

    @View(R.id.txt_bill_session_name)
    private TextView tv_payment_name;

    @View(R.id.txt_bill_type_name)
    private TextView tv_payment_session_name;

    private Bill mBill;
    private Context mContext;

    public ItemViewBill(Context context, Bill bill) {
        mContext = context;
        mBill = bill;
    }

    @Resolve
    private void onResolved() {
        tv_payment_name.setText(mBill.getPaymentName());
        tv_payment_session_name.setText(mBill.getPaymentSessionName());
    }

    @LongClick(R.id.rootView_bill)
    private void onLongClick()
    {
        Log.d(this.getClass().getSimpleName(), mBill.getPaymentName());

        Intent intent=new Intent(mContext, PaymentDetailActivity.class);
        intent.putExtra("payment_name", mBill.getPaymentName());
        intent.putExtra("payment_session_name", mBill.getPaymentSessionName());

        mContext.startActivity(intent);
    }
}
