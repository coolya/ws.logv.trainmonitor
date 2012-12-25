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

package ws.logv.trainmonitor.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.gms.auth.GoogleAuthUtil;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.app.Constants;
import ws.logv.trainmonitor.ui.contract.GenericDialogFragment;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 09.12.12
 * Time: 14:20
 * To change this template use File | Settings | File Templates.
 */
public class ChooseAccountFragment extends GenericDialogFragment {
    private String[] mNames;
    private String mSelectedName;
    private Runnable mOnOk;

    public ChooseAccountFragment(Runnable onOk) {
        super(R.string.accept, R.string.cancel);
        this.mOnOk = onOk;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = createBuilder();
        builder.setTitle(R.string.choose_account_title);
        AccountManager mng = AccountManager.get(getActivity());
        Account[] accounts = mng.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        mNames = new String[accounts.length];
        int size = accounts.length;

       for (int i = 0; i < size; i++)
       {
           mNames[i] = accounts[i].name;
       }
        builder.setSingleChoiceItems(mNames, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedName = mNames[i];
            }
        });
        return builder.create();
    }

    @Override
    protected void onOK() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit =  settings.edit();
        edit.putString(Constants.Settings.CURRENT_ACCOUNT, mSelectedName);
        edit.commit();
        mOnOk.run();
    }

    @Override
    protected void onCancel() {
        mOnOk.run();
    }
}