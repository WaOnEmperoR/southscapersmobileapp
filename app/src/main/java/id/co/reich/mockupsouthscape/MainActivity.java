package id.co.reich.mockupsouthscape;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    GridView androidGridView;

    Integer[] imageIDs = {
            R.drawable.ic_icons8_home,
            R.drawable.ic_if_money_322468,
            R.drawable.ic_if_store_326704,
            R.drawable.ic_if_calendar_115762
    };

    String[] imageNames = {
            "Rumah",
            "Keuangan",
            "Layanan",
            "Agenda"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        androidGridView = findViewById(R.id.gridview_android_example);
        androidGridView.setAdapter(new ImageAdapterGridView(this));

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();

                switch(position){
                    case 3 :
                        Intent intent = new Intent(MainActivity.this,PlaceholderActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageIDs.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gridViewAndroid;

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                gridViewAndroid = inflater.inflate(R.layout.gridview_single_item, null);
                TextView textViewAndroid = gridViewAndroid.findViewById(R.id.android_gridview_text);
                ImageView imageViewAndroid = gridViewAndroid.findViewById(R.id.android_gridview_image);
                textViewAndroid.setText(imageNames[position]);
                imageViewAndroid.setImageResource(imageIDs[position]);

            } else {
                gridViewAndroid = convertView;
            }
            return gridViewAndroid;
        }
    }
}
