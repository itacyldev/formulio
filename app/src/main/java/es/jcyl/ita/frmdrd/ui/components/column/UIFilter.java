package es.jcyl.ita.frmdrd.ui.components.column;

import es.jcyl.ita.crtrepo.query.Operator;
import es.jcyl.ita.frmdrd.el.ValueBindingExpression;

public class UIFilter {

    private final Operator DEFAULT_OPERATOR = Operator.CONTAINS;

    private String filterProperty;
    private ValueBindingExpression filterValueExpression;
    private Operator matchingOperator = DEFAULT_OPERATOR;
    private String orderPropery;

    public String getFilterProperty() {
        return filterProperty;
    }

    public void setFilterProperty(String filterProperty) {
        this.filterProperty = filterProperty;
    }

    public ValueBindingExpression getFilterValueExpression() {
        return filterValueExpression;
    }

    public void setFilterValueExpression(ValueBindingExpression filterValueExpression) {
        this.filterValueExpression = filterValueExpression;
    }

    public Operator getMatchingOperator() {
        return matchingOperator;
    }

    public void setMatchingOperator(Operator matchingOperator) {
        this.matchingOperator = matchingOperator;
    }

    public String getOrderPropery() {
        return orderPropery;
    }

    public void setOrderPropery(String orderPropery) {
        this.orderPropery = orderPropery;
    }
}