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

package ws.logv.trainmonitor.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.korovyansk.android.slideout.SlideoutHelper;
import ws.logv.trainmonitor.ui.fragments.MenuFragment;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 25.01.13
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class MenuActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
        mSlideoutHelper = new SlideoutHelper(this);
        mSlideoutHelper.activate();

        getFragmentManager().beginTransaction().add(com.korovyansk.android.slideout.R.id.slideout_placeholder,
                new MenuFragment(), "menu").commit();
        mSlideoutHelper.open();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mSlideoutHelper.close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public SlideoutHelper getSlideoutHelper() {
        return mSlideoutHelper;
    }

    private SlideoutHelper mSlideoutHelper;
}