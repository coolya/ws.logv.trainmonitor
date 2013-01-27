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

package ws.logv.trainmonitor.event.ui;

import com.actionbarsherlock.app.ActionBar;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class SetUpActionBarEvent {
    private boolean enableSearch;
    private boolean enableRefresh;
    private String[] dropDownItems;
    private ActionBar.OnNavigationListener navigationListener;

    public SetUpActionBarEvent(boolean enableSearch, boolean enableRefresh) {
        this.enableSearch = enableSearch;
        this.enableRefresh = enableRefresh;
        this.dropDownItems = null;
    }

    public SetUpActionBarEvent(boolean enableSearch, boolean enableRefresh, String[] dropDownItems, ActionBar.OnNavigationListener navigationListener) {
        this.enableSearch = enableSearch;
        this.enableRefresh = enableRefresh;
        this.dropDownItems = dropDownItems;
        this.navigationListener = navigationListener;
    }

    public boolean isSearchEnabled() {
        return enableSearch;
    }

    public boolean isRefreshEnabled() {
        return enableRefresh;
    }

    public String[] getDropDownItems() {
        return this.dropDownItems;
    }

    public ActionBar.OnNavigationListener getNavigationListener() {
        return navigationListener;
    }
}
