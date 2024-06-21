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

                    var unidades_teoricas_elaboradas = unidad_kg*peso_despues_oreo;
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
                    var unidades_teoricas_expedidas = unidad_kg*kg_expedidos;
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
                component.setValueExpression(null);
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

