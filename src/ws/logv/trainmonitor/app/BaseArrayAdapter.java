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

package ws.logv.trainmonitor.app;

import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 01.10.12
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class BaseArrayAdapter<T> extends ArrayAdapter<T> {
    public BaseArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public  void addAll(Collection<? extends T> collection)
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
        {
            super.addAll(collection);
        }else
        {
            setNotifyOnChange(false);
            for(T item : collection)
            {
                super.add(item);
            }
            setNotifyOnChange(true);
            notifyDataSetChanged();
        }
    }
}
