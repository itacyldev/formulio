package es.jcyl.ita.formic.forms.components.tab;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */
public class TabRenderer extends AbstractGroupRenderer<UITab, Widget<UITab>> {

    @Override
    protected int getWidgetLayoutId() {
        return R.layout.widget_tab;
    }

    @Override
    protected void composeWidget(RenderingEnv env, Widget<UITab> widget) {
        UITab component = widget.getComponent();
        FragmentActivity fragmentActivity = (FragmentActivity) env.getViewContext();

        TabLayout tabLayout = widget.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = widget.findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentActivity);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayoutMediator.TabConfigurationStrategy strategy = new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                UIComponent[] children = component.getChildren();
                UITabItem tabItem = (UITabItem) children[position];
                tab.setText(tabItem.getLabel());
            }
        };

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, strategy);
        mediator.attach();
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UITab> root, View[] views) {
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();

        int fragCount = 0;
        for (View view : views) {
            adapter.addView(view, fragCount);

            fragCount++;
        }
    }

}
