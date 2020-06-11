package es.jcyl.ita.frmdrd.config.builders;

import es.jcyl.ita.frmdrd.config.elements.RepoFilter;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;

public class RepoFilterBuilder extends AbstractComponentBuilder<RepoFilter> {
    public RepoFilterBuilder(Class<? extends RepoFilter> clazz) {
        super("repofilter", clazz);
    }

    @Override
    protected void doWithAttribute(RepoFilter element, String name, String value) {

    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoFilter> node) {

    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoFilter> node) {

    }
}
