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

package ws.logv.trainmonitor.ui.contract;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import ws.logv.trainmonitor.R;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 09.12.12
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericDialogFragment extends DialogFragment {
    private int okId;
    private int cancelId = 0;
    public  GenericDialogFragment(int okId)
    {
        super();
        this.okId = okId;
    }
    public  GenericDialogFragment(int okId, int cancelId)
    {
        super();
        this.okId = okId;
        this.cancelId = cancelId;
    }

    protected AlertDialog.Builder createBuilder()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(okId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onOK();
            }
        });

        if(cancelId != 0)
        {
            builder.setNegativeButton(cancelId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onCancel();
                }
            });
        }
        return builder;
    }

    protected abstract void onOK();
    protected abstract void onCancel();
}
