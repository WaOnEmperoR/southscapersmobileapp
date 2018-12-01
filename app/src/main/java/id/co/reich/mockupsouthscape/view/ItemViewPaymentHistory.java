package id.co.reich.mockupsouthscape.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Payment;

@Layout(R.layout.payment_history_more_item_view)
public class ItemViewPaymentHistory {
    @View(R.id.txt_payment_session)
    private TextView tv_payment_session;

    @View(R.id.txt_payment_type)
    private TextView tv_payment_type;

    @View(R.id.txt_submit_date)
    private TextView tv_submit_date;

    @View(R.id.txt_verification_status)
    private TextView tv_verification_status;

    @View(R.id.txt_verified_date)
    private TextView tv_verified_date;

    @View(R.id.txt_rejection_cause)
    private TextView tv_rejection_cause;

    @View(R.id.imgView_verification_status)
    private ImageView img_verification_status;

    private Payment mPayment;
    private Context mContext;

    public ItemViewPaymentHistory(Context context, Payment payment) {
        mContext = context;
        mPayment = payment;
    }

    @Resolve
    private void onResolved() {
        SimpleDateFormat preFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat postFormatter = new SimpleDateFormat("dd MMMM yyyy HH:mm");

        tv_payment_type.setText(mPayment.getPaymentType());
        tv_payment_session.setText(mPayment.getPaymentSession());

        try {
            Date date_submitted = preFormatter.parse(mPayment.getPaymentSubmitted());
            String submitted_date = postFormatter.format(date_submitted);

            tv_submit_date.setText(submitted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // It means that the payment is already verified (either accepted or rejected)
        if (!mPayment.getPaymentVerified().equals(""))
        {
            try {
                Date date_verified = preFormatter.parse(mPayment.getPaymentVerified());
                String verified_date = postFormatter.format(date_verified);

                tv_verified_date.setText(verified_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // If the rejection field is null then it is accepted. Otherwise rejected
            if (!mPayment.getRejectionCause().equals(""))
            {
                img_verification_status.setImageResource(R.drawable.ic_check_outline);
                tv_verification_status.setText("Pembayaran Diterima");
            }
            else
            {
                img_verification_status.setImageResource(R.drawable.ic_error_outline);
                tv_verification_status.setText("Pembayaran Ditolak");

                tv_rejection_cause.setText(mPayment.getRejectionCause());
            }
        }
        // Payment not yet verified
        else
        {
            tv_verification_status.setText("Menunggu Verifikasi Bendahara");
        }


    }

}
