    var tipoMorcillaInstalacionRepo = null;
    var entityTipoMorcillaInstalacion = null;
    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

    function setContraetiquetasNumeracionesUtilizadas (){
        //console.log("setContraetiquetasNumeracionesUtilizadas");
        if (view.get("lote_control_documental_id") != null){
            var lote_control_documental_id_view = new java.lang.Long(view.get("lote_control_documental_id"))

            var numeracionContraEtiquetadoLoteRepo = ctx.get("repos").get("numeracionContraEtiquetadoLoteRepo");
            var loteControlDocumentalRepo = ctx.get("repos").get("loteControlDocumentalRepo");

            var lstNumeracionContraEtiquetadoLote = numeracionContraEtiquetadoLoteRepo.listAll();
            //console.log("lstNumeracionContraEtiquetadoLote.size(): " + lstNumeracionContraEtiquetadoLote.size());

            var unidadesTotales=0;
            var i = 0;
            while(i != lstNumeracionContraEtiquetadoLote.size()){
                //console.log("i: " + i);
                var entityNumeracionContraEtiquetadoLote = lstNumeracionContraEtiquetadoLote.get(i);
                var lote_control_documental_id_bbdd = new java.lang.Long(entityNumeracionContraEtiquetadoLote.get("lote_control_documental_id"))
                //console.log("lote_control_documental_id_view: "+ lote_control_documental_id_view)
                //console.log("lote_control_documental_id_bbdd: " + lote_control_documental_id_bbdd);

                if (lote_control_documental_id_view == lote_control_documental_id_bbdd){
                    var unidades = new java.lang.Long(entityNumeracionContraEtiquetadoLote.get("hasta") - entityNumeracionContraEtiquetadoLote.get("desde") + 1)
                    //console.log("unidades: " + unidades);
                    entityNumeracionContraEtiquetadoLote.set("unidades", unidades);
                    numeracionContraEtiquetadoLoteRepo.save(entityNumeracionContraEtiquetadoLote);
                    unidadesTotales = unidadesTotales + unidades
                }
                i++;
            }
            //console.log("unidadesTotales: " + unidadesTotales);
            var entityLoteControlDocumental = loteControlDocumentalRepo.findById(lote_control_documental_id_view);
            //console.log("unidadesTotales: " + unidadesTotales);
            entityLoteControlDocumental.set("contraetiquetas_numeraciones_utilizadas", new java.lang.Long((unidadesTotales)))
            //console.log("entityLoteControlDocumental.get('contraetiquetas_numeraciones_utilizadas'): " + entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas"));
            loteControlDocumentalRepo.save(entityLoteControlDocumental)
        }
    }

    function deleteTipoMorcillaBalanceMasas(lote_control_documental_id){
            //console.log("deleteTipoMorcillaBalanceMasas");
             var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");

            var lstTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.listAll();
            //console.log("lstTipoMorcillaBalanceMasas.size(): " + lstTipoMorcillaBalanceMasas.size());

            var i = 0;
            while(i != lstTipoMorcillaBalanceMasas.size()){
                var entityTipoMorcillaBalanceMasas = lstTipoMorcillaBalanceMasas.get(i);

                var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("lote_control_documental_id"));
                var tipo_morcilla_balance_masas_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("tipo_morcilla_balance_masas_id"));

                 //console.log("lote_control_documental_id_bbdd: "+ lote_control_documental_id_bbdd)
                 //console.log("tipo_morcilla_balance_masas_id_bbdd: "+ tipo_morcilla_balance_masas_id_bbdd)
                 if (lote_control_documental_id_bbdd == lote_control_documental_id){
                    entityTipoMorcillaBalanceMasas.set("peso_despues_oreo", 0);
                    entityTipoMorcillaBalanceMasas.set("unidades_teoricas_elaboradas", 0);
                    entityTipoMorcillaBalanceMasas.set("kg_expedidos", 0);
                    entityTipoMorcillaBalanceMasas.set("unidades_teoricas_expedidas", 0);
                    tipoMorcillaBalanceMasasRepo.save(entityTipoMorcillaBalanceMasas);
                }
                i++;
            }
        }

    function existeTipoMorcillaBalanceMasas(entityTipoMorcilla){
        //console.log("existeTipoMorcillaBalanceMasas");
         var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");

        var lstTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.listAll();
        //console.log("lstTipoMorcillaBalanceMasas.size(): " + lstTipoMorcillaBalanceMasas.size());

        var i = 0;
        var encontrado = 0;
        while(i != lstTipoMorcillaBalanceMasas.size()){
            var entityTipoMorcillaBalanceMasas = lstTipoMorcillaBalanceMasas.get(i);

            var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("lote_control_documental_id"));
            var tipo_morcilla_instalacion_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("tipo_morcilla_instalacion_id"));

             //console.log("lote_control_documental_id_bbdd: "+ lote_control_documental_id_bbdd)
             //console.log("tipo_morcilla_instalacion_id_bbdd: " + tipo_morcilla_instalacion_id_bbdd);
             if (lote_control_documental_id_bbdd == entityTipoMorcilla.get("lote_control_documental_id")){
                if (tipo_morcilla_instalacion_id_bbdd==entityTipoMorcilla.get("tipo_morcilla_instalacion_id")){
                    encontrado = 1;
                }
            }
            i++;
        }
        return encontrado==1;
    }

   function updateTipoMorcillaBalanceMasasElaboracion(entityTipoMorcillaElaboracion){
           //console.log("updateTipoMorcillaBalanceMasasElaboracion");
           var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");

           var lstTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.listAll();
           //console.log("lstTipoMorcillaBalanceMasas.size(): " + lstTipoMorcillaBalanceMasas.size());

           var i = 0;
           while(i != lstTipoMorcillaBalanceMasas.size()){
               var entityTipoMorcillaBalanceMasas = lstTipoMorcillaBalanceMasas.get(i);

               var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("lote_control_documental_id"));
               var tipo_morcilla_instalacion_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("tipo_morcilla_instalacion_id"));
               //console.log("lote_control_documental_id_bbdd: "+ lote_control_documental_id_bbdd)
               //console.log("tipo_morcilla_instalacion_id_bbdd: " + tipo_morcilla_instalacion_id_bbdd);
               if (lote_control_documental_id_bbdd == entityTipoMorcillaElaboracion.get("lote_control_documental_id")){
                   if (tipo_morcilla_instalacion_id_bbdd==entityTipoMorcillaElaboracion.get("tipo_morcilla_instalacion_id")){
                       entityTipoMorcillaBalanceMasas.set("peso_despues_oreo", entityTipoMorcillaElaboracion.get("peso_despues_oreo"));
                       entityTipoMorcillaBalanceMasas.set("unidades_teoricas_elaboradas", entityTipoMorcillaElaboracion.get("unidades_teoricas_elaboradas"));
                       tipoMorcillaBalanceMasasRepo.save(entityTipoMorcillaBalanceMasas);
                   }
               }
               i++;
           }
       }

    function updateTipoMorcillaBalanceMasasExpedicion(entityTipoMorcillaExpedicion){
        //console.log("updateTipoMorcillaBalanceMasasExpedicion");
        var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");

        var lstTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.listAll();
        //console.log("lstTipoMorcillaBalanceMasas.size(): " + lstTipoMorcillaBalanceMasas.size());

        var i = 0;
        while(i != lstTipoMorcillaBalanceMasas.size()){
            var entityTipoMorcillaBalanceMasas = lstTipoMorcillaBalanceMasas.get(i);

            var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("lote_control_documental_id"));
            var tipo_morcilla_instalacion_id_bbdd = new java.lang.Long(entityTipoMorcillaBalanceMasas.get("tipo_morcilla_instalacion_id"));
            //console.log("lote_control_documental_id_bbdd: "+ lote_control_documental_id_bbdd)
            //console.log("tipo_morcilla_instalacion_id_bbdd: " + tipo_morcilla_instalacion_id_bbdd);
            if (lote_control_documental_id_bbdd == entityTipoMorcillaExpedicion.get("lote_control_documental_id")){
                if (tipo_morcilla_instalacion_id_bbdd==entityTipoMorcillaExpedicion.get("tipo_morcilla_instalacion_id")){
                    entityTipoMorcillaBalanceMasas.set("kg_expedidos", entityTipoMorcillaExpedicion.get("kg_expedidos"));
                    entityTipoMorcillaBalanceMasas.set("unidades_teoricas_expedidas", entityTipoMorcillaExpedicion.get("unidades_teoricas_expedidas"));
                    tipoMorcillaBalanceMasasRepo.save(entityTipoMorcillaBalanceMasas);
                }
            }
            i++;
        }
    }

    function anadirTipoMorcillaBalanceMasasElaboracion(entityTipoMorcillaElaboracion){
            //console.log("anadirTipoMorcillaBalanceMasasElaboracion");
            var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");
            var newEntityTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.newEntity();
            newEntityTipoMorcillaBalanceMasas.set("lote_control_documental_id", entityTipoMorcillaElaboracion.get("lote_control_documental_id"));
            newEntityTipoMorcillaBalanceMasas.set("tipo_morcilla_instalacion_id", entityTipoMorcillaElaboracion.get("tipo_morcilla_instalacion_id"));
            newEntityTipoMorcillaBalanceMasas.set("peso_despues_oreo", entityTipoMorcillaElaboracion.get("peso_despues_oreo"));
            newEntityTipoMorcillaBalanceMasas.set("unidades_teoricas_elaboradas", entityTipoMorcillaElaboracion.get("unidades_teoricas_elaboradas"));
            tipoMorcillaBalanceMasasRepo.save(newEntityTipoMorcillaBalanceMasas);
        }

    function anadirTipoMorcillaBalanceMasasExpedicion(entityTipoMorcillaExpedicion){
        //console.log("anadirTipoMorcillaBalanceMasasExpedicion");
        var tipoMorcillaBalanceMasasRepo = ctx.get("repos").get("tipoMorcillaBalanceMasasRepo");
        var newEntityTipoMorcillaBalanceMasas = tipoMorcillaBalanceMasasRepo.newEntity();
        newEntityTipoMorcillaBalanceMasas.set("lote_control_documental_id", entityTipoMorcillaExpedicion.get("lote_control_documental_id"));
        newEntityTipoMorcillaBalanceMasas.set("tipo_morcilla_instalacion_id", entityTipoMorcillaExpedicion.get("tipo_morcilla_instalacion_id"));
        newEntityTipoMorcillaBalanceMasas.set("kg_expedidos", entityTipoMorcillaExpedicion.get("kg_expedidos"));
        newEntityTipoMorcillaBalanceMasas.set("unidades_teoricas_expedidas", entityTipoMorcillaExpedicion.get("unidades_teoricas_expedidas"));
        tipoMorcillaBalanceMasasRepo.save(newEntityTipoMorcillaBalanceMasas);
    }

    function setTotalesElaboracion (){
        //console.log("setTotalesElaboracion");
        if (view.get("lote_control_documental_id") != null){
            var lote_control_documental_id_view = new java.lang.Long(view.get("lote_control_documental_id"));

            deleteTipoMorcillaBalanceMasas(lote_control_documental_id_view);

            var tipoMorcillaElaboracionRepo = ctx.get("repos").get("tipoMorcillaElaboracionRepo");
            var loteControlDocumentalRepo = ctx.get("repos").get("loteControlDocumentalRepo");

            var lstTipoMorcillaElaboracion = tipoMorcillaElaboracionRepo.listAll();
            //console.log("lstTipoMorcillaElaboracion.size(): " + lstTipoMorcillaElaboracion.size());

            var total_kg_elaborados=0;
            var total_peso_despues_oreo=0;
            var total_unidades_teoricas_elaboradas=0;
            var i = 0;
            while(i != lstTipoMorcillaElaboracion.size()){
                //console.log("i: " + i);
                var entityTipoMorcillaElaboracion = lstTipoMorcillaElaboracion.get(i);

                var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaElaboracion.get("lote_control_documental_id"))
                //console.log("lote_control_documental_id_view: "+ lote_control_documental_id_view)
                //console.log("lote_control_documental_id_bbdd: " + lote_control_documental_id_bbdd);

                if (lote_control_documental_id_view == lote_control_documental_id_bbdd){
                    setEntityTipoMorcillaInstalacion(entityTipoMorcillaElaboracion.get("tipo_morcilla_instalacion_id"));

                    var kg_elaborados = entityTipoMorcillaElaboracion.get("kg_elaborados");
                    //console.log("kg_elaborados: " + kg_elaborados);
                    total_kg_elaborados = total_kg_elaborados + kg_elaborados;

                    var merma_porcentaje = entityTipoMorcillaInstalacion.get("merma_porcentaje");
                    //console.log("merma_porcentaje: " + merma_porcentaje);
                    var peso_masa = (kg_elaborados * merma_porcentaje)/100;
                    //console.log("peso_masa: " + peso_masa);

                    var peso_despues_oreo = (kg_elaborados * (100-merma_porcentaje))/100;
                    //console.log("peso_despues_oreo: " + peso_despues_oreo);

                    var unidad_kg = entityTipoMorcillaInstalacion.get("unidad_kg");
                    //console.log("unidad_kg: " + unidad_kg);

                    var unidades_teoricas_elaboradas = new java.math.BigDecimal(unidad_kg*peso_despues_oreo).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
                    //console.log("unidades_teoricas_elaboradas: " + unidades_teoricas_elaboradas);

                    entityTipoMorcillaElaboracion.set("unidades_teoricas_elaboradas", unidades_teoricas_elaboradas);
                    entityTipoMorcillaElaboracion.set("peso_despues_oreo", peso_despues_oreo);
                    entityTipoMorcillaElaboracion.set("peso_masa", peso_masa);

                    tipoMorcillaElaboracionRepo.save(entityTipoMorcillaElaboracion)

                    var existe = existeTipoMorcillaBalanceMasas(entityTipoMorcillaElaboracion);
                    //console.log("existe:: "+existe);
                    if (existe){
                        updateTipoMorcillaBalanceMasasElaboracion(entityTipoMorcillaElaboracion);
                    }else{
                        anadirTipoMorcillaBalanceMasasElaboracion(entityTipoMorcillaElaboracion);
                    }


                    total_peso_despues_oreo = total_peso_despues_oreo + peso_despues_oreo;
                    total_unidades_teoricas_elaboradas = total_unidades_teoricas_elaboradas + unidades_teoricas_elaboradas;
                }
                i++;
            }
            //console.log("total_kg_elaborados: " + total_kg_elaborados);
            //console.log("total_peso_despues_oreo: " + total_peso_despues_oreo);
            //console.log("total_unidades_teoricas_elaboradas: " + total_unidades_teoricas_elaboradas);
            var entityLoteControlDocumental = loteControlDocumentalRepo.findById(lote_control_documental_id_view);
            entityLoteControlDocumental.set("total_kg_elaborados", total_kg_elaborados);
            entityLoteControlDocumental.set("total_peso_despues_oreo", total_peso_despues_oreo);
            entityLoteControlDocumental.set("total_unidades_teoricas_elaboradas", total_unidades_teoricas_elaboradas);
            loteControlDocumentalRepo.save(entityLoteControlDocumental)
        }
    }

    function setTotalesExpedicion (){
        //console.log("setTotalesExpedicion");
        if (view.get("lote_control_documental_id") != null){
            var lote_control_documental_id_view = new java.lang.Long(view.get("lote_control_documental_id"));

            var tipoMorcillaExpedicionRepo = ctx.get("repos").get("tipoMorcillaExpedicionRepo");
            var loteControlDocumentalRepo = ctx.get("repos").get("loteControlDocumentalRepo");

            var lstTipoMorcillaExpedicion = tipoMorcillaExpedicionRepo.listAll();
            //console.log("lstTipoMorcillaExpedicion.size(): " + lstTipoMorcillaExpedicion.size());

            var total_kg_expedidos=0;
            var total_unidades_teoricas_expedidas=0;
            var i = 0;
            while(i != lstTipoMorcillaExpedicion.size()){
                //console.log("i: " + i);
                var entityTipoMorcillaExpedicion = lstTipoMorcillaExpedicion.get(i);
                var lote_control_documental_id_bbdd = new java.lang.Long(entityTipoMorcillaExpedicion.get("lote_control_documental_id"))
                //console.log("lote_control_documental_id_view: "+ lote_control_documental_id_view)
                //console.log("lote_control_documental_id_bbdd: " + lote_control_documental_id_bbdd);

                if (lote_control_documental_id_view == lote_control_documental_id_bbdd){
                    var kg_expedidos = entityTipoMorcillaExpedicion.get("kg_expedidos");
                    //console.log("kg_expedidos: " + kg_expedidos);
                    total_kg_expedidos = total_kg_expedidos + kg_expedidos;

                    setEntityTipoMorcillaInstalacion(entityTipoMorcillaExpedicion.get("tipo_morcilla_instalacion_id"));
                    var unidad_kg = entityTipoMorcillaInstalacion.get("unidad_kg");
                    //console.log("unidad_kg: " + unidad_kg);
                    var unidades_teoricas_expedidas = new java.math.BigDecimal(unidad_kg*kg_expedidos).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
                    //console.log("unidades_teoricas_expedidas: " + unidades_teoricas_expedidas);

                    entityTipoMorcillaExpedicion.set("unidades_teoricas_expedidas", unidades_teoricas_expedidas);

                    tipoMorcillaExpedicionRepo.save(entityTipoMorcillaExpedicion)

                    var existe = existeTipoMorcillaBalanceMasas(entityTipoMorcillaExpedicion);
                    //console.log("existe:: "+existe);
                    if (existe){
                        updateTipoMorcillaBalanceMasasExpedicion(entityTipoMorcillaExpedicion);
                    }else{
                        anadirTipoMorcillaBalanceMasasExpedicion(entityTipoMorcillaExpedicion);
                    }

                    total_unidades_teoricas_expedidas = total_unidades_teoricas_expedidas + unidades_teoricas_expedidas;
                }
                i++;
            }
            //console.log("total_kg_expedidos: " + total_kg_expedidos);
            //console.log("total_unidades_teoricas_expedidas: " + total_unidades_teoricas_expedidas);
            var entityLoteControlDocumental = loteControlDocumentalRepo.findById(lote_control_documental_id_view);
            entityLoteControlDocumental.set("total_unidades_teoricas_expedidas", total_unidades_teoricas_expedidas);
            entityLoteControlDocumental.set("total_kg_expedidos", total_kg_expedidos);
            loteControlDocumentalRepo.save(entityLoteControlDocumental)
        }
    }

    function setEntityTipoMorcillaInstalacion(tipo_morcilla_id){
            //console.log("setEntityTipoMorcillaInstalacion");
            var tipo_morcilla_instalacion_id = null;
            if (tipo_morcilla_id==null){
                if (view.get("tipo_morcilla_instalacion_id") != null){
                    tipo_morcilla_instalacion_id = new java.lang.Long(view.get("tipo_morcilla_instalacion_id"));
                }
            }else{
                tipo_morcilla_instalacion_id = tipo_morcilla_id;
            }
            if (tipo_morcilla_instalacion_id != null){
                //console.log("tipo_morcilla_instalacion_id: "+tipo_morcilla_instalacion_id);

                tipoMorcillaInstalacionRepo = ctx.get("repos").get("tipoMorcillaInstalacionRepo");
                entityTipoMorcillaInstalacion = tipoMorcillaInstalacionRepo.findById(tipo_morcilla_instalacion_id);
                //console.log("entityTipoMorcillaInstalacion.get('unidad_kg'): "+entityTipoMorcillaInstalacion.get("unidad_kg"));
                //console.log("entityTipoMorcillaInstalacion.get('peso_unitario_kg'): "+entityTipoMorcillaInstalacion.get("peso_unitario_kg"));
            }
        }

        function updatePesoUnitarioKG(component){
            //console.log("updatePesoUnitarioKG");
            //console.log("view.get('tipo_morcilla_instalacion_id')::: "+view.get("tipo_morcilla_instalacion_id"));
            //console.log("view.get('instalacion_id')::: "+view.get("instalacion_id"));
            if (view.get("tipo_morcilla_instalacion_id") != null){
                setEntityTipoMorcillaInstalacion(null);
                //console.log("entityTipoMorcillaInstalacion.get('peso_unitario_kg'): "+entityTipoMorcillaInstalacion.get("peso_unitario_kg"));
                component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityTipoMorcillaInstalacion.get("peso_unitario_kg")));
            } else {
                if (entity.get("tipo_morcilla_instalacion_id") != null){
                    setEntityTipoMorcillaInstalacion(entity.get("tipo_morcilla_instalacion_id"));
                    component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityTipoMorcillaInstalacion.get("peso_unitario_kg")));
                }else{
                    component.setValueExpression(null);
                }
            }
        }

        function updateUnidadesTeoricasKG(component){
                //console.log("updateUnidadesTeoricasKG");
                //console.log("view.get('tipo_morcilla_instalacion_id')::: "+view.get("tipo_morcilla_instalacion_id"));
                //console.log("view.get('instalacion_id')::: "+view.get("instalacion_id"));
                if (view.get("tipo_morcilla_instalacion_id") != null){
                    setEntityTipoMorcillaInstalacion(null);
                    //console.log("entityTipoMorcillaInstalacion.get('unidad_kg'): "+entityTipoMorcillaInstalacion.get("unidad_kg"));

                    var unidad_kg = new java.math.BigDecimal(entityTipoMorcillaInstalacion.get("unidad_kg")).setScale(2, java.math.RoundingMode.HALF_UP);

                    component.setValueExpression(valueExpressionFactory.getInstance().create(""+unidad_kg.toString()));

                } else {
                    if (entity.get("tipo_morcilla_instalacion_id") != null){
                        //console.log("entity.get('tipo_morcilla_instalacion_id'): "+entity.get("tipo_morcilla_instalacion_id"));
                        setEntityTipoMorcillaInstalacion(entity.get("tipo_morcilla_instalacion_id"));
                        //console.log("entityTipoMorcillaInstalacion.get('unidad_kg'): "+entityTipoMorcillaInstalacion.get("unidad_kg"));
                        var unidad_kg = new java.math.BigDecimal(entityTipoMorcillaInstalacion.get("unidad_kg")).setScale(2, java.math.RoundingMode.HALF_UP);
                        component.setValueExpression(valueExpressionFactory.getInstance().create(""+unidad_kg.toString()));
                    }
                    else{
                        component.setValueExpression(null);
                    }
                }
            }

        function updateMermaPorcentaje(component){
            //console.log("updateMermaPorcentaje");
            //console.log("view.get('tipo_morcilla_instalacion_id')::: "+view.get("tipo_morcilla_instalacion_id"));
            //console.log("view.get('instalacion_id')::: "+view.get("instalacion_id"));
            if (view.get("tipo_morcilla_instalacion_id") != null){
                setEntityTipoMorcillaInstalacion(null);
                //console.log("entityTipoMorcillaInstalacion.get('merma_porcentaje'): "+entityTipoMorcillaInstalacion.get("merma_porcentaje"));

                var merma_porcentaje = new java.math.BigDecimal(entityTipoMorcillaInstalacion.get("merma_porcentaje")).setScale(2, java.math.RoundingMode.HALF_UP);

                component.setValueExpression(valueExpressionFactory.getInstance().create(""+merma_porcentaje.toString()));

            } else {
                if (entity.get("tipo_morcilla_instalacion_id") != null){
                    //console.log("entity.get('tipo_morcilla_instalacion_id'): "+entity.get("tipo_morcilla_instalacion_id"));
                    setEntityTipoMorcillaInstalacion(entity.get("tipo_morcilla_instalacion_id"));
                    //console.log("entityTipoMorcillaInstalacion.get('merma_porcentaje'): "+entityTipoMorcillaInstalacion.get("merma_porcentaje"));
                    var merma_porcentaje = new java.math.BigDecimal(entityTipoMorcillaInstalacion.get("merma_porcentaje")).setScale(2, java.math.RoundingMode.HALF_UP);
                    component.setValueExpression(valueExpressionFactory.getInstance().create(""+merma_porcentaje.toString()));
                }
                else{
                    component.setValueExpression(null);
                }
            }
        }

    function setCumpleResultadosMorfologicos(){
        var loteControlResultadosMorfologicosRepo = ctx.get("repos").get("loteControlResultadosMorfologicosRepo");
        var lstLoteControlResultadosMorfologicos = loteControlResultadosMorfologicosRepo.listAll();

        var i = 0;
        while(i != lstLoteControlResultadosMorfologicos.size()){
            var entityLoteControlResultadosMorfologicos = lstLoteControlResultadosMorfologicos.get(i);
            var diametro = entityLoteControlResultadosMorfologicos.get("diametro");
            var longitud = entityLoteControlResultadosMorfologicos.get("longitud");
            var cumpleMinDiametro = Math.min(diametro, 30)==30;
            var cumpleMaxDiametro = Math.max(diametro, 100)==100;
            var cumpleMinLongitud = Math.min(longitud, 150)==150;
            var cumpleMaxLongitud = Math.max(longitud, 350)==350;
            var cumple = (cumpleMinDiametro && cumpleMaxDiametro && cumpleMinLongitud && cumpleMaxLongitud)?'S':'N';
            entityLoteControlResultadosMorfologicos.set("cumple", cumple);
            loteControlResultadosMorfologicosRepo.save(entityLoteControlResultadosMorfologicos);
            i++;
        }

    }

    function updateDireccionOperador(component){
        //console.log("updateDireccionOperador");
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            var municipio = getMunicipio(entityOperador.get("provmuni_id_social"));
            var provincia = getProvincia(entityOperador.get("prov_id_social"));
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("direccion_social")+", "+municipio+", "+entityOperador.get("codigo_postal_social")+"-"+provincia));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateDireccionInstalacion(component){
        //console.log("updateDireccionInstalacion");
        entityOperador = setEntityOperador();
        entityInstalacion = setEntityInstalacion();
        if (entityOperador != null){
            var municipio = getMunicipio(entityInstalacion.get("provmuni_id_instalacion"));
            var provincia = getProvincia(entityInstalacion.get("prov_id_instalacion"));
            //console.log("municipio::: "+municipio);
            //console.log("provincia::: "+provincia);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityInstalacion.get("direccion_instalacion")+", "+municipio+", "+entityInstalacion.get("codigo_postal_instalacion")+"-"+provincia));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateRepresentante(component){
        //console.log("updateRepresentante");
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("responsable")));
        }else{
            component.setValueExpression(null);
        }
    }

    function getMunicipio(municipio_id){
        //console.log("getMunicipio");
        var municipioRepo = ctx.get("repos").get("muniRepo");
        //console.log("municipio_id::: "+municipio_id);
        var entidadMunicipio = municipioRepo.findById(municipio_id);
        var municipio = entidadMunicipio.get("name");
        return municipio;
    }

    function getProvincia(provincia_id){
        //console.log("getMunicipio");
        var provinciaRepo = ctx.get("repos").get("provRepo");
        //console.log("provincia_id::: "+provincia_id);
        var entidadProvincia = provinciaRepo.findById(provincia_id);
        var provincia = entidadProvincia.get("name");
        return provincia;
    }

    function setEntityOperador(){
            //console.log("setEntityOperador");
            var operador_id_view = view.get("operador_id");
            //console.log("operador_id_view::: "+operador_id_view);
            if (operador_id_view == null){
                operador_id_view = entity.get("operador_id");
            }
            //console.log("operador_id_view::: "+operador_id_view);
            var entityOperador = null;
            if (operador_id_view != null){
                var operadorRepo = ctx.get("repos").get("operadorRepo");
                var lstOperadores = operadorRepo.listAll();
                var i = 0;

                while(i != lstOperadores.size()){
                    //console.log("i: " + i);
                    entityOperador = lstOperadores.get(i);
                    var operador_id_bbdd = entityOperador.get("operador_id");
                    if (operador_id_bbdd == operador_id_view){
                        encontrado = true;
                        break;
                    }
                    i++;
                }

                if (!encontrado){
                    entityOperador = null;
                }
            }
            return entityOperador;
        }

        function setEntityInstalacion(){
            //console.log("setEntityInstalacion");
            var operador_id_view = view.get("operador_id");
            //console.log("operador_id_view::: "+operador_id_view);
            if (operador_id_view == null){
                operador_id_view = entity.get("operador_id");
            }

            //console.log("operador_id_view::: "+operador_id_view);
            if (operador_id_view != null){
                var instalacionRepo = ctx.get("repos").get("instalacionRepo");
                var lstInstalaciones = instalacionRepo.listAll();
                var i = 0;
                var entityInstalacion = null;
                while(i != lstInstalaciones.size()){
                    //console.log("i: " + i);
                    entityInstalacion = lstInstalaciones.get(i);
                    var operador_id_bbdd = entityInstalacion.get("operador_id");
                    if (operador_id_bbdd == operador_id_view){
                        encontrado = true;
                        break;
                    }
                    i++;
                }
                if (!encontrado){
                    entityInstalacion = null;
                }
            }
            return entityInstalacion;
        }