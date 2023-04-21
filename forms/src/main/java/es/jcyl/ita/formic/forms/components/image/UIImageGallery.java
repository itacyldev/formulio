package es.jcyl.ita.formic.forms.components.image;

import java.util.Set;

import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.components.EntitySelectorComponent;
import es.jcyl.ita.formic.forms.components.ExpressionHelper;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverterFactory;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

public class UIImageGallery extends AbstractUIComponent implements FilterableComponent, EntitySelectorComponent {

    private static final ViewValueConverterFactory viewConverterFactory = ViewValueConverterFactory.getInstance();

    private String valueConverter;
    private Repository repo;
    private Filter filter;
    private String route;

    private Integer columns;

    private int numItems;

    public UIImageGallery() {
        rendererType = "imagegallery";
        renderChildren = true;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public Repository getRepo() {
        return repo;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public String[] getMandatoryFilters() {
        return new String[0];
    }

    @Override
    public void setMandatoryFilters(String[] mandatoryFilters) {

    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public String getValueConverter() {
        return valueConverter;
    }

    public ViewValueConverter getConverter() {
        return viewConverterFactory.get(this.getValueConverter());
    }

    public void setValueConverter(String valueConverter) {
        this.valueConverter = valueConverter;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }


    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    @Override
    public Set<ValueBindingExpression> getValueBindingExpressions() {
        Set<ValueBindingExpression> expressions = super.getValueBindingExpressions();
        // If repo filter is defined, add binding expressions to establish dependencies
        if (this.filter != null) {
            expressions.addAll(ExpressionHelper.getExpressions(this.filter.getExpression()));
        }
        return expressions;
    }
}
