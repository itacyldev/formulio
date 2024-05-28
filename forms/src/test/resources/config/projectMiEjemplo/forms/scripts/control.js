    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
    var entityOperador;



    function onbefore(component){
        //console.log("component.value " + component.getValue(ctx));
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(0));
    }

    function onafter(widget){
        widget.getInputView().requestLayout();
    }

    function updateFormato(component){
        //console.log("updateFormato");
        //console.log("view.get('dop_id')::: "+view.get("dop_id"));
        if (view.get("dop_id") != null){
            var dop_id = new java.lang.Long(view.get("dop_id"));
            var dopRepo = ctx.get("repos").get("dopRepo");
            //console.log("dop_id::: "+dop_id);
            var entityDop = dopRepo.findById(dop_id);
            //console.log("entityDop.get('formato'): "+entityDop.get("formato"));
            //console.log("entityDop.get('revision'): "+entityDop.get("revision"));
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityDop.get("formato") +" "+ entityDop.get("revision")));
        } else {
            component.setValueExpression(null);
        }
    }