package id.co.reich.mockupsouthscape;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import id.co.reich.mockupsouthscape.utils.ImagePicker;

public class PaymentDetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ID = 234;

    private Button btn_take_picture;
    private ImageView mImgTransferProofView;
    private EditText mPaymentSessionView;
    private EditText mPaymentTypeView;
    private EditText mSubmitDateView;

    private Bitmap bitmapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        btn_take_picture = findViewById(R.id.btn_take_picture);
        mImgTransferProofView = findViewById(R.id.imgView_proof);
        mPaymentSessionView = findViewById(R.id.edt_payment_session);
        mPaymentTypeView = findViewById(R.id.edt_payment_type);
        mSubmitDateView = findViewById(R.id.edt_payment_submit_date);

        Bundle b = new Bundle();
        b = getIntent().getExtras();
        String payment_name = b.getString("payment_name");
        String payment_session_name = b.getString("payment_session_name");

        mPaymentTypeView.setText(payment_name);
        mPaymentSessionView.setText(payment_session_name);

        btn_take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private AppController app() {
        return AppController.getInstance();
    }

    private void takePicture()
    {
        app().toast("Ambil Gambar dari Kamera atau Gallery");

        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bitmap!=null)
                {
                    mImgTransferProofView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

                    byte[] byteArray = stream.toByteArray();
                    Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    bitmapActivity = compressedBitmap;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
