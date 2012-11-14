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

package ws.logv.trainmonitor.model;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created with IntelliJ IDEA.
 * User: Kolja
 * Date: 9/29/12
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class FavouriteTrain {
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(canBeNull = false, unique = true, uniqueIndex = true)
    private String trainId;

    public void setId(Integer id)
    {
        this.id = id;
    }
    public void setTrainId(String trainId)
    {
        this.trainId = trainId;
    }

    public String getTrainId()
    {
        return this.trainId;
    }

    public Integer getId()
    {
        return id;
    }


}
