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

public class Constants {

    public class IntentsExtra {
        public static final String NOTIFICATION = "ws.logv.trainmonitor.notification";
    }

    public static class GCM {
        public static final String SENDER_ID = "643069411443";
    }

    public static class Settings {
        public static final String PERF = "trainmonitor";
        public static final String CURRENT_ACCOUNT = "current_account";
        public static final String LICENSE = "about_license";
        public static final String NOTIFICATION_ON = "notify_on";
        public static final String SELECTED_TRAIN_TYPE = "selected_train_type";
    }

    public static class Actions {
        public static final String TRAIN_ACTION = "ws.logv.trainmonitor.SHOWTRAIN:";
    }


}
