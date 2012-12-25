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

package ws.logv.trainmonitor.ui.adapter;

import android.accounts.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ws.logv.trainmonitor.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 09.12.12
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
public class AccountAdapter extends BaseArrayAdapter<Account> {

    LayoutInflater mInflate = null;

    public AccountAdapter(Context context, int textViewResourceId, List<Account> objects) {
        super(context, textViewResourceId, objects);
        mInflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflate.inflate(R.layout.account_item, parent, false);
        Account account = this.getItem(position);
        TextView tvName = (TextView)rowView.findViewById(R.id.name);
        tvName.setText(account.name);
        return  rowView;
    }
}
