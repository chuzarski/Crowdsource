/**
 *
 * Copyright 2014 Cody Huzarski (chuzarski.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *
 */

package net.chuzarski.crowdednews.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.chuzarski.crowdednews.R;
import net.chuzarski.crowdednews.utils.reddit.RedditPost;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


public class PostsAdapter extends BaseAdapter {

    private Context appContext;

    private List<RedditPost> posts;

    private DateFormat df;

    public PostsAdapter(Context context) {
        this.appContext = context;
        posts = new ArrayList<RedditPost>();
        this.df = DateFormat.getDateInstance();

    }

    public List<RedditPost> getPosts() {
        return posts;
    }

    public void addAll(List<RedditPost> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }


    public void clearAllPosts() {
        posts.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public RedditPost getItem(int position) {
        return posts.get(position);
    }


    /**
     * Highly useless and will ALWAYS return 0
     * @param position
     * @return 0
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.main_list_view, parent, false);
        } else {
            rowView = convertView;
        }

        TextView title = (TextView) rowView.findViewById(R.id.titleText);
        TextView source = (TextView) rowView.findViewById(R.id.article_source);
        TextView date = (TextView) rowView.findViewById(R.id.article_date);

        title.setText(getItem(position).getTitle());
        source.setText(getItem(position).getLinkDomain());
        date.setText(" - " + df.format(getItem(position).getCreatedDate()));
        return rowView;
    }




    private Context getContext() {
        return this.appContext;
    }

}
