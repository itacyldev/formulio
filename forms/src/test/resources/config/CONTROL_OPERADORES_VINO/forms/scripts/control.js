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
            //console.log("expediente_id::: "+expediente_id);

            //Blanco
            var entityExpedienteVino = expedienteVinoRepo.newEntity();
            var totalGranelDeposito = getTotalVolumenAforadoTipoVino(expediente_id,  new java.lang.Long(1), "granelDepositoRepo");
            var totalGranelBarrica = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "granelBarricaRepo");
            var totalEnvasado = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "envasadoRepo");
            var totalEtiquetado = getTotalVolumenAforadoTipoVino(expediente_id, new java.lang.Long(1), "etiquetadoRepo");
            var totalBodega = totalGranelDeposito + totalGranelBarrica + totalEnvasado + totalEtiquetado;

            //console.log("totalGranelDeposito::: "+totalGranelDeposito);
            //console.log("totalGranelBarrica::: "+totalGranelBarrica);
            //console.log("totalEnvasado::: "+totalEnvasado);
            //console.log("totalEtiquetado::: "+totalEtiquetado);
            //console.log("totalBodega::: "+totalBodega);

            totalTotalBodega = totalTotalBodega + totalBodega;
            totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
            totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
            totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
            totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

            //console.log("totalTotalBodega::: "+totalTotalBodega);
            //console.log("totalTotalGranelDeposito::: "+totalTotalGranelDeposito);
            //console.log("totalTotalGranelBarrica::: "+totalTotalGranelBarrica);
            //console.log("totalTotalEnvasado::: "+totalTotalEnvasado);
            //console.log("totalTotalEtiquetado::: "+totalTotalEtiquetado);

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

            //console.log("totalGranelDeposito::: "+totalGranelDeposito);
            //console.log("totalGranelBarrica::: "+totalGranelBarrica);
            //console.log("totalEnvasado::: "+totalEnvasado);
            //console.log("totalEtiquetado::: "+totalEtiquetado);
            //console.log("totalBodega::: "+totalBodega);

            totalTotalBodega = totalTotalBodega + totalBodega;
            totalTotalGranelDeposito = totalTotalGranelDeposito + totalGranelDeposito;
            totalTotalGranelBarrica = totalTotalGranelBarrica + totalGranelBarrica;
            totalTotalEnvasado = totalTotalEnvasado + totalEnvasado;
            totalTotalEtiquetado = totalTotalEtiquetado + totalEtiquetado;

            //console.log("totalTotalBodega::: "+totalTotalBodega);
            //console.log("totalTotalGranelDeposito::: "+totalTotalGranelDeposito);
            //console.log("totalTotalGranelBarrica::: "+totalTotalGranelBarrica);
            //console.log("totalTotalEnvasado::: "+totalTotalEnvasado);
            //console.log("totalTotalEtiquetado::: "+totalTotalEtiquetado);

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
        var entidadLocalidad = localidadRepo.findById(localidad_id);
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

    function updateKgPermitidos(component){
        //console.log("updateKgPermitidos");
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

     function updateGradoVariedad(component){
        //console.log("updateGradoVariedad");
        //console.log("view.get('variedad_id')::: "+view.get("variedad_id"));
        if (view.get("variedad_id") != null){
            var grado_probable = getGradoVariedad(new java.lang.Long(view.get("variedad_id")));
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+grado_probable.toString()));
        } else {
            component.setValueExpression(null);
        }
    }

    function getGradoVariedad(variedad_id){
        var variedadRepo = ctx.get("repos").get("variedadRepo");
        var entityVariedad = variedadRepo.findById(variedad_id);
        return new java.math.BigDecimal(entityVariedad.get("grado_probable")).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    function updateMesesBarrica(component){
        //console.log("updateMesesBarrica");
        //console.log("view.get('tipo_envejecimiento_id')::: "+view.get("tipo_envejecimiento_id"));
        var valueExpression = null;
        if (view.get("tipo_envejecimiento_id") != null){
            //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
            var meses_en_barrica = getMesesBarrica();
            if (meses_en_barrica != null){
                valueExpression = valueExpressionFactory.getInstance().create(""+meses_en_barrica.toString());
            }
        }
        component.setValueExpression(valueExpression);
    }

    function getMesesBarrica(){
        //console.log("getMesesBarrica");
        var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
        var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
        var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
        return entityTipoEnvejecimiento.get("meses_barrica");
    }



    function updateCumpleEnvejecimientoBarrica(component){
        //console.log("updateCumpleEnvejecimientoBarrica");
        var cumpleEnvejecimiento = null;
        var tiempo_barrica = view.get("tiempo_barrica");
        var meses_barrica = view.get("meses_barrica");
        //console.log("tiempo_barrica::: "+tiempo_barrica);
        //console.log("meses_barrica::: "+meses_barrica);
        if (tiempo_barrica != null && meses_barrica != null){
           if (tiempo_barrica > meses_barrica || tiempo_barrica == meses_barrica){
                cumpleEnvejecimiento = "SI";
                //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
            }else{
                cumpleEnvejecimiento = "NO";
            }
        }
        //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+cumpleEnvejecimiento));
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
        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        //if (dop_id == 1 && vino_aptitud_id == 'Blanco'){
        if (dop_id == 1){
            entityTrazabilidad.set("desc_denominacion_producto","<strong><u>DENOMINACION DEL PRODUCTO</u></strong><br><br>ARLANZA. Denominación de Origen Protegida o Denominación de Origen <br><br><strong>Requisitos</strong><br><br>Estas menciones se podrán incluir tanto en minúscula como mayúscula, excepto la mención “ARLANZA” que irá siempre en mayúscula</br></br><br><br>1,2 mm<br><br>Denominación de Origen<br><br><strong>Legislación</strong><br><br>Pliego Condiciones Arlanza<br><br>Art 9 Rgto CE 1169/11<br><br>Art. 119 Rgto 1308/2013<br><br>Art. 40 Rgto Delegado 2019/33");
        }else{
            entityTrazabilidad.set("desc_denominacion_producto","<strong><u>DENOMINACION DEL PRODUCTO</u></strong><br><br>Denominación de Origen Protegida o Denominación de Origen <br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br>Denominación de Origen<br><br><strong>Legislación</strong><br><br>Art 9 Rgto CE 1169/11<br><br>Art. 119 Rgto 1308/2013<br><br>Art. 40 Rgto Delegado 2019/33");
        }
        entityTrazabilidad.set("desc_grado_alcoholico","<strong><u>GRADO ALCOHOLICO (% VOL), (ALC % VOL) (ALCOHOL ADQUIRIDO % VOL), (GRADO ALCOHOLICO ADQUIRIDO % VOL)</u></strong><br><br><strong>Requisitos</strong><br><br>Sin perjuicio de las tolerancias previstas en relación con el método de análisis de referencia utilizado, el grado alcohólico indicado no podrá ser superior ni inferior en más de un 0.5% vol al grado determinado por el análisis. En vinos almacenados en botella durante más de tres años sin perjuicio de las tolerancias previstas en relación con el método de análisis de referencia utilizado, el grado alcohólico indicado no podrá ser superior ni inferior en más de un 0,8 % vol. al grado determinado por el análisis<br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 40 y 44 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_procedencia","<strong><u>PROCEDENCIA</u></strong><br><br>'Producto de España', 'Producido en España', 'Vino de España' o expresiones equivalentes<br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 45 Rgto Delegado 2019/33<br><br>Art 13 Rgto CE 1169/11");
        entityTrazabilidad.set("desc_embotellado_por","<strong><u>'EMBOTELLADO POR' O 'EMBOTELLADOR'</u></strong><br><br>Nombre o Razón Social – Municipio - Estado<br><br><strong>Requisitos</strong><br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 46 Rgto Delegado 2019/33<br><br>Códigos (art. 6 RD1363/2011)");
        entityTrazabilidad.set("desc_embotellado_por_encargo","<strong><u>EMBOTELLADO POR ENCARGO</u></strong><br><br>'Embotellado para .. Nombre C  o Razón Social y Municipio y Estado  por … Nombre Comercial o Razón Social Municipio y Estado. Se puede sustituir por  el Reg Embotellador y quitar la Razón Social, Mun y Est<br><br><strong>Legislación</strong><br><br>Art. 46.2 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_importador","<strong><u>IMPORTADOR (En su caso)</u></strong><br><br><strong>Requisitos</strong><br><br>> 1,2 mm<br><br><strong>Legislación</strong><br><br>Art. 46 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_volumen_nominal","<strong><u>VOLUMEN NOMINAL (l), (ml), (cl)</u></strong><br><br><strong>Requisitos</strong><br><br>>4 mm<br><br><strong>Legislación</strong><br><br>Art 13 y 23 Rgto UE 1169/11<br><br>Art. 9 R.D.1801/2008");
        entityTrazabilidad.set("desc_alergenos","<strong><u>ALERGENOS - CONTIENE SULFITOS</u></strong><br><br>(Si contiene más de 10 mg/l SO2 TOTAL)<br><br><strong>Requisitos</strong><br><br>En los vinos deberá indicarse en el etiquetado, precedidos por el término contiene, los siguientes términos: sulfitos o dióxido de azufre. Además se informará de otros alérgenos como huevo o leche y podrá ir acompañada de los pictogramas del Anexo I, Parte B, del Reglamento Delegado 2019/33 de la Comisión, de 17 de octubre de 2018<br><br>1,2 mm<br><br><strong>Legislación</strong><br><br>Art 13 y 21 Rgto UE 1169/11.<br><br>Art. 41 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_num_registro_embotellador","<strong><u>Nº DE REGISTRO DE EMBOTELLADOR (NRE nº)</u></strong><br><br><strong>Legislación</strong><br><br>Art 4 y 6 RD 1363/2011<br><br>Art. 46,5 Rgto.Delegado 2019/33");
        entityTrazabilidad.set("desc_num_lote","<strong><u>Nº DE LOTE</u></strong><br><br>Obligatorio en el Etiquetado<br><br><strong>Legislación</strong><br><br>Art. 12 RD 1334//1999<br><br>Directiva 2011/91/UE<br><br>RD 1808/1991<br><br>RD 1801/2008");
        entityTrazabilidad.set("desc_punto_verde","<strong><u>PUNTO VERDE</u></strong><br><br><strong>Legislación</strong><br><br>Ley 11/1997 Directiva 94/62");
        entityTrazabilidad.set("desc_ano_cosecha","<strong><u>AÑO DE LA COSECHA, AÑADA, VENDIMIA</u></strong><br><br>(Obligatorio en el Etiquetado para esta DO.P. aunque no sean envejecidos)<br><br><strong>Requisitos</strong><br><br>> 85 % Cosecha<br><br><strong>Legislación</strong><br><br>Art. 49 y 58 Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_variedades_vid","<strong><u>VARIEDADES DE VID</u></strong><br><br><strong>Requisitos</strong><br><br>> 85 % una Variedad<br><br>100% dos variedades o más. En este caso, deberán aparecer en orden descendente en función de la proporción utilizada y en caracteres del mismo tamaño<br><br><strong>Legislación</strong><br><br>Art 50  Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_menciones_tradicionales","<strong><u>MENCIONES TRADICIONALES</u></strong><br><br>(Crianza, Reserva, Gran Reserva)<br><br><strong>Legislación</strong><br><br>Art. 24 RD 1363/11");
        entityTrazabilidad.set("desc_contenido_azucar","<strong><u>CONTENIDO EN AZÚZAR</u></strong><br><br><strong>Legislación</strong><br><br>Art 52  Rgto Delegado 2019/33");
        entityTrazabilidad.set("desc_simbolo_comunitario","<strong><u>SÍMBOLO COMUNITARIO DE DOP/IGP</u></strong><br><br><strong>Legislación</strong><br><br>Anexo de Rgto Delegado 664/2014");
        entityTrazabilidad.set("desc_metodos_produccion","<strong><u>MÉTODOS DE PRODUCCIÓN (FERMENTADO EN BARRICA O ROBLE)</u></strong><br><br><strong>Requisitos</strong><br><br>(Incluir meses o años para mención roble)<br><br>La barrica ha de ser de roble<br><br><strong>Legislación</strong><br><br>Anexo V Delegado 2019/33<br><br>Art. 18 RD 1363/11");
        entityTrazabilidad.set("desc_metodos_ecologicos","<strong><u>MÉTODOS DE PRODUCCIÓN ECOLÓGICOS</u></strong><br><br><strong>Legislación</strong><br><br>Art 53.6  Rgto Delegado 2019/33<br><br>Reglamento 834/2007, desarrollado por 889/2008");
        entityTrazabilidad.set("desc_unidad_geografica","<strong><u>UNIDAD GEOGRÁFICA MENOS O MAYOR VINO DE PUEBLO + TÉRMINO MUNICIPAL O ENTIDAD LOCAL MENOR</u></strong><br><br><strong>Requisitos</strong><br><br>Al menos el 85% de las uvas con las que se ha elaborado el producto vitivinícola, deberá proceder de esa unidad geográfica menor sea municipio o entidad local menor<br><br>> 85% de la uva de ese municipio<br><br><strong>Legislación</strong><br><br>Art. 55  Rgto Delegado 2019/33<br><br>Art. 21 Decreto 50/2018");
        entityTrazabilidad.set("desc_explotacion_viticola","<strong><u>EXPLOTACIÓN VITÍCOLA</u></strong><br><br><strong>Legislación</strong><br><br>Art. 54 Sección 2 Rgto. Delegado 2019/33 y anexo VI");
        entityTrazabilidad.set("desc_menciones_color","<strong><u>MENCIONES RELATIVAS AL COLOR</u></strong><br><br><strong>Legislación</strong><br><br>Art. 12 y anexo I de RD 1363/2011");
        entityTrazabilidad.set("desc_simbolo_ce","<strong><u>SIMBOLO CE 'E'</u></strong><br><br><strong>Legislación</strong><br><br>c) art. 9 RD 1801/2008");
        entityTrazabilidad.set("desc_marca", "<strong><u>MARCA</u></strong><br><br><strong>Legislación</strong><br><br>Nacional, Comunitaria y/o Internacional");

        entityTrazabilidad.set("desc_mencion_seleccion", "<strong><u>MENCIÓN “SELECCIÓN” O SIMILARES</u></strong><br><br><strong>Requisitos</strong><br><br>Para utilizar menciones coincidentes o similares a “Selección especial”, “Selección de la Familia”, “Selección de viñedos”, “Seleccionado”, “Colección”, “Edición limitada”, “Serie”, se deberá informar sucintamente al Consejo Regulador, de los criterios o procesos seguidos para realizar la selección y/o personas que la realizaron.");
        entityTrazabilidad.set("desc_mencion_vinas", "<strong><u>MENCIÓN “VIÑAS VIEJAS”, “VIÑEDOS VIEJOS”, “VIÑEDOS CENTENARIOS”, “VIÑEDO PREFILOXÉRICO” O SIMILARES</u></strong><br><br><strong>Requisitos</strong><br><br>Las menciones “Viñas viejas” o “Viñedos viejos”, deberá completarse con una referencia concreta a la edad de la viña o del viñedo, siempre que el vino en cuestión proceda de cepas plantadas en el mismo que tengan una edad superior a 35 años. Para la mención “Viñedos centenarios” la edad mínima de las viñas deberá ser de 100 años. Se podrá incluir también la expresión equivalente en cada caso, como por ejemplo “Viñedos de más de X años”. Para el cálculo de la edad del viñedo se utilizará la media ponderada por la superficie de los viñedos afectados. En el caso de la mención “Viñedo prefiloxérico” se podrá utilizar exclusivamente en aquellos cuyas cepas hayan sido plantadas con anterioridad al año 1990.");
        entityTrazabilidad.set("desc_mencion_vinedos_altura", "<strong><u>MENCIÓN “VIÑEDOS DE ALTURA” O SIMILARES</u></strong><br><br><strong>Requisitos</strong><br><br>Deberá completarse con una referencia concreta a la altura promedio del viñedo sobre el nivel del mar. Para el cálculo de la altura del viñedo se utilizará la media ponderada por la altitud de los viñedos afectados.");
        entityTrazabilidad.set("desc_mencion_vinedos_propios", "<strong><u>MENCIÓN “VIÑEDOS PROPIOS” O SIMILARES</u></strong><br><br><strong>Requisitos</strong><br><br>Podrá utilizarse si el vino procede de viñedos de titularidad o propiedad de la bodega donde se realice el etiquetado.");
        //entityTrazabilidad.set("desc_mencion_no_admitidas", "<strong><u>MENCIONES NO ADMITIDAS SEGÚN REQUISITOS MÍNIMOS ESTABLECIDOS: “VINO DE AUTOR”, “VINO DE ALTA EXPRESIÓN” O SIMILARES.</u></strong>");

        trazabilidadRepo.save(entityTrazabilidad);
    }

    function anadirDescripcionAptitudFisicoQuimica(vino_aptitud_id){
        //console.log("anadirDescripcionAptitudFisicoQuimica");
        reset();
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);
        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        //if (dop_id == 1 && vino_aptitud_id == 'Blanco'){
        if (dop_id == 1 && vino_aptitud_id == 1){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 10.5 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 10.5 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.8 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color amarillo acerado-amarillo dorado, limpio y/o brillante y sin partículas en suspensión");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas frutales y sin defectos aromáticos. En caso de vinos blancos envejecidos aromas procedentes de la madera");
            entityTrazabilidad.set("gustativa_descripcion", "Equilibrados y frescos");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 10.5);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 10.5);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.8);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);

        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Rosado (Comercializado primer año)'){
        else if (dop_id == 1 && vino_aptitud_id == 2){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.8 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color cebolla-rojo fresa, limpio y/o brillante y sin partículas en suspensión. En vinos envejecidos color piel de cebolla-rojo frambuesa");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas de frutas rojas y/o negras. Sin olores defectuosos. En vinos envejecidos aromas de frutas frescas y/o compotadas rojas y aromas procedentes de la madera");
            entityTrazabilidad.set("gustativa_descripcion", "Frescos y equilibrados");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.8);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Rosado (Comercializado superior un año)'){
        else if (dop_id == 1 && vino_aptitud_id == 3){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "0.8 + 0.06 * (grado alcoholico adquirido – 10) > 1.08 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color cebolla-rojo fresa, limpio y/o brillante y sin partículas en suspensión. En vinos envejecidos color piel de cebolla-rojo frambuesa");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas de frutas rojas y/o negras. Sin olores defectuosos. En vinos envejecidos aromas de frutas frescas y/o compotadas rojas y aromas procedentes de la madera");
            entityTrazabilidad.set("gustativa_descripcion", "Frescos y equilibrados");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 1.08);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Tinto (Comercializado primer año)'){
        else if (dop_id == 1 && vino_aptitud_id == 4){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11.5 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11.5 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.8 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color rojo violáceo-rojo púrpura, con ribetes que denotan juventud. Limpios y sin partículas en suspensión");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas a frutas rojas y/o negras con intensidad media o alta y sin olores defectuosos");
            entityTrazabilidad.set("gustativa_descripcion", "Equilibrados y frescos");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11.5);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11.5);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.8);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Tinto (Comercializado superior un año)'){
        else if (dop_id == 1 && vino_aptitud_id == 5){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11.5 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11.5 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "0.8 + 0.06 * (grado alcoholico adquirido – 10) > 1.2 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color rojo violáceo-rojo púrpura, con ribetes que denotan juventud. Limpios y sin partículas en suspensión");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas a frutas rojas y/o negras con intensidad media o alta y sin olores defectuosos");
            entityTrazabilidad.set("gustativa_descripcion", "Equilibrados y frescos");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11.5);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11.5);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 1.2);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Tinto envejecido (Comercializado primer año)'){
        else if (dop_id == 1 && vino_aptitud_id == 6){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.8 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color rojo granate-rojo teja con tonalidades de envejecimiento, limpio y sin partículas en suspensión");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas equilibrados madera-fruta y sin olores defectuosos");
            entityTrazabilidad.set("gustativa_descripcion", "Secos y equilibrados en acidez");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.8);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 1 && vino_aptitud_id == 'Tinto envejecido (Comercializado superior un año)'){
        else if (dop_id == 1 && vino_aptitud_id == 7){
            entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
            entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
            entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "1 + 0.06 * (grado alcoholico adquirido – 10) > 1.2 -> NO CUMPLE");
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
            entityTrazabilidad.set("visual_descripcion", "Color rojo granate-rojo teja con tonalidades de envejecimiento, limpio y sin partículas en suspensión");
            entityTrazabilidad.set("olfativa_descripcion", "Aromas equilibrados madera-fruta y sin olores defectuosos");
            entityTrazabilidad.set("gustativa_descripcion", "Secos y equilibrados en acidez");

            entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12);
            entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
            entityTrazabilidad.set("acidez_total_minima_valor", 4);
            entityTrazabilidad.set("acidez_volatil_maxima_valor", 1.2);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
        }
        //else if (dop_id == 2 && vino_aptitud_id == 'Blanco seco'){
        else if (dop_id == 2 && vino_aptitud_id == 8){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.6 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Si los azúcares totales máximos <= 5: Dato > 160 -> NO CUMPLE. Si los azúcares totales máximos > 5: Dato > 250 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color amarillo a dorado, limpio y/o brillante y sin partículas en suspensión. Los vinos envejecidos en barrica presentan colores más intensos que los tipos de vino joven aportado por su proceso de elaboración, y de igual forma deben ser limpios y brillantes");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos. En caso de vinos fermentados en barrica deben presentar aromas limpios con tonos ahumados y tostados entremezclados con aromas frutales y florales propios de la variedad de intensidad media");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado. Los vinos fermentados en barrica deben transmitir sensaciones grasas, amplias y complejas con un roble bien ensamblado. Este tipo de vino debe tener un importante componente varietal");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.6);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_min", 160);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_max", 250);
         }
        //else if (dop_id == 2 && vino_aptitud_id == 'Blanco semidulce'){
        else if (dop_id == 2 && vino_aptitud_id == 9){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 11 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion",  "Dato > 45 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.6 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Si los azúcares totales máximos <= 5: Dato > 160 -> NO CUMPLE. Si los azúcares totales máximos > 5: Dato > 250 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color amarillo a dorado, limpio y/o brillante y sin partículas en suspensión. Los vinos envejecidos en barrica presentan colores más intensos que los tipos de vino joven aportado por su proceso de elaboración, y de igual forma deben ser limpios y brillantes");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos. En caso de vinos fermentados en barrica deben presentar aromas limpios con tonos ahumados y tostados entremezclados con aromas frutales y florales propios de la variedad de intensidad media");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado. Los vinos fermentados en barrica deben transmitir sensaciones grasas, amplias y complejas con un roble bien ensamblado. Este tipo de vino debe tener un importante componente varietal");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 11);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 11);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 45);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.6);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_min", 160);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_max", 250);
        }
        //else if (dop_id == 2 && vino_aptitud_id == 'Clarete'){
        else if (dop_id == 2 && vino_aptitud_id == 10){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.7 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Si los azúcares totales máximos <= 5: Dato > 160 -> NO CUMPLE. Si los azúcares totales máximos > 5: Dato > 250 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color rojo claro a rojo intenso. Limpio y brillante. Sin partículas en suspensión");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.7);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_min", 160);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_max", 250);
         }
         //else if (dop_id == 2 && vino_aptitud_id == 'Rosado'){
         else if (dop_id == 2 && vino_aptitud_id == 11){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 9 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.7 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Si los azúcares totales máximos <= 5: Dato > 160 -> NO CUMPLE. Si los azúcares totales máximos > 5: Dato > 250 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color rosa a rojo claro. Limpio y brillante. Sin partículas en suspensión");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 9);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.7);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_min", 160);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor_max", 250);
         }
         //else if (dop_id == 2 && vino_aptitud_id == 'Tinto'){
         else if (dop_id == 2 && vino_aptitud_id == 12){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12.5 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12.5 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "Dato > 0.7 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color rojo intenso con tonalidades de juventud. Intensidad media/alta. Limpio y brillante. Sin partículas en suspensión");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado. Buena estructura");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12.5);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12.5);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.7);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
          }
         //else if (dop_id == 2 && vino_aptitud_id == 'Crianza, Reserva, Gran Reserva'){
          else if (dop_id == 2 && vino_aptitud_id == 13){
             entityTrazabilidad.set("grado_alcoholico_total_minimo_descripcion", "Dato < 12.5 -> NO CUMPLE");
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_descripcion", "Dato < 12.5 -> NO CUMPLE");
             entityTrazabilidad.set("azucares_totales_maximos_descripcion", "Dato > 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_total_minima_descripcion", "Dato < 4 -> NO CUMPLE");
             entityTrazabilidad.set("acidez_volatil_maxima_descripcion", "0.7 + 0.06 * (grado alcoholico adquirido – 10) > 0.7 -> NO CUMPLE");
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_descripcion", "Dato > 150 -> NO CUMPLE");
             entityTrazabilidad.set("visual_descripcion", "Color rojo intenso con tonalidades de envejecimiento. Intensidad media/alta. Limpio y brillante. Sin partículas en suspensión");
             entityTrazabilidad.set("olfativa_descripcion", "Aromas francos, agradables e intensos. Sin olores defectuosos");
             entityTrazabilidad.set("gustativa_descripcion", "Sabroso y equilibrado. Buena estructura");

             entityTrazabilidad.set("grado_alcoholico_total_minimo_valor", 12.5);
             entityTrazabilidad.set("grado_alcoholico_adquirido_minimo_valor", 12.5);
             entityTrazabilidad.set("azucares_totales_maximos_valor", 4);
             entityTrazabilidad.set("acidez_total_minima_valor", 4);
             entityTrazabilidad.set("acidez_volatil_maxima_valor", 0.7);
             entityTrazabilidad.set("anhidrido_sulforoso_total_maximo_valor", 150);
         }
        trazabilidadRepo.save(entityTrazabilidad);

    }

    function reset(){
        if (vh.widget('grado_alcoholico_total_minimo_cumple') != null){
            var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
            var trazabilidad_id = entity.get("trazabilidad_id");
            entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
            entityTrazabilidad.set("grado_alcoholico_total_minimo", null);
            entityTrazabilidad.set("grado_alcoholico_adquirido_minimo", null);
            entityTrazabilidad.set("azucares_totales_maximos", null);
            entityTrazabilidad.set("acidez_total_minima", null);
            entityTrazabilidad.set("acidez_volatil_maxima", null);
            entityTrazabilidad.set("anhidrido_sulforoso_total_maximo", null);
            trazabilidadRepo.save(entityTrazabilidad);

            vh.setWidgetValue('grado_alcoholico_total_minimo_cumple',null);
            vh.updateWidget('grado_alcoholico_total_minimo_cumple');
            vh.setWidgetValue('grado_alcoholico_adquirido_minimo_cumple',null);
            vh.updateWidget('grado_alcoholico_adquirido_minimo_cumple');
            vh.setWidgetValue('azucares_totales_maximos_cumple',null);
            vh.updateWidget('azucares_totales_maximos_cumple');
            vh.setWidgetValue('acidez_total_minima_cumple',null);
            vh.updateWidget('acidez_total_minima_cumple');
            vh.setWidgetValue('acidez_volatil_maxima_cumple',null);
            vh.updateWidget('acidez_volatil_maxima_cumple');
            vh.setWidgetValue('anhidrido_sulforoso_total_maximo_cumple',null);
            vh.updateWidget('anhidrido_sulforoso_total_maximo_cumple');

            vh.setWidgetValue('grado_alcoholico_total_minimo',null);
            vh.updateWidget('grado_alcoholico_total_minimo');
            vh.setWidgetValue('grado_alcoholico_adquirido_minimo',null);
            vh.updateWidget('grado_alcoholico_adquirido_minimo');
            vh.setWidgetValue('azucares_totales_maximos',null);
            vh.updateWidget('azucares_totales_maximos');
            vh.setWidgetValue('acidez_total_minima',null);
            vh.updateWidget('acidez_total_minima');
            vh.setWidgetValue('acidez_volatil_maxima',null);
            vh.updateWidget('acidez_volatil_maxima');
            vh.setWidgetValue('anhidrido_sulforoso_total_maximo',null);
            vh.updateWidget('anhidrido_sulforoso_total_maximo');

        }
    }

    function cumpleGradoAlcoholicoTotalMinimo(grado_alcoholico_total_minimo, grado_alcoholico_total_minimo_valor){
        //console.log("cumpleGradoAlcoholicoTotalMinimo");
        //console.log("grado_alcoholico_total_minimo::: "+grado_alcoholico_total_minimo);
        //console.log("grado_alcoholico_total_minimo_valor::: "+grado_alcoholico_total_minimo_valor);
        if (vh.widget("grado_alcoholico_total_minimo_cumple") != null){
            vh.setWidgetValue('grado_alcoholico_total_minimo_cumple',grado_alcoholico_total_minimo!=null?(grado_alcoholico_total_minimo < grado_alcoholico_total_minimo_valor?'NO':'SI'):null);
        }
    }

    function cumpleGradoAlcoholicoAdquiridoMinimo(grado_alcoholico_adquirido_minimo, grado_alcoholico_adquirido_minimo_valor, vino_aptitud_id){
        //console.log("cumpleGradoAlcoholicoAdquiridoMaximo");
        //console.log("grado_alcoholico_adquirido_minimo::: "+grado_alcoholico_adquirido_minimo);
        //console.log("grado_alcoholico_adquirido_minimo_valor::: "+grado_alcoholico_adquirido_minimo_valor);
        if (vh.widget("grado_alcoholico_adquirido_minimo_cumple") != null){
            vh.setWidgetValue('grado_alcoholico_adquirido_minimo_cumple',grado_alcoholico_adquirido_minimo!=null?(grado_alcoholico_adquirido_minimo < grado_alcoholico_adquirido_minimo_valor?'NO':'SI'):null);
        }

        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);
        if (dop_id == 1 && (vino_aptitud_id == 3 || vino_aptitud_id == 5 || vino_aptitud_id == 7)){
            cumpleAcidezVolatilMaximaDesdeGradoAlcoholicoAdquiridoMinimo(dop_id, vino_aptitud_id, grado_alcoholico_adquirido_minimo);
        }

    }

    function cumpleAzucaresTotalesMaximos(azucares_totales_maximos, azucares_totales_maximos_valor, anhidrido_sulforoso_total_maximo, anhidrido_sulforoso_total_maximo_valor, anhidrido_sulforoso_total_maximo_valor_min, anhidrido_sulforoso_total_maximo_valor_max, vino_aptitud_id){
        //console.log("cumpleAzucaresTotalesMaximos");
        //console.log("azucares_totales_maximos::: "+azucares_totales_maximos);
        //console.log("azucares_totales_maximos_valor::: "+azucares_totales_maximos_valor);
        if (vh.widget("azucares_totales_maximos_cumple") != null){
            vh.setWidgetValue('azucares_totales_maximos_cumple',azucares_totales_maximos!=null?(azucares_totales_maximos > azucares_totales_maximos_valor?'NO':'SI'):null);
        }
        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        if (dop_id == 2 && (vino_aptitud_id == 8 || vino_aptitud_id == 9 || vino_aptitud_id == 10 || vino_aptitud_id == 11)){
            cumpleAnhidridoSulfurosoTotalMaximo(anhidrido_sulforoso_total_maximo, anhidrido_sulforoso_total_maximo_valor, azucares_totales_maximos, anhidrido_sulforoso_total_maximo_valor_min, anhidrido_sulforoso_total_maximo_valor_max, vino_aptitud_id);
        }
    }

    function cumpleAcidezTotalMinima(acidez_total_minima, acidez_total_minima_valor){
        //console.log("cumpleAcidezTotalMinima");
        //console.log("acidez_total_minima::: "+acidez_total_minima);
        //console.log("acidez_total_minima_valor::: "+acidez_total_minima_valor);
        if (vh.widget("acidez_total_minima_cumple") != null){
            vh.setWidgetValue('acidez_total_minima_cumple',acidez_total_minima!=null?(acidez_total_minima < acidez_total_minima_valor?'NO':'SI'):null);
        }
    }

    function cumpleAcidezVolatilMaximaDesdeGradoAlcoholicoAdquiridoMinimo(dop_id, vino_aptitud_id, grado_alcoholico_adquirido_minimo){
        //console.log("cumpleAcidezVolatilMaximaDesdeGradoAlcoholicoAdquiridoMinimo");
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);
        //console.log("grado_alcoholico_adquirido_minimo::: "+grado_alcoholico_adquirido_minimo);
        //console.log("dop_id::: "+dop_id);
        var valor = (dop_id == 1 && vino_aptitud_id == 3)?1.08:1.2;
        if (vh.widget("acidez_volatil_maxima_cumple") != null){
            vh.setWidgetValue('acidez_volatil_maxima_cumple',grado_alcoholico_adquirido_minimo!=null?(0.8+0.06*(grado_alcoholico_adquirido_minimo-10) > valor?'NO':'SI'):null);
        }
    }

    function cumpleAcidezVolatilMaxima(acidez_volatil_maxima, acidez_volatil_maxima_valor, grado_alcoholico_adquirido_minimo, vino_aptitud_id){
        //console.log("cumpleAcidezVolatilMaxima");
        //console.log("acidez_volatil_maxima::: "+acidez_volatil_maxima);
        //console.log("acidez_volatil_maxima_valor::: "+acidez_volatil_maxima_valor);
        //console.log("grado_alcoholico_adquirido_minimo::: "+grado_alcoholico_adquirido_minimo);
        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);
        if (vh.widget("acidez_volatil_maxima_cumple") != null){
            if (dop_id == 2 && vino_aptitud_id == 13){
                vh.setWidgetValue('acidez_volatil_maxima_cumple',acidez_volatil_maxima!=null && grado_alcoholico_adquirido_minimo!=null?(acidez_volatil_maxima > 0.7 + 0.06*(grado_alcoholico_adquirido_minimo-10)?'NO':'SI'):null);
            }else{
                vh.setWidgetValue('acidez_volatil_maxima_cumple',acidez_volatil_maxima!=null?(acidez_volatil_maxima < acidez_volatil_maxima_valor?'SI':'NO'):null);
            }
        }
    }

    function cumpleAnhidridoSulfurosoTotalMaximo(anhidrido_sulforoso_total_maximo, anhidrido_sulforoso_total_maximo_valor, azucares_totales_maximos, anhidrido_sulforoso_total_maximo_valor_min, anhidrido_sulforoso_total_maximo_valor_max, vino_aptitud_id){
        //console.log("cumpleAnhidridoSulfurosoTotalMaximo");
        //console.log("anhidrido_sulforoso_total_maximo::: "+anhidrido_sulforoso_total_maximo);
        //console.log("anhidrido_sulforoso_total_maximo_valor::: "+anhidrido_sulforoso_total_maximo_valor);
        //console.log("anhidrido_sulforoso_total_maximo_valor_min::: "+anhidrido_sulforoso_total_maximo_valor_min);
        //console.log("anhidrido_sulforoso_total_maximo_valor_max::: "+anhidrido_sulforoso_total_maximo_valor_max);
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);

        var dop_id = new java.lang.Long(view.get("dop_id"));
        //console.log("dop_id::: "+dop_id);
        //console.log("vino_aptitud_id::: "+vino_aptitud_id);
        if (vh.widget("anhidrido_sulforoso_total_maximo_cumple") != null){
            if (dop_id == 2 && (vino_aptitud_id == 8 || vino_aptitud_id == 9 || vino_aptitud_id == 10 || vino_aptitud_id == 11)){
                var cumple = "SI";
                if (azucares_totales_maximos > 5 && anhidrido_sulforoso_total_maximo > anhidrido_sulforoso_total_maximo_valor_max){
                    cumple = "NO";
                }
                if ((azucares_totales_maximos < 5 || azucares_totales_maximos == 5) && anhidrido_sulforoso_total_maximo > anhidrido_sulforoso_total_maximo_valor_min){
                    cumple = "NO";
                }

                vh.setWidgetValue('anhidrido_sulforoso_total_maximo_cumple',anhidrido_sulforoso_total_maximo!=null && azucares_totales_maximos!=null?cumple:null);
            }else{
                vh.setWidgetValue('anhidrido_sulforoso_total_maximo_cumple',anhidrido_sulforoso_total_maximo!=null?(anhidrido_sulforoso_total_maximo > anhidrido_sulforoso_total_maximo_valor?'NO':'SI'):null);
            }
        }
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
                //console.log("i: " + i);
                var entityNumeracionContraetiquetado = lstNumeracionContraetiquetado.get(i);
                var trazabilidad_id_bbdd = new java.lang.Long(entityNumeracionContraetiquetado.get("trazabilidad_id"))
                var asignada_bbdd = entityNumeracionContraetiquetado.get("asignada");
                //console.log("trazabilidad_id_view: "+ trazabilidad_id_view)
                //console.log("trazabilidad_id_bbdd: " + trazabilidad_id_bbdd);
                //console.log("asignada_view: "+ asignada_view)
                //console.log("asignada_bbdd: " + asignada_bbdd);

                if (trazabilidad_id_view == trazabilidad_id_bbdd && asignada_view == asignada_bbdd){
                    var unidades = new java.lang.Long(entityNumeracionContraetiquetado.get("hasta") - entityNumeracionContraetiquetado.get("desde") + 1)
                    //console.log("unidades: " + unidades);
                    entityNumeracionContraetiquetado.set("unidades", unidades);
                    numeracionContraetiquetadoRepo.save(entityNumeracionContraetiquetado);
                    unidadesTotales = unidadesTotales + unidades;
                }
                i++;
            }
            //console.log("unidadesTotales: " + unidadesTotales);
            var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id_view);
            //console.log("unidadesTotales: " + unidadesTotales);
            if (asignada_view == 'Y'){
                entityTrazabilidad.set("contras_usadas", new java.lang.Long((unidadesTotales)))
            }else{
                entityTrazabilidad.set("contras_sin_usar", new java.lang.Long((unidadesTotales)))
            }
            //console.log("entityLoteControlDocumental.get('contraetiquetas_numeraciones_utilizadas'): " + entityLoteControlDocumental.get("contraetiquetas_numeraciones_utilizadas"));
            trazabilidadRepo.save(entityTrazabilidad)
        }

        updateBalanceContraetiquetasUsadasDesdeContrasUsadas();
    }

    function setContraetiquetasNumeracionesUtilizadas(){
        //console.log("setContraetiquetasNumeracionesUtilizadas");
        setContraetiquetasNumeraciones('Y');
    }
    function setContraetiquetasNumeracionesSinUtilizar(){
        //console.log("setContraetiquetasNumeracionesSinUtilizar");
        setContraetiquetasNumeraciones('N');
    }

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
        var operador_id_view = entity.get("operador_id");
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

    function updateCapacidadInstalada(component){
       //console.log("updateCapacidadInstalada");
        var operador_id_view = entity.get("operador_id");
        //console.log("operador_id_view::: "+operador_id_view);

        var barricaRepo = ctx.get("repos").get("barricaRepo");
        var lstBarricas = barricaRepo.listAll();

        var capacidad_total_barricas = getCapacidadTotalBarricas(operador_id_view);
        //console.log("capacidad_total_barricas: " + capacidad_total_barricas);

        var capacidad_total_depositos = getCapacidadTotalDepositos(operador_id_view);
        //console.log("capacidad_total_depositos: " + capacidad_total_depositos);

        var capacidad_total = capacidad_total_depositos + capacidad_total_barricas;

        //console.log("capacidad_total: " + capacidad_total);
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+capacidad_total.toString()));

        var operadorRepo = ctx.get("repos").get("operadorRepo");
        var entityOperador = operadorRepo.findById(operador_id_view);
        entityOperador.set("capacidad_instalada", capacidad_total);
        operadorRepo.save(entityOperador);
    }

    function getCapacidadTotalBarricas(operador_id_view){
        //console.log("getCapacidadTotalBarricas ");
        var barricaRepo = ctx.get("repos").get("barricaRepo");
        var lstBarricas = barricaRepo.listAll();

        var capacidad_total_barricas=0;
        var i = 0;
        while(i != lstBarricas.size()){
            //console.log("i: " + i);
            var entityBarrica = lstBarricas.get(i);

            var operador_id_bbdd = new java.lang.Long(entityBarrica.get("operador_id"))
            //console.log("operador_id_bbdd: "+ operador_id_bbdd)
            //console.log("operador_id_view: " + operador_id_view);

            if (operador_id_view == operador_id_bbdd && entityBarrica.get("cap_total") != null){
                var cap_total = new java.math.BigDecimal(entityBarrica.get("cap_total")).setScale(2, java.math.RoundingMode.HALF_UP);
                //console.log("cap_total: " + cap_total);
                capacidad_total_barricas = capacidad_total_barricas + cap_total;
            }
            i++;
        }
        return capacidad_total_barricas;
    }

    function getCapacidadTotalDepositos(operador_id_view){
        //console.log("getCapacidadTotalDepositos ");
        var depositoRepo = ctx.get("repos").get("depositoRepo");
        var lstDepositos = depositoRepo.listAll();

        var capacidad_total_depositos=0;
        i = 0;
        while(i != lstDepositos.size()){
            //console.log("i: " + i);
            var entityDeposito = lstDepositos.get(i);

            var operador_id_bbdd = new java.lang.Long(entityDeposito.get("operador_id"))
            //console.log("operador_id_bbdd: "+ operador_id_bbdd)
            //console.log("operador_id_view: " + operador_id_view);
            if (operador_id_view == operador_id_bbdd && entityDeposito.get("capacidad") != null){
                var capacidad = new java.math.BigDecimal(entityDeposito.get("capacidad")).setScale(2, java.math.RoundingMode.HALF_UP);
                //console.log("capacidad: " + capacidad);
                capacidad_total_depositos = capacidad_total_depositos + capacidad;
            }
            i++;
        }
        return capacidad_total_depositos;
    }

    function updateTotalDepositos(component){
        //console.log("updateTotalDepositos");
        var operador_id_view = entity.get("operador_id");
        var capacidad_total_depositos= getCapacidadTotalDepositos(operador_id_view);
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+capacidad_total_depositos));
    }

    function updateTotalBarricas(component){
        //console.log("updateTotalBarricas");
        var operador_id_view = entity.get("operador_id");
        var capacidad_total_barricas = getCapacidadTotalBarricas(operador_id_view);
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+capacidad_total_barricas));
    }

    function updateConformeRendimiento(component){
        var conformeRendimiento = null;
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
        if (kg_cupo>0 && kg_totales_recepcionados>0){
            if (kg_cupo < kg_totales_recepcionados){
                conformeRendimiento = "NO";
             }
             else{
                conformeRendimiento = "SI";
             }
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
                    cumpleRendimiento = "NO";
                 }
                 else{
                     //console.log("else");
                    cumpleRendimiento = "SI";
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

    function setCumpleDocumentacionRegistro(component){
        //console.log("setCumpleDocumentacionRegistro");
        var expediente_id = entity.get("expediente_id");
        //console.log("expediente_id::: "+expediente_id);

        var expedienteRepo = ctx.get("repos").get("expedienteRepo");
        var operadorRepo = ctx.get("repos").get("operadorRepo");
        var documentacionRegistroRepo = ctx.get("repos").get("documentacionRegistroRepo");

        var entityExpediente = expedienteRepo.findById(expediente_id);
        var operador_id = entityExpediente.get("operador_id");
        //console.log("operador_id::: "+operador_id);

        var entityOperador = operadorRepo.findById(operador_id);
        var num_reovi = entityOperador.get("num_reovi");
        var num_reg_envasadores_embotelladores = entityOperador.get("num_reg_envasadores_embotelladores");
        var num_reg_empresas_actividades = entityOperador.get("num_reg_empresas_actividades");
        var num_cae = entityOperador.get("num_cae");

        //console.log("num_reovi::: "+num_reovi);
        //console.log("num_reg_envasadores_embotelladores::: "+num_reg_envasadores_embotelladores);
        //console.log("num_reg_empresas_actividades::: "+num_reg_empresas_actividades);
        //console.log("num_cae::: "+num_cae);

        if (num_reovi == null || num_reg_envasadores_embotelladores == null || num_reg_empresas_actividades == null || num_reg_empresas_actividades == null){
            var lstDocumentacionRegistro = documentacionRegistroRepo.listAll();
            var i = 0;

            while(i != lstDocumentacionRegistro.size()){
                entityDocumentacionRegistro = lstDocumentacionRegistro.get(i);
                if (entityDocumentacionRegistro.get("expediente_id") == expediente_id){
                    if (entityDocumentacionRegistro.get("pregunta_id") == 13 || entityDocumentacionRegistro.get("pregunta_id") == 105){
                        if (num_reovi == null){
                            entityDocumentacionRegistro.set("cumple","NO");
                        }else{
                            entityDocumentacionRegistro.set("cumple","SI");
                        }
                    }
                    if (entityDocumentacionRegistro.get("pregunta_id") == 14 ||entityDocumentacionRegistro.get("pregunta_id") == 106){
                        if (num_reg_envasadores_embotelladores == null){
                            entityDocumentacionRegistro.set("cumple","NO");
                        }else{
                            entityDocumentacionRegistro.set("cumple","SI");
                        }
                    }
                    if (entityDocumentacionRegistro.get("pregunta_id") == 15 || entityDocumentacionRegistro.get("pregunta_id") == 107){
                        if (num_reg_empresas_actividades == null){
                            entityDocumentacionRegistro.set("cumple","NO");
                        }else{
                            entityDocumentacionRegistro.set("cumple","SI");
                        }
                    }
                    if (entityDocumentacionRegistro.get("pregunta_id") == 16 || entityDocumentacionRegistro.get("pregunta_id") == 108){
                        if (num_cae == null){
                            entityDocumentacionRegistro.set("cumple","NO");
                        }else{
                            entityDocumentacionRegistro.set("cumple","SI");
                        }
                    }
                    entityDocumentacionRegistro.set("f_actualizacion", obtenerTimestampActual());
                    documentacionRegistroRepo.save(entityDocumentacionRegistro);
                }
             i++;
            }
        }
    }

    function updateDesviaciones(){
        //console.log("updateDesviaciones");
        //console.log("entity::: "+entity);
        if (entity.getId() != null){
            //console.log("entity::: "+entity);

            //var expediente_id_view = view.get("newExpediente");
            var expediente_id_view = new java.lang.String(entity.getId());
            var operador_id_view = entity.get("operador_id");
            var dop_id_view = entity.get("dop_id");
            //console.log("expediente_id_view::: "+expediente_id_view);
            //console.log("operador_id_view::: "+operador_id_view);
            //console.log("dop_id_view::: "+dop_id_view);
            //console.log("expediente_id::: "+entity.get("expediente_id"));
            //console.log("operador_id::: "+operador_id_view);
            //console.log("dop_id_view::: "+dop_id_view);
            //console.log("expediente_id_view::: "+view.get("newExpediente"));

            if (expediente_id_view != null && operador_id_view != null && dop_id_view != null){
                //Revisión documental previa
                updateDesviacionesRevisionDocumentalPrevia(expediente_id_view, dop_id_view); //Para la f_actualizacion

                //Declaraciones obligatorias
                updateDesviacionesControlDocumentalDeclaracionesObligatorias(expediente_id_view, dop_id_view);

                //Documentacion registro
                updateDesviacionesDocumentacionRegistro(expediente_id_view, dop_id_view);



                //Control documental libro registro
                updateDesviacionesControlDocumentalLibroRegistro(expediente_id_view, dop_id_view);

                //Control documental documentos acompanamiento
                updateDesviacionesControlDocumentalDocumentosAcompanamiento(expediente_id_view, dop_id_view);

                if (dop_id_view != null){
                    //Verificación pliego condiciones
                    updateDesviacionesVerificacionPliegoCondiciones(expediente_id_view, dop_id_view);

                    //REndimientos de la extracción
                    updateDesviacionesPCC41(expediente_id_view, dop_id_view);

                    updateDesviacionesTrazabilidad(expediente_id_view, dop_id_view);

                    updateDesviacionesAforo(expediente_id_view);
                }

                //Verificación de la instalación
                updateDesviacionesVerificacionInstalacion(expediente_id_view, dop_id_view);

                if (operador_id_view != null){
                    //Capacidad de la instalación
                    updateDesviacionesCapacidadInstalacion(expediente_id_view, operador_id_view, dop_id_view);
                }
            }
        }
    }

    function updateDesviacionesRevisionDocumentalPrevia(expediente_id_view, dop_id_view){
            //console.log("updateDesviacionesRevisionDocumentalPrevia");
            var revisionDocumentalRepo = ctx.get("repos").get("revisionDocumentalRepo");
            var lstRevisionDocumental = revisionDocumentalRepo.listAll();

            //console.log("expediente_id_view::: "+expediente_id_view);

            var i = 0;
            while(i != lstRevisionDocumental.size()){
                var entityRevisionDocumental = lstRevisionDocumental.get(i);
                var expediente_id_bbdd = new java.lang.String(entityRevisionDocumental.get("expediente_id"));
                var pregunta_id_bbdd = entityRevisionDocumental.get("pregunta_id");
                //console.log("--------------------------------------------------------------------------------------------");
                //console.log("i::: "+i);
                //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);

                 var pregunta;
                if (dop_id_view == 1){
                    pregunta = pregunta_id_bbdd == 1 || pregunta_id_bbdd == 2 || pregunta_id_bbdd == 3 || pregunta_id_bbdd == 4 || pregunta_id_bbdd == 5 || pregunta_id_bbdd == 6 || pregunta_id_bbdd == 7 || pregunta_id_bbdd == 8 || pregunta_id_bbdd == 9 || pregunta_id_bbdd == 10 || pregunta_id_bbdd == 11;
                }else{
                    pregunta = pregunta = pregunta_id_bbdd == 93 || pregunta_id_bbdd == 94 || pregunta_id_bbdd == 95 || pregunta_id_bbdd == 96 || pregunta_id_bbdd == 97 || pregunta_id_bbdd == 98 || pregunta_id_bbdd == 99 || pregunta_id_bbdd == 100 || pregunta_id_bbdd == 101 || pregunta_id_bbdd == 102 || pregunta_id_bbdd == 103;
                }

                if (expediente_id_bbdd == expediente_id_view && pregunta){
                    //console.log("expediente_id_bbdd == expediente_id_view");
                    entityRevisionDocumental.set("f_actualizacion",obtenerTimestampActual());
                    revisionDocumentalRepo.save(entityRevisionDocumental);
                }
                i++;
            }
        }

    function updateDesviacionesDocumentacionRegistro(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesDocumentacionRegistro");
        var documentacionRegistroRepo = ctx.get("repos").get("documentacionRegistroRepo");
        //console.log("documentacionRegistroRepo::: "+documentacionRegistroRepo);

        var lstDocumentacionRegistro = documentacionRegistroRepo.listAll();
        var i = 0;
        //console.log("lstDocumentacionRegistro.size()::: "+lstDocumentacionRegistro.size());

       while(i != lstDocumentacionRegistro.size()){
            var entityDocumentoRegistro = lstDocumentacionRegistro.get(i);
            var expediente_id_bbdd = new java.lang.String(entityDocumentoRegistro.get("expediente_id"));
            var pregunta_id_bbdd = entityDocumentoRegistro.get("pregunta_id");
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);
            //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);

            var pregunta;
            if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 12 || pregunta_id_bbdd == 13 || pregunta_id_bbdd == 14 || pregunta_id_bbdd == 15 || pregunta_id_bbdd == 16;
            }else{
                pregunta = pregunta_id_bbdd == 104 || pregunta_id_bbdd == 105 || pregunta_id_bbdd == 106 || pregunta_id_bbdd == 107 || pregunta_id_bbdd == 108;
            }
             //console.log("--------------------------------------------------------------------------------------------");
            //console.log("i::: "+i);


            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("expediente_id_bbdd == expediente_id_view");
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityDocumentoRegistro.get("cumple"), entityDocumentoRegistro.get("observaciones"));
                entityDocumentoRegistro.set("f_actualizacion",obtenerTimestampActual());
                documentacionRegistroRepo.save(entityDocumentoRegistro);
            }
            i++;
        }
    }

    function updateDesviacionesControlDocumentalDeclaracionesObligatorias(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesControlDocumentalDeclaracionesObligatorias");
        var controlDocumentalDeclaracionesObligatoriasRepo = ctx.get("repos").get("controlDocumentalDeclaracionesObligatoriasRepo");
        //console.log("controlDocumentalDeclaracionesObligatoriasRepo::: "+controlDocumentalDeclaracionesObligatoriasRepo);
        var lstControlDocumentalDeclaracionesObligatorias = controlDocumentalDeclaracionesObligatoriasRepo.listAll();
        //console.log("lstControlDocumentalDeclaracionesObligatorias.size()::: "+lstControlDocumentalDeclaracionesObligatorias.size());
        var i = 0;
        while(i != lstControlDocumentalDeclaracionesObligatorias.size()){
            var entityControlDocumentalDeclaracionesObligatorias = lstControlDocumentalDeclaracionesObligatorias.get(i);
            expediente_id_bbdd = entityControlDocumentalDeclaracionesObligatorias.get("expediente_id");
            pregunta_id_bbdd = entityControlDocumentalDeclaracionesObligatorias.get("pregunta_id");

            var pregunta;
            if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 41 || pregunta_id_bbdd == 42;
            }else{
                pregunta = pregunta_id_bbdd == 133 || pregunta_id_bbdd == 134;
            }
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);

            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityControlDocumentalDeclaracionesObligatorias.get("cumple"), entityControlDocumentalDeclaracionesObligatorias.get("observaciones"));
                entityControlDocumentalDeclaracionesObligatorias.set("f_actualizacion",obtenerTimestampActual());
                controlDocumentalDeclaracionesObligatoriasRepo.save(entityControlDocumentalDeclaracionesObligatorias);
            }
            i++
        }
    }

    function updateDesviacionesControlDocumentalLibroRegistro(expediente_id_view, dop_id_view){
         //console.log("updateDesviacionesControlDocumentalLibroRegistro");
         var controlDocumentalLibroRegistroRepo = ctx.get("repos").get("controlDocumentalLibroRegistroRepo");
         var lstControlDocumentalLibroRegistro = controlDocumentalLibroRegistroRepo.listAll();
         var i = 0;
         while(i != lstControlDocumentalLibroRegistro.size()){
            var entityControlDocumentalLibroRegistro = lstControlDocumentalLibroRegistro.get(i);
            expediente_id_bbdd = entityControlDocumentalLibroRegistro.get("expediente_id");
            pregunta_id_bbdd = entityControlDocumentalLibroRegistro.get("pregunta_id");
            if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 36 || pregunta_id_bbdd == 37 || pregunta_id_bbdd == 38 || pregunta_id_bbdd == 39 || pregunta_id_bbdd == 40;
            }else{
                pregunta = pregunta_id_bbdd == 128 || pregunta_id_bbdd == 129 || pregunta_id_bbdd == 130 || pregunta_id_bbdd == 131 || pregunta_id_bbdd == 132;
            }
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);

            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityControlDocumentalLibroRegistro.get("cumple"), entityControlDocumentalLibroRegistro.get("observaciones"));
                entityControlDocumentalLibroRegistro.set("f_actualizacion",obtenerTimestampActual());
                controlDocumentalLibroRegistroRepo.save(entityControlDocumentalLibroRegistro);
            }
            i++
         }
    }

    function updateDesviacionesControlDocumentalDocumentosAcompanamiento(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesControlDocumentalDocumentosAcompanamiento");
        var controlDocumentalDocumentosAcompanamientoRepo = ctx.get("repos").get("controlDocumentalDocumentosAcompanamientoRepo");
        var lstControlDocumentalDocumentosAcompanamiento = controlDocumentalDocumentosAcompanamientoRepo.listAll();
        var i = 0;
        while(i != lstControlDocumentalDocumentosAcompanamiento.size()){
            var entityControlDocumentalDocumentosAcompanamiento = lstControlDocumentalDocumentosAcompanamiento.get(i);
            expediente_id_bbdd = entityControlDocumentalDocumentosAcompanamiento.get("expediente_id");
            pregunta_id_bbdd = entityControlDocumentalDocumentosAcompanamiento.get("pregunta_id");
             if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 43 || pregunta_id_bbdd == 44;
            }else{
                pregunta = pregunta_id_bbdd == 135 || pregunta_id_bbdd == 136;
            }
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);

            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityControlDocumentalDocumentosAcompanamiento.get("cumple"), entityControlDocumentalDocumentosAcompanamiento.get("observaciones"));
                entityControlDocumentalDocumentosAcompanamiento.set("f_actualizacion",obtenerTimestampActual());
                controlDocumentalDocumentosAcompanamientoRepo.save(entityControlDocumentalDocumentosAcompanamiento);
            }
            i++
        }
    }

    function updateDesviacionesVerificacionPliegoCondiciones(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesVerificacionPliegoCondiciones");
        var verificacionPliegoCondicionesRepo = ctx.get("repos").get("verificacionPliegoCondicionesRepo");
        var lstVerificacionPliegoCondiciones = verificacionPliegoCondicionesRepo.listAll();
        var i = 0;
        while(i != lstVerificacionPliegoCondiciones.size()){
            var entityVerificacionPliegoCondiciones = lstVerificacionPliegoCondiciones.get(i);
            expediente_id_bbdd = entityVerificacionPliegoCondiciones.get("expediente_id");
            pregunta_id_bbdd = entityVerificacionPliegoCondiciones.get("pregunta_id");

            var pregunta;
            if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 48 || pregunta_id_bbdd == 49 || pregunta_id_bbdd == 50
                        || pregunta_id_bbdd == 52
                        || pregunta_id_bbdd == 54
                        || pregunta_id_bbdd == 61 || pregunta_id_bbdd == 62 || pregunta_id_bbdd == 63 || pregunta_id_bbdd == 64
                        || pregunta_id_bbdd == 66
                        || pregunta_id_bbdd == 68
                        || pregunta_id_bbdd == 70
                        || pregunta_id_bbdd == 72
                        || pregunta_id_bbdd == 74
                        || pregunta_id_bbdd == 79 || pregunta_id_bbdd == 80 || pregunta_id_bbdd == 81 || pregunta_id_bbdd == 82
                        || pregunta_id_bbdd == 85
                        || pregunta_id_bbdd == 86
                        || pregunta_id_bbdd == 88
                        || pregunta_id_bbdd == 89 || pregunta_id_bbdd == 90
                        || pregunta_id_bbdd == 91 || pregunta_id_bbdd == 92;
            }else{
                pregunta = pregunta_id_bbdd == 45 || pregunta_id_bbdd == 46 || pregunta_id_bbdd == 47
                        || pregunta_id_bbdd == 51
                        || pregunta_id_bbdd == 53
                        || pregunta_id_bbdd == 57 || pregunta_id_bbdd == 58 || pregunta_id_bbdd == 59 || pregunta_id_bbdd == 60
                        || pregunta_id_bbdd == 65
                        || pregunta_id_bbdd == 67
                        || pregunta_id_bbdd == 69
                        || pregunta_id_bbdd == 71
                        || pregunta_id_bbdd == 73
                        || pregunta_id_bbdd == 75 || pregunta_id_bbdd == 76 || pregunta_id_bbdd == 77 || pregunta_id_bbdd == 78
                        || pregunta_id_bbdd == 83
                        || pregunta_id_bbdd == 84
                        || pregunta_id_bbdd == 87
                        || pregunta_id_bbdd == 89 || pregunta_id_bbdd == 90
                        || pregunta_id_bbdd == 91 || pregunta_id_bbdd == 92;
            }
            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityVerificacionPliegoCondiciones.get("cumple"), entityVerificacionPliegoCondiciones.get("observaciones"));
                entityVerificacionPliegoCondiciones.set("f_actualizacion",obtenerTimestampActual());
                verificacionPliegoCondicionesRepo.save(entityVerificacionPliegoCondiciones);
            }
            i++
        }
    }

    function updateDesviacionesVerificacionInstalacion(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesControlInstalaciones");
        var verificacionInstalacionRepo = ctx.get("repos").get("verificacionInstalacionRepo");
        var lstVerificacionInstalacion = verificacionInstalacionRepo.listAll();

        var i = 0;
        while(i != lstVerificacionInstalacion.size()){
            var entityVerificacionInstalacion = lstVerificacionInstalacion.get(i);
            var expediente_id_bbdd = entityVerificacionInstalacion.get("expediente_id");
            var pregunta_id_bbdd = entityVerificacionInstalacion.get("pregunta_id");
             if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 26
                || pregunta_id_bbdd == 27
                || pregunta_id_bbdd == 28
                || pregunta_id_bbdd == 29
                || pregunta_id_bbdd == 30
                || pregunta_id_bbdd == 31
                || pregunta_id_bbdd == 32
                || pregunta_id_bbdd == 33
                || pregunta_id_bbdd == 34
                || pregunta_id_bbdd == 35;
            }else{
                pregunta = pregunta_id_bbdd == 118
                || pregunta_id_bbdd == 119
                || pregunta_id_bbdd == 120
                || pregunta_id_bbdd == 121
                || pregunta_id_bbdd == 122
                || pregunta_id_bbdd == 123
                || pregunta_id_bbdd == 124
                || pregunta_id_bbdd == 125
                || pregunta_id_bbdd == 126
                || pregunta_id_bbdd == 127;
            }
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);

            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                saveDesviacion(pregunta_id_bbdd, expediente_id_view, entityVerificacionInstalacion.get("cumple"), entityVerificacionInstalacion.get("observaciones"));
                entityVerificacionInstalacion.set("f_actualizacion",obtenerTimestampActual());
                verificacionInstalacionRepo.save(entityVerificacionInstalacion);
            }
            i++;
        }
    }

    function updateDesviacionesCapacidadInstalacion(expediente_id_view, operador_id_view, dop_id_view){
        //console.log("updateDesviacionesControlInstalaciones");
        var operadorRepo = ctx.get("repos").get("operadorRepo");
        var entityOperador = operadorRepo.findById(operador_id_view);
        //console.log("cumple capacidad::: "+entityOperador.get("cumple_capacidad"));
        //console.log("observaciones capacidad::: "+entityOperador.get("observaciones"));
        saveDesviacion("capacidad_"+dop_id_view, expediente_id_view, entityOperador.get("cumple_capacidad"), entityOperador.get("observaciones"));
    }

    function updateDesviacionesTrazabilidad(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesTrazabilidad");
        //console.log("expediente_id_view::: "+expediente_id_view);
        //console.log("dop_id_view::: "+dop_id_view);
        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var lstTrazabilidad = trazabilidadRepo.listAll();

        var pregunta_id_view = dop_id_view == 1?48:45;
        setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, null);
        pregunta_id_view = dop_id_view == 1?54:53;
        setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, null);
        pregunta_id_view = dop_id_view == 1?66:65;
        setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, null);
        pregunta_id_view = dop_id_view == 1?79:75;
        setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, null);

        var incumplimientoPPC9 = false;

        var i = 0;
        while(i != lstTrazabilidad.size()){
            var entityTrazabilidad = lstTrazabilidad.get(i);
            var expediente_id_bbdd = entityTrazabilidad.get("expediente_id");
            //console.log("expediente_id_bbdd::: "+expediente_id_bbdd);

             if (expediente_id_bbdd == expediente_id_view){
                var trazabilidad_id = new java.lang.String(entityTrazabilidad.get("trazabilidad_id"));

                if (entityTrazabilidad.get("cumple_etiqueta_autorizada_consejo_regulador") == 'NO'){
                    incumplimientoPPC9 = true;
                }

                var cumple_aptitud = 'SI';
                if (
                    entityTrazabilidad.get("grado_alcoholico_total_minimo_cumple") == 'NO' ||
                    entityTrazabilidad.get("grado_alcoholico_adquirido_minimo_cumple") == 'NO' ||
                    entityTrazabilidad.get("azucares_totales_maximos_cumple") == 'NO' ||
                    entityTrazabilidad.get("acidez_total_minima_cumple") == 'NO' ||
                    entityTrazabilidad.get("acidez_volatil_maxima_cumple") == 'NO' ||
                    entityTrazabilidad.get("anhidrido_sulforoso_total_maximo_cumple") == 'NO' ||
                    entityTrazabilidad.get("visual_cumple") == 'NO' ||
                    entityTrazabilidad.get("olfativa_cumple") == 'NO' ||
                    entityTrazabilidad.get("gustativa_cumple") == 'NO'
                    ){
                        cumple_aptitud = 'NO';
                    }

                //console.log("trazabilidad_id::: "+trazabilidad_id);
                saveDesviacion("trazabilidad_partida", trazabilidad_id, entityTrazabilidad.get("cumple_trazabilidad_partida"), entityTrazabilidad.get("observaciones_trazabilidad_partida"));
                saveDesviacion("trazabilidad_calificacion", trazabilidad_id, entityTrazabilidad.get("cumple_calificacion"), entityTrazabilidad.get("observaciones_trazabilidad_calificacion"));
                saveDesviacion("trazabilidad_envejecimiento", trazabilidad_id, entityTrazabilidad.get("cumple_envejecimiento"), entityTrazabilidad.get("observaciones_trazabilidad_envejecimiento"));
                saveDesviacion("trazabilidad_balance", trazabilidad_id, entityTrazabilidad.get("cumple_balance"), entityTrazabilidad.get("observaciones_balance"));
                saveDesviacion("trazabilidad_aptitud", trazabilidad_id, cumple_aptitud, null);

                saveDesviacion("etiquetado_autorizada_consejo_regulador", trazabilidad_id, entityTrazabilidad.get("cumple_etiqueta_autorizada_consejo_regulador"), entityTrazabilidad.get("observaciones_etiqueta_autorizada_consejo_regulador"));
                saveDesviacion("etiquetado_denominacion_producto", trazabilidad_id, entityTrazabilidad.get("cumple_denominacion_producto"), entityTrazabilidad.get("observaciones_etiquetado_denominacion_producto"));
                saveDesviacion("etiquetado_grado_alcoholico", trazabilidad_id, entityTrazabilidad.get("cumple_grado_alcoholico"), entityTrazabilidad.get("observaciones_etiquetado_grado_alcoholico"));
                saveDesviacion("etiquetado_procedencia", trazabilidad_id, entityTrazabilidad.get("cumple_procedencia"), entityTrazabilidad.get("observaciones_etiquetado_procedencia"));
                saveDesviacion("etiquetado_embotellado_por", trazabilidad_id, entityTrazabilidad.get("cumple_embotellado_por"), entityTrazabilidad.get("observaciones_etiquetado_embotellado_por"));
                saveDesviacion("etiquetado_embotellado_por_encargo", trazabilidad_id, entityTrazabilidad.get("cumple_embotellado_por_encargo"), entityTrazabilidad.get("observaciones_etiquetado_embotellado_por_encargo"));
                saveDesviacion("etiquetado_importador", trazabilidad_id, entityTrazabilidad.get("cumple_importador"), entityTrazabilidad.get("observaciones_etiquetado_importador"));
                saveDesviacion("etiquetado_volumen_nominal", trazabilidad_id, entityTrazabilidad.get("cumple_volumen_nominal"), entityTrazabilidad.get("observaciones_etiquetado_volumen_nominal"));
                saveDesviacion("etiquetado_num_registro_embotellador", trazabilidad_id, entityTrazabilidad.get("cumple_num_registro_embotellador"), entityTrazabilidad.get("observaciones_etiquetado_num_registro_embotellador"));
                saveDesviacion("etiquetado_alergenos", trazabilidad_id, entityTrazabilidad.get("cumple_alergenos"), entityTrazabilidad.get("observaciones_etiquetado_alergenos"));
                saveDesviacion("etiquetado_num_lote", trazabilidad_id, entityTrazabilidad.get("cumple_num_lote"), entityTrazabilidad.get("observaciones_etiquetado_num_lote"));
                saveDesviacion("etiquetado_punto_verde", trazabilidad_id, entityTrazabilidad.get("cumple_punto_verde"), entityTrazabilidad.get("observaciones_etiquetado_punto_verde"));
                saveDesviacion("etiquetado_ano_cosecha", trazabilidad_id, entityTrazabilidad.get("cumple_ano_cosecha"), entityTrazabilidad.get("observaciones_etiquetado_ano_cosecha"));
                saveDesviacion("etiquetado_variedades_vid", trazabilidad_id, entityTrazabilidad.get("cumple_variedades_vid"), entityTrazabilidad.get("observaciones_etiquetado_variedades_vid"));
                saveDesviacion("etiquetado_menciones_tradicionales", trazabilidad_id, entityTrazabilidad.get("cumple_menciones_tradicionales"), entityTrazabilidad.get("observaciones_etiquetado_menciones_tradicionales"));
                saveDesviacion("etiquetado_contenido_azucar", trazabilidad_id, entityTrazabilidad.get("cumple_contenido_azucar"), entityTrazabilidad.get("observaciones_etiquetado_contenido_azucar"));
                saveDesviacion("etiquetado_simbolo_comunitario", trazabilidad_id, entityTrazabilidad.get("cumple_simbolo_comunitario"), entityTrazabilidad.get("observaciones_etiquetado_simbolo_comunitario"));
                saveDesviacion("etiquetado_metodos_produccion", trazabilidad_id, entityTrazabilidad.get("cumple_metodos_produccion"), entityTrazabilidad.get("observaciones_etiquetado_metodos_produccion"));
                saveDesviacion("etiquetado_metodos_ecologicos", trazabilidad_id, entityTrazabilidad.get("cumple_metodos_ecologicos"), entityTrazabilidad.get("observaciones_etiquetado_metodos_ecologicos"));
                saveDesviacion("etiquetado_unidad_geografica", trazabilidad_id, entityTrazabilidad.get("cumple_unidad_geografica"), entityTrazabilidad.get("observaciones_etiquetado_unidad_geografica"));
                saveDesviacion("etiquetado_explotacion_viticola", trazabilidad_id, entityTrazabilidad.get("cumple_explotacion_viticola"), entityTrazabilidad.get("observaciones_etiquetado_explotacion_viticola"));
                saveDesviacion("etiquetado_menciones_color", trazabilidad_id, entityTrazabilidad.get("cumple_menciones_color"), entityTrazabilidad.get("observaciones_etiquetado_menciones_color"));
                saveDesviacion("etiquetado_simbolo_ce", trazabilidad_id, entityTrazabilidad.get("cumple_simbolo_ce"), entityTrazabilidad.get("observaciones_etiquetado_simbolo_ce"));
                saveDesviacion("etiquetado_marca", trazabilidad_id, entityTrazabilidad.get("cumple_marca"), entityTrazabilidad.get("observaciones_etiquetado_marca"));

                saveDesviacion("etiquetado_mencion_seleccion", trazabilidad_id, entityTrazabilidad.get("cumple_mencion_seleccion"), entityTrazabilidad.get("observaciones_etiquetado_mencion_seleccion"));
                saveDesviacion("etiquetado_mencion_vinas", trazabilidad_id, entityTrazabilidad.get("cumple_mencion_vinas"), entityTrazabilidad.get("observaciones_etiquetado_mencion_vinas"));
                saveDesviacion("etiquetado_mencion_vinedos_altura", trazabilidad_id, entityTrazabilidad.get("cumple_mencion_vinedos_altura"), entityTrazabilidad.get("observaciones_etiquetado_mencion_vinedos_altura"));
                saveDesviacion("etiquetado_mencion_vinedos_propios", trazabilidad_id, entityTrazabilidad.get("cumple_mencion_vinedos_propios"), entityTrazabilidad.get("observaciones_etiquetado_mencion_vinedos_propios"));
                saveDesviacion("etiquetado_mencion_no_admitidas", trazabilidad_id, entityTrazabilidad.get("cumple_mencion_no_admitidas"), entityTrazabilidad.get("observaciones_etiquetado_mencion_no_admitidas"));

                updateDesviacionesEntradaUva(trazabilidad_id, expediente_id_view, dop_id_view);
             }
             i++;
        }
        var pregunta_id_view;
        if (incumplimientoPPC9){
            pregunta_id_view = dop_id_view == 1?79:75;
            setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, 'NO');
        }
    }

    function updateDesviacionesAforo(expediente_id_view){
        //console.log("updateDesviacionesAforo");
        var partidaAforoRepo = ctx.get("repos").get("partidaAforoRepo");
        var lstPartidaAforo = partidaAforoRepo.listAll();
        var i = 0;
        while(i != lstPartidaAforo.size()){
            var entityPartidaAforo = lstPartidaAforo.get(i);
            var partida_aforo_id = new java.lang.String(entityPartidaAforo.get("partida_aforo_id"));
            //console.log("partida_aforo_id::: "+partida_aforo_id);
            var expediente_id_bbdd = entityPartidaAforo.get("expediente_id");

            if (expediente_id_bbdd == expediente_id_view){
                updateDesviacionesGranelBarrica(partida_aforo_id);
                updateDesviacionesGranelDeposito(partida_aforo_id);
                updateDesviacionesEnvasado(partida_aforo_id);
                updateDesviacionesEtiquetado(partida_aforo_id);
            }
            i++
        }
    }

     function updateDesviacionesGranelBarrica(partida_aforo_id_view){
        //console.log("updateDesviacionesGranelBarrica");
        var granelBarricaRepo = ctx.get("repos").get("granelBarricaRepo");
        var lstGranelBarrica = granelBarricaRepo.listAll();
        var i = 0;
        while(i != lstGranelBarrica.size()){
            var entityGranelBarrica = lstGranelBarrica.get(i);
            var partida_aforo_id_bbdd = new java.lang.String(entityGranelBarrica.get("partida_aforo_id"));
            //console.log("partida_aforo_id_bbdd::: "+partida_aforo_id_bbdd);
            if (partida_aforo_id_bbdd == partida_aforo_id_view){
                var granel_barrica_id = new java.lang.String(entityGranelBarrica.get("granel_barrica_id"));
                //console.log("granel_barrica_id::: "+granel_barrica_id);
                saveDesviacion("aforo_granel_barrica", granel_barrica_id, entityGranelBarrica.get("cumple"), entityGranelBarrica.get("observaciones"));
            }
            i++
        }
     }

     function updateDesviacionesGranelDeposito(partida_aforo_id_view){
        //console.log("updateDesviacionesGranelDeposito");
        var granelDepositoRepo = ctx.get("repos").get("granelDepositoRepo");
        var lstGranelDeposito = granelDepositoRepo.listAll();
        var i = 0;
        while(i != lstGranelDeposito.size()){
            var entityGranelDeposito = lstGranelDeposito.get(i);
            var partida_aforo_id_bbdd = new java.lang.String(entityGranelDeposito.get("partida_aforo_id"));
            //console.log("partida_aforo_id_bbdd::: "+partida_aforo_id_bbdd);
            if (partida_aforo_id_bbdd == partida_aforo_id_view){
                var granel_deposito_id = new java.lang.String(entityGranelDeposito.get("granel_deposito_id"));
                //console.log("granel_deposito_id::: "+granel_deposito_id);
                saveDesviacion("aforo_granel_deposito", granel_deposito_id, entityGranelDeposito.get("cumple"), entityGranelDeposito.get("observaciones"));
            }
            i++
        }
     }

     function updateDesviacionesEnvasado(partida_aforo_id_view){
        //console.log("updateDesviacionesEnvasado");
        var envasadoRepo = ctx.get("repos").get("envasadoRepo");
        var lstEnvasado = envasadoRepo.listAll();
        var i = 0;
        while(i != lstEnvasado.size()){
            var entityEnvasado = lstEnvasado.get(i);
            var partida_aforo_id_bbdd = new java.lang.String(entityEnvasado.get("partida_aforo_id"));
            //console.log("partida_aforo_id_bbdd::: "+partida_aforo_id_bbdd);
            if (partida_aforo_id_bbdd == partida_aforo_id_view){
                var envasado_id = new java.lang.String(entityEnvasado.get("envasado_id"));
                //console.log("envasado_id::: "+envasado_id);
                saveDesviacion("aforo_envasado", envasado_id, entityEnvasado.get("cumple"), entityEnvasado.get("observaciones"));
            }
            i++
        }
     }

     function updateDesviacionesEtiquetado(partida_aforo_id_view){
        //console.log("updateDesviacionesEtiquetado");
        var etiquetadoRepo = ctx.get("repos").get("etiquetadoRepo");
        var lstEtiquetado = etiquetadoRepo.listAll();
        var i = 0;
        while(i != lstEtiquetado.size()){
            var entityEtiquetado = lstEtiquetado.get(i);
            var partida_aforo_id_bbdd = new java.lang.String(entityEtiquetado.get("partida_aforo_id"));
            //console.log("partida_aforo_id_bbdd::: "+partida_aforo_id_bbdd);
            if (partida_aforo_id_bbdd == partida_aforo_id_view){
                var etiquetado_id = new java.lang.String(entityEtiquetado.get("etiquetado_id"));
                //console.log("etiquetado_id::: "+etiquetado_id);
                saveDesviacion("aforo_etiquetado", etiquetado_id, entityEtiquetado.get("cumple"), entityEtiquetado.get("observaciones"));
            }
            i++
        }
     }

    function updateDesviacionesEntradaUva(trazabilidad_id_view, expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesEntradaUva");
        var entradaUvaRepo = ctx.get("repos").get("entradaUvaRepo");
        var lstEntradaUva = entradaUvaRepo.listAll();

        var i = 0;
        var incumplimientoPPC1 = false;
        var incumplimientoPPC3 = false;
        var incumplimientoPPC5 = false;
        while(i != lstEntradaUva.size()){
            var entityEntradaUva = lstEntradaUva.get(i);
            var trazabilidad_id_bbdd = entityEntradaUva.get("trazabilidad_id");

            if (trazabilidad_id_bbdd == trazabilidad_id_view){
                var entrada_uva_id = new java.lang.String(entityEntradaUva.get("entrada_uva_id"));
                saveDesviacion("entrada_uva_registro_vitivinicola", entrada_uva_id, entityEntradaUva.get("registro_viticola"), entityEntradaUva.get("observaciones"));
                saveDesviacion("entrada_uva_estado_sanitario", entrada_uva_id, entityEntradaUva.get("estado_sanitario"), entityEntradaUva.get("observaciones"));
                saveDesviacion("entrada_uva_tickets_pesada", entrada_uva_id, entityEntradaUva.get("tickets_pesada"), entityEntradaUva.get("observaciones"));
                saveDesviacion("entrada_uva_grado", entrada_uva_id, entityEntradaUva.get("conforme_grado"), entityEntradaUva.get("observaciones"));
                saveDesviacion("entrada_uva_rendimiento", entrada_uva_id, entityEntradaUva.get("conforme_rendimiento"), entityEntradaUva.get("observaciones"));
                saveDesviacion("entrada_uva_items_minimos", entrada_uva_id, entityEntradaUva.get("items_minimos"), entityEntradaUva.get("observaciones"));

                //console.log("registro_viticola::: "+entityEntradaUva.get("registro_viticola"));
                //console.log("conforme_grado::: "+entityEntradaUva.get("conforme_grado"));
                //console.log("tickets_pesada::: "+entityEntradaUva.get("tickets_pesada"));
                //console.log("estado_sanitario::: "+entityEntradaUva.get("estado_sanitario"));
                //console.log("conforme_rendimiento::: "+entityEntradaUva.get("conforme_rendimiento"));
                //console.log("items_minimos::: "+entityEntradaUva.get("items_minimos"));
                if (entityEntradaUva.get("registro_viticola") == 'NO'){
                    incumplimientoPPC1 = true;
                }
                if (entityEntradaUva.get("conforme_grado") == 'NO' || entityEntradaUva.get("tickets_pesada") == 'NO' || entityEntradaUva.get("estado_sanitario") == 'NO' || entityEntradaUva.get("items_minimos") == 'NO'){
                    incumplimientoPPC3 = true;
                }
                if (entityEntradaUva.get("conforme_rendimiento") == 'NO'){
                    incumplimientoPPC5 = true;
                    //console.log("incumplimientoPPC5::: ");
                }

             }
            i++;
        }
        var pregunta_id_view;
        if (incumplimientoPPC1){
            pregunta_id_view = dop_id_view == 1?48:45;
            setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, 'NO');
        }
        if (incumplimientoPPC3){
            pregunta_id_view = dop_id_view == 1?54:53;
            setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, 'NO');
        }
        if (incumplimientoPPC5){
            pregunta_id_view = dop_id_view == 1?66:65;
            setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, 'NO');
        }
    }

    function updateDesviacionesPCC41(expediente_id_view, dop_id_view){
        //console.log("updateDesviacionesPCC41");
        var verificacionPliegoCondicionesRepo = ctx.get("repos").get("verificacionPliegoCondicionesRepo");
        var lstVerificacionPliegoCondiciones = verificacionPliegoCondicionesRepo.listAll();
        var cumple = true;
        var observaciones = null;
        var i = 0;
        while(i != lstVerificacionPliegoCondiciones.size()){
            var entityVerificacionPliegoCondiciones = lstVerificacionPliegoCondiciones.get(i);
            expediente_id_bbdd = entityVerificacionPliegoCondiciones.get("expediente_id");
            pregunta_id_bbdd = entityVerificacionPliegoCondiciones.get("pregunta_id");

            var pregunta;
            if (dop_id_view == 1){
                pregunta = pregunta_id_bbdd == 56;
            }else{
                pregunta = pregunta_id_bbdd == 55;
            }
            if (expediente_id_bbdd == expediente_id_view && pregunta){
                //console.log("pregunta_id_bbdd::: "+pregunta_id_bbdd);
                var litros_producidos = entityVerificacionPliegoCondiciones.get("litros_producidos");
                var kg_uva = entityVerificacionPliegoCondiciones.get("kg_uva");

                if (litros_producidos != null && kg_uva != null){
                    litros_producidos = new java.math.BigDecimal(litros_producidos).setScale(2, java.math.RoundingMode.HALF_UP);
                    kg_uva = new java.math.BigDecimal(kg_uva).setScale(2, java.math.RoundingMode.HALF_UP);
                    //console.log("litros_producidos::: "+litros_producidos);
                    //console.log("kg_uva::: "+kg_uva);

                    if (kg_uva > 0){
                        if (litros_producidos*100/kg_uva > 72 || litros_producidos*100/kg_uva == 72){
                            cumple = false;
                        }
                        else{
                            cumple = true;
                       }
                    }
                    saveDesviacion(pregunta_id_bbdd, expediente_id_view, cumple?"SI":"NO", entityVerificacionPliegoCondiciones.get("observaciones"));
                    entityVerificacionPliegoCondiciones.set("f_actualizacion",obtenerTimestampActual());
                    verificacionPliegoCondicionesRepo.save(entityVerificacionPliegoCondiciones);
                }
            }
            i++
        }

    }


    function saveDesviacion(pregunta_id, expediente_id_view, cumple, observaciones){
        //console.log("saveDesviacion");

        var desviacionRepo = ctx.get("repos").get("desviacionRepo");
        //console.log("expediente_id_view+'_'+pregunta_id::: "+expediente_id_view+'_'+pregunta_id);
        var entityDesviacion = desviacionRepo.findById(expediente_id_view+'_'+pregunta_id);
        //console.log("expediente_id_view+'_'+pregunta_id::: "+expediente_id_view+'_'+pregunta_id);
        //console.log("cumple::: "+cumple);
         //console.log("cumple::: "+observaciones);


        if (entityDesviacion != null){
            entityDesviacion.set("cumple",cumple);
            entityDesviacion.set("observaciones",observaciones);
            entityDesviacion.set("f_actualizacion",obtenerTimestampActual());
            desviacionRepo.save(entityDesviacion);
        }
    }

    function nuevaDesviacion(){
        if (entity.get("desviacion_id")==null){
            var desviacionRepo = ctx.get("repos").get("desviacionRepo");

            var entityDesviacion = desviacionRepo.newEntity();
            entityDesviacion.set("desviacion_id", new java.lang.Long(view.get("expediente_id"))+"_nuevaDesviacion");
            entityDesviacion.set("expediente_id", new java.lang.Long(view.get("expediente_id")));
            entityDesviacion.set("dop_id", view.get("dop_id"));
            entityDesviacion.set("requisito", view.get("requisito"));
            entityDesviacion.set("hallazgo", view.get("hallazgo"));
            entityDesviacion.set("cumple", "NO");
            entityDesviacion.set("observaciones", view.get("observaciones"));
            entityDesviacion.set("f_actualizacion",obtenerTimestampActual());
            desviacionRepo.save(entityDesviacion);
       }

    }

    function updateObservacionesCuestionario(observaciones){
        //console.log("updateObservacionesCuestionario");
        if (entity.get("desviacion_id")!=null){
            var observaciones = view.get("observaciones");
            //console.log("observaciones::: "+observaciones);

            var desviacion_id = entity.get("desviacion_id");
            var id = desviacion_id.split("_")[0];
            //console.log("desviacion_id::: "+desviacion_id);
            //console.log("id::: "+id);

            var listaRepo=["revisionDocumentalRepo","controlDocumentalDocumentosAcompanamientoRepo","controlDocumentalLibroRegistroRepo", "controlDocumentalDeclaracionesObligatoriasRepo", "verificacionPliegoCondicionesRepo", "documentacionRegistroRepo", "verificacionInstalacionRepo"];
            for(var i=0; i<listaRepo.length && varEntity==null; i++) {
                 //console.log("listaRepo[i]::: "+listaRepo[i]);
                 var varRepo = ctx.get("repos").get(listaRepo[i]);
                 var varEntity = varRepo.findById(desviacion_id);
            }

           if (varEntity == null){
                listaRepo=["envasadoRepo", "etiquetadoRepo", "granelDepositoRepo", "granelBarricaRepo", "entradaUvaRepo"];
                for(var i=0; i<listaRepo.length && varEntity==null; i++) {
                    //console.log("listaRepo[i]::: "+listaRepo[i]);
                    var varRepo = ctx.get("repos").get(listaRepo[i]);
                    var varEntity = varRepo.findById(new java.lang.Long(id));
                }
           }

           if (varEntity == null && desviacion_id.split("_")[1] == 'capacidad'){
                var operador_id = view.get("operador_id");
                //console.log("operador_id::: "+operador_id);
                varRepo = ctx.get("repos").get("operadorRepo");
                varEntity = varRepo.findById(new java.lang.Long(operador_id));
            }

           var txtObservaciones = "observaciones";
           if (varEntity == null){
                varRepo = ctx.get("repos").get("trazabilidadRepo");
                var varEntity = varRepo.findById(new java.lang.Long(id));
                txtObservaciones = desviacion_id.replace(id, 'observaciones');
                //console.log("txtObservaciones::: "+txtObservaciones);
           }



            if (varEntity != null){
                 //console.log("varRepo::: "+varRepo);
                 //console.log("varEntity::: "+varEntity);
                 varEntity.set(txtObservaciones, observaciones);
                 varEntity.set("f_actualizacion",obtenerTimestampActual());
                 varRepo.save(varEntity);
            }


         }

    }

    function updateCumpleEntradaUva(){
        //console.log("updateCumpleEntradaUva");
        var entradaUvaRepo = ctx.get("repos").get("entradaUvaRepo");
        var lstEntradaUva = entradaUvaRepo.listAll();
        var i = 0;
        var trazabilidad_id_view = new java.lang.Long(view.get("trazabilidad_id"));
        //console.log("trazabilidad_id_view::: "+trazabilidad_id_view);
        while(i != lstEntradaUva.size()){
            entityEntradaUva = lstEntradaUva.get(i);
            var trazabilidad_id_bbdd = new java.lang.Long(entityEntradaUva.get("trazabilidad_id"));
            //console.log("trazabilidad_id_bbdd::: "+trazabilidad_id_bbdd);
            if (trazabilidad_id_view == trazabilidad_id_bbdd && entityEntradaUva.get("variedad_id") != null){
                var variedad_id = new java.lang.Long(entityEntradaUva.get("variedad_id"));
                //console.log("variedad_id::: "+variedad_id);
                var sup_plantacion = null;
                if (entityEntradaUva.get("sup_plantacion") != null){
                    sup_plantacion = new java.math.BigDecimal(entityEntradaUva.get("sup_plantacion")).setScale(2, java.math.RoundingMode.HALF_UP);
                }
                //console.log("sup_plantacion::: "+sup_plantacion);
                var kg_totales_recepcionados = null
                if (entityEntradaUva.get("kg_totales_recepcionados") != null){
                    kg_totales_recepcionados = new java.math.BigDecimal(entityEntradaUva.get("kg_totales_recepcionados")).setScale(2, java.math.RoundingMode.HALF_UP);
                }
                //console.log("kg_totales_recepcionados::: "+kg_totales_recepcionados);
                if (sup_plantacion != null && kg_totales_recepcionados != null){
                    var entityVariedad = getEntityVariedad(variedad_id);
                    var kg_permitidos = new java.math.BigDecimal(entityVariedad.get("kg_permitidos")).setScale(2, java.math.RoundingMode.HALF_UP);
                    //console.log("kg_permitidos::: "+kg_permitidos);
                    var kg_cupo = kg_permitidos * sup_plantacion;
                    //console.log("kg_cupo::: "+kg_cupo);
                    var conformeRendimiento = null;
                    if (kg_cupo>0 && kg_totales_recepcionados>0){
                        if (kg_cupo < kg_totales_recepcionados){
                            conformeRendimiento = "NO";
                        }
                        else{
                            conformeRendimiento = "SI";
                        }
                        //console.log("conformeRendimiento::: "+conformeRendimiento);
                        entityEntradaUva.set("conforme_rendimiento",conformeRendimiento);
                    }
                    entityEntradaUva.set("cupo",kg_cupo);

                }

                var conformeGrado = null;
                var grado_variedad = getGradoVariedad(variedad_id);
                var grado_probable = entityEntradaUva.get("grado_probable");
                //console.log("grado_variedad::: "+grado_variedad);
                //console.log("grado_probable::: "+grado_probable);
                if (grado_variedad>0 && grado_probable>0){
                    if (grado_probable<grado_variedad){
                        conformeGrado = "NO";
                    }
                    else{
                        conformeGrado = "SI";
                    }
                    //console.log("conformeGrado::: "+conformeGrado);
                    entityEntradaUva.set("conforme_grado",conformeGrado);
                }
                entradaUvaRepo.save(entityEntradaUva);
            }
            i++
        }
    }

    function getEntityVariedad(variedad_id){
        var variedadRepo = ctx.get("repos").get("variedadRepo");
        var entityVariedad = variedadRepo.findById(variedad_id);
        return entityVariedad;
    }

    function updateCumpleVolumenLibros(cumple_id){
        //console.log("cumple_id:: "+cumple_id);
        var volumen_aforado = view.get("volumen_aforado");
        //console.log("volumen_aforado:: "+volumen_aforado);
        var volumen_libros = view.get("volumen_libros");
        //console.log("volumen_libros:: "+volumen_libros);
        var cumple = null;
        if (volumen_libros != null && volumen_aforado != null && volumen_libros >0){
            var resultado = (volumen_aforado - volumen_libros)*100/volumen_libros;
            if (resultado > 5 || resultado < -5){
                cumple = 'NO';
            }else{
                cumple = 'SI';
            }
        }

        var widgetCumple = vh.widget(cumple_id);
        widgetCumple.setValue(cumple);
    }

     function setCumpleBalance(){
        //console.log("setCumpleBalance");

        /*var cantidad_vino_etiquetado = view.get("cantidad_vino_etiquetado");
        //console.log("cantidad_vino_etiquetado::: "+cantidad_vino_etiquetado);

        var balance_contraetiquetas_usadas = view.get("balance_contraetiquetas_usadas");
        //console.log("balance_contraetiquetas_usadas::: "+balance_contraetiquetas_usadas);

        var balance_uso_contras = view.get("balance_uso_contras");
        //console.log("balance_uso_contras::: "+balance_uso_contras);

        var cumple_balance = true;
        if (balance_uso_contras > balance_contraetiquetas_usadas*5/100){
            cumple_balance = false;
        }*/

        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        var balance_contraetiquetas_usadas = entityTrazabilidad.get("contras_usadas") - (entityTrazabilidad.get("cantidad_vino_etiquetado") / entityTrazabilidad.get("capacidad_botella"));
        //console.log("balance_contraetiquetas_usadas::: "+balance_contraetiquetas_usadas);
        var balance_uso_contras = balance_contraetiquetas_usadas - entityTrazabilidad.get("contras_sin_usar")
        //console.log("balance_uso_contras::: "+balance_uso_contras);
        var cumple_balance = 'S';
        if (balance_uso_contras > balance_contraetiquetas_usadas*5/100){
            cumple_balance = 'N';
        }
        //console.log("cumple_balance::: "+cumple_balance);

        entityTrazabilidad.set("cumple_balance", cumple_balance);
        trazabilidadRepo.save(entityTrazabilidad);

    }

    function setIncumplientoPPC1_PCC3_PPC5_PCC9(expediente_id_view, pregunta_id_view, cumple){
        //console.log("setIncumplientoPPC1_PCC3_PPC5_PCC9");
        //console.log("expediente_id_view::: "+expediente_id_view);
        //console.log("pregunta_id_view::: "+pregunta_id_view);

        var verificacionPliegoCondicionesRepo = ctx.get("repos").get("verificacionPliegoCondicionesRepo");
        var lstVerificacionPliegoCondiciones = verificacionPliegoCondicionesRepo.listAll();
        var i = 0;
        while(i != lstVerificacionPliegoCondiciones.size()){
            var entityVerificacionPliegoCondiciones = lstVerificacionPliegoCondiciones.get(i);
            expediente_id_bbdd = entityVerificacionPliegoCondiciones.get("expediente_id");
            pregunta_id_bbdd = entityVerificacionPliegoCondiciones.get("pregunta_id");

            if (expediente_id_bbdd == expediente_id_view && pregunta_id_bbdd == pregunta_id_view){
                entityVerificacionPliegoCondiciones.set("cumple",cumple);
                //console.log("cumple::: "+cumple);
                entityVerificacionPliegoCondiciones.set("f_actualizacion",obtenerTimestampActual());
                verificacionPliegoCondicionesRepo.save(entityVerificacionPliegoCondiciones);
                break;
            }
            i++
       }
    }

    function updateCumpleAforo(total_aforo_granel, total_litros_libros_granel){
        //console.log("updateCumpleAforo");
        //var total_aforo_granel = view.get("total_aforo_granel");
        //console.log("total_aforo_granel:: "+total_aforo_granel);
        //var total_litros_libros_granel = view.get("total_litros_libros_granel");
        //console.log("total_litros_libros_granel:: "+total_litros_libros_granel);
        var cumple = null;
        var resultado = null;
        if (total_aforo_granel != null && total_litros_libros_granel != null && total_litros_libros_granel >0){
            var resultado = (total_aforo_granel - total_litros_libros_granel)*100/total_litros_libros_granel;
            resultado = new java.math.BigDecimal(resultado).setScale(2, java.math.RoundingMode.HALF_UP);
            if (resultado < 5 || resultado > -5){
                cumple = 'SI';
            }else{
                cumple = 'NO';
            }
            //console.log("cumple:: "+cumple);
            //console.log("resultado:: "+resultado);
        }

        var widgetCumple = vh.widget("cumple_balance");
        widgetCumple.setValue(cumple);
        var widgetBalanceAforo = vh.widget("balance_aforo");
        widgetBalanceAforo.setValue(resultado);
    }

    function updateBalanceContraetiquetasUsadasDesdeContrasUsadas(){
        //console.log("updateBalanceContraetiquetasUsadasDesdeContrasUsadas");
        updateBalanceContraetiquetasUsadas(entity.get("contras_usadas"), entity.get("contras_sin_usar"), entity.get("cantidad_vino_etiquetado"), entity.get("capacidad_botella"));

    }

    function updateFechasEnvejecimiento(tipo_envejecimiento_id, fecha_inicio_envejecimiento, fecha_calificacion_bodega){
        //console.log("updateFechasEnvejecimiento");

        //console.log("tipo_envejecimiento_id::: "+tipo_envejecimiento_id);
        //console.log("fecha_inicio_envejecimiento::: "+fecha_inicio_envejecimiento);
        //console.log("fecha_calificacion_bodega::: "+fecha_calificacion_bodega);

        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);


        var tiempoBodega = null;
        var mesesModega = null;
        var mesesBarrica = null;
        var cumpleEnvejecimiento = null;

        if (tipo_envejecimiento_id != null){
            mesesModega = getMesesBodega();
            mesesBarrica = getMesesBarrica();

            if (fecha_inicio_envejecimiento != null && fecha_calificacion_bodega != null){
                tiempoBodega = getDMonths(fecha_inicio_envejecimiento, fecha_calificacion_bodega);
                if (tiempoBodega > mesesModega || tiempoBodega == mesesModega){
                    cumpleEnvejecimiento = "SI";
                }else{
                    cumpleEnvejecimiento = "NO";
               }
            }
        }

        //console.log("tipo_envejecimiento_id::: "+tipo_envejecimiento_id);
        //console.log("tiempoBodega::: "+tiempoBodega);
        //console.log("mesesModega::: "+mesesModega);
        //console.log("mesesBarrica::: "+mesesBarrica);
        //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);

        //entityTrazabilidad.set("tipo_envejecimiento_id", tipo_envejecimiento_id!=null?new java.lang.Long(tipo_envejecimiento_id):null);
        entityTrazabilidad.set("fecha_inicio_envejecimiento", fecha_inicio_envejecimiento);
        entityTrazabilidad.set("fecha_calificacion_bodega", fecha_calificacion_bodega);
        entityTrazabilidad.set("meses_bodega", tiempoBodega!=null?new java.lang.Long(tiempoBodega):null);
        entityTrazabilidad.set("meses_bodega_envejecimento", mesesModega!=null?new java.lang.Long(mesesModega):null);
        entityTrazabilidad.set("meses_barrica_envejecimento", mesesBarrica!=null?new java.lang.Long(mesesBarrica):null);
        entityTrazabilidad.set("cumple_envejecimiento_bodega", cumpleEnvejecimiento);
        trazabilidadRepo.save(entityTrazabilidad);

        if (tipo_envejecimiento_id == null){
            //console.log("tipo_envejecimiento_id == null");
            var widgetFechaInicioEnvejecimento = vh.widget("fecha_inicio_envejecimiento");
            if (widgetFechaInicioEnvejecimento != null) {
                //console.log("widgetFechaInicioEnvejecimento != null");
                widgetFechaInicioEnvejecimento.setValue(null);
                vh.setWidgetValue('fecha_inicio_envejecimiento', "");
                vh.updateWidget('fecha_inicio_envejecimiento');
            }

            var widgetFechaCalificacionBodega = vh.widget("fecha_calificacion_bodega");
            if (widgetFechaCalificacionBodega != null) {
                //console.log("widgetFechaCalificacionBodega != null");
                vh.setWidgetValue('fecha_calificacion_bodega', "");
                vh.updateWidget('fecha_calificacion_bodega');
            }
        }

        var widgetTiempoBodega = vh.widget("tiempo_bodega");
        if (widgetTiempoBodega != null) {
            //console.log("widgetTiempoBodega != null");
            vh.setWidgetValue('tiempo_bodega', tiempoBodega);
            vh.updateWidget('tiempo_bodega');
        }

        var widgetMesesBodega = vh.widget("meses_bodega");
        if (widgetMesesBodega != null) {
            //console.log("widgetMesesBodega != null");
            vh.setWidgetValue('meses_bodega', mesesModega);
            vh.updateWidget('meses_bodega');
        }

        var widgetMesesBarrica = vh.widget("meses_barrica");
        if (widgetMesesBarrica != null) {
            //console.log("widgetMesesBarrica != null");
            vh.setWidgetValue('meses_barrica', mesesBarrica);
            vh.updateWidget('meses_barrica');
        }

        var widgetCumpleEnvejecimentoBodega = vh.widget("cumple_envejecimiento_bodega");
        if (widgetCumpleEnvejecimentoBodega != null) {
            //console.log("widgetCumpleEnvejecimentoBodega != null");
            vh.setWidgetValue('cumple_envejecimiento_bodega', cumpleEnvejecimiento);
            vh.updateWidget('cumple_envejecimiento_bodega');
        }

    }

    function updateBalanceContraetiquetasUsadas(contras_usadas, contras_sin_usar, cantidad_vino_etiquetado, capacidad_botella){
        //console.log("updateBalanceContraetiquetasUsadas");
        //Contraetiquetas asignadas - contraetiquetas usadas(=número de botellas)
        //console.log("contras_usadas::: "+contras_usadas);
        //console.log("contras_sin_usar::: "+contras_sin_usar);
        //console.log("cantidad_vino_etiquetado::: "+cantidad_vino_etiquetado);
        //console.log("capacidad_botella::: "+capacidad_botella);
        var balance_contraetiquetas_usadas = null;
        var numero_botellas = null;
        var balance_uso_contras = null;
        if (contras_usadas != null && cantidad_vino_etiquetado != null && capacidad_botella!=null && capacidad_botella > 0){
            numero_botellas = Math.floor(cantidad_vino_etiquetado/capacidad_botella);
            balance_contraetiquetas_usadas = contras_usadas - numero_botellas;
            if (contras_sin_usar != null){
                balance_uso_contras = balance_contraetiquetas_usadas - contras_sin_usar;
            }

            //console.log("numero_botellas::: "+numero_botellas);
            //console.log("balance_contraetiquetas_usadas::: "+balance_contraetiquetas_usadas);
            //console.log("balance_uso_contras::: "+balance_uso_contras);
        }

        var widgetBalanceContraetiquetasUsadas = vh.widget("balance_contraetiquetas_usadas");
        if (widgetBalanceContraetiquetasUsadas != null) {
            widgetBalanceContraetiquetasUsadas.setValue(balance_contraetiquetas_usadas);
        }
        var widgetBalanceUsoContras = vh.widget("balance_uso_contras");
        if (widgetBalanceUsoContras != null) {
            widgetBalanceUsoContras.setValue(balance_uso_contras);
        }

        var widgetNumeroBotellas = vh.widget("numero_botellas");
        if (widgetNumeroBotellas != null){
            widgetNumeroBotellas.setValue(numero_botellas);
        }

        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        if (contras_usadas != null){
            entityTrazabilidad.set("contras_usadas", new java.lang.Long(contras_usadas));
        }
        if (contras_sin_usar != null){
            entityTrazabilidad.set("contras_sin_usar", new java.lang.Long(contras_sin_usar));
        }
        if (cantidad_vino_etiquetado != null){
            entityTrazabilidad.set("cantidad_vino_etiquetado", cantidad_vino_etiquetado);
        }
        if (capacidad_botella != null){
            entityTrazabilidad.set("capacidad_botella", capacidad_botella);
        }
        if (numero_botellas != null){
            entityTrazabilidad.set("numero_botellas", new java.lang.Long(numero_botellas));
        }

        if (balance_contraetiquetas_usadas != null){
            entityTrazabilidad.set("balance_contraetiquetas_usadas", new java.lang.Long(balance_contraetiquetas_usadas));
        }

        if (balance_uso_contras != null){
            entityTrazabilidad.set("balance_uso_contras", new java.lang.Long(balance_uso_contras));
        }
        var cumple_balance = null;
        if (balance_contraetiquetas_usadas != null && balance_uso_contras != null){
            cumple_balance = 'S';
            if (balance_uso_contras > balance_contraetiquetas_usadas*5/100){
                cumple_balance = 'N';
            }
            entityTrazabilidad.set("cumple_balance", cumple_balance);

            var widgetCumpleBalance = vh.widget("cumple_balance");
            if (widgetCumpleBalance != null) {
                widgetCumpleBalance.setValue(cumple_balance);
            }
        }

        trazabilidadRepo.save(entityTrazabilidad);

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
        var fecha_inicio_envejecimiento = view.get("fecha_inicio_envejecimiento");
        var fecha_calificacion_bodega = view.get("fecha_calificacion_bodega");
        //console.log("fecha_inicio_envejecimiento::: "+fecha_inicio_envejecimiento);
        //console.log("fecha_calificacion_bodega::: "+fecha_calificacion_bodega);
        dayDiff(component, fecha_inicio_envejecimiento, fecha_calificacion_bodega);
    }

    function getMesesBodega(){
        var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
        var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
        var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
        return entityTipoEnvejecimiento.get("meses_bodega");
    }

    function updateMesesBodega(component){
        //console.log("updateMesesBodega");
        //console.log("view.get('tipo_envejecimiento_id')::: "+view.get("tipo_envejecimiento_id"));
        var valueExpresssion = null;
        if (view.get("tipo_envejecimiento_id") != null){
            var meses_bodega = getMesesBodega();
            //console.log("meses_bodega::: "+meses_bodega);
            if (meses_bodega != null){
                valueExpresssion = valueExpressionFactory.getInstance().create(""+meses_bodega.toString());
            }
        }
        component.setValueExpression(valueExpresssion);
    }

    function dayDiff(component, fecha1, fecha2){
        //console.log("dayDiff");
        //console.log("fecha1::: "+fecha1);
        //console.log("fecha2::: "+fecha2);
        if (fecha1 != null && fecha2 != null){
            var dMonths = getDMonths(fecha1, fecha2);
            //console.log("dMonths: "+dMonths);
            //console.log("component: "+component);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+dMonths.toString()));
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

    function updateCumpleEnvejecimientoBotella(component){
        //console.log("updateCumpleEnvejecimientoBotella");
        var fecha_llenado_botella = view.get("fecha_llenado_botella");
        var fecha_calificacion_botella = view.get("fecha_calificacion_botella");
        //console.log("fecha_llenado_botella::: "+fecha_llenado_botella);
        //console.log("fecha_calificacion_botella::: "+fecha_calificacion_botella);
        var meses_en_bodega = 0;
        if (view.get("tipo_envejecimiento_id") != null){
            meses_en_bodega = getMesesBodega();
        }
        updateCumpleEnvejecimiento(component, fecha_llenado_botella, fecha_calificacion_botella, meses_en_bodega);
    }

    function updateCumpleEnvejecimientoBodega(component){
        //console.log("updateCumpleEnvejecimientoBodega");
        var fecha_inicio_envejecimiento = view.get("fecha_inicio_envejecimiento");
        var fecha_calificacion_bodega = view.get("fecha_calificacion_bodega");
        //console.log("fecha_inicio_envejecimiento::: "+fecha_inicio_envejecimiento);
        //console.log("fecha_calificacion_bodega::: "+fecha_calificacion_bodega);
        var meses_en_bodega = 0;
        if (view.get("tipo_envejecimiento_id") != null){
            meses_en_bodega = getMesesBodega();
            //console.log("meses_en_bodega::: "+meses_en_bodega);
            //console.log("meses_en_bodega::: "+meses_en_bodega);
            updateCumpleEnvejecimiento(component, fecha_inicio_envejecimiento, fecha_calificacion_bodega, meses_en_bodega);
        }else{
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+null));
        }
    }

    function updateCumpleEnvejecimiento(component, fecha_inicio_envejecimiento, fecha_calificacion, meses){
        //console.log("updateCumpleEnvejecimiento");
        var cumpleEnvejecimiento = null;

        var meses_en_barrica = 0;
        var diff = 0;

        if (fecha_inicio_envejecimiento != null && fecha_calificacion != null){
            //console.log("fecha_inicio_envejecimiento:::"+fecha_inicio_envejecimiento);
            //console.log("fecha_calificacion:::"+fecha_calificacion);
            diff = getDMonths(fecha_inicio_envejecimiento, fecha_calificacion);
            //console.log("DIFFF::: "+diff);

            if (diff > meses || diff == meses){
                cumpleEnvejecimiento = "SI";
                //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
            }else{
                cumpleEnvejecimiento = "NO";
            }
        }
        //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
        //var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(""+cumpleEnvejecimiento));
    }

    function updateCumpleEnvejecimientoTotalBodega(component){
         //console.log("updateCumpleEnvejecimientoTotalBodega");
         var cumple_envejecimiento_bodega = view.get("cumple_envejecimiento_bodega");
         //console.log("cumple_envejecimiento_bodega::: "+cumple_envejecimiento_bodega);
         updateCumpleEnvejecimientoTotal(component, cumple_envejecimiento_bodega);
    }

    function updateCumpleEnvejecimientoTotalBotella(component){
         //console.log("updateCumpleEnvejecimientoTotalBotella");
         var cumple_envejecimiento_botella = view.get("cumple_envejecimiento_botella");
         //console.log("cumple_envejecimiento_botella::: "+cumple_envejecimiento_botella);
         updateCumpleEnvejecimientoTotal(component, cumple_envejecimiento_botella);
    }

    function updateCumpleEnvejecimientoTotal(component, cumple_envejecimiento_botella_bodega){
        //console.log("updateCumpleEnvejecimientoTotal");
        var cumple_envejecimiento_barrica = view.get("cumple_envejecimiento_barrica");

        //console.log("cumple_envejecimiento_barrica::: "+cumple_envejecimiento_barrica);
        //console.log("cumple_envejecimiento_botella_bodega::: "+cumple_envejecimiento_botella_bodega);

        var cumple_envejecimiento = null;
        if (view.get("tipo_envejecimiento_id") !=null && (cumple_envejecimiento_botella_bodega != null || cumple_envejecimiento_barrica != null)){
            cumple_envejecimiento = (cumple_envejecimiento_botella_bodega == 'NO' || cumple_envejecimiento_barrica == 'NO')?'NO':'SI';
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

     function updateTiempoBarrica(component){
        //console.log("updateTiempoBarrica");
        var fecha_entrada_barrica = view.get("fecha_entrada_barrica");
        var fecha_salida_barrica = view.get("fecha_salida_barrica");
        //console.log("fecha_entrada_barrica::: "+fecha_entrada_barrica);
        //console.log("fecha_salida_barrica::: "+fecha_salida_barrica);
        dayDiff(component, fecha_entrada_barrica, fecha_salida_barrica);

    }

    function setMesesBodega(){
        //console.log("setMesesBodega");
        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");


        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad.set("meses_bodega", new java.lang.Long(view.get("tiempo_bodega")));


        var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));

        if (tipo_envejecimiento_id == 6 || tipo_envejecimiento_id <6){



            var cumpleEnvejecimiento = "SI";
            var cumpleEnvejecimientoBarrica = entityTrazabilidad.get("cumple_envejecimiento_barrica");
            var cumpleEnvejecimientoBodega = view.get("cumple_envejecimiento_bodega");

            if (cumpleEnvejecimientoBarrica == 'NO' || cumpleEnvejecimientoBodega == 'NO'){
                cumpleEnvejecimiento = "NO";
            }

            entityTrazabilidad.set("meses_bodega_envejecimento", new java.lang.Long(view.get("meses_bodega")));
            entityTrazabilidad.set("cumple_envejecimiento_bodega", cumpleEnvejecimientoBodega);
            entityTrazabilidad.set("cumple_envejecimiento", cumpleEnvejecimiento);
        }

        trazabilidadRepo.save(entityTrazabilidad);
    }


    function resetMesesBodegaBarrica(){
        //console.log("resetMesesBodegaBarrica");
        var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
        var trazabilidad_id = entity.get("trazabilidad_id");
        //console.log("trazabilidad_id::: "+trazabilidad_id);
        var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
        entityTrazabilidad.set("tipo_envejecimiento_id", null);
        entityTrazabilidad.set("fecha_inicio_envejecimiento", null);
        entityTrazabilidad.set("fecha_calificacion_bodega", null);

        entityTrazabilidad.set("meses_bodega", null);
        entityTrazabilidad.set("meses_bodega_envejecimento", null);

        entityTrazabilidad.set("meses_barrica", null);
        entityTrazabilidad.set("meses_barrica_envejecimento", null);

        entityTrazabilidad.set("cumple_envejecimiento_bodega", null);
        entityTrazabilidad.set("cumple_envejecimiento_barrica", null);
        entityTrazabilidad.set("cumple_envejecimiento", null);

        trazabilidadRepo.save(entityTrazabilidad);

        deleteTazabilidadBarrica(trazabilidad_id);

        if (vh.widget('datatableEnvejecimientoBodega') != null){
            vh.updateWidget('datatableEnvejecimientoBodega');
        }
        if (vh.widget('datatableEnvejecimientoBarrica') != null){
            vh.updateWidget('datatableEnvejecimientoBarrica');
        }
        if (vh.widget('datatableEnvejecimientoBarricaTotal') != null){
            vh.updateWidget('datatableEnvejecimientoBarricaTotal');
        }
        if (vh.widget('cumple_envejecimiento') != null){
            vh.updateWidget('cumple_envejecimiento');
        }
    }

     function deleteTazabilidadBarrica(trazabilidad_id) {
         //console.log("deleteTazabilidadBarrica");

         var trazabilidadBarricaRepo = ctx.get("repos").get("trazabilidadBarricaRepo");
         var lstTrazabilidadBarrica = trazabilidadBarricaRepo.listAll();
         var i = 0;
         while(i != lstTrazabilidadBarrica.size()){
            var entityTrazabilidadBarrica = lstTrazabilidadBarrica.get(i);

            //console.log("trazabilidad_id::: "+trazabilidad_id);
            //console.log("entityTrazabilidadBarrica.get('trazabilidad_id')::: "+entityTrazabilidadBarrica.get("trazabilidad_id"));
            if (entityTrazabilidadBarrica.get("trazabilidad_id") == trazabilidad_id){
                //console.log("entityTrazabilidadBarrica.get('trazabilidad_barrica_id')::: "+entityTrazabilidadBarrica.get("trazabilidad_barrica_id"));
             //console.log("new java.lang.Long(entityTrazabilidadBarrica.get('trazabilidad_barrica_id'))::: "+new java.lang.String(entityTrazabilidadBarrica.get("trazabilidad_barrica_id")));
                trazabilidadBarricaRepo.doDelete(entityTrazabilidadBarrica);
            }
            i++
         }
     }

    function setMesesBarrica(){
        //console.log("setMesesBarrica");
        var trazabilidadBarricaRepo = ctx.get("repos").get("trazabilidadBarricaRepo");
        var trazabilidad_barrica_id = entity.get("trazabilidad_barrica_id");
        //console.log("trazabilidad_barrica_id::: "+trazabilidad_barrica_id);
        var entityTrazabilidadBarrica = trazabilidadBarricaRepo.findById(trazabilidad_barrica_id);
        entityTrazabilidadBarrica.set("meses", new java.lang.Long(view.get("tiempo_barrica")));
        trazabilidadBarricaRepo.save(entityTrazabilidadBarrica);
        setMediaPonderada(entityTrazabilidadBarrica.get("trazabilidad_id"));
    }

    function setMediaPonderada(trazabilidad_id_view){
        //console.log("setMediaPonderada");
        var trazabilidadBarricaRepo = ctx.get("repos").get("trazabilidadBarricaRepo");
        var listTrazabilidadBarrica = trazabilidadBarricaRepo.listAll();
        var i = 0;

        var volumenTotal = 0;
        var total = 0;
        while(i != listTrazabilidadBarrica.size()){
            entityTrazabilidadBarrica = listTrazabilidadBarrica.get(i);
            var trazabilidad_id_bbdd = entityTrazabilidadBarrica.get("trazabilidad_id");
            //console.log("trazabilidad_id_bbdd::: "+trazabilidad_id_bbdd);
            //console.log("trazabilidad_id_view::: "+trazabilidad_id_view);
            if (trazabilidad_id_bbdd == trazabilidad_id_view){
                volumenTotal = volumenTotal + entityTrazabilidadBarrica.get("capacidad")*entityTrazabilidadBarrica.get("numero_barricas");
                total = total + (entityTrazabilidadBarrica.get("capacidad")*entityTrazabilidadBarrica.get("numero_barricas")*entityTrazabilidadBarrica.get("meses"));
                //console.log("volumenTotal::: "+volumenTotal);
                //console.log("total::: "+total);
            }
            i++;
        }
        if (volumenTotal > 0){
            var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
            var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id_view);

            var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));



            var mesesBarrica = new java.lang.Long(total/volumenTotal);
            entityTrazabilidad.set("meses_barrica", mesesBarrica);
            var mesesBarricaEnvejecimiento = getMesesBarrica();
            entityTrazabilidad.set("meses_barrica_envejecimento", mesesBarricaEnvejecimiento);

            if (tipo_envejecimiento_id == 6 || tipo_envejecimiento_id <6){

                var cumpleEnvejecimientoBarrica = null;
                var cumpleEnvejecimientoBodega = entityTrazabilidad.get("cumple_envejecimiento_bodega");
                var cumpleEnvejecimiento = "SI";

                if (mesesBarrica > mesesBarricaEnvejecimiento || mesesBarrica == mesesBarricaEnvejecimiento){
                    cumpleEnvejecimientoBarrica = "SI";
                    //console.log("cumpleEnvejecimiento::: "+cumpleEnvejecimiento);
                }else{
                    cumpleEnvejecimientoBarrica = "NO";
                }
               if (cumpleEnvejecimientoBarrica == 'NO' || cumpleEnvejecimientoBodega == 'NO'){
                    cumpleEnvejecimiento = "NO";
                }
                entityTrazabilidad.set("cumple_envejecimiento", cumpleEnvejecimiento);
                entityTrazabilidad.set("cumple_envejecimiento_barrica", cumpleEnvejecimientoBarrica);
            }


            trazabilidadRepo.save(entityTrazabilidad);
        }
    }

    function setCumpleEnvejecimiento(tipo_envejecimiento_id, cumple_envejecimiento_botella, cumple_envejecimiento_bodega, cumple_envejecimiento_barrica){
        //console.log("setCumpleEnvejecimiento");
        //console.log("tipo_envejecimiento_id::: "+tipo_envejecimiento_id);
        //console.log("cumple_envejecimiento_botella::: "+cumple_envejecimiento_botella);
        //console.log("cumple_envejecimiento_bodega::: "+cumple_envejecimiento_bodega);
        //console.log("cumple_envejecimiento_barrica::: "+cumple_envejecimiento_barrica);
        if (tipo_envejecimiento_id != null && tipo_envejecimiento_id<=6){
            var cumple_envejecimiento = null;
            if (tipo_envejecimiento_id != null && (cumple_envejecimiento_botella != null || cumple_envejecimiento_bodega != null || cumple_envejecimiento_barrica != null)){
                if (tipo_envejecimiento_id <= 2){
                     cumple_envejecimiento = (cumple_envejecimiento_bodega == 'NO' || cumple_envejecimiento_barrica == 'NO')?'NO':'SI';
                }else if (tipo_envejecimiento_id > 2 && tipo_envejecimiento_id<=6){
                    cumple_envejecimiento = (cumple_envejecimiento_botella == 'NO' || cumple_envejecimiento_barrica == 'NO')?'NO':'SI';
                }
            }
            var trazabilidadRepo = ctx.get("repos").get("trazabilidadRepo");
            var trazabilidad_id = entity.get("trazabilidad_id");
            var entityTrazabilidad = trazabilidadRepo.findById(trazabilidad_id);
            entityTrazabilidad.set("cumple_envejecimiento", cumple_envejecimiento);
            trazabilidadRepo.save(entityTrazabilidad);
        }
    }

     function updateTipoEnvejecimiento(component){
        if (view.get("tipo_envejecimiento_id") != null){
            var tipo_envejecimiento_id = new java.lang.Long(view.get("tipo_envejecimiento_id"));
            var tipoEnvejecimientoRepo = ctx.get("repos").get("tipoEnvejecimientoRepo");
            var entityTipoEnvejecimiento = tipoEnvejecimientoRepo.findById(tipo_envejecimiento_id);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityTipoEnvejecimiento.get("tipo_envejecimiento")));
         } else {
             component.setValueExpression(null);
         }
    }

    function obtenerTimestampActual() {
        var fechaActual = new Date(); // Obtiene la fecha y hora actual
        var timestampEnMilisegundos = fechaActual.getTime(); // Obtiene el timestamp en milisegundos
        var timestampEnSegundos = new java.lang.Long(Math.floor(timestampEnMilisegundos / 1000)); // Convierte a segundos
        return timestampEnSegundos;
    }






