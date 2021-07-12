package es.jcyl.ita.formic.forms.components.tab;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.render.renderer.MessageHelper;

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
public class UITabRenderer extends AbstractGroupRenderer<UITab, TabWidget> {

    @Override
    protected int getWidgetLayoutId(UITab component) {
        return R.layout.widget_tab;
    }

    @Override
    protected void composeWidget(RenderingEnv env, TabWidget widget) {
        UITab component = widget.getComponent();
        FragmentActivity fragmentActivity = (FragmentActivity) env.getAndroidContext();

        TabLayout tabLayout = widget.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = widget.findViewById(R.id.viewPager);

        widget.setTabLayout(tabLayout);
        widget.setViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentActivity);
        viewPager.setAdapter(viewPagerAdapter);

        /*viewPager.post(new Runnable(){
            @Override
            public void run(){
                int currentItem = getCurrentItem(env, component);
                viewPager.setCurrentItem(currentItem, false);
            }
        });*/

        viewPagerAdapter.notifyDataSetChanged();

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

        viewPager.getParent().requestChildFocus(viewPager, viewPager);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPagerAdapter.getTabFragments().size() > position) {
                            TabFragment tabFragment = viewPagerAdapter.getTabFragments().get(position);
                            updatePagerHeightForChild(tabFragment.getTabView(), viewPager);
                            viewPager.requestLayout();
                            viewPager.getRootView().findViewById(R.id.scroll).setScrollY(widget.getPositionScrollY());
                        }
                    }
                });
            }
        });

        ViewTreeObserver vto = viewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        TabFragment tabFragment = viewPagerAdapter.getTabFragments().get(viewPager.getCurrentItem());
                        updatePagerHeightForChild(tabFragment.getTabView(), viewPager);
                        viewPager.requestLayout();
                    }
                });
            }
        });
    }

    private void updatePagerHeightForChild(View view, ViewPager2 viewPager) {
        int wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
        int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(wMeasureSpec, hMeasureSpec);

        if (viewPager.getLayoutParams().height != view.getMeasuredHeight()) {
            ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
            lp.height = view.getMeasuredHeight();
        }
    }

    @Override
    public void addViews(RenderingEnv env, Widget<UITab> root, Widget[] views) {
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();

        int fragCount = 0;
        for (View view : views) {
            adapter.addView(view, fragCount);
            fragCount++;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setNestedMessage(RenderingEnv env, Widget<UITab> widget) {
        TabLayout tabLayout = widget.findViewById(R.id.tab_layout);
        // find which tabs has error messages
        UIComponent[] kids = widget.getComponent().getChildren();
        if (kids == null) {
            return;
        }
        int pos = 0;
        for (UIComponent tabItem : kids) {
            String message = MessageHelper.getMessage(env, tabItem);
            if (!StringUtils.isBlank(message)) {
                tabLayout.getTabAt(pos).setIcon(R.drawable.ic_input_error);
            }
            pos++;
        }
    }

    private int getCurrentItem(RenderingEnv env, UITab component) {
        int currentItem = -1;
        boolean isSelected = false;
        for (int i = 0; i < component.getChildren().length && !isSelected; i++) {
            UITabItem tabItem = (UITabItem) component.getChildren()[i];
            isSelected = tabItem.isSelected(env.getWidgetContext());
            currentItem++;
        }
        return isSelected ? currentItem : 0;
    }

}
