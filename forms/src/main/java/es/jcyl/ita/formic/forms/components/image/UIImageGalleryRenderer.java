package es.jcyl.ita.formic.forms.components.image;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.view.render.AbstractGroupRenderer;
import es.jcyl.ita.formic.forms.view.render.renderer.RenderingEnv;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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
public class UIImageGalleryRenderer extends AbstractGroupRenderer<UIImageGallery, ImageGalleryWidget> {

    @Override
    protected int getWidgetLayoutId(UIImageGallery component) {
        return R.layout.widget_imagegallery;
    }

    @Override
    protected void composeWidget(RenderingEnv env, ImageGalleryWidget widget) {
        UIImageGallery component = widget.getComponent();
        FragmentActivity fragmentActivity = (FragmentActivity) env.getAndroidContext();

        GridView gridView = widget.findViewById(R.id.imagegrid_view);

        widget.setGridView(gridView);

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
    public void addViews(RenderingEnv env, Widget<UIImageGallery> root, Widget[] views) {
        GridView gridView = root.findViewById(R.id.imagegrid_view);
        ListAdapter adapter = gridView.getAdapter();



//        int fragCount = 0;
//        for (View view : views) {
//            adapter.addView(view, fragCount);
//            fragCount++;
//        }
//  º
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected boolean isAbleToShowNestedMessages() {
        return true;
    }

//    @Override
//    protected void setNestedMessage(RenderingEnv env, Widget<UITab> widget) {
//        TabLayout tabLayout = widget.findViewById(R.id.tab_layout);
//        // find which tabs has error messages
//        UIComponent[] kids = widget.getComponent().getChildren();
//        if (kids == null) {
//            return;
//        }
//        int pos = 0;
//        for (UIComponent tabItem : kids) {
//            String message = MessageHelper.getMessage(env, tabItem);
//            if (!StringUtils.isBlank(message)) {
//                tabLayout.getTabAt(pos).setIcon(R.drawable.ic_input_error);
//            }
//            pos++;
//        }
//    }

}
