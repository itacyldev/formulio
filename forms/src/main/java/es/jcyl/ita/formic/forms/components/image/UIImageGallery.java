package es.jcyl.ita.formic.forms.components.image;

import es.jcyl.ita.formic.forms.components.AbstractUIComponent;
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverter;
import es.jcyl.ita.formic.forms.view.converters.ViewValueConverterFactory;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

public class UIImageGallery extends AbstractUIComponent implements FilterableComponent {
    private static final ViewValueConverterFactory viewConverterFactory = ViewValueConverterFactory.getInstance();

    private String valueConverter;
    private Repository repo;

    private int numItems;

    public UIImageGallery() {
        rendererType ="imagegallery";
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

    }

    @Override
    public Filter getFilter() {
        return null;
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
}
