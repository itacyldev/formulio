package es.jcyl.ita.frmdrd.ui.components.image;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * without warranties or conditions of any kind, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.crtrepo.Entity;
import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.context.CompositeContext;
import es.jcyl.ita.crtrepo.db.SQLQueryFilter;
import es.jcyl.ita.crtrepo.query.Criteria;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.crtrepo.query.Sort;
import es.jcyl.ita.frmdrd.R;
import es.jcyl.ita.frmdrd.context.ContextUtils;
import es.jcyl.ita.frmdrd.context.impl.AndViewContext;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;
import es.jcyl.ita.frmdrd.el.ValueExpressionFactory;
import es.jcyl.ita.frmdrd.repo.query.ConditionBinding;
import es.jcyl.ita.frmdrd.repo.query.FilterHelper;
import es.jcyl.ita.frmdrd.ui.components.DynamicComponent;
import es.jcyl.ita.frmdrd.ui.components.EntitySelector;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumn;
import es.jcyl.ita.frmdrd.ui.components.column.UIColumnFilter;
import es.jcyl.ita.frmdrd.ui.components.datatable.ListEntityAdapter;
import es.jcyl.ita.frmdrd.ui.components.datatable.UIDatatable;
import es.jcyl.ita.frmdrd.util.DataUtils;
import es.jcyl.ita.frmdrd.view.converters.TextViewConverter;
import es.jcyl.ita.frmdrd.view.render.RenderingEnv;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ImageWidget extends LinearLayout  {


    public ImageWidget(Context context) {
        super(context);
    }

    public ImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
