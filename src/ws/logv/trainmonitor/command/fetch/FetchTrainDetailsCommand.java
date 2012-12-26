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

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class FetchTrainDetailsCommand {
    private String train;
    private Object tag;

    public FetchTrainDetailsCommand(String train)
    {
        this.train = train;
    }
    public FetchTrainDetailsCommand(String train, Object tag)
    {
        this.train = train;
        this.tag = tag;
    }

    public String getTrain() {
        return train;
    }

    public Object getTag() {
        return tag;
    }
}
