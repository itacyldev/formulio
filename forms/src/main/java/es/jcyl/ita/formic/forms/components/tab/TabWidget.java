package es.jcyl.ita.formic.forms.components.tab;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.widget.StatefulWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class TabWidget extends Widget<UITab>
        implements StatefulWidget {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    int positionScrollY = 0;

    public TabWidget(Context context) {
        super(context);
    }

    public TabWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setState(Object value) {
        viewPager.setCurrentItem(((TabState)value).selectedTabPosition, false);
        setPositionScrollY(((TabState)value).scrollY);
    }

    @Override
    public Object getState() {
        TabState tabState = new TabState();
        tabState.setScrollX(viewPager.getRootView().findViewById(R.id.scroll).getScrollX());
        tabState.setScrollY(viewPager.getRootView().findViewById(R.id.scroll).getScrollY());
        tabState.setSelectedTabPosition(this.tabLayout.getSelectedTabPosition());

        return tabState;
    }

    @Override
    public boolean allowsPartialRestore() {
        return this.component.getAllowsPartialRestore();
    }

   public TabLayout getTabLayout() {
        return tabLayout;
    }

    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    public int getPositionScrollY() {
        return positionScrollY;
    }

    public void setPositionScrollY(int positionScrollY) {
        this.positionScrollY = positionScrollY;
    }
}
