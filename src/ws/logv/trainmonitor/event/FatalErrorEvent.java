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
 * Date: 27.12.12
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class FatalErrorEvent {
    private Exception e;
    private int resId;
    private String message;

    public FatalErrorEvent(Exception e)
    {
        this.e = e;
    }

    public FatalErrorEvent(Exception e, int resId)
    {
        this.e = e;
        this.resId = resId;
    }

    public FatalErrorEvent(Exception e, String message)
    {
        this.e = e;
        this.message = message;
    }

    public Exception getException() {
        return e;
    }

    public String getMessage() {
        return message;
    }

    public int getResId() {
        return resId;
    }
}
