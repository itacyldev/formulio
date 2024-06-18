    var partidaRepo = null;
    var entityPartida = null;
    var solicitudRepo = null;
    var entitySolicitud = null;
    var operadorRepo = null;
    var entityOperador = null;
    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

    function setEntityOperador(){
        //console.log("setEntityOperador");
        if (view.get("partida_id") != null){
            setEntityPartida();

            solicitudRepo = ctx.get("repos").get("solicitudRepo");
            entitySolicitud = solicitudRepo.findById(entityPartida.get("solicitud_id"));
            //console.log("operador_id: "+entitySolicitud.get("operador_id"));

            operadorRepo = ctx.get("repos").get("operadorRepo");
            entityOperador = operadorRepo.findById(entitySolicitud.get("operador_id"));
            //console.log("bodega: "+entityOperador.get("bodega"));
        }
    }

    function setEntityPartida(){
        //console.log("setEntityPartida");
        if (view.get("partida_id") != null){
            var partida_id = new java.lang.Long(view.get("partida_id"));
            //console.log("partida_id: "+partida_id);

            partidaRepo = ctx.get("repos").get("partidaRepo");
            entityPartida = partidaRepo.findById(partida_id);
            //console.log("solicitud_id: "+entityPartida.get("solicitud_id"));
        }
    }

    function updateBodega(component){
        //console.log("updateBodega");
        //console.log("view.get('partida_id')::: "+view.get("partida_id"));
        if (view.get("partida_id") != null){
            setEntityOperador();
            //console.log("entityOperador.get('cif'): "+entityOperador.get("cif"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("bodega") +" "+entityOperador.get("cif")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateRepresentante(component){
        //console.log("updateRepresentante");
        if (view.get("partida_id") != null){
            setEntityOperador();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("representante") +" "+entityOperador.get("dni_representante")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updatePartida(component){
        //console.log("updatePartida");
        if (view.get("partida_id") != null){
            setEntityPartida();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityPartida.get("partida")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateTipoVino(component){
        //console.log("updateTipoVino");
        if (view.get("partida_id") != null){
            setEntityPartida();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityPartida.get("tipo_vino")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateLitros(component){
        //console.log("updateLitros");
        if (view.get("partida_id") != null){
            setEntityPartida();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityPartida.get("litros")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateNumEnvasesYCapacidad(component){
        //console.log("updateNumEnvasesYCapacidad");
        if (view.get("partida_id") != null){
            setEntityPartida();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityPartida.get("numero_envases")+" bot "+entityPartida.get("capacidad_envase")));
        } else {
            component.setValueExpression(null);
        }
    }

    function anadirEntradaUva(){
        //console.log("anadirEntradaUva")
        if (view.get("acta_id") != null){
            var entradaUvaRepo = ctx.get("repos").get("entradaUvaRepo");
            var entity = entradaUvaRepo.newEntity();
            entity.set("acta_id", view.get("acta_id"));
            entradaUvaRepo.save(entity);
        }
    }

    function setKilogramosTotales(){
        //console.log("setKilogramosTotales");
        var acta_id_view = view.get("acta_id");
        if (acta_id_view != null){
            var entradaUvaRepo = ctx.get("repos").get("entradaUvaRepo");
            var lstEntradaUva = entradaUvaRepo.listAll()

                /*var filterRepoUtils = Packages.es.jcyl.ita.formic.repo.query.FilterRepoUtils;
                var filter = Packages.es.jcyl.ita.formic.repo.query.Filter;
                filter = filterRepoUtils.createInstance(entradaUvaRepo);

                var cond = new Packages.es.jcyl.ita.formic.repo.query.Condition("acta_id", "eq", (java.lang.Object)acta_id);
                filter.setExpression(Packages.es.jcyl.ita.formic.repo.query.Criteria.single(cond));
                var lstEntradaUva = entradaUvaRepo.find(filter);*/

            var kilogramos_totales = 0;
            var i = 0;
            while(i != lstEntradaUva.size()){
                //console.log("i: " + i);
                var entityEntradaUva = lstEntradaUva.get(i);
                var acta_id_bbdd = new java.lang.Long(entityEntradaUva.get("acta_id"))
                if (acta_id_bbdd == acta_id_view){
                    var cantidad_kg = new java.lang.Double(entityEntradaUva.get("cantidad_kg"));
                    //console.log("cantidad_kg:: "+cantidad_kg);
                    kilogramos_totales = kilogramos_totales + cantidad_kg;
                }
                i++;
            }
            //console.log("kilogramos_totales:: "+kilogramos_totales);

            var actaRepo = ctx.get("repos").get("actaRepo");
            var entityActa = actaRepo.findById(acta_id_view);
            entityActa.set("kilogramos_totales", new java.lang.Double(kilogramos_totales));
            //console.log("entityActa.get('kilogramos_totales'): " + entityActa.get("kilogramos_totales"));
            actaRepo.save(entityActa);
        }
    }

    function setMuestrasBodega(){
        //console.log("setMuestrasBodega");
        var acta_id_view = view.get("acta_id");
        if (acta_id_view != null){
            var muestraRepo = ctx.get("repos").get("muestraRepo");
            var lstMuestra = muestraRepo.listAll()

            var muestras_bodega = 0;
            var i = 0;
            while(i != lstMuestra.size()){
                //console.log("i: " + i);
                var entityMuestra = lstMuestra.get(i);
                var acta_id_bbdd = new java.lang.Long(entityMuestra.get("acta_id"))
                if (acta_id_bbdd == acta_id_view){
                    muestras_bodega++;
                }
                i++;
            }
            //console.log("muestras_bodega:: "+muestras_bodega);

            var actaRepo = ctx.get("repos").get("actaRepo");
            var entityActa = actaRepo.findById(acta_id_view);
            entityActa.set("muestras_bodega", new java.lang.Long(muestras_bodega));
            actaRepo.save(entityActa);
        }
    }

