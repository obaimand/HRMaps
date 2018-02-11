package heremapsexapmles.hrmaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class TripDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);


        HRDbAdapter hrDbAdapter= new HRDbAdapter(getBaseContext());
        hrDbAdapter.open();

        List<DataHolder> result = hrDbAdapter.getAllNotes();

        ListView listView= findViewById(R.id.listvw);

        ListViewAdapter listViewAdapter=new ListViewAdapter(getBaseContext() ,result);

        listView.setAdapter(listViewAdapter);

    }
}
