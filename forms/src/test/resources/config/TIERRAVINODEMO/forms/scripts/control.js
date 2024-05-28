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

    function updateVolumenAforadoGranelBarrica(){
        //console.log("updateVolumenAforadoGranelBarrica");

        var granelBarricaRepo = ctx.get("repos").get("granelBarricaRepo");
        var lstGranelBarrica = granelBarricaRepo.listAll();
        var i = 0;
        while(i != lstGranelBarrica.size()){
            entityGranelBarrica = lstGranelBarrica.get(i);
            var volumenAforado = entityGranelBarrica.get("capacidad")*entityGranelBarrica.get("numero_barricas");
            entityGranelBarrica.set("volumen_aforado",volumenAforado);
            granelBarricaRepo.save(entityGranelBarrica);
            i++
        }
    }

    function updateVolumenAforadoEnvasado(){
        //console.log("updateVolumenAforadoEnvasado");

        var envasadoRepo = ctx.get("repos").get("envasadoRepo");
        var lstEnvasado = envasadoRepo.listAll();
        var i = 0;
        while(i != lstEnvasado.size()){
            entityEnvasado = lstEnvasado.get(i);
            var volumenAforado = (entityEnvasado.get("numero_jaulones")*entityEnvasado.get("numero_botellas_jaulon") + entityEnvasado.get("numero_botellas_sueltas"))*entityEnvasado.get("volumen_botella");
            entityEnvasado.set("volumen_aforado",volumenAforado);
            envasadoRepo.save(entityEnvasado);
            i++
        }
    }

    function updateVolumenAforadoEtiquetado(){
        //console.log("updateVolumenAforadoEtiquetado");

        var etiquetadoRepo = ctx.get("repos").get("etiquetadoRepo");
        var lstEtiquetado = etiquetadoRepo.listAll();
        var i = 0;
        while(i != lstEtiquetado.size()){
            entityEtiquetado = lstEtiquetado.get(i);
            var volumenAforado = (entityEtiquetado.get("numero_pales")*entityEtiquetado.get("numero_cajas_pales")*entityEtiquetado.get("numero_botellas_caja") + entityEtiquetado.get("numero_cajas_sueltas")*entityEtiquetado.get("numero_botellas_caja") + entityEtiquetado.get("numero_botellas_sueltas"))*entityEtiquetado.get("volumen_botella");
            entityEtiquetado.set("volumen_aforado",volumenAforado);
            etiquetadoRepo.save(entityEtiquetado);
            i++
        }
    }

    function updateVolumenAforado(){
        //console.log("updateVolumenAforado");

        var partidaAforoRepo = ctx.get("repos").get("partidaAforoRepo");
        var lstPartidaAforo = partidaAforoRepo.listAll();
        var i = 0;
        while(i != lstPartidaAforo.size()){
            var entityPartidaAforo = lstPartidaAforo.get(i);
            var partida_aforo_id = entityPartidaAforo.get("partida_aforo_id");
            //console.log("partida_aforo_id::: "+partida_aforo_id);
            var volumenAforadoGranelDeposito = getVolumenAforado(partida_aforo_id, "granelDepositoRepo");
            //console.log("volumenAforadoGranelDeposito::: "+volumenAforadoGranelDeposito);
            var volumenAforadoGranelBarrica = getVolumenAforado(partida_aforo_id, "granelBarricaRepo");
            //console.log("volumenAforadoGranelBarrica::: "+volumenAforadoGranelBarrica);
            var volumenAforadoEnvasado = getVolumenAforado(partida_aforo_id, "envasadoRepo");
            //console.log("volumenAforadoEnvasado::: "+volumenAforadoEnvasado);
            var volumenAforadoEtiquetado = getVolumenAforado(partida_aforo_id, "etiquetadoRepo");
            //console.log("volumenAforadoEtiquetado::: "+volumenAforadoEtiquetado);
            var volumenAforado = volumenAforadoGranelDeposito + volumenAforadoGranelBarrica + volumenAforadoEnvasado + volumenAforadoEtiquetado;
            //console.log("volumenAforado::: "+volumenAforado);
            if (volumenAforado!=0){
                entityPartidaAforo.set("total_volumen_aforado", volumenAforado);
                partidaAforoRepo.save(entityPartidaAforo);
            }
            i++;
        }
    }

    function getVolumenAforado(partida_aforo_id_view, txtRepo){
        //console.log("getVolumenAforado");
        var repo = ctx.get("repos").get(txtRepo);
        var lst = repo.listAll();
        var i = 0;
        var volumenAforado = 0;
        while(i != lst.size()){
            entity = lst.get(i);
            var partida_aforo_id_bbdd = entity.get("partida_aforo_id");
            //console.log("partida_aforo_id_bbdd::: "+partida_aforo_id_bbdd);
            //console.log("partida_aforo_id_view::: "+partida_aforo_id_view);
            if (partida_aforo_id_bbdd == partida_aforo_id_view){
                volumenAforado = volumenAforado + entity.get("volumen_aforado");
                //console.log("volumenAforado::: "+volumenAforado);
            }
            i++;
        }
        return volumenAforado;
    }

    function updateTotalesExpedienteVino(){
        //console.log("updateTotalesExpedienteVino");
        var expedienteVinoRepo = ctx.get("repos").get("expedienteVinoRepo");
        var expedienteRepo = ctx.get("repos").get("expedienteRepo");
        var lstExpediente = expedienteRepo.listAll();
        var i = 0;
        expedienteVinoRepo.deleteAll();

        var totalTotalBodega=0;
        var totalTotalGranelDeposito=0;
        var totalTotalGranelBarrica=0;
        var totalTotalEnvasado=0;
        var totalTotalEtiquetado=0;

        while(i != lstExpediente.size()){
            var entityExpediente = lstExpediente.get(i);
            var expediente_id = entityExpediente.get("expediente_id");

            //Blanco
            var entityExpedienteVino = expedienteVinoRepo.newEntity();
            var totalGranelDeposito = getTotalVolumenAforadoTipoVino(expediente_id,  new java.lang.Long(1), "granelDepositoRepo");
            var totalGranelBarrica = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "granelBarricaRepo");
            var totalEnvasado = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "envasadoRepo");
            var totalEtiquetado = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "etiquetadoRepo");
            var totalBodega = totalGranelDeposito + totalGranelBarrica + totalEnvasado + totalEtiquetado;

            totalTotalBodega = totalTotalBodega + totalBodega;
            totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
            totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
            totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
            totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

            entityExpedienteVino.set("expediente_id", expediente_id);
            entityExpedienteVino.set("vino", new java.lang.String("Total blanco"));
            entityExpedienteVino.set("total_granel_deposito", totalGranelDeposito);
            entityExpedienteVino.set("total_granel_barrica", totalGranelBarrica);
            entityExpedienteVino.set("total_envasado", totalEnvasado);
            entityExpedienteVino.set("total_etiquetado", totalEtiquetado);
            entityExpedienteVino.set("total_bodega", totalBodega);
            expedienteVinoRepo.save(entityExpedienteVino);

            //Tintos = tinto + clarete + rosado
            totalGranelDeposito = 0;
            totalGranelBarrica = 0;
            totalEnvasado = 0;
            totalEtiquetado = 0;
            totalBodega = 0;
            for (var j=2; j< 5; j++){
                //console.log("updateTotalesExpedienteVino j:::"+j);
                totalGranelDeposito = totalGranelDeposito + getTotalVolumenAforadoTipoVino(expediente_id,  new java.lang.Long(j), "granelDepositoRepo");
                totalGranelBarrica = totalGranelBarrica + getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(j), "granelBarricaRepo");
                totalEnvasado = totalEnvasado + getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(j), "envasadoRepo");
                totalEtiquetado = totalEtiquetado + getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(j), "etiquetadoRepo");
            }
            totalBodega = totalGranelDeposito + totalGranelBarrica + totalEnvasado + totalEtiquetado;

            totalTotalBodega = totalTotalBodega + totalBodega;
            totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
            totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
            totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
            totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

            entityExpedienteVino = expedienteVinoRepo.newEntity();
            entityExpedienteVino.set("expediente_id", expediente_id);
            entityExpedienteVino.set("vino", new java.lang.String("Total tintos"));
            entityExpedienteVino.set("total_granel_deposito", totalGranelDeposito);
            entityExpedienteVino.set("total_granel_barrica", totalGranelBarrica);
            entityExpedienteVino.set("total_envasado", totalEnvasado);
            entityExpedienteVino.set("total_etiquetado", totalEtiquetado);
            entityExpedienteVino.set("total_bodega", totalBodega);
            expedienteVinoRepo.save(entityExpedienteVino);

            entityExpedienteVino = expedienteVinoRepo.newEntity();
            entityExpedienteVino.set("expediente_id", expediente_id);
            entityExpedienteVino.set("vino", new java.lang.String("TOTAL BODEGA"));
            entityExpedienteVino.set("total_granel_deposito", totalTotalGranelDeposito);
            entityExpedienteVino.set("total_granel_barrica", totalTotalGranelBarrica);
            entityExpedienteVino.set("total_envasado", totalTotalEnvasado);
            entityExpedienteVino.set("total_etiquetado", totalTotalEtiquetado);
            entityExpedienteVino.set("total_bodega", totalTotalBodega);
            expedienteVinoRepo.save(entityExpedienteVino);
            i++;
        }
    }

    function updateTotalesExpedienteNivelProteccion(){
        //console.log("updateTotalesExpedienteNivelProteccion");
        var expedienteNivelProteccionRepo = ctx.get("repos").get("expedienteNivelProteccionRepo");
        var expedienteRepo = ctx.get("repos").get("expedienteRepo");
        var lstExpediente = expedienteRepo.listAll();
        var i = 0;
        expedienteNivelProteccionRepo.deleteAll();

        var totalTotalBodega=0;
        var totalTotalGranelDeposito=0;
        var totalTotalGranelBarrica=0;
        var totalTotalEnvasado=0;
        var totalTotalEtiquetado=0;

        while(i != lstExpediente.size()){
            var entityExpediente = lstExpediente.get(i);
            var expediente_id = entityExpediente.get("expediente_id");


            var totalGranelDeposito = 0;
            var totalGranelBarrica = 0;
            var totalEnvasado = 0;
            var totalEtiquetado = 0;
            var totalBodega = 0;
            //DOP E IGP
            for (var j=1; j< 3; j++){
                //console.log("updateTotalesExpedienteNivelProteccion j:::"+j);
                var entityExpedienteNivelProteccion = expedienteNivelProteccionRepo.newEntity();
                totalGranelDeposito = getTotalVolumenAforadoNivelProteccion(expediente_id,  new java.lang.Long(j), "granelDepositoRepo");
                totalGranelBarrica = getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "granelBarricaRepo");
                totalEnvasado = getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "envasadoRepo");
                totalEtiquetado = getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "etiquetadoRepo");
                totalBodega = totalGranelDeposito + totalGranelBarrica + totalEnvasado + totalEtiquetado;

                totalTotalBodega = totalTotalBodega + totalBodega;
                totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
                totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
                totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
                totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

                var nivel_proteccion = (j==1)?new java.lang.String("Total DOP"):new java.lang.String("Total IGP");
                entityExpedienteNivelProteccion.set("expediente_id", expediente_id);
                entityExpedienteNivelProteccion.set("nivel_proteccion", nivel_proteccion);
                entityExpedienteNivelProteccion.set("total_granel_deposito", totalGranelDeposito);
                entityExpedienteNivelProteccion.set("total_granel_barrica", totalGranelBarrica);
                entityExpedienteNivelProteccion.set("total_envasado", totalEnvasado);
                entityExpedienteNivelProteccion.set("total_etiquetado", totalEtiquetado);
                entityExpedienteNivelProteccion.set("total_bodega", totalBodega);
                expedienteNivelProteccionRepo.save(entityExpedienteNivelProteccion);
            }
            totalGranelDeposito = 0;
            totalGranelBarrica = 0;
            totalEnvasado = 0;
            totalEtiquetado = 0;
            totalBodega = 0;
            //TOTAL VARIETALES + SIN IG
            for (var j=3; j< 5; j++){
                totalGranelDeposito = totalGranelDeposito + getTotalVolumenAforadoNivelProteccion(expediente_id,  new java.lang.Long(j), "granelDepositoRepo");
                totalGranelBarrica = totalGranelBarrica + getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "granelBarricaRepo");
                totalEnvasado = totalEnvasado + getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "envasadoRepo");
                totalEtiquetado = totalEtiquetado + getTotalVolumenAforadoNivelProteccion(expediente_id, new java.lang.Long(j), "etiquetadoRepo");
            }
            totalBodega = totalGranelDeposito + totalGranelBarrica + totalEnvasado + totalEtiquetado;

            totalTotalBodega = totalTotalBodega + totalBodega;
            totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
            totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
            totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
            totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

            var entityExpedienteNivelProteccion = expedienteNivelProteccionRepo.newEntity();
            entityExpedienteNivelProteccion.set("expediente_id", expediente_id);
            entityExpedienteNivelProteccion.set("nivel_proteccion", new java.lang.String("Total Varietales + SIN IG"));
            entityExpedienteNivelProteccion.set("total_granel_deposito", totalGranelDeposito);
            entityExpedienteNivelProteccion.set("total_granel_barrica", totalGranelBarrica);
            entityExpedienteNivelProteccion.set("total_envasado", totalEnvasado);
            entityExpedienteNivelProteccion.set("total_etiquetado", totalEtiquetado);
            entityExpedienteNivelProteccion.set("total_bodega", totalBodega);
            expedienteNivelProteccionRepo.save(entityExpedienteNivelProteccion);

            entityExpedienteNivelProteccion = expedienteNivelProteccionRepo.newEntity();
            entityExpedienteNivelProteccion.set("expediente_id", expediente_id);
            entityExpedienteNivelProteccion.set("nivel_proteccion", new java.lang.String("TOTAL BODEGA"));
            entityExpedienteNivelProteccion.set("total_granel_deposito", totalTotalGranelDeposito);
            entityExpedienteNivelProteccion.set("total_granel_barrica", totalTotalGranelBarrica);
            entityExpedienteNivelProteccion.set("total_envasado", totalTotalEnvasado);
            entityExpedienteNivelProteccion.set("total_etiquetado", totalTotalEtiquetado);
            entityExpedienteNivelProteccion.set("total_bodega", totalTotalBodega);
            expedienteNivelProteccionRepo.save(entityExpedienteNivelProteccion);
            i++;
        }
    }



    function getTotalVolumenAforadoTipoVino(expediente_id, tipo_vino_id, repo){
        //console.log("getTotalVolumenAforadoTipoVino");
        var partidaAforoRepo = ctx.get("repos").get("partidaAforoRepo");
        var lstPartidaAforo = partidaAforoRepo.listAll();
        var i = 0;
        var totalVolumenAforado = 0;
        while(i != lstPartidaAforo.size()){
            var entityPartidaAforo = lstPartidaAforo.get(i);
            var expediente_id_bbdd = entityPartidaAforo.get("expediente_id");
            var tipo_vino_id_bbdd = entityPartidaAforo.get("vino_id");
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);
            //console.log("tipo_vino_id_bbdd::: "+tipo_vino_id_bbdd);
            if (expediente_id_bbdd == expediente_id && tipo_vino_id_bbdd == tipo_vino_id){
                var partida_aforo_id = entityPartidaAforo.get("partida_aforo_id");
                totalVolumenAforado = totalVolumenAforado + getVolumenAforado(partida_aforo_id, repo);
                //console.log("totalVolumenAforado::: "+totalVolumenAforado);
            }
            i++;
        }
        return totalVolumenAforado;
    }

    function getTotalVolumenAforadoNivelProteccion(expediente_id, nivel_proteccion_id, repo){
        //console.log("getTotalVolumenAforadoNivelProteccion");
        var partidaAforoRepo = ctx.get("repos").get("partidaAforoRepo");
        var lstPartidaAforo = partidaAforoRepo.listAll();
        var i = 0;
        var totalVolumenAforado = 0;
        while(i != lstPartidaAforo.size()){
            var entityPartidaAforo = lstPartidaAforo.get(i);
            var expediente_id_bbdd = entityPartidaAforo.get("expediente_id");
            var nivel_proteccion_id_bbdd = entityPartidaAforo.get("nivel_proteccion_id");
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);
            //console.log("nivel_proteccion_id_bbdd::: "+nivel_proteccion_id_bbdd);
            if (expediente_id_bbdd == expediente_id && nivel_proteccion_id_bbdd == nivel_proteccion_id){
                var partida_aforo_id = entityPartidaAforo.get("partida_aforo_id");
                totalVolumenAforado = totalVolumenAforado + getVolumenAforado(partida_aforo_id, repo);
                //console.log("totalVolumenAforado::: "+totalVolumenAforado);
            }
            i++;
        }
        return totalVolumenAforado;
    }

    function updateNifOperador(component){
        //console.log("updateNifOperador");
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("nif_operador")));
          }else{
            component.setValueExpression(null);
        }
    }

    function getLocalidad(localidad_id){
        //console.log("getLocalidad");
        var localidadRepo = ctx.get("repos").get("locaRepo");
        //console.log("localidad_id::: "+localidad_id);
        var entidadLocalidad = localidadRepo.findById(localidad_id.toString());
        var localidad = entidadLocalidad.get("d_exten_loca");
        //console.log("d_exten_loca::: "+localidad);
        return localidad;
    }

    function getMunicipio(municipio_id){
        //console.log("getMunicipio");
        var municipioRepo = ctx.get("repos").get("muniRepo");
        //console.log("municipio_id::: "+municipio_id);
        var entidadMunicipio = municipioRepo.findById(municipio_id);
        var municipio = entidadMunicipio.get("d_exten_muni");
        //console.log("d_exten_muni::: "+municipio);
        return municipio;
    }

    function getProvincia(provincia_id){
        //console.log("getMunicipio");
        var provinciaRepo = ctx.get("repos").get("provRepo");
        //console.log("provincia_id::: "+provincia_id);
        var entidadProvincia = provinciaRepo.findById(provincia_id);
        var provincia = entidadProvincia.get("d_exten_prov");
        //console.log("d_exten_prov::: "+provincia);
        return provincia;
    }

    function updateDireccionOperador(component){
            //console.log("updateDireccionOperador");
            entityOperador = setEntityOperador();

            if (entityOperador != null){
                var localidad = getLocalidad(entityOperador.get("localidad_operador_id"));
                var municipio = getMunicipio(entityOperador.get("municipio_operador_id"));
                var provincia = getProvincia(entityOperador.get("provincia_operador_id"));
                component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("tipo_via_operador")+" "+entityOperador.get("via_operador")+" "+localidad+", "+provincia+" "+entityOperador.get("codigo_postal_operador")+"-"+entityOperador.get("pais")));
            }else{
                component.setValueExpression(null);
            }
        }

    function updateDireccionInstalacion(component){
        //console.log("updateDireccionInstalacion");
        entityOperador = setEntityOperador();
        if (entityOperador != null){
            //console.log("entityOperador.get('localidad_instalacion_id')::: "+entityOperador.get("localidad_instalacion_id"));
            //console.log("entityOperador.get('municipio_instalacion_id')::: "+entityOperador.get("municipio_instalacion_id"));
           //console.log("entityOperador.get('provincia_instalacion_id')::: "+entityOperador.get("provincia_instalacion_id"));
            var localidad = getLocalidad(entityOperador.get("localidad_instalacion_id"));
            var municipio = getMunicipio(entityOperador.get("municipio_instalacion_id"));
            var provincia = getProvincia(entityOperador.get("provincia_instalacion_id"));
            //console.log("localidad::: "+localidad);
            //console.log("municipio::: "+municipio);
            //console.log("provincia::: "+provincia);
           //console.log("component.getId()::: "+component.getId());
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("direccion_instalacion")+" "+localidad+", "+provincia+" "+entityOperador.get("codigo_postal_instalacion")+"-"+entityOperador.get("pais")));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateNifRepresentante(component){
        //console.log("updateNifRepresentante");
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("nif_representante")));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateRepresentante(component){
        //console.log("updateRepresentante");
        entityOperador = setEntityOperador();

        if (entityOperador != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityOperador.get("nombre_representante")+" "+entityOperador.get("apellidos_representante")));
        }else{
            component.setValueExpression(null);
        }
    }

    function udateKgPermitidos(component){
        //console.log("udateKgPermitidos");
        //console.log("view.get('variedad_id')::: "+view.get("variedad_id"));
        if (view.get("variedad_id") != null){
            var variedad_id = new java.lang.Long(view.get("variedad_id"));
            var variedadRepo = ctx.get("repos").get("variedadRepo");
            var entityVariedad = variedadRepo.findById(variedad_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var kg_permitidos = new java.math.BigDecimal(entityVariedad.get("kg_permitidos")).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+kg_permitidos.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

     function udateGradoVariedad(component){
        //console.log("udateGradoVariedad");
        //console.log("view.get('variedad_id')::: "+view.get("variedad_id"));
        if (view.get("variedad_id") != null){
            var variedad_id = new java.lang.Long(view.get("variedad_id"));
            var variedadRepo = ctx.get("repos").get("variedadRepo");
            var entityVariedad = variedadRepo.findById(variedad_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var grado_probable = new java.math.BigDecimal(entityVariedad.get("grado_probable")).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+grado_probable.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateMesesBarrica(component){
        //console.log("updateMesesBarrica");
        //console.log("view.get('tipo_envejecimiento_id')::: "+view.get("tipo_envejecimiento_id"));
        if (view.get("tipo_envejecimiento_id") != null){
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var meses_en_barrica = getMesesBarrica();
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+meses_en_barrica.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

    function getMesesBarrica(){
        var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
        var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
        var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
        return entityTipoEnvejecimiento.get("meses_barrica");
    }

    function getMesesBotella(){
            var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
            var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
            var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
            return entityTipoEnvejecimiento.get("meses_botella");
        }

    function updateMesesBotella(component){
        //console.log("updateMesesBotella");
        //console.log("view.get('tipo_envejecimiento_id')::: "+view.get("tipo_envejecimiento_id"));
        if (view.get("tipo_envejecimiento_id") != null){
            var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
            var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
            var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var meses_botella = entityTipoEnvejecimiento.get("meses_botella");
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+meses_botella.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateCumpleEnvejecimientoBarrica(component){
        var fecha_llenado_barricas = view.get("fecha_llenado_barricas");
        var fecha_salida_barricas = view.get("fecha_salida_barricas");
        var meses_en_barrica = 0;
        if (view.get("tipo_envejecimiento_id") != null){
            //console.log("view.get('tipo_envejecimiento_id'):::"+view.get("tipo_envejecimiento_id"));
            meses_en_barrica = getMesesBarrica();
            //console.log("updateCumpleEnvejecimientoBarrica MESES BARRICA::: "+meses_en_barrica);
        }
        updateCumpleEnvejecimiento(component, fecha_llenado_barricas, fecha_salida_barricas, meses_en_barrica);
    }

    function updateCumpleEnvejecimientoBotella(component){
        var fecha_llenado_botella = view.get("fecha_llenado_botella");
        var fecha_calificacion_botella = view.get("fecha_calificacion_botella");
        var meses_en_botella = 0;
        if (view.get("tipo_envejecimiento_id") != null){
            meses_en_botella = getMesesBotella();
            //console.log("updateCumpleEnvejecimientoBotella MESES BOTELLA::: "+meses_en_botella);
        }
        updateCumpleEnvejecimiento(component, fecha_llenado_botella, fecha_calificacion_botella, meses_en_botella);
    }

    function updateCumplePermanenciaVinoBodega(component){
        var fecha_llenado_barricas = view.get("fecha_llenado_barricas");
        var fecha_primera_salida_comercializada = view.get("fecha_primera_salida_comercializada");
        var meses_en_botella = 0;
        if (view.get("tipo_envejecimiento_id") != null){
            meses_en_botella = getMesesBotella();
            //console.log("updateCumplePermanenciaVinoBodega MESES BOTELLA::: "+meses_en_botella);
        }
        updateCumpleEnvejecimiento(component, fecha_llenado_barricas, fecha_primera_salida_comercializada, meses_en_botella);
    }

    function updateCumpleEnvejecimiento(component, fecha_llenado, fecha_salida, meses){
        var cumpleEnvejecimiento = "NO";
        //console.log("updateCumpleEnvejecimiento");
        var meses_en_barrica = 0;
        var diff = 0;

        if (fecha_llenado != null && fecha_salida != null){
            //console.log("fecha_llenado:::"+fecha_llenado);
            //console.log("fecha_salida:::"+fecha_salida);
            diff = getDMonths(fecha_llenado, fecha_salida);
            //console.log("DIFFF::: "+diff);

            if (diff > meses || diff == meses){
                cumpleEnvejecimiento = "SI";
                //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
            }
        }
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+cumpleEnvejecimiento));
    }

    function updateCumpleEnvejecimientoTotal(component){
        //console.log("updateCumpleEnvejecimientoTotal");
        var cumple_envejecimiento_botella = view.get("cumple_envejecimiento_botella");
        var cumple_envejecimiento_barrica = view.get("cumple_envejecimiento_barrica");
        var cumple_permanencia_vino_bodega = view.get("cumple_permanencia_vino_bodega");

        //console.log("cumple_envejecimiento_botella::: "+cumple_envejecimiento_botella);
        //console.log("cumple_envejecimiento_barrica::: "+cumple_envejecimiento_barrica);
        //console.log("cumple_permanencia_vino_bodega::: "+cumple_permanencia_vino_bodega);

        var cumple_envejecimiento = 'NO';
        if (view.get("tipo_envejecimiento_id") !=null){
            var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
            //console.log("tipo_envejecimiento_id::: "+tipo_envejecimiento_id);

            if (tipo_envejecimiento_id>2){
                cumple_envejecimiento = (cumple_envejecimiento_botella == 'SI' && cumple_envejecimiento_barrica == 'SI')?'SI':'NO';
            }else{
                cumple_envejecimiento = (cumple_permanencia_vino_bodega == 'SI' && cumple_envejecimiento_barrica == 'SI')?'SI':'NO';
            }
        }
        //console.log("cumple_envejecimiento::: "+cumple_envejecimiento);
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+cumple_envejecimiento));

        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad.set("cumple_envejecimiento", cumple_envejecimiento);
        trazabilidadRepo.save(entityTrazabilidad);
    }


    function updateVolumenNominal(component){
        //console.log("updateVolumenNominal");
        //console.log("view.get('deposito_id')::: "+view.get("deposito_id"));
        //console.log("entity.get('deposito_id')::: "+entity.get("deposito_id"));
        var deposito_id = null;
        if (view.get("deposito_id") != null){
            deposito_id = new java.lang.Long(view.get("deposito_id"));
        }
        else if (entity.get("deposito_id") != null){
            deposito_id = entity.get("deposito_id");
        }
        if (deposito_id != null){
            var depositoRepo = ctx.get("repos").get("depositoRepo");
            var entityDeposito = depositoRepo.findById(deposito_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var capacidad = new java.math.BigDecimal(entityDeposito.get("capacidad")).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+capacidad.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateMaterialDeposito(component){
        //console.log("updateMaterialDeposito");
        //console.log("view.get('deposito_id')::: "+view.get("deposito_id"));
        //console.log("entity.get('deposito_id')::: "+entity.get("deposito_id"));
        var deposito_id = null;
        if (view.get("deposito_id") != null){
            deposito_id = new java.lang.Long(view.get("deposito_id"));
        }
        else if (entity.get("deposito_id") != null){
            deposito_id = entity.get("deposito_id");
        }
        if (deposito_id != null){
            var depositoRepo = ctx.get("repos").get("depositoRepo");
            var entityDeposito = depositoRepo.findById(deposito_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var materialDeposito = entityDeposito.get("material");
                component.setValueExpression(valueExpressionFactory.getInstance().create(""+materialDeposito));
            } else {
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

    function updateNivelProteccion(component){
        //console.log("updateNivelProteccion");
        //console.log("view.get('nivel_proteccion_id')::: "+view.get("nivel_proteccion_id"));
        if (view.get("nivel_proteccion_id") != null){
            var nivel_proteccion_id = new java.lang.Long(view.get("nivel_proteccion_id"));
            var nivelProteccionRepo = ctx.get("repos").get("nivelProteccionRepo");
            var entityNivelProteccion = nivelProteccionRepo.findById(nivel_proteccion_id);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityNivelProteccion.get("nivel_proteccion")));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateTipoVino(component){
            //console.log("updateVino");
            //console.log("view.get('vino_id')::: "+view.get("vino_id"));
            if (view.get("vino_id") != null){
                var vino_id = new java.lang.Long(view.get("vino_id"));
                var vinoRepo = ctx.get("repos").get("vinoRepo");
                var entityVino = vinoRepo.findById(vino_id);
                //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
                component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityVino.get("vino")));
            } else {
                component.setValueExpression(null);
            }
        }

    function updateDop(component){
            //console.log("updateDop");
            //console.log("view.get('dop_id')::: "+view.get("dop_id"));
            if (view.get("dop_id") != null){
                var dop_id = new java.lang.Long(view.get("dop_id"));
                var dopRepo = ctx.get("repos").get("dopRepo");
                var entityDop = dopRepo.findById(dop_id);
                //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
                component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityDop.get("dop")));
            } else {
                component.setValueExpression(null);
            }
        }


    function updateTiempoBarrica(component){
        //console.log("updateTiempoBarrica");
        var fecha_llenado_barricas = view.get("fecha_llenado_barricas");
        var fecha_salida_barricas = view.get("fecha_salida_barricas");
        var diff = dayDiff(component, fecha_llenado_barricas, fecha_salida_barricas);

        //console.log("diff::: "+diff);

    }

    function updateTiempoBotella(component){
        //console.log("updateTiempoBotella");
        var fecha_llenado_botella = view.get("fecha_llenado_botella");
        var fecha_calificacion_botella = view.get("fecha_calificacion_botella");
        //console.log("fecha_llenado_botella::: "+fecha_llenado_botella);
        //console.log("fecha_calificacion_botella::: "+fecha_calificacion_botella);
        dayDiff(component, fecha_llenado_botella, fecha_calificacion_botella);
    }

      function updateTiempoBodega(component){
            //console.log("updateTiempoBodega");
            var fecha_llenado_barricas = view.get("fecha_llenado_barricas");
            var fecha_primera_salida_comercializada = view.get("fecha_primera_salida_comercializada");
            dayDiff(component, fecha_llenado_barricas, fecha_primera_salida_comercializada);
        }

    function dayDiff(component, fecha1, fecha2){
        //console.log("dayDiff");
        if (fecha1 != null && fecha2 != null){
            var dMonths = getDMonths(fecha1, fecha2);
            //console.log("dMonths: "+dMonths);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+dMonths.toString()));
            //component.setValueExpression(valueExpressionFactory.getInstance().create(""+dDays.toString()));
        } else {
            component.setValueExpression(null);
        }
        return dMonths;
    }

    function getDMonths(fecha1, fecha2){
        //console.log("getDMonths");
        var dMonths = 0;
         if (fecha1 != null && fecha2 != null){
            var one_day=1000*60*60*24;
            var x = fecha1.split("-");
            var y = fecha2.split("-");

            var date1=new Date(x[0],(x[1]-1),x[2]);
            //console.log(date1.toDateString());
            var date2=new Date(y[0],(y[1]-1),y[2]);
            //console.log(date2.toDateString());

            var dDays = (date2.getTime()-date1.getTime())/one_day;

                    //var dMonths = Math.floor(dDays/30);

            //console.log("date2.getTime(): "+date2.getTime());
            //console.log("date1.getTime(): "+date1.getTime());
            //console.log("one_day: "+one_day);
            //console.log("dDays: "+dDays);
            dMonths = new java.math.BigDecimal(dDays/30).setScale(0, java.math.RoundingMode.HALF_UP);
         }
         return dMonths;
    }

    function anadirDescripcion(){
        //console.log("anadirDescripcion");
        //console.log("entity.get('expediente_id')::: "+entity.get("expediente_id"));
        //console.log("view.get('expediente_id')::: "+view.get("expediente_id"));

        //console.log("entity.get('trazabilidad_id')::: "+entity.get("trazabilidad_id"));
                //console.log("view.get('trazabilidad_id')::: "+view.get("trazabilidad_id"));

        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        //var expediente_id = entity.get("expediente_id");
        var trazabilidad_id = entity.get("trazabilidad_id");
        entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad.set("desc_denominacion_producto","<strong><u>DENOMINACION DEL PRODUCTO</u></strong><br><br>Denominación de Origen Protegida o Denominación de Origen <br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br>Denominación de Origen<br><br><strong>Legislación</strong><br><br>Pliego Condiciones Arlanza<br><br>Art 9 Rgto CE 1169/11<br><br>Art. 119 Rgto 1308/2013<br><br>Art. 40 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_grado_alcoholico","<strong><u>GRADO ALCOHOLICO (% VOL), (ALC % VOL) (ALCOHOL ADQUIRIDO % VOL), (GRADO ALCOHOLICO ADQUIRIDO % VOL)</u></strong><br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 40 y 44 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_procedencia","<strong><u>PROCEDENCIA</u></strong><br><br>'Producto de España', 'Producido en España', 'Vino de España' o expresiones equivalentes<br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 45 Rgto Delegado 2019/33<br><br>Art 13 Rgto CE 1169/11");
        entityTrazabilidad.set("desc_embotellado_por","<strong><u>'EMBOTELLADO POR' O 'EMBOTELLADOR'</u></strong><br><br>Nombre o Razón Social – Municipio - Estado<br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 46 Rgto Delegado 2019/33<br><br>Códigos (art. 6 RD1363/2011)");
        entityTrazabilidad.set("desc_embotellado_por_encargo","<strong><u>EMBOTELLADO POR ENCARGO</u></strong><br><br>'Embotellado para .. Nombre C  o Razón Social y Municipio y Estado  por … Nombre Comercial o Razón Social Municipio y Estado. Se puede sustituir por  el Reg Embotellador y quitar la Razón Social, Mun y Est<br><br><strong>Legislación</strong><br><br>Art. 46.2 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_importador","<strong><u>IMPORTADOR (En su caso)</u></strong><br><br><strong>Requisitos</strong><br><br>> 1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 46 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_volumen_nominal","<strong><u>VOLUMEN NOMINAL (l), (ml), (cl)</u></strong><br><br><strong>Requisitos</strong><br><br>>4 mm<br><br><strong>Legislación</strong><br><br>Art 13 y 23 Rgto UE 1169/11<br><br>Art. 9 R.D.1801/2008");
        entityTrazabilidad.set("desc_alergenos","<strong><u>ALERGENOS - CONTIENE SULFITOS</u></strong><br><br>(Si contiene más de 10 mg/l SO2 TOTAL)<br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art 13 y 21 Rgto UE 1169/11.<br><br>Art. 41 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_num_registro_embotellador","<strong><u>Nº DE REGISTRO DE EMBOTELLADOR (NRE nº)</u></strong><br><br><strong>Legislación</strong><br><br>Art 4 y 6 RD 1363/2011<br><br>Art. 46,5 Rgto.Delegado 2019/33");
        entityTrazabilidad.set("desc_num_lote","<strong><u>Nº DE LOTE</u></strong><br><br>Obligatorio en el Etiquetado<br><br><strong>Legislación</strong><br><br>Art. 12 RD 1334//1999<br><br>Directiva 2011/91/UE<br><br>RD 1808/1991<br><br>RD 1801/2008");
        entityTrazabilidad.set("desc_punto_verde","<strong><u>PUNTO VERDE</u></strong><br><br><strong>Legislación</strong><br><br>Ley 11/1997 Directiva 94/62");
        entityTrazabilidad.set("desc_ano_cosecha","<strong><u>AÑO DE LA COSECHA, AÑADA, VENDIMIA</u></strong><br><br>(Obligatorio en el Etiquetado para esta DO.P. aunque no sean envejecidos)<br><br><strong>Requisitos</strong><br><br>> 85 % Cosecha<br><br><strong>Legislación</strong><br><br>Art. 49 y 58 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_variedades_vid","<strong><u>VARIEDADES DE VID</u></strong><br><br><strong>Requisitos</strong><br><br>> 85 % una Variedad<br><br>100% dos variedades o más<br><br><strong>Legislación</strong><br><br>Art 50  Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_menciones_tradicionales","<strong><u>MENCIONES TRADICIONALES</u></strong><br><br>(Crianza, Reserva, Gran Reserva)<br><br><strong>Legislación</strong><br><br>Art. 24 RD 1363/11");
        entityTrazabilidad.set("desc_contenido_azucar","<strong><u>CONTENIDO EN AZÚZAR</u></strong><br><br><strong>Legislación</strong><br><br>Art 52  Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_simbolo_comunitario","<strong><u>SÍMBOLO COMUNITARIO DE DOP/IGP</u></strong><br><br><strong>Legislación</strong><br><br>Anexo de Rgto Delegado 664/2014");
        entityTrazabilidad.set("desc_metodos_produccion","<strong><u>MÉTODOS DE PRODUCCIÓN (FERMENTADO EN BARRICA O ROBLE)</u></strong><br><br><strong>Requisitos</strong><br><br>(Incluir meses o años para mención roble)<br><br>La barrica ha de ser de roble<br><br><strong>Legislación</strong><br><br>Anexo V Delegado 2019/33<br><br>Art. 18 RD 1363/11");
        entityTrazabilidad.set("desc_metodos_ecologicos","<strong><u>MÉTODOS DE PRODUCCIÓN ECOLÓGICOS</u></strong><br><br><strong>Legislación</strong><br><br>Art 53.6  Rgto Delegado 2019/33<br><br>Reglamento 834/2007, desarrollado por 889/2008");
        entityTrazabilidad.set("desc_unidad_geografica","<strong><u>UNIDAD GEOGRÁFICA MENOS O MAYOR VINO DE PUEBLO + TÉRMINO MUNICIPAL O ENTIDAD LOCAL MENOR</u></strong><br><br><strong>Requisitos</strong><br><br>> 85% de la uva de ese municipio<br><br><strong>Legislación</strong><br><br>Art. 55  Rgto Delegado 2019/33<br><br>Art. 21 Decreto 50/2018");
        entityTrazabilidad.set("desc_explotacion_viticola","<strong><u>EXPLOTACIÓN VITÍCOLA</u></strong><br><br><strong>Legislación</strong><br><br>Art. 54 Sección 2 Rgto. Delegado 2019/33 y anexo VI");
        entityTrazabilidad.set("desc_menciones_color","<strong><u>MENCIONES RELATIVAS AL COLOR</u></strong><br><br><strong>Legislación</strong><br><br>Art. 12 y anexo I de RD 1363/2011");
        entityTrazabilidad.set("desc_simbolo_ce","<strong><u>SIMBOLO CE 'E'</u></strong><br><br><strong>Legislación</strong><br><br>c) art. 9 RD 1801/2008");
        entityTrazabilidad.set("desc_marca", "<strong><u>MARCA</u></strong><br><br><strong>Legislación</strong><br><br>Nacional, Comunitaria y/o Internacional");
        trazabilidadRepo.save(entityTrazabilidad);
    }

    function setContraetiquetasNumeraciones(asignada_view){
        //console.log("setContraetiquetasNumeraciones");
        if (view.get("trazabilidad_id") != null){
            var trazabilidad_id_view = new java.lang.Long(view.get("trazabilidad_id"))
            var numeracionContraetiquetadoRepo = ctx.get("repos").get("numeracionContraetiquetadoRepo");
            var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
            var lstNumeracionContraetiquetado = numeracionContraetiquetadoRepo.listAll();

            var unidadesTotales=0;
            var i = 0;
            while(i != lstNumeracionContraetiquetado.size()){
                ////console.log("i: " + i);
                var entityNumeracionContraetiquetado = lstNumeracionContraetiquetado.get(i);
                var trazabilidad_id_bbdd = new java.lang.Long(entityNumeracionContraetiquetado.get("trazabilidad_id"))
                var asignada_bbdd = entityNumeracionContraetiquetado.get("asignada");
                //console.log("trazabilidad_id_view: "+ trazabilidad_id_view)
                //console.log("trazabilidad_id_bbdd: " + trazabilidad_id_bbdd);
                //console.log("asignada_view: "+ asignada_view)
                //console.log("asignada_bbdd: " + asignada_bbdd);

                if (trazabilidad_id_view == trazabilidad_id_bbdd && asignada_view == asignada_bbdd){
                    var unidades = new java.lang.Long(entityNumeracionContraetiquetado.get("hasta") - entityNumeracionContraetiquetado.get("desde") + 1)
                    ////console.log("unidades: " + unidades);
                    entityNumeracionContraetiquetado.set("unidades", unidades);
                    numeracionContraetiquetadoRepo.save(entityNumeracionContraetiquetado);
                    unidadesTotales = unidadesTotales + unidades;
                }
                i++;
            }
            ////console.log("unidadesTotales: " + unidadesTotales);
            var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id_view);
            ////console.log("unidadesTotales: " + unidadesTotales);
            if (asignada_view == 'Y'){
                entityTrazabilidad.set("contras_usadas", new java.lang.Long((unidadesTotales)))
            }else{
                entityTrazabilidad.set("contras_sin_usar", new java.lang.Long((unidadesTotales)))
            }
            ////console.log("entityLoteControlDocumental.get('contraetiquetas_numeraciones_utilizadas'): " + entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas"));
            trazabilidadRepo.save(entityTrazabilidad)
        }
    }

    function setContraetiquetasNumeracionesUtilizadas(){
        //console.log("setContraetiquetasNumeracionesUtilizadas");
        setContraetiquetasNumeraciones('Y');
    }
    function setContraetiquetasNumeracionesSinUtilizar(){
            //console.log("setContraetiquetasNumeracionesSinUtilizar");
            setContraetiquetasNumeraciones('N');
        }

    /*function updateNumeroBotellas(component){
        //console.log("updateNumeroBotellas");
        if (view.get("capacidad_botella") != 0){
            var capacidad_botella = new Long(view.get("capacidad_botella"));
            var cantidad_vino_etiquetado = new Long(view.get("cantidad_vino_etiquetado"));
            var numero_botellas = ew java.math.BigDecimal(cantidad_vino_etiquetado / capacidad_botella).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+numero_botellas.toString()));
        } else {
            component.setValueExpression(null);
        }
    }*/

    /*
    function getEntityExpedienteVino(expediente_id, vino_id){
            //console.log("getEntityExpedienteVino");
            //console.log("expediente_id::: "+expediente_id);
            //console.log("vino_id::: "+vino_id);
            var expedienteVinoRepo = ctx.get("repos").get("expedienteVinoRepo");
            var lstExpedienteVino = expedienteVinoRepo.listAll();
            //console.log("lstExpedienteVino.size()::: "+lstExpedienteVino.size());
            var entityExpedienteVino;
            var i = 0;
            var encontrado = false;
            while(i != lstExpedienteVino.size()){
                var entityExpedienteVino = lstExpedienteVino.get(i);
                //console.log("entityExpedienteVino.get(expediente_id)::: " + entityExpedienteVino.get("expediente_id"));
                if (entityExpedienteVino.get("expediente_id") == expediente_id){
                    expedienteVinoRepo.deleteById(entityExpedienteVino.get("expediente_vino_id"));
                }
                i++;
            }
            entityExpedienteVino = expedienteVinoRepo.newEntity();
            entityExpedienteVino.set("expediente_id", expediente_id);

            return entityExpedienteVino;
        }
        */

        /*
        function getEntityExpedienteNivelProteccion(expediente_id, nivel_proteccion_id){
                    //console.log("getEntityExpedienteNivelProteccion");
                    //console.log("expediente_id::: "+expediente_id);
                    //console.log("nivel_proteccion_id::: "+nivel_proteccion_id);
                    var expedienteNivelProteccionRepo = ctx.get("repos").get("expedienteNivelProteccionRepo");
                    var lstExpedienteNivelProteccion = expedienteNivelProteccionRepo.listAll();
                    //console.log("expedienteNivelProteccionRepo.size()::: "+lstExpedienteNivelProteccion.size());
                    var entityExpedienteNivelProteccion;
                    var i = 0;
                    var encontrado = false;
                    while(i != lstExpedienteNivelProteccion.size()){
                        var entityExpedienteNivelProteccion = lstExpedienteNivelProteccion.get(i);
                        //console.log("entityExpedienteNivelProteccion.get(expediente_id)::: " + entityExpedienteNivelProteccion.get("expediente_id"));
                        //console.log("entityExpedienteNivelProteccion.get(vino_id)" + entityExpedienteNivelProteccion.get("vino_id"));
                        if (entityExpedienteNivelProteccion.get("expediente_id") == expediente_id && entityExpedienteNivelProteccion.get("nivel_proteccion_id") == nivel_proteccion_id){
                            encontrado = true;
                            break;
                        }
                        i++;
                    }
                    if (!encontrado){
                        //console.log("! encontador");
                        entityExpedienteNivelProteccion = expedienteNivelProteccionRepo.newEntity();
                        entityExpedienteNivelProteccion.set("expediente_id", expediente_id);
                        entityExpedienteNivelProteccion.set("nivel_proteccion_id", new java.lang.Long(nivel_proteccion_id));
                    }
                    return entityExpedienteNivelProteccion;
                }*/

    function anadirGranelDeposito(component){
        //console.log("anadirGranelDeposito");
        var granelDepositoRepo = ctx.get("repos").get("granelDepositoRepo");
        var entity = granelDepositoRepo.newEntity();
        granelDepositoRepo.save(entity);
        var lstGranelDeposito = granelDepositoRepo.listAll();
        var entityGranelDeposito = lstGranelDeposito.get(lstGranelDeposito.size()-1);

        //console.log(" entityGranelDeposito.get('granel_deposito_id')::: "+ entityGranelDeposito.get("granel_deposito_id"));
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityGranelDeposito.get("granel_deposito_id")));
    }

    function updateTipoRoble(component){
        //console.log("updateTipoRoble");
        //console.log("view.get('barrica_id')::: "+view.get("barrica_id"));
        //console.log("entity.get('barrica_id')::: "+entity.get("barrica_id"));
        var barrica_id = null;
        if (view.get("barrica_id") != null){
            barrica_id = new java.lang.Long(view.get("barrica_id"));
        }
        else if (entity.get("barrica_id") != null){
            barrica_id = entity.get("barrica_id");
        }
         //console.log("1. barrica_id::: "+barrica_id);
        if (barrica_id != null){
            //console.log("2. barrica_id::: "+barrica_id);
            var barricaRepo = ctx.get("repos").get("barricaRepo");
            var entityBarrica = barricaRepo.findById(barrica_id);
            var tipoRoble = entityBarrica.get("tipo_roble");
            //console.log("tipoRoble::: "+tipoRoble);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+tipoRoble));
        } else {
            component.setValueExpression(null);
        }
    }

    function updateUnidades(component){
        //console.log("updateUnidades");
        //console.log("view.get('barrica_id')::: "+view.get("barrica_id"));
        //console.log("entity.get('barrica_id')::: "+entity.get("barrica_id"));
        var barrica_id = null;
        if (view.get("barrica_id") != null){
            barrica_id = new java.lang.Long(view.get("barrica_id"));
        }
        else if (entity.get("barrica_id") != null){
            barrica_id = entity.get("barrica_id");
        }
        if (barrica_id != null){
            var barricaRepo = ctx.get("repos").get("barricaRepo");
            var entityBarrica = barricaRepo.findById(barrica_id);
            var unidades = entityBarrica.get("unidades");
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+unidades));
        } else {
            component.setValueExpression(null);
        }
    }


    function updateCapacidad(component){
        //console.log("updateCapacidad");
        //console.log("view.get('barrica_id')::: "+view.get("barrica_id"));
        //console.log("entity.get('barrica_id')::: "+entity.get("barrica_id"));
        var barrica_id = null;
        if (view.get("barrica_id") != null){
            barrica_id = new java.lang.Long(view.get("barrica_id"));
        }
        else if (entity.get("barrica_id") != null){
            barrica_id = entity.get("barrica_id");
        }
        if (barrica_id != null){
            var barricaRepo = ctx.get("repos").get("barricaRepo");
            var entityBarrica = barricaRepo.findById(barrica_id);
            var capacidad = entityBarrica.get("cap_total");
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+capacidad));
        } else {
            component.setValueExpression(null);
        }
    }

    function setTotalBarricas(component){
        //console.log("setTotalBarricas");
        var operador_id_view = entity.get("operador_id");;
       //console.log("operador_id_view::: "+operador_id_view);

        var barricaRepo = ctx.get("repos").get("barricaRepo");
        var lstBarricas = barricaRepo.listAll();

        var total_barricas=0;
        var i = 0;
        while(i != lstBarricas.size()){
            //console.log("i: " + i);
            var entityBarrica = lstBarricas.get(i);

            var operador_id_bbdd = new java.lang.Long(entityBarrica.get("operador_id"))
            //console.log("operador_id_bbdd: "+ operador_id_bbdd)
            //console.log("operador_id_view: " + operador_id_view);

            if (operador_id_view == operador_id_bbdd){
                var unidades = entityBarrica.get("unidades");
                //console.log("unidades: " + unidades);
                total_barricas = total_barricas + unidades;
            }
            i++;
        }
        //console.log("total_barricas: " + total_barricas);
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+total_barricas));

    }

    function updateConformeRendimiento(component){
            var conformeRendimiento = "NO";
            //console.log("updateConformeRendimiento");
            var kg_cupo = 0;
            var kg_totales_recepcionados = 0;
            if (view.get("kg_cupo") != null){
                kg_cupo = new java.math.BigDecimal(view.get("kg_cupo")).setScale(2, java.math.RoundingMode.HALF_UP);
            }
            if (view.get("kg_totales_recepcionados") != null){
                kg_totales_recepcionados = new java.math.BigDecimal(view.get("kg_totales_recepcionados")).setScale(2, java.math.RoundingMode.HALF_UP);
           }
            //console.log("kg_cupo::: "+kg_cupo);
            //console.log("kg_totales_recepcionados::: "+kg_totales_recepcionados);
            if (kg_cupo > kg_totales_recepcionados || kg_cupo == kg_totales_recepcionados){
                conformeRendimiento = "SI";
                //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
            }
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+conformeRendimiento));
        }

        function saveLitrosProducidos(component){
            //console.log("saveLitrosProducidos");
            //console.log("expediente_id::: "+entity.get("expediente_id"));
            //console.log("view.dop_id::: "+view.get("dop_id"));
        }

        function updateCumpleRendimientosExtraccion(component){
            var cumpleRendimiento = "NA";
            //console.log("updateCumpleRendimientosExtraccion");
            //console.log("view.get('litros_producidos')::: "+view.get("litros_producidos"));
            //console.log("expediente_id::: "+entity.get("expediente_id"));
            //console.log("view.dop_id::: "+view.get("dop_id"));
            view.get("litros_producidos")
            var litros_producidos = 0;
            var kg_uva = 0;
            if (view.get("litros_producidos") != null){
                litros_producidos = new java.math.BigDecimal(view.get("litros_producidos")).setScale(2, java.math.RoundingMode.HALF_UP);
            }
            if (view.get("kg_uva") != null){
                kg_uva = new java.math.BigDecimal(view.get("kg_uva")).setScale(2, java.math.RoundingMode.HALF_UP);
            }
            //console.log("litros_producidos::: "+litros_producidos);
            //console.log("kg_uva::: "+kg_uva);
            if (kg_uva > 0){
                //console.log("kg_uva > 0");
                if (litros_producidos*100/kg_uva > 72 || litros_producidos*100/kg_uva == 72){
                    //console.log("if");
                    cumpleRendimiento = "SI";
                 }
                 else{
                     //console.log("else");
                    cumpleRendimiento = "NO";
                 }
            }
             //console.log("cumpleRendimiento::: "+cumpleRendimiento);
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+cumpleRendimiento));
        }

        function updateEntity(et){
            //console.log("updateEntity::: "+et);
            var repo = ctx.get("repos").get("verificacionPliegoCondicionesRepo");
            repo.save(et);
        }

