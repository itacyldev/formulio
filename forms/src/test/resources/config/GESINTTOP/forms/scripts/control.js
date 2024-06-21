    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
    var entityRecinto;

    function updateAgregado(component){
        //console.log("updateAgregado");
        updateCampo(component, "c_agregado");
    }

    function updateZona(component){
        //console.log("updateZona");
        updateCampo(component, "c_zona");
    }

    function updatePoligono(component){
        //console.log("updatePoligono");
        updateCampo(component, "c_poligono");
    }

    function updateParcela(component){
        //console.log("updateParcela");
        updateCampo(component, "c_parcela");
    }

    function updateRecinto(component){
        //console.log("updateRecinto");
        updateCampo(component, "c_recinto");
    }

    function updateCampo(component, campo){
        //console.log("updateCampo");
        entityRecinto = setEntityRecinto();

        if (entityRecinto != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityRecinto.get(campo)));
        }else{
            component.setValueExpression(null);
        }
    }

    function setEntityRecinto(){
        //console.log("setEntityRecinto");
        var recinto_id_view = view.get("c_recinto_id");
        //console.log("recinto_id_view::: "+recinto_id_view);
        /*var recinto_id_bbdd = entity.get("c_recinto_id");
        //console.log("recinto_id_bbdd::: "+recinto_id_bbdd);*/

        entityRecinto = null;
        if (recinto_id_view != null){
            var recintoRepo = ctx.get("repos").get("recintoRepo");
            var lstRecintos = recintoRepo.listAll();
            var i = 0;
             var encontrado = false;
            while(i != lstRecintos.size()){
                //console.log("i: " + i);
                entityRecinto = lstRecintos.get(i);
                var recinto_id_bbdd = entityRecinto.get("c_recinto_id");
                if (recinto_id_bbdd == recinto_id_view){
                    encontrado = true;
                    break;
                }
                i++;
            }

            if (!encontrado){
                entityRecinto = null;
            }
        }
        return entityRecinto;
    }

    function createTransecto(visita_id, tipo_transecto_id, num_unidades_muestrales_transecto, num_transecto, visita){
        //console.log("createTransecto");

        var visita_id = new java.lang.Long(entity.get("visita_id"));
        //console.log("visita_id::: "+visita_id);
        //console.log("tipo_transecto_id::: "+tipo_transecto_id);
        //console.log("num_unidades_muestrales_transecto::: "+num_unidades_muestrales_transecto);
        //console.log("num_transecto::: "+num_transecto);

        var transectoRepo = ctx.get("repos").get("transectoRepo");
        var entityTransecto = transectoRepo.newEntity();
        entityTransecto.set("visita_id", visita_id);
        entityTransecto.set("tipo_transecto_id", new java.lang.Long(tipo_transecto_id));
        entityTransecto.set("num_unidades_muestrales_transecto", new java.lang.Long(num_unidades_muestrales_transecto));
        entityTransecto.set("num_transecto", new java.lang.Long(num_transecto));
        entityTransecto.set("prefijo_gps", visita+"-"+num_transecto);
        entityTransecto.set("trabajo_realizado", 'NO');


        transectoRepo.save(entityTransecto);

        var transecto_id = entityTransecto.get("transecto_id");

        createUnidadesMuestrales(transecto_id, new java.lang.Long(num_unidades_muestrales_transecto));

    }

    function createUnidadesMuestrales(transecto_id, num_unidades_muestrales_transecto){
        //console.log("createUnidadesMuestrales");
        var i = 1;
        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");

        while(i != num_unidades_muestrales_transecto + 1){
            var entityUnidadMuestral = unidadMuestralRepo.newEntity();
            entityUnidadMuestral.set("num_unidad_muestral", new java.lang.Long(i));
            entityUnidadMuestral.set("transecto_id",transecto_id);
            unidadMuestralRepo.save(entityUnidadMuestral);
            i++;
        }

    }

    function aplicarRellenoAutomatico(transecto_id, hurasTotalesAutomatico, hurasActivasAutomatico, densidadCubiertaAutomatico,
        porcentajeDanoAutomatico, trampasAutomatico, caminillosAutomatico, alturaCubiertaAutomatico){

        //console.log("aplicarRellenoAutomatico");

        aplicar("c_huras_totales_transecto_id", hurasTotalesAutomatico);
        aplicar("c_huras_activas_transecto_id", hurasActivasAutomatico);
        aplicar("c_densidad_cubierta_transecto_id", densidadCubiertaAutomatico);
        aplicar("c_porcentaje_dano_transecto_id", porcentajeDanoAutomatico);
        aplicar("c_presencia_trampas_transecto_id", trampasAutomatico);
        aplicar("c_caminillos_transecto_id", caminillosAutomatico);
        aplicar("c_altura_cubierta_transecto_id", alturaCubiertaAutomatico);

    }

    function aplicar(id, valorAutomatico){
        //console.log("aplicarRellenoAutomatico");

        // obtener todos los context Holders de la vista
        var viewContexts = vh.viewHolders();

        // filtramos para quedarnos con lo datalistitems
        var strView = "view."+id;
        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitemUnidadMuestral') && obj.widgetContext.get(strView) == null);

        // obtenemos los contextos de vista ('view')
        lst = lst.apply(obj => obj.widgetContext.viewContext);

        lst.apply(obj => obj.set(id,valorAutomatico));
    }

    function getNumUnidadMuestral(transecto_id){
        //console.log("getNumUnidadMuestral");
        var num_unidad_muestral = 1;
        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");

        var lstUnidadesMuestrales = unidadMuestralRepo.listAll();
        var i = lstUnidadesMuestrales.size();
        //console.log(" lstUnidadesMuestrales.size():::" +i)
        var encontrado = false;
        while(i != 0){
            entityUnidadMuestral = lstUnidadesMuestrales.get(i-1);
            var transecto_id_bbdd = entityUnidadMuestral.get("transecto_id");
            if (transecto_id_bbdd == transecto_id){
                encontrado = true;
                break;
            }
        i--;
        }

        if (encontrado){
            num_unidad_muestral = entityUnidadMuestral.get("num_unidad_muestral")+1;
        }
        //console.log("num_unidad_muestral:::" +num_unidad_muestral)
        return num_unidad_muestral;
    }

    function anadirUnidadMuestral(transecto_id){
        //console.log("anadirUnidadMuestral");

        //console.log("transecto_id::: "+transecto_id);

        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");
        var entityUnidadMuestral = unidadMuestralRepo.newEntity();
        entityUnidadMuestral.set("transecto_id", new java.lang.Long(transecto_id));
        var num_unidades_muestrales_transecto = new java.lang.Long(getNumUnidadMuestral(transecto_id));
        entityUnidadMuestral.set("num_unidad_muestral", num_unidades_muestrales_transecto);
        unidadMuestralRepo.save(entityUnidadMuestral);

        var transectoRepo = ctx.get("repos").get("transectoRepo");
        //console.log("transecto_id::: "+transecto_id);
        entityTransecto = transectoRepo.findById(new java.lang.Long(transecto_id));
        entityTransecto.set("num_unidades_muestrales_transecto",num_unidades_muestrales_transecto);
        transectoRepo.save(entityTransecto);

        vh.updateWidget('num_unidades_muestrales_transecto')



    }

    function setTrabajoRealizado(){
        //console.log("setTrabajoRealizado");
        var visita_id = entity.get("visita_id");

        var transectoRepo = ctx.get("repos").get("transectoRepo");
        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");
        var lstTransectos = transectoRepo.listAll();
        var i = 0;
        while(i != lstTransectos.size()){
            entityTransecto = lstTransectos.get(i);
            var trabajo_realizado = 'SI';
            var lstUnidadesMuestrales = unidadMuestralRepo.listAll();
            var j = 0;
            while(j != lstUnidadesMuestrales.size()){
                entityUnidadMuestral = lstUnidadesMuestrales.get(j);
                if (entityUnidadMuestral.get("transecto_id") == entityTransecto.get("transecto_id")){
                    if (
                        entityUnidadMuestral.get("c_huras_totales_transecto_id") == null ||
                        entityUnidadMuestral.get("c_huras_activas_transecto_id") == null ||
                        entityUnidadMuestral.get("c_densidad_cubierta_transecto_id") == null ||
                        entityUnidadMuestral.get("c_porcentaje_dano_transecto_id") == null ||
                        entityUnidadMuestral.get("c_porcentaje_dano_transecto_id") == null ||
                        entityUnidadMuestral.get("c_caminillos_transecto_id") == null ||
                        entityUnidadMuestral.get("c_altura_cubierta_transecto_id") == null){
                        trabajo_realizado = 'NO';
                        break;
                    }
                }
                j++
            }
            //console.log("trabajo_realizado::: "+trabajo_realizado);
            entityTransecto.set("trabajo_realizado", trabajo_realizado);
            transectoRepo.save(entityTransecto);
            i++;
        }
    }

    function updateCodigoProvincia(){
        var widgetProvincia= vh.widget("c_provincia_id");
        var valueProvincia = widgetProvincia.getValue()!=null?completeWithZeros(""+widgetProvincia.getValue(),2):"";
        widgetCodProvincia = vh.widget("cod_provincia");
        widgetCodProvincia.setValue(valueProvincia);
    }

    function updateCodigoMunicipio(){
        var widgetMunicipio= vh.widget("c_provmuni_id");
        var valueMunicipio = widgetMunicipio.getValue()!=null?completeWithZeros(""+widgetMunicipio.getValue(),5):"";
        widgetCodMunicipio = vh.widget("cod_municipio");
        widgetCodMunicipio.setValue(valueMunicipio);
    }

    function updateReferencia(){
        var widgetReferencia= vh.widget("c_refrec");

        var widgetProvincia= vh.widget("c_provincia_id");
        var valueProvincia = widgetProvincia.getValue()!=null?completeWithZeros(""+widgetProvincia.getValue(),2):"";
        widgetCodProvincia = vh.widget("cod_provincia");
        widgetCodProvincia.setValue(valueProvincia);

        var widgetMunicipio= vh.widget("c_provmuni_id");
        var valueMunicipio = widgetMunicipio.getValue()!=null?completeWithZeros(""+widgetMunicipio.getValue(),5):"";
        widgetCodMunicipio = vh.widget("cod_municipio");
        widgetCodMunicipio.setValue(valueMunicipio);

        var widgetAgregado= vh.widget("c_agregado");
        var valueAgregado = widgetAgregado.getValue()!=null?widgetAgregado.getValue():"";

        var widgetZona= vh.widget("c_zona");
        var valueZona = widgetZona.getValue()!=null?widgetZona.getValue():"";


        var widgetPoligono= vh.widget("c_poligono");
        var valuePoligono = widgetPoligono.getValue()!=null?widgetPoligono.getValue():"";

        var widgetParcela= vh.widget("c_parcela");
        var valueParcela = widgetParcela.getValue()!=null?widgetParcela.getValue():"";

        var widgetRecinto= vh.widget("c_recinto");
        var valueRecinto = widgetRecinto.getValue()!=null?widgetRecinto.getValue():"";

        var valueAgregado = valueAgregado.length!=0?completeWithZeros(""+valueAgregado,1):"";
        var valueZona = valueZona.length!=0?completeWithZeros(""+valueZona,1):"";
        var valuePoligono = valuePoligono.length!=0?completeWithZeros(""+valuePoligono,6):"";
        var valueParcela = valueParcela.length!=0?completeWithZeros(""+valueParcela,5):"";
        var valueRecinto = valueRecinto.length!=0?completeWithZeros(""+valueRecinto,5):"";

        if (valueProvincia.length!=0 && valueMunicipio.length!=0 && valueAgregado.length!=0 && valueZona.length!=0 && valuePoligono.length!=0 && valueParcela.length!=0 && valueRecinto.length!=0){
            widgetReferencia.setValue(valueMunicipio+valueAgregado+valueZona+valuePoligono+valueParcela+valueRecinto);
            widgetAgregado.setValue(valueAgregado);
            widgetZona.setValue(valueZona);
            widgetPoligono.setValue(valuePoligono);
            widgetParcela.setValue(valueParcela);
            widgetRecinto.setValue(valueRecinto);
        }

    }

    function updateCamposRecinto(){
        var widgetReferencia= vh.widget("c_refrec");
        var valueReferencia = widgetReferencia.getValue();
        if (valueReferencia.length!=0){

            var widgetProvincia= vh.widget("c_provincia_id");
            widgetProvincia.setValue(valueReferencia.substring(0,2));
            updateCodigoProvincia();

            var widgetMunicipio= vh.widget("c_provmuni_id");
            widgetMunicipio.setValue(valueReferencia.substring(0,5));
            updateCodigoMunicipio();

            var widgetAgregado= vh.widget("c_agregado");
            widgetAgregado.setValue(valueReferencia.substring(5,6));

            var widgetZona= vh.widget("c_zona");
            widgetZona.setValue(valueReferencia.substring(6,7));

            var widgetPoligono= vh.widget("c_poligono");
            widgetPoligono.setValue(valueReferencia.substring(7,13));

            var widgetParcela= vh.widget("c_parcela");
            widgetParcela.setValue(valueReferencia.substring(13,18));

            var widgetRecinto= vh.widget("c_recinto");
            widgetRecinto.setValue(valueReferencia.substring(18,23));

        }

    }

    function completeWithZeros(txt, lengthTxt){
        while (txt.length<lengthTxt){
            txt = "0"+txt;
        }
        return txt.substring(0,lengthTxt);
    }

    function setColor(widget){
        //console.log("setColor");
        //console.log("This is a widget" + widget.getId());

        widget.getInputView().setBackgroundColor(-500136);
        widget.getInputView().setTextColor(-500136);
    }


        function aplicarRellenoAutomaticoParaGustavo(transecto_id, num_unidades_muestrales_transecto, hurasTotalesAutomatico){
            //console.log("-------------------------------------------------------------");
            //console.log("-------------------------------------------------------------");
            //console.log("-------------------------------------------------------------");
             //console.log("aplicarRellenoAutomaticoParaGustavo");
             var transecto_id = entity.get("transecto_id");
             //console.log("transecto_id::: "+transecto_id);

             //console.log("hurasTotalesAutomatico::: "+hurasTotalesAutomatico);

             var num_unidad_muestral = view.get("num_unidad_muestral");
             //console.log("num_unidad_muestral::: "+num_unidad_muestral);

             var c_huras_totales_transecto_id = view.get("c_huras_totales_transecto_id");
             //console.log("c_huras_totales_transecto_id::: "+c_huras_totales_transecto_id);

             //console.log("num_unidad_muestral_1::: "+view.get("num_unidad_muestral_1"));
             //console.log("num_unidad_muestral_2::: "+view.get("num_unidad_muestral_2"));



        }

    function updatePrefijoGPS(){
        //console.log("updatePrefijoGPS");

        var visita_id_view = new java.lang.Long(entity.get("visita_id"));

        var transectoRepo = ctx.get("repos").get("transectoRepo");
        var lstTransectos = transectoRepo.listAll();
        var i = 0;
        while(i != lstTransectos.size()){
            entityTransecto = lstTransectos.get(i);
            var visita_id_bbdd = entityTransecto.get("visita_id");
            if (visita_id_view == visita_id_bbdd){
                  entityTransecto.set("prefijo_gps", view.get("visita")+"-"+entityTransecto.get("num_transecto"));
                   transectoRepo.save(entityTransecto);
            }
            i++;
        }
    }

    function setNumUnidadMuestral(component){
        //console.log("setNumUnidadMuestral");

    }

     function deleteUnidadMuestral(transecto_id) {
         //console.log("deleteUnidadMuestral");
         //console.log("transecto_id::: "+transecto_id);
         var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");
         var lstUnidadesMuestrales = unidadMuestralRepo.listAll();
         var i = 0;
         while(i != lstUnidadesMuestrales.size()){
            entityUnidadMuestral = lstUnidadesMuestrales.get(i);
            if (entityUnidadMuestral.get("transecto_id") == transecto_id){
                //console.log("i::: "+entityUnidadMuestral.get("unidad_muestral_id"));
                unidadMuestralRepo.deleteById(entityUnidadMuestral.get("unidad_muestral_id"));
            }
            i++
         }
     }

    function deleteTransecto(transecto_id) {
        //console.log("deleteTransecto");
        //console.log("transecto_id::: "+transecto_id);
        var transectoRepo = ctx.get("repos").get("transectoRepo");
        deleteUnidadMuestral(transecto_id);
        transectoRepo.deleteById(new java.lang.Long(transecto_id));
    }

    function deleteVisita(visita_id) {
        //console.log("deleteVisita");
        //console.log("visita_id::: "+visita_id);
        var visita_id_view = new java.lang.Long(entity.get("visita_id"));

        var transectoRepo = ctx.get("repos").get("transectoRepo");
        var lstTransectos = transectoRepo.listAll();
        var i = 0;
        while(i != lstTransectos.size()){
            entityTransecto = lstTransectos.get(i);
            var visita_id_bbdd = entityTransecto.get("visita_id");
            if (visita_id_view == visita_id_bbdd){
                deleteTransecto(entityTransecto.get("transecto_id"));
            }
            i++;
        }
         var visitaRepo = ctx.get("repos").get("visitaRepo");
         visitaRepo.deleteById(visita_id);

    }

     function saveValorUnidadMuestral(unidad_muestral_id, campo, valor){
        //console.log("saveValorUnidadMuestral");
        //console.log("unidad_muestral_id::: "+new java.lang.Long(unidad_muestral_id));
        //console.log("campo::: "+campo);
        //console.log("valor::: "+valor);

         if (valor == null){
             var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");
             var entityUnidadMuestral =  unidadMuestralRepo.findById(new java.lang.Long(unidad_muestral_id));
             //console.log("entityUnidadMuestral::: "+entityUnidadMuestral);
             entityUnidadMuestral.set(campo, valor);
             unidadMuestralRepo.save(entityUnidadMuestral);
         }
     }

    function updateHurasActivas(num_unidad_muestral){
        //console.log("updateHurasActivas");
        //console.log("num_unidad_muestral::: "+num_unidad_muestral);

        updateHuras(num_unidad_muestral, "c_huras_totales_transecto_id", "c_huras_activas_transecto_id");

    }



     function updateHurasTotales(num_unidad_muestral){
        //console.log("updateHurasTotales");
        //console.log("num_unidad_muestral::: "+num_unidad_muestral);

        updateHuras(num_unidad_muestral, "c_huras_activas_transecto_id", "c_huras_totales_transecto_id");

   }

    function updateHuras(num_unidad_muestral, strHurasOrigen, strHurasDestino){
        //console.log("updateHurasTotales");
        //console.log("num_unidad_muestral::: "+num_unidad_muestral);

        var viewContexts = vh.viewHolders();

        // filtramos para quedarnos con lo datalistitems
        var strView = "view."+strHurasDestino;
        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitemUnidadMuestral') && obj.widgetContext.get("view.num_unidad_muestral") == num_unidad_muestral && obj.widgetContext.get(strView) == null);

        // obtenemos los contextos de vista ('view')
        lst = lst.apply(obj => obj.widgetContext.viewContext);

        lst.apply(obj => obj.set(strHurasDestino,obj.get(strHurasOrigen)));

    }




