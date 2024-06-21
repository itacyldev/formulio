    var partidaRepo = null;
    var entityPartida = null;
    var solicitudRepo = null;
    var entitySolicitud = null;
    var operadorRepo = null;
    var entityOperador = null;
    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

    function saveOperador(){
        //console.log("saveOperador");
        //console.log("view.get('operador_id')::: "+view.get("operador_id"));
        //console.log("view.get('solicitud_id')::: "+view.get("solicitud_id"));

        solicitudRepo = ctx.get("repos").get("solicitudRepo");
        entitySolicitud = solicitudRepo.findById(view.get("solicitud_id"));
        entitySolicitud.set("operador_id", view.get("operador_id"));
        solicitudRepo.save(entitySolicitud);
    }

    function setEntityOperador(){
        //console.log("setEntityOperador");
        if (view.get("operador_id") != null){
            operadorRepo = ctx.get("repos").get("operadorRepo");
            entityOperador = operadorRepo.findById(view.get("operador_id"));
        }
    }

    function updateBodega(component){
        //console.log("updateBodega");
        if (view.get("operador_id") != null){
            //console.log("view.get('operador_id')::: "+view.get("operador_id"));
            setEntityOperador();
            //console.log("entityOperador.get('bodega')::: "+entityOperador.get("bodega"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(entityOperador.get("bodega")));
        } else {
            //console.log("view.get('operador_id') == null");
            component.setValueExpression(null);
        }
    }

    function updateRepresentante(component){
        //console.log("updateRepresentante");
        if (view.get("operador_id") != null){
            //console.log("view.get('operador_id')::: "+view.get("operador_id"));
            setEntityOperador();
            //console.log("entityOperador.get('representante')::: "+entityOperador.get("representante"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(entityOperador.get("representante")));
        }else{
            //console.log("view.get('operador_id') == null");
            component.setValueExpression(null);
        }
    }

    function updateCif(component){
        //console.log("updateCif");
        if (view.get("operador_id") != null){
            //console.log("view.get('operador_id')::: "+view.get("operador_id"));
            setEntityOperador();
            //console.log("entityOperador.get('cif')::: "+entityOperador.get("cif"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(entityOperador.get("cif")));
         }else{
            //console.log("view.get('operador_id') == null");
            component.setValueExpression(null);
        }
    }

    function updateDniRepresentante(component){
        //console.log("updateDniRepresentante");
        if (view.get("operador_id") != null){
            //console.log("view.get('operador_id')::: "+view.get("operador_id"));
            setEntityOperador();
            //console.log("entityOperador.get('dni_representante')::: "+entityOperador.get("dni_representante"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(entityOperador.get("dni_representante")));
         }else{
            //console.log("view.get('operador_id') == null");
            component.setValueExpression(null);
        }
    }