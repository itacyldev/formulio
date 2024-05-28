function setContraetiquetasNumeracionesUtilizadas (){
    if (view.get("lote_control_documental_id") != null){
        var lote_control_documental_id_view = new java.lang.Long(view.get("lote_control_documental_id"))

        var numeracionContraEtiquetadoLoteRepo = ctx.get("repos").get("numeracionContraEtiquetadoLoteRepo");
        var loteControlDocumentalRepo = ctx.get("repos").get("loteControlDocumentalRepo");

        var lstNumeracionContraEtiquetadoLote = numeracionContraEtiquetadoLoteRepo.listAll();
        console.log("lstNumeracionContraEtiquetadoLote.size(): " + lstNumeracionContraEtiquetadoLote.size());

        var unidadesTotales=0;
        var i = 0;
        while(i != lstNumeracionContraEtiquetadoLote.size()){
            console.log("i: " + i);
            var entityNumeracionContraEtiquetadoLote = lstNumeracionContraEtiquetadoLote.get(i);
            var lote_control_documental_id_bbdd = new java.lang.Long(entityNumeracionContraEtiquetadoLote.get("lote_control_documental_id"))
            console.log("lote_control_documental_id_view: "+ lote_control_documental_id_view)
            console.log("lote_control_documental_id_bbdd: " + lote_control_documental_id_bbdd);

            if (lote_control_documental_id_view == lote_control_documental_id_bbdd){
                var unidades = new java.lang.Long(entityNumeracionContraEtiquetadoLote.get("hasta") - entityNumeracionContraEtiquetadoLote.get("desde") + 1)
                console.log("unidades: " + unidades);
                entityNumeracionContraEtiquetadoLote.set("unidades", unidades);
                numeracionContraEtiquetadoLoteRepo.save(entityNumeracionContraEtiquetadoLote);
                unidadesTotales = unidadesTotales + unidades
            }
            i++;
        }
        console.log("unidadesTotales: " + unidadesTotales);
        if (unidadesTotales != 0){
            var entityLoteControlDocumental = loteControlDocumentalRepo.findById(lote_control_documental_id_view);
            console.log("unidadesTotales: " + unidadesTotales);
            entityLoteControlDocumental.set("contraetiquetas_numeraciones_utilizadas", new java.lang.Long((unidadesTotales)))
            console.log("entityLoteControlDocumental.get('contraetiquetas_numeraciones_utilizadas'): " + entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas"));
            loteControlDocumentalRepo.save(entityLoteControlDocumental)

            }
        }
    }

    function updateUnidadesTeoricasKG(component){

        console.log("setUnidadesTeoricasKG");

        if (view.get("tipo_morcilla_id") != null){
            var tipo_morcilla_id = new java.lang.Long(view.get("tipo_morcilla_id"));
            console.log("tipo_morcilla_id::: "+tipo_morcilla_id);

            var tipoMorcillaRepo = ctx.get("repos").get("tipoMorcillaRepo");
            var entityTipoMorcilla = tipoMorcillaRepo.findById(tipo_morcilla_id);

            var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            if (entityTipoMorcilla.get("peso_unitario_kg") != null){
                var peso_unitario_kg = new java.lang.Double(entityTipoMorcilla.get("peso_unitario_kg"));
                console.log("peso_unitario_kg::: "+peso_unitario_kg);
                if (peso_unitario_kg > 0){
                    var unidad_kg = new java.lang.Double(1/entityTipoMorcilla.get("peso_unitario_kg"));
                    console.log("unidad_kg"+unidad_kg);

                    var bdUnidadKG = new java.math.BigDecimal(unidad_kg).setScale(2, java.math.RoundingMode.HALF_UP);

                    component.setValueExpression(valueExpressionFactory.getInstance().create(""+bdUnidadKG.toString()));
                }
            }

            //component.setValueExpression(valueExpressionFactory.getInstance().create(""+new java.lang.Math.round(unidad_kg*java.lang.Math.pow(10,2))/java.lang.Math.pow(10,2)));
        }
    }


    function setContraetiquetasNumeraciones(component){
        console.log("setContraetiquetasNumeraciones")
        console.log("lote_control_documental_id_view:: "+lote_control_documental_id_view)

        var lote_control_documental_id_view = new java.lang.Long(view.get("lote_control_documental_id"))

        var loteControlDocumentalRepo = ctx.get("repos").get("loteControlDocumentalRepo");

        var entityLoteControlDocumental = loteControlDocumentalRepo.findById(lote_control_documental_id_view);

        console.log("entityLoteControlDocumental.get(unidades'): "+entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas"))
        if (entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas") != null){
            console.log("entityLoteControlDocumental.get('contraetiquetas_numeraciones_utilizadas') != null")
            console.log("This is a component " + component.getId());
            var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas")));
           }
    }

