package heremapsexapmles.hrmaps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Sachin on 2018-02-07.
 */

public  class ListViewAdapter extends ArrayAdapter<DataHolder> {


    public ListViewAdapter(@NonNull Context context, List<DataHolder> titles) {
        super(context, 0, titles);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem= convertView;
        if(listItem == null){
            listItem= LayoutInflater.from(getContext()).inflate(R.layout.trip_details_items, parent, false);
        }

        DataHolder dataHolder = getItem(position);

        TextView tv1= (TextView)  listItem.findViewById(R.id.tv1);
        TextView tv2= (TextView) listItem.findViewById(R.id.tv2);
        TextView tv3= (TextView) listItem.findViewById(R.id.tv3);

        tv1.setText( dataHolder.getTitle() ) ;
        tv2.setText( dataHolder.getLanes() ) ;
        tv3.setText( dataHolder.getTime() ) ;


        return  listItem;
    }
}
