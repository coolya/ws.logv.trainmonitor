package ws.logv.trainmonitor.data;

import android.content.Context;
import android.os.AsyncTask;

public class DatabaseTask<T> extends AsyncTask<Context, Integer, T> {
	
	private final Func<T, Context> toExcec;
    private final Func2<T, Context, Action<Integer>> toExcec2;
	private final Action<T> callback;
    private final Action<Integer> progress;

	public DatabaseTask(Func<T, Context> func, Action<T> callback)
	{
		this.toExcec = func;
        this.toExcec2 = null;
		this.callback = callback;
        this.progress = null;
	}
    public DatabaseTask(Func2<T, Context, Action<Integer>> func, Action<T> callback, Action<Integer> progress)
    {
        this.toExcec2 = func;
        this.toExcec = null;
        this.callback = callback;
        this.progress = progress;
    }
	@Override
	protected T doInBackground(Context... params) {
        if(toExcec != null)
		    return toExcec.exec(params[0]);
        else
            return toExcec2.exec(params[0], progress);
	}
	
	@Override
	protected void onPostExecute(T result)
	{
        if(callback != null)
		   callback.exec(result);
	}

}
