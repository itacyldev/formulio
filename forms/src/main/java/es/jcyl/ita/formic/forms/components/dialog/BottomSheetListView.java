package es.jcyl.ita.formic.forms.components.dialog;/*
 * Copyright 2020 Rosa María Muñiz (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
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
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class BottomSheetListView extends ListView {

 public BottomSheetListView(Context p_context, AttributeSet p_attrs){
  super(p_context, p_attrs);
 }

 @Override
 public boolean onInterceptTouchEvent(MotionEvent p_event){
  return true;
 }

 @Override
 public boolean onTouchEvent(MotionEvent p_event){
  if (canScrollVertically(this)){
   getParent().requestDisallowInterceptTouchEvent(true);
  }
  return super.onTouchEvent(p_event);
 }

 public boolean canScrollVertically(AbsListView view) {

  boolean canScroll = false;

  if (view != null && view.getChildCount() > 0) {

   boolean isOnTop = view.getFirstVisiblePosition() != 0 || view.getChildAt(0).getTop() != 0;
   boolean isAllItemsVisible = isOnTop && getLastVisiblePosition() == view.getChildCount();

   if(isOnTop || isAllItemsVisible)
    canScroll = true;
  }

  return canScroll;
 }

}