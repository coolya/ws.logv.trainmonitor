package ws.logv.trainmonintor.app;

import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.model.Train;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class TrainAdapter extends ArrayAdapter<Train>{

	private Context ctx;
	public TrainAdapter(Context context, int textViewResourceId, Train[] objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View rowView = inflater.inflate(R.layout.all_trains_item, parent, false);
		    Train item = this.getItem(position);
		    
		    return rowView;
	}
	
	
	
	

}
