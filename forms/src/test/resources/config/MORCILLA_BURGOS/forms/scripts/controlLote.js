    var tipoMorcillaInstalacionRepo = null;
    var entityTipoMorcillaInstalacion = null;
     var entityOperador;
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

    function updateDesviacionesResultadosMorfologicos(){

        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesResultadosMorfologicos");

        var loteControlResultadosMorfologicosRepo = ctx.get("repos").get("loteControlResultadosMorfologicosRepo");
        var lstLoteControlResultadosMorfologicos = loteControlResultadosMorfologicosRepo.listAll();

         var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstLoteControlResultadosMorfologicos.size()){
            var entityLoteControlResultadosMorfologicos = lstLoteControlResultadosMorfologicos.get(i);
            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityLoteControlResultadosMorfologicos.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var diametro = entityLoteControlResultadosMorfologicos.get("diametro");
                var longitud = entityLoteControlResultadosMorfologicos.get("longitud");
                var cumpleMinDiametro = Math.min(diametro, 30)==30;
                var cumpleMaxDiametro = Math.max(diametro, 100)==100;
                var cumpleMinLongitud = Math.min(longitud, 150)==150;
                var cumpleMaxLongitud = Math.max(longitud, 350)==350;
                var cumple = (cumpleMinDiametro && cumpleMaxDiametro && cumpleMinLongitud && cumpleMaxLongitud)?'S':'N';
                entityLoteControlResultadosMorfologicos.set("cumple", cumple);
                var lote_control_resultados_morfologicos_id = new java.lang.String(entityLoteControlResultadosMorfologicos.get("lote_control_resultados_morfologicos_id"));
                //console.log("lote_control_resultados_morfologicos_id::: "+lote_control_resultados_morfologicos_id);
                loteControlResultadosMorfologicosRepo.save(entityLoteControlResultadosMorfologicos);
                saveDesviacion("resultados_morfologicos", lote_control_resultados_morfologicos_id, cumple, entityLoteControlResultadosMorfologicos.get("notas_aclaratorias"));
            }
             i++;
        }
    }

    function updateDesviacionesResultadosFisicoQuimicos(){

        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesResultadosFisicoQuimicos");

        var loteControlResultadosFqRepo = ctx.get("repos").get("loteControlResultadosFqRepo");
        var lstLoteControlResultadosFq = loteControlResultadosFqRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstLoteControlResultadosFq.size()){
            var entityLoteControlResultadosFq = lstLoteControlResultadosFq.get(i);
            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityLoteControlResultadosFq.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var ph = entityLoteControlResultadosFq.get("ph");
                var cumpleMinPh = Math.min(ph, 6)==6;
                var cumpleMaxPh = Math.max(ph, 7)==7;
                //console.log("ph::: "+ph);
                //console.log("cumpleMinPh::: "+cumpleMinPh);
                //console.log("cumpleMaxPh::: "+cumpleMaxPh);

                var humedad = entityLoteControlResultadosFq.get("humedad");
                var cumpleMinHumedad = Math.min(humedad, 54)==54;
                var cumpleMaxHumedad = Math.max(humedad, 67)==67;
                //console.log("humedad::: "+humedad);
                //console.log("cumpleMinHumedad::: "+cumpleMinHumedad);
                //console.log("cumpleMaxHumedad::: "+cumpleMaxHumedad);

                var grasa = entityLoteControlResultadosFq.get("grasa");
                var cumpleMinGrasa = Math.min(grasa, 15)==15;
                var cumpleMaxGrasa = Math.max(grasa, 25)==25;
                //console.log("grasa::: "+grasa);
                //console.log("cumpleMinGrasa::: "+cumpleMinGrasa);
                //console.log("cumpleMaxGrasa::: "+cumpleMaxGrasa);

                var azucares = entityLoteControlResultadosFq.get("azucares");
                var cumpleMinAzucares = Math.min(azucares, 3.5)==3.5;
                //console.log("azucares::: "+azucares);
                //console.log("cumpleMinAzucares::: "+cumpleMinAzucares);

                var fibra = entityLoteControlResultadosFq.get("fibra");
                var cumpleMinFibra = Math.min(fibra, 2.7)==2.7;
                //console.log("fibra::: "+fibra);
                //console.log("cumpleMinFibra::: "+cumpleMinFibra);

                var sal = entityLoteControlResultadosFq.get("sal");
                var cumpleMinSal = Math.min(sal, 0.5)==0.5;
                var cumpleMaxSal = Math.max(sal, 2)==2;
                //console.log("sal::: "+sal);
                //console.log("cumpleMinSal::: "+cumpleMinSal);
                //console.log("cumpleMaxSal::: "+cumpleMaxSal);

                var cumple = (cumpleMinPh && cumpleMaxPh && cumpleMinHumedad && cumpleMaxHumedad && cumpleMinGrasa && cumpleMaxGrasa && cumpleMinAzucares && cumpleMinFibra && cumpleMinSal && cumpleMaxSal)?'S':'N';
                entityLoteControlResultadosFq.set("cumple", cumple);
                var lote_control_resultados_fq_id = new java.lang.String(entityLoteControlResultadosFq.get("lote_control_resultados_fq_id"));
                //console.log("lote_control_resultados_fq_id::: "+lote_control_resultados_fq_id);
                loteControlResultadosFqRepo.save(entityLoteControlResultadosFq);
                saveDesviacion("resultados_fq", lote_control_resultados_fq_id, cumple, entityLoteControlResultadosFq.get("notas_aclaratorias"));
            }
            i++;
        }
    }

    function updateDesviacionesResultadosOrganolepticos(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesResultadosOrganolepticos");

        var loteControlResultadosOrganolepticosRepo = ctx.get("repos").get("loteControlResultadosOrganolepticosRepo");
        var lstLoteControlResultadosOrganolepticos = loteControlResultadosOrganolepticosRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstLoteControlResultadosOrganolepticos.size()){
            var entityLoteControlResultadosOrganolepticos = lstLoteControlResultadosOrganolepticos.get(i);
            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityLoteControlResultadosOrganolepticos.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var lote_control_resultados_organolepticos_id = new java.lang.String(entityLoteControlResultadosOrganolepticos.get("lote_control_resultados_organolepticos_id"));
                //console.log("lote_control_resultados_organolepticos_id::: "+lote_control_resultados_organolepticos_id);
                saveDesviacion("resultados_organolepticos", lote_control_resultados_organolepticos_id, entityLoteControlResultadosOrganolepticos.get("cumple"), entityLoteControlResultadosOrganolepticos.get("notas_aclaratorias"));
            }
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

    function updateRazonSocial(component){
        //console.log("updateRazonSocial");
        //console.log("view.get('operador_id')::: "+view.get("operador_id"));
        if (view.get("operador_id") != null){
            var operador_id = new java.lang.Long(view.get("operador_id"));
            var operadorRepo = ctx.get("repos").get("operadorRepo");
            var entityOperador = operadorRepo.findById(operador_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("razon_social")));
        } else {
            component.setValueExpression(null);
        }
    }


     function updateInstalacion(component){
        //console.log("updateInstalacion");
        //console.log("view.get('instalacion_id')::: "+view.get("instalacion_id"));
        if (view.get("instalacion_id") != null){
            var instalacion_id = new java.lang.Long(view.get("instalacion_id"));
            var instalacionRepo = ctx.get("repos").get("instalacionRepo");
            var entityInstalacion = instalacionRepo.findById(instalacion_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityInstalacion.get("nombre_instalacion")));
        } else {
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

    function setEntityOperador(){
            //console.log("setEntityOperador");
            var operador_id_view = view.get("operador_id");
            //console.log("operador_id_view::: "+operador_id_view);
            var operador_id_bbdd = entity.get("operador_id");
            //console.log("operador_id_bbdd::: "+operador_id_bbdd);
            if (operador_id_view == null){
                operador_id_view = entity.get("operador_id");
            }
            //console.log("operador_id_view::: "+operador_id_view);
            entityOperador = null;
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

    function updateRepresentante(component){
        //console.log("updateRepresentante");
        var operador_id_view = view.get("operador_id");
        //console.log("operador_id_view::: "+operador_id_view);
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("responsable")));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateDesviaciones(){
        //console.log("updateDesviaciones");

         if (entity.getId() != null){

            var expediente_id = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id);

            //Instalaciones
            updateDesviacionesInstalaciones(expediente_id);

            //Proceso de fabricación in situ
            updateDesviacionesProcesoFabricacionInSitu(expediente_id);

            //Materias primas
            updateDesviacionesMateriasPrimas(expediente_id);

        }
    }

    function updateDesviacionesInstalaciones(expediente_id){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesInstalaciones");
        //console.log("expediente_id::: "+expediente_id);
        //console.log("q1_1_cumple_municipio::: "+entity.get("q1_1_cumple_municipio"));
        //console.log("q1_1_notas::: "+entity.get("q1_1_notas"));
        //console.log("q1_2_cumple_certificado::: "+entity.get("q1_2_cumple_certificado"));
        //console.log("q1_2_notas::: "+entity.get("q1_2_notas"));
        saveDesviacion("q1_1_municipio", expediente_id, entity.get("q1_1_cumple_municipio"), entity.get("q1_1_notas"));
        saveDesviacion("q1_2_certificado", expediente_id, entity.get("q1_2_cumple_certificado"), entity.get("q1_2_notas"));
    }

    function updateDesviacionesProcesoFabricacionInSitu(expediente_id){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProcesoFabricacionInSitu");
        //console.log("expediente_id::: "+expediente_id);
        saveCumpleProcesoFabricacionInSitu();

        //console.log("q3_1_1_cebolla_cumple::: "+entity.get("q3_1_1_cebolla_cumple"));
        //console.log("q3_1_1_cebolla_notas::: "+entity.get("q3_1_1_cebolla_notas"));
        //console.log("q3_1_2_arroz_cumple::: "+entity.get("q3_1_2_arroz_cumple"));
        //console.log("q3_1_2_arroz_notas::: "+entity.get("q3_1_2_arroz_notas"));
        //console.log("q3_1_3_sangre_cumple::: "+entity.get("q3_1_3_sangre_cumple"));
        //console.log("q3_1_3_sangre_notas::: "+entity.get("q3_1_3_sangre_notas"));
        //console.log("q3_1_4_manteca_cumple::: "+entity.get("q3_1_4_manteca_cumple"));
        //console.log("q3_1_4_manteca_notas::: "+entity.get("q3_1_4_manteca_notas"));

        saveDesviacion("q3_1_1_cebolla_peso", expediente_id, entity.get("q3_1_1_cebolla_cumple"), entity.get("q3_1_1_cebolla_notas"));
        saveDesviacion("q3_1_2_arroz_peso", expediente_id, entity.get("q3_1_2_arroz_cumple"), entity.get("q3_1_2_arroz_notas"));
        saveDesviacion("q3_1_3_sangre_peso", expediente_id, entity.get("q3_1_3_sangre_cumple"), entity.get("q3_1_3_sangre_notas"));
        saveDesviacion("q3_1_4_manteca_peso", expediente_id, entity.get("q3_1_4_manteca_cumple"), entity.get("q3_1_4_manteca_notas"));

        //Preparación y pesado de las materias primas
        saveDesviacion("q3_2_1_1", expediente_id, entity.get("q3_2_1_1_cumple"), entity.get("q3_2_1_5_notas"));
        saveDesviacion("q3_2_1_2", expediente_id, entity.get("q3_2_1_2_cumple"), entity.get("q3_2_1_5_notas"));
        saveDesviacion("q3_2_1_3", expediente_id, entity.get("q3_2_1_3_cumple"), entity.get("q3_2_1_5_notas"));
        saveDesviacion("q3_2_1_4", expediente_id, entity.get("q3_2_1_4_cumple"), entity.get("q3_2_1_5_notas"));

        //Picado de la manteca o sebo
        saveDesviacion("q3_2_2_1", expediente_id, entity.get("q3_2_2_cumple"), entity.get("q3_2_2_notas"));

        //Picado de la cebolla
        saveDesviacion("q3_2_3_1", expediente_id, entity.get("q3_2_3_cumple"), entity.get("q3_2_3_notas"));

        //Sofrito opcional de la cebolla con toda o parte de la manteca de cerdo
        saveDesviacion("q3_2_4_1", expediente_id, entity.get("q3_2_4_cumple"), entity.get("q3_2_4_notas"));

        //Amasado de las materias primas
        saveDesviacion("q3_2_5_1", expediente_id, entity.get("q3_2_5_cumple"), entity.get("q3_2_5_notas"));

        //Embutido de la masa
        saveDesviacion("q3_2_6_1", expediente_id, entity.get("q3_2_6_cumple"), entity.get("q3_2_6_notas"));

        //Delimitación de las piezas de morcilla
        saveDesviacion("q3_2_7_1", expediente_id, entity.get("q3_2_7_1_cumple"), entity.get("q3_2_7_3_notas"));
        saveDesviacion("q3_2_7_2", expediente_id, entity.get("q3_2_7_2_cumple"), entity.get("q3_2_7_3_notas"));

        //Cocción de las morcillas
        saveDesviacion("q3_2_8_1", expediente_id, entity.get("q3_2_8_cumple"), entity.get("q3_2_8_notas"));

        //Oreo
        saveDesviacion("q3_2_9_1", expediente_id, entity.get("q3_2_9_cumple"), entity.get("q3_2_9_notas"));
}

   function saveCumpleProcesoFabricacionInSitu(){
        //console.log("saveCumpleProcesoFabricacionInSitu");
        var actasControlRepo = ctx.get("repos").get("actasControlRepo");
        var entityExpediente = actasControlRepo.findById(entity.getId());

        if (vh.widget("cumple_cebolla_peso") != null){
            var widgetCumpleCebollaPeso= vh.widget("cumple_cebolla_peso");
            //console.log("q3_1_1_cebolla_porcentaje::: "+widgetCumpleCebollaPeso.getValue());
            entityExpediente.set("q3_1_1_cebolla_cumple", widgetCumpleCebollaPeso.getValue());
        }

        if (vh.widget("cumple_arroz_peso") != null){
            var widgetCumpleArrozPeso= vh.widget("cumple_arroz_peso");
            //console.log("q3_1_2_arroz_cumple::: "+widgetCumpleArrozPeso.getValue());
            entityExpediente.set("q3_1_2_arroz_cumple", widgetCumpleArrozPeso.getValue());
        }

        if (vh.widget("cumple_sangre_peso") != null){
            var widgetCumpleSangrePeso= vh.widget("cumple_sangre_peso");
            //console.log("q3_1_3_sangre_cumple::: "+widgetCumpleSangrePeso.getValue());
            entityExpediente.set("q3_1_3_sangre_cumple", widgetCumpleSangrePeso.getValue());
        }

        if (vh.widget("cumple_manteca_peso") != null){
            var widgetCumpleMantecaPeso= vh.widget("cumple_manteca_peso");
            //console.log("q3_1_4_manteca_cumple::: "+widgetCumpleMantecaPeso.getValue());
            entityExpediente.set("q3_1_4_manteca_cumple", widgetCumpleMantecaPeso.getValue());
        }

        if (vh.widget("cumple_sal_peso") != null){
            var widgetCumpleSalPeso= vh.widget("cumple_sal_peso");
            //console.log("q3_1_5_sal_cumple::: "+widgetCumpleSalPeso.getValue());
            entityExpediente.set("q3_1_5_sal_cumple", widgetCumpleSalPeso.getValue());
        }

        if (vh.widget("cumple_especias_peso") != null){
            var widgetCumpleEspeciasPeso= vh.widget("cumple_especias_peso");
            //console.log("q3_1_6_especias_cumple::: "+widgetCumpleEspeciasPeso.getValue());
            entityExpediente.set("q3_1_6_especias_cumple", widgetCumpleEspeciasPeso.getValue());
        }

         actasControlRepo.save(entityExpediente);
   }

    function updateDesviacionesMateriasPrimas(expediente_id){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesMateriasPrimas");
        //console.log("expediente_id::: "+expediente_id);
        //console.log("q2_cumple_materias_primas::: "+entity.get("q2_cumple_materias_primas"));
        saveDesviacion("q2_materias_primas", expediente_id, entity.get("q2_cumple_materias_primas"), '');
    }

     function updateDesviacionesProductoTerminado(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProductoTerminado");
        if (entity.getId() != null){

            var expediente_id_view = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id_view);
            var productoTerminadoRepo = ctx.get("repos").get("productoTerminadoRepo");

            var lstProductoTerminado = productoTerminadoRepo.listAll();

            var i = 0;
            while(i != lstProductoTerminado.size()){
                //console.log("i: " + i);
                var entityProductoTerminado = lstProductoTerminado.get(i);
                var expediente_id_bbdd = entityProductoTerminado.get("expediente_id");

                if (expediente_id_view == expediente_id_bbdd){
                    var producto_terminado_id = new java.lang.String(entityProductoTerminado.get("producto_terminado_id"));
                    //console.log("producto_terminado_id::: "+producto_terminado_id);
                    //console.log("cumple_producto_terminado::: "+entityProductoTerminado.get("cumple_producto_terminado"));
                    saveDesviacion("producto_terminado", producto_terminado_id, entityProductoTerminado.get("cumple_producto_terminado"), entityProductoTerminado.get("notas_aclaratorias"));
                }
                i++;
            }
       }
    }

    function updateDesviacionesProductoTransformado(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProductoTransformado");
        if (entity.getId() != null){

            var expediente_id_view = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id_view);
            var productoTransformadoRepo = ctx.get("repos").get("productoTransformadoRepo");

            var lstProductoTransformado = productoTransformadoRepo.listAll();

            var i = 0;
            while(i != lstProductoTransformado.size()){
                //console.log("i: " + i);
                var entityProductoTransformado = lstProductoTransformado.get(i);
                var expediente_id_bbdd = entityProductoTransformado.get("expediente_id");

                if (expediente_id_view == expediente_id_bbdd){
                    var producto_transformado_id = new java.lang.String(entityProductoTransformado.get("producto_transformado_id"));
                    //console.log("producto_transformado_id::: "+producto_transformado_id);
                    //console.log("cumple_producto_transformado::: "+entityProductoTransformado.get("cumple_producto_transformado"));
                    saveDesviacion("producto_transformado", producto_transformado_id, entityProductoTransformado.get("cumple_producto_transformado"), entityProductoTransformado.get("notas_aclaratorias"));
                }
                i++;
            }
       }
    }

    function updateDesviacionesProductoEtiquetado(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProductoEtiquetado");
        if (entity.getId() != null){

            var expediente_id_view = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id_view);
            var productoEtiquetadoRepo = ctx.get("repos").get("productoEtiquetadoRepo");

            var lstProductoEtiquetado = productoEtiquetadoRepo.listAll();

            var i = 0;
            while(i != lstProductoEtiquetado.size()){
                //console.log("i: " + i);
                var entityProductoEtiquetado = lstProductoEtiquetado.get(i);
                var expediente_id_bbdd = entityProductoEtiquetado.get("expediente_id");

                if (expediente_id_view == expediente_id_bbdd){
                    var producto_etiquetado_id = new java.lang.String(entityProductoEtiquetado.get("producto_etiquetado_id"));
                    //console.log("producto_etiquetado_id::: "+producto_etiquetado_id);

                    //console.log("cumple_producto_etiquetado::: "+entityProductoEtiquetado.get("cumple_producto_etiquetado"));
                    saveDesviacion("producto_etiquetado_producto", producto_etiquetado_id, entityProductoEtiquetado.get("cumple_producto_etiquetado"), entityProductoEtiquetado.get("notas_aclaratorias"));

                    //console.log("cumple_material_etiquetado::: "+entityProductoEtiquetado.get("cumple_material_etiquetado"));
                    saveDesviacion("producto_etiquetado_material", producto_etiquetado_id, entityProductoEtiquetado.get("cumple_material_etiquetado"), entityProductoEtiquetado.get("notas_aclaratorias_material_etiquetado"));

                    //console.log("cumple_valor_nutricional::: "+entityProductoEtiquetado.get("cumple_valor_nutricional"));
                    saveDesviacion("producto_etiquetado_valor_nutricional", producto_etiquetado_id, entityProductoEtiquetado.get("cumple_valor_nutricional"), entityProductoEtiquetado.get("notas_aclaratorias_valor_nutricional"));
                }
                i++;
            }
       }
    }

    function updateDesviacionesVerificacionEquipoMedida(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesVerificacionEquipoMedida");
        if (entity.getId() != null){

            var expediente_id_view = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id_view);
            var verificacionEquipoMedidaRepo = ctx.get("repos").get("verificacionEquipoMedidaRepo");

            var lstVerificacionEquipoMedida = verificacionEquipoMedidaRepo.listAll();

            var i = 0;
            while(i != lstVerificacionEquipoMedida.size()){
                //console.log("i: " + i);
                var entityVerificacionEquipoMedida = lstVerificacionEquipoMedida.get(i);
                var expediente_id_bbdd = entityVerificacionEquipoMedida.get("expediente_id");

                if (expediente_id_view == expediente_id_bbdd){
                    var verificacion_equipo_medida_id = new java.lang.String(entityVerificacionEquipoMedida.get("verificacion_equipo_medida_id"));
                    //console.log("verificacion_equipo_medida_id::: "+verificacion_equipo_medida_id);
                    //console.log("cumple_verificacion_equipo_medida::: "+entityVerificacionEquipoMedida.get("cumple_equipo_medida"));
                    saveDesviacion("verificacion_equipo_medida", verificacion_equipo_medida_id, entityVerificacionEquipoMedida.get("cumple_equipo_medida"), entityVerificacionEquipoMedida.get("notas_aclaratorias"));
                }
                i++;
            }
       }
    }

    function updateDesviacionesProductoNoConforme(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProductoNoConforme");
        if (entity.getId() != null){

            var expediente_id_view = new java.lang.String(entity.getId());
            //console.log("expediente_id::: "+expediente_id_view);
            var productoNoConformeRepo = ctx.get("repos").get("productoNoConformeRepo");

            var lstProductoNoConforme = productoNoConformeRepo.listAll();

            var i = 0;
            while(i != lstProductoNoConforme.size()){
                //console.log("i: " + i);
                var entityProductoNoConforme = lstProductoNoConforme.get(i);
                var expediente_id_bbdd = entityProductoNoConforme.get("expediente_id");

                if (expediente_id_view == expediente_id_bbdd){
                    var producto_no_conforme_id = new java.lang.String(entityProductoNoConforme.get("producto_no_conforme_id"));
                    //console.log("producto_no_conforme_id::: "+producto_no_conforme_id);
                    //console.log("cumple_producto_no_conforme::: "+entityProductoNoConforme.get("cumple_producto_no_conforme"));
                    saveDesviacion("producto_no_conforme", producto_no_conforme_id, entityProductoNoConforme.get("cumple_producto_no_conforme"), entityProductoNoConforme.get("notas_aclaratorias"));
                }
                i++;
            }
       }
    }

    function updateDesviacionesControlDocumental(){
        //console.log("updateDesviacionesControlDocumental");

        if (entity.get("expediente_id") != null){

            var expediente_id = new java.lang.String(entity.get("expediente_id"));
            //console.log("expediente_id::: "+expediente_id);

            var procedimiento_autocontrol_id = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
            //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id);

            //Procedimiento autocontrol
            updateDesviacionesProcedimientoAutocontrol(procedimiento_autocontrol_id);


        }
    }

    /*function updateDesviacionesControlLote(){
        //console.log("updateDesviacionesControlLote");

        if (entity.get("expediente_id") != null){

            var expediente_id = new java.lang.String(entity.get("expediente_id"));
            //console.log("expediente_id::: "+expediente_id);

            var lote_control_documental_id = new java.lang.String(entity.get("lote_control_documental_id"));
            //console.log("lote_control_documental_id::: "+lote_control_documental_id);

            //ControlLote
            updateDesviacionesControlLote(lote_control_documental_id);
        }
    }*/

    function updateDesviacionesControlLote(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesControlLote");

        var lote_control_documental_id = new java.lang.String(entity.get("lote_control_documental_id"));
        //console.log("lalala lote_control_documental_id::: "+lote_control_documental_id);
        //console.log("cumple_cebolla_peso::: "+view.get("cumple_cebolla_peso"));
        //console.log("cebolla_notas::: "+view.get("cebolla_notas"))
        saveDesviacion("cebolla_horcal", lote_control_documental_id, view.get("cumple_cebolla_peso"), view.get("cebolla_notas"));
        saveDesviacion("arroz", lote_control_documental_id, view.get("cumple_arroz_peso"), view.get("arroz_notas"));
        saveDesviacion("sangre", lote_control_documental_id, view.get("cumple_sangre_peso"), view.get("sangre_notas"));
        saveDesviacion("manteca", lote_control_documental_id, view.get("cumple_manteca_peso"), view.get("manteca_notas"));
        saveDesviacion("especias_utilizadas", lote_control_documental_id, view.get("especias_utilizadas_cumple"), view.get("especias_utilizadas_notas"));
        saveDesviacion("fecha_precoccion", lote_control_documental_id, view.get("fecha_precoccion_cumple"), view.get("fecha_precoccion_notas"));
        saveDesviacion("comprobaciones_horcal", lote_control_documental_id, view.get("comprobaciones_horcal_cumple"), view.get("comprobaciones_horcal_notas"));
        saveDesviacion("temperatura_conservacion", lote_control_documental_id, view.get("temperatura_camara_manteca_cumple"), view.get("temperatura_camara_manteca_notas"));
        saveDesviacion("tripas_utilizadas", lote_control_documental_id, view.get("tripas_utilizadas_cumple"), view.get("tripas_utilizadas_notas"));
        saveDesviacion("temperatura_coccion", lote_control_documental_id, view.get("temperatura_coccion_morcilla_cumple"), view.get("temperatura_coccion_morcilla_notas"));
        saveDesviacion("tiempo_oreo", lote_control_documental_id, view.get("tiempo_oreo_cumple"), view.get("tiempo_oreo_notas"));
        saveDesviacion("cantidad_elaborada_despues_oreo", lote_control_documental_id, view.get("cantidad_elaborada_despues_oreo_cumple"), view.get("cantidad_elaborada_despues_oreo_notas"));
        saveDesviacion("fecha_transformado", lote_control_documental_id, view.get("fecha_transformado_cumple"), view.get("fecha_transformado_notas"));
        saveDesviacion("num_piezas_tranformadas", lote_control_documental_id, view.get("numero_piezas_cumple"), view.get("numero_piezas_notas"));
        saveDesviacion("num_envases_obtenidos", lote_control_documental_id, view.get("numero_envases_cumple"), view.get("numero_envases_notas"));
        saveDesviacion("marcas_etiquetas_enteras", lote_control_documental_id, view.get("marcas_etiquetadas_cumple"), view.get("marcas_etiquetadas_notas"));
        saveDesviacion("marcas_utilizadas_transformado", lote_control_documental_id, view.get("marcas_utilizadas_cumple"), view.get("marcas_utilizadas_notas"));
        saveDesviacion("balance_masas", lote_control_documental_id, view.get("balance_masas_cumple"), view.get("balance_masas_notas"));
        }

    function updateDesviacionesComunicacionesAlOrganoDeGestion(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesComunicacionesAlOrganoDeGestion");

        var marcaComunicadaRepo = ctx.get("repos").get("marcaComunicadaRepo");
        var lstMarcaComunicada = marcaComunicadaRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstMarcaComunicada.size()){
            var entityMarcaComunicada = lstMarcaComunicada.get(i);

            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityMarcaComunicada.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var marca_comunicada_id = new java.lang.String(entityMarcaComunicada.get("marca_comunicada_id"));
                //console.log("marca_comunicada_id::: "+marca_comunicada_id);
                saveDesviacion("marca_comunicada", marca_comunicada_id, entityMarcaComunicada.get("marca_comunicada_cumple"), entityMarcaComunicada.get("marca_comunicada_nota"));
            }
            i++;
        }
    }

    function updateDesviacionesRegistroProductoNoConforme(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("=updateDesviacionesRegistroProductoNoConforme");

        var registroProductoNoConformeRepo = ctx.get("repos").get("registroProductoNoConformeRepo");
        var lstRegistroProductoNoConforme = registroProductoNoConformeRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstRegistroProductoNoConforme.size()){
            var entityRegistroProductoNoConforme = lstRegistroProductoNoConforme.get(i);

            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityRegistroProductoNoConforme.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var registro_producto_no_conforme_id = new java.lang.String(entityRegistroProductoNoConforme.get("registro_producto_no_conforme_id"));
                //console.log("registro_producto_no_conforme_id::: "+registro_producto_no_conforme_id);
                saveDesviacion("registro_producto_no_conforme", registro_producto_no_conforme_id, entityRegistroProductoNoConforme.get("cumple"), entityRegistroProductoNoConforme.get("notas"));
            }
            i++;
        }
    }

    function updateDesviacionesCompraProductoEntreOperadores(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("=updateDesviacionesCompraProductoEntreOperadores");

        var compraProductoEntreOperadoresRepo = ctx.get("repos").get("compraProductoEntreOperadoresRepo");
        var lstCompraProductoEntreOperadores = compraProductoEntreOperadoresRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstCompraProductoEntreOperadores.size()){
            var entityCompraProductoEntreOperadores = lstCompraProductoEntreOperadores.get(i);

            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityCompraProductoEntreOperadores.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var compra_producto_entre_operadores_id = new java.lang.String(entityCompraProductoEntreOperadores.get("compra_producto_entre_operadores_id"));
                //console.log("compra_producto_entre_operadores_id::: "+compra_producto_entre_operadores_id);
                saveDesviacion("compra_producto_entre_operadores", compra_producto_entre_operadores_id, entityCompraProductoEntreOperadores.get("cumple"), entityCompraProductoEntreOperadores.get("notas"));
            }
            i++;
        }
    }

    function updateDesviacionesLoteControlCebollaProcesada(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("=updateDesviacionesLoteControlCebollaProcesada");

        var loteControlCebollaProcesadaRepo = ctx.get("repos").get("loteControlCebollaProcesadaRepo");
        var lstLoteControlCebollaProcesada = loteControlCebollaProcesadaRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstLoteControlCebollaProcesada.size()){
            var entityLoteControlCebollaProcesada = lstLoteControlCebollaProcesada.get(i);

            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityLoteControlCebollaProcesada.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var lote_control_cebolla_procesada_id = new java.lang.String(entityLoteControlCebollaProcesada.get("lote_control_cebolla_procesada_id"));
                saveDesviacion("lote_control_cebolla_procesada", lote_control_cebolla_procesada_id, entityLoteControlCebollaProcesada.get("cumple"), entityLoteControlCebollaProcesada.get("notas_aclaratorias"));
            }
            i++;
        }
    }

    function updateDesviacionesLoteControlCebollaFresca(){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("=updateDesviacionesLoteControlCebollaFresca");

        var loteControlCebollaFrescaRepo = ctx.get("repos").get("loteControlCebollaFrescaRepo");
        var lstLoteControlCebollaFresca = loteControlCebollaFrescaRepo.listAll();

        var procedimiento_autocontrol_id_view = new java.lang.String(entity.get("procedimiento_autocontrol_id"));
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id_view);

        var i = 0;
        while(i != lstLoteControlCebollaFresca.size()){
            var entityLoteControlCebollaFresca = lstLoteControlCebollaFresca.get(i);

            var procedimiento_autocontrol_id_bbdd = new java.lang.String(entityLoteControlCebollaFresca.get("procedimiento_autocontrol_id"));
            if (procedimiento_autocontrol_id_bbdd == procedimiento_autocontrol_id_view){
                var lote_control_cebolla_fresca_id = new java.lang.String(entityLoteControlCebollaFresca.get("lote_control_cebolla_fresca_id"));
                saveDesviacion("lote_control_cebolla_fresca", lote_control_cebolla_fresca_id, entityLoteControlCebollaFresca.get("cumple"), entityLoteControlCebollaFresca.get("notas_aclaratorias"));
            }
            i++;
        }
    }


    function updateDesviacionesProcedimientoAutocontrol(procedimiento_autocontrol_id){
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("--------------------------------------------------------------------------------------------");
        //console.log("updateDesviacionesProcedimientoAutocontrol");
        //console.log("procedimiento_autocontrol_id::: "+procedimiento_autocontrol_id);
        saveDesviacion("diagrama_flujo", procedimiento_autocontrol_id, view.get("diagrama_flujo_cumple"), view.get("diagrama_flujo_notas"));
        saveDesviacion("relacion_proveedores", procedimiento_autocontrol_id, view.get("relacion_proveedores_cumple"), view.get("relacion_proveedores_notas"));
        saveDesviacion("ficha_materias_primas", procedimiento_autocontrol_id, view.get("ficha_materias_primas_cumple"), view.get("ficha_materias_primas_notas"));
        saveDesviacion("ficha_cebolla", procedimiento_autocontrol_id, view.get("ficha_cebolla_cumple"), view.get("ficha_cebolla_notas"));
        saveDesviacion("ficha_arroz", procedimiento_autocontrol_id, view.get("ficha_arroz_cumple"), view.get("ficha_arroz_notas"));
        saveDesviacion("ficha_cebolla", procedimiento_autocontrol_id, view.get("ficha_cebolla_cumple"), view.get("ficha_cebolla_notas"));
        saveDesviacion("ficha_manteca", procedimiento_autocontrol_id, view.get("ficha_manteca_cumple"), view.get("ficha_manteca_notas"));
        saveDesviacion("ficha_sangre", procedimiento_autocontrol_id, view.get("ficha_sangre_cumple"), view.get("ficha_sangre_notas"));
        saveDesviacion("ficha_sal", procedimiento_autocontrol_id, view.get("ficha_sal_cumple"), view.get("ficha_sal_notas"));
        saveDesviacion("ficha_especias_1", procedimiento_autocontrol_id, view.get("ficha_especias_1_cumple"), view.get("ficha_especias_1_notas"));
        saveDesviacion("ficha_especias_2", procedimiento_autocontrol_id, view.get("ficha_especias_2_cumple"), view.get("ficha_especias_2_notas"));
        saveDesviacion("ficha_especias_3", procedimiento_autocontrol_id, view.get("ficha_especias_3_cumple"), view.get("ficha_especias_3_notas"));
        saveDesviacion("ficha_tripa", procedimiento_autocontrol_id, view.get("ficha_tripa_cumple"), view.get("ficha_tripa_notas"));
        saveDesviacion("registros_cebolla_horcal_fresca", procedimiento_autocontrol_id, view.get("registros_cebolla_horcal_fresca_cumple"), view.get("registros_cebolla_horcal_fresca_notas"));
        saveDesviacion("registros_cebolla_horcal_procesada", procedimiento_autocontrol_id, view.get("registros_cebolla_horcal_procesada_cumple"), view.get("registros_cebolla_horcal_procesada_notas"));
        saveDesviacion("proceso_fabricacion", procedimiento_autocontrol_id, view.get("proceso_fabricacion_cumple"), view.get("proceso_fabricacion_notas"));
        saveDesviacion("plan_analisis", procedimiento_autocontrol_id, view.get("plan_analisis_cumple"), view.get("plan_analisis_notas"));
        saveDesviacion("plan_analisis_periodicidad_fq", procedimiento_autocontrol_id, view.get("plan_analisis_periodicidad_fq_cumple"), view.get("plan_analisis_periodicidad_fq_notas"));
        saveDesviacion("plan_analisis_periodicidad_morf", procedimiento_autocontrol_id, view.get("plan_analisis_periodicidad_morf_cumple"), view.get("plan_analisis_periodicidad_morf_notas"));
        saveDesviacion("plan_analisis_periodicidad_org", procedimiento_autocontrol_id, view.get("plan_analisis_periodicidad_org_cumple"), view.get("plan_analisis_periodicidad_org_notas"));
        saveDesviacion("plan_analisis_parametros_fq", procedimiento_autocontrol_id, view.get("plan_analisis_parametros_fq_cumple"), view.get("plan_analisis_parametros_fq_notas"));
        saveDesviacion("plan_analisis_parametros_morfologicos", procedimiento_autocontrol_id, view.get("plan_analisis_parametros_morfologicos_cumple"), view.get("plan_analisis_parametros_morfologicos_notas"));
        saveDesviacion("plan_analisis_parametros_sensoriales", procedimiento_autocontrol_id, view.get("plan_analisis_parametros_sensoriales_cumple"), view.get("plan_analisis_parametros_sensoriales_notas"));
        saveDesviacion("plan_analisis_ficha_cata", procedimiento_autocontrol_id, view.get("plan_analisis_ficha_cata_cumple"), view.get("plan_analisis_ficha_cata_notas"));
        saveDesviacion("calibracion_equipos_medida", procedimiento_autocontrol_id, view.get("calibracion_equipos_medida_cumple"), view.get("calibracion_equipos_medida_notas"));
        saveDesviacion("registro_contraetiquetas_medida", procedimiento_autocontrol_id, view.get("registro_contraetiquetas_medida_cumple"), view.get("registro_contraetiquetas_medida_notas"));
        saveDesviacion("procedimiento_segregacion_gestion", procedimiento_autocontrol_id, view.get("procedimiento_segregacion_gestion_cumple"), view.get("ficha_especias_notas"));

    }

    function saveDesviacion(pregunta_id, id_view, cumple, observaciones){
        //console.log("saveDesviacion");
        var desviacionRepo = ctx.get("repos").get("desviacionRepo");
        //console.log("id: "+id_view+'_'+pregunta_id);
        var entityDesviacion = desviacionRepo.findById(id_view+'_'+pregunta_id);
        if (entityDesviacion != null){
            //console.log("cumple: "+cumple);
            //console.log("observaciones: "+observaciones);
            entityDesviacion.set("cumple",cumple);
            entityDesviacion.set("notas_aclaratorias",observaciones);
            desviacionRepo.save(entityDesviacion);
        }
    }

    function updateNotasAclaratoriasCuestionario(){
        //console.log("updateNotasAclaratoriasCuestionario");
        var notasAclaratorias = view.get("notas_aclaratorias");
        //console.log("notasAclaratorias::: "+notasAclaratorias);

        var desviacion_id = entity.get("desviacion_id");
        var apartado = entity.get("apartado");
        //console.log("apartado::: "+apartado);

        var id = desviacion_id.split("_", 2)[0];
        var restoId = desviacion_id.split("_", 2)[1];
        //console.log("id::: "+id);
        //console.log("restoId::: "+restoId);

        if (restoId.startsWith("q1") || restoId.startsWith("q3")){
            //console.log("apartado.startsWith('q')");
            var varRepo = ctx.get("repos").get("actasControlRepo");
            var varEntity = varRepo.findById(new java.lang.Long(id));
        }
        var listaRepo=["productoTerminadoRepo","productoTransformadoRepo","productoEtiquetadoRepo", "productoNoConformeRepo", "verificacionEquipoMedidaRepo", "procedimientoAutocontrolRepo", "marcaComunicadaRepo", "marcaComunicadaRepo", "registroProductoNoConformeRepo", "compraProductoEntreOperadoresRepo", "loteControlDocumentalRepo", "loteControlCebollaProcesadaRepo", "loteControlCebollaFrescaRepo", "loteControlResultadosFqRepo", "loteControlResultadosOrganolepticosRepo", "loteControlResultadosMorfologicosRepo"];
        for(var i=0; i<listaRepo.length && varEntity==null; i++) {
             //console.log("listaRepo[i]::: "+listaRepo[i]);
             var varRepo = ctx.get("repos").get(listaRepo[i]);
             var varEntity = varRepo.findById(new java.lang.Long(id));
             //console.log("listaRepo[i]::: "+listaRepo[i]);
        }

        varEntity.set(apartado, notasAclaratorias);
        varRepo.save(varEntity);
    }


    function updateDatatableDesviaciones(){
        //console.log("updateDatatableDesviaciones");

    }