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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import es.jcyl.ita.formic.forms.R;


/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

 public static List<String> lstResources;

 public static BottomSheetFragment newInstance() {
  BottomSheetFragment fragment = new BottomSheetFragment();
  return fragment;
 }

  @Override
 public void onCreate(@Nullable Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
   setLstResources(getArguments().getStringArrayList("lstResources"));
 }

 @SuppressLint("RestrictedApi")
 @Override
 public void setupDialog(Dialog dialog, int style) {
  View contentView = View.inflate(getContext(), R.layout.bottom_sheet_fragment, null);
  dialog.setContentView(contentView);

 }

 @Nullable
 @Override
 public View onCreateView(
         @NonNull LayoutInflater inflater,
         @Nullable ViewGroup container,
         @Nullable Bundle savedInstanceState) {
  // Observe que o primeiro argumento do inflate é o layout com a ListView que criamos no primeiro passo
  View view = inflater.inflate(R.layout.bottom_sheet_fragment, container, false);

  // Aqui você instancia sua ListView
  ListView listView = (ListView) view.findViewById(R.id.lsViewBtmSheet);

  ArrayAdapter<String> adapter;
  //adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
  adapter=new ArrayAdapter<String>(view.getContext(), R.layout.botton_sheet_item, R.id.bottom_sheet_item_id, getLstResources());
  listView.setAdapter(adapter);

  return view;
 }

 public List<String> getLstResources() {
  return lstResources;
 }

 public void setLstResources(List<String> lstResources) {
  this.lstResources = lstResources;
 }

}