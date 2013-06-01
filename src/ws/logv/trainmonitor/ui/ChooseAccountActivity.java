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

package ws.logv.trainmonitor.ui;

import android.app.Activity;
import android.os.Bundle;
import ws.logv.trainmonitor.Workflow;
import ws.logv.trainmonitor.event.AccountChoosnEvent;
import ws.logv.trainmonitor.ui.fragments.ChooseAccountFragment;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 25.12.12
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class ChooseAccountActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ChooseAccountActivity that = this;
        new ChooseAccountFragment(new Runnable() {
            @Override
            public void run() {
                Workflow.getEventBus(that).post(new AccountChoosnEvent());
                that.finish();
            }
        }).show(getFragmentManager(),"choose_account");
    }
}