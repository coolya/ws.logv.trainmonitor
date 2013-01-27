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

package ws.logv.trainmonitor.ui.contract;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.01.13
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class MenuItem {
    private int caption;
    private int res;
    private NavigationTarget target;

    public MenuItem(int caption, int res, NavigationTarget target) {
        this.caption = caption;
        this.res = res;
        this.target = target;
    }

    public int getCaption() {
        return caption;
    }

    public int getRes() {
        return res;
    }

    public NavigationTarget getTarget() {
        return target;
    }
}
