package ws.logv.trainmonitor.data;

import android.content.Context;
import android.os.AsyncTask;

public class DatabaseTask<T> extends AsyncTask<Context, Integer, T> {
	
	private final Func<T, Context> toExcec;
	private final Action<T> callback;

	public DatabaseTask(Func<T, Context> func, Action<T> callback)
	{
		this.toExcec = func;
		this.callback = callback;
	}
	@Override
	protected T doInBackground(Context... params) {
		return toExcec.exec(params[0]);
	}
	
	@Override
	protected void onPostExecute(T result)
	{
		callback.exec(result);
	}

}
