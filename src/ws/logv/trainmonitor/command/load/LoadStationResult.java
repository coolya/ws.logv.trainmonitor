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

package ws.logv.trainmonitor.command.load;

import ws.logv.trainmonitor.command.BaseResult;
import ws.logv.trainmonitor.model.Station;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class LoadStationResult extends BaseResult {
    private Station result;
    private Object tag;

    public LoadStationResult(Station result)
    {

        this.result = result;
    }

    public LoadStationResult(Station result, Object tag)
    {
        this.result = result;
        this.tag = tag;
    }

    public LoadStationResult(Exception e)
    {
        super(e);
    }

    public LoadStationResult(Exception e, Object tag)
    {
        super(e);
        this.tag = tag;
    }

    public Station getResult() {
        return result;
    }

    public Object getTag() {
        return tag;
    }
}
