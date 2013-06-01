/*
 * Copyright 2013. Kolja Dummann <k.dummann@gmail.com>
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

package ws.logv.trainmonitor.ui.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import de.greenrobot.event.EventBus;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.event.ui.NavigateToEvent;
import ws.logv.trainmonitor.ui.MenuActivity;
import ws.logv.trainmonitor.ui.adapter.MenuAdapter;
import ws.logv.trainmonitor.ui.contract.MenuItem;
import ws.logv.trainmonitor.ui.contract.NavigationTarget;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 25.01.13
 * Time: 20:30
 * To change this template use File | Settings | File Templates.
 */
public class MenuFragment extends ListFragment {

    private EventBus mBus = EventBus.getDefault();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new MenuAdapter(getActivity(), new MenuItem[]{
                new MenuItem(R.string.menu_fav_trains, R.drawable.check_on_big, NavigationTarget.MY_TRAINS),
                new MenuItem(R.string.menu_all_trains, R.drawable.menu_all_trains, NavigationTarget.ALL_TRAINS),
                new MenuItem(R.string.Settings, R.drawable.tools, NavigationTarget.SETTINGS)
        }));
        // getListView().setCacheColorHint(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ((MenuActivity) getActivity()).getSlideoutHelper().close();
        MenuItem item = (MenuItem) getListAdapter().getItem(position);
        mBus.post(new NavigateToEvent(item.getTarget()));
    }
}
