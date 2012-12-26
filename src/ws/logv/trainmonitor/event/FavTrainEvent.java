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

package ws.logv.trainmonitor.event;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.12.12
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class FavTrainEvent {
    private String train;
    private boolean fav = true;
    public FavTrainEvent(String train)
    {
        this.train = train;
    }

    public FavTrainEvent(String train, boolean fav)
    {
        this(train);
        this.fav = fav;
    }

    public  String getTrain()
    {
        return  train;
    }

    public boolean isFav()
    {
        return fav;
    }
}