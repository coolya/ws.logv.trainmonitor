package ws.logv.trainminitor;

import ws.logv.trainmonintor.app.Constants;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.api.ApiClient;
import ws.logv.trainmonitor.api.IApiCallback;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class Train extends Activity implements IApiCallback<ws.logv.trainmonitor.model.Train>{

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        
        Intent intent = this.getIntent();
        
        if(intent != null)
        {
        	String trainId = intent.getStringExtra(Constants.IntentsExtra.Train);
            ApiClient client = new ApiClient(this);
            client.getTrainDetail(trainId, this);
            //TODO show wait courser
        }       

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_train, menu);
        return true;
    }

	public void onComplete(ws.logv.trainmonitor.model.Train data) {
		// TODO Auto-generated method stub
		
	}

	public void onError(Throwable tr) {
		Log.e(this.getClass().getName(), "Error getting train details ", tr);
		Toast toast = Toast.makeText(this.getApplicationContext(),R.string.train_details_error, Toast.LENGTH_LONG);
		toast.show();
		this.finish();
	}

	public void onNoConnection() {
		Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
		toast.show();
		this.finish();
	}
}
