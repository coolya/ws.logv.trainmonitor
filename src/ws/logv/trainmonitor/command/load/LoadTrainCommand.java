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

import ws.logv.trainmonitor.data.TrainType;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 00:56
 * To change this template use File | Settings | File Templates.
 */
public class LoadTrainCommand {
    private String query;
    private long count;
    private long start;
    private TrainType type;
    private boolean hasType;

    public LoadTrainCommand() {

    }

    public LoadTrainCommand(String query) {
        this.query = query;
    }

    public LoadTrainCommand(long count, long start) {
        this.count = count;
        this.start = start;
    }

    public LoadTrainCommand(long count, int start, TrainType type) {
        this.count = count;
        this.start = start;
        this.type = type;
        this.hasType = true;
    }

    public String getQuery() {
        return query;
    }

    public long getCount() {
        return count;
    }

    public long getStart() {
        return start;
    }

    public TrainType getType() {
        return type;
    }

    public boolean hasType() {
        return hasType;
    }
}
