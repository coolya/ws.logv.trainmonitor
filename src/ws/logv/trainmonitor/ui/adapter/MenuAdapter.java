/*
 * Copyright 2013. Kolja Dummann <k.dummann@gmail.com>
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ws.logv.trainmonitor.R;
import ws.logv.trainmonitor.ui.contract.MenuItem;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: kdummann
 * Date: 26.01.13
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class MenuAdapter extends BaseArrayAdapter<MenuItem> {
    public MenuAdapter(Context context, MenuItem[] objects) {
        super(context, 0, Arrays.asList(objects));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) super.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_menu, parent, false);
        MenuItem item = this.getItem(position);
        ImageView image = (ImageView) rowView.findViewById(R.id.menu_item_image);
        TextView tv = (TextView) rowView.findViewById(R.id.menu_item_caption);

        image.setImageResource(item.getRes());
        tv.setText(super.getContext().getString(item.getCaption()));
        return rowView;
    }
}
