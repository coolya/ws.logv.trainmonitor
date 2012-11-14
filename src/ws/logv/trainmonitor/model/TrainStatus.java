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

public enum TrainStatus {
    E("E") ,
    F("F") ,
    S("S"),
    X("X"),
    N("N");
    
    private String value;
    private TrainStatus(String value)
    {
    	this.value = value;
    }
    
    public static TrainStatus findByAbbr(String value)
    {
    	for (TrainStatus c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }
    }
