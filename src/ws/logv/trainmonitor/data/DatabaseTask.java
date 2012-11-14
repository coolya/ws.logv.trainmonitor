/*
 * Copyright 2012. Kolja Dummann <k.dummann@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
