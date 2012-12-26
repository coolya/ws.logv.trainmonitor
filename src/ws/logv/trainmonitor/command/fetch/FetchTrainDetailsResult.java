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

package ws.logv.trainmonitor.command.fetch;

import ws.logv.trainmonitor.command.BaseResult;
import ws.logv.trainmonitor.model.Train;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class FetchTrainDetailsResult extends BaseResult {
    private Train train;
    private Object tag;

    public FetchTrainDetailsResult(Train train)
    {
        this.train = train;
    }

    public FetchTrainDetailsResult(Train train, Object tag)
    {
        this.train = train;
        this.tag = tag;
    }

    public FetchTrainDetailsResult(Exception e)
    {
        super(e);
    }

    public FetchTrainDetailsResult(Exception e, Object tag)
    {
        super(e);
        this.tag = tag;
    }

    public Train getTrain() {
        return train;
    }

    public Object getTag() {
        return tag;
    }
}
