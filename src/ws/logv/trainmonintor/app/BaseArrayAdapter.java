package ws.logv.trainmonintor.app;

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
