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

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Device {
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGcmRegId() {
        return gcmRegId;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    @SerializedName("id")private UUID id;
    @SerializedName("provider_handle")private String gcmRegId;

    public String toString()
    {
        return id.toString() + ";" + gcmRegId;
    }

    public static Device fromString(String value)
    {
        String[] parts = value.split(";");

        Device ret = new Device();
        ret.setId(UUID.fromString(parts[0]));
        if(parts.length > 1)
            ret.setGcmRegId(parts[1]);

        return ret;
    }
}
