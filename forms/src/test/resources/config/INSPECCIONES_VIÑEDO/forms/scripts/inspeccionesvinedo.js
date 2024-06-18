    function insertChangeRegistry(){
        //console.log("insertChangeRegistry");
        var plaga_id_view = new java.lang.String(entity.get("plaga_id"));
        //console.log("plaga_id_view::: " + plaga_id_view);

        var changeRegistryRepo = ctx.get("repos").get("changeRegistryRepo");
        var entityChangeRegistry = changeRegistryRepo.newEntity();
        entityChangeRegistry.set("table_name", "plaga");
        entityChangeRegistry.set("event_type", "D");
        entityChangeRegistry.set("pk", plaga_id_view);
        changeRegistryRepo.save(entityChangeRegistry);

        var plagaImagesRepo = ctx.get("repos").get("plagaImagesRepo");
        var lstPlagaImages = plagaImagesRepo.listAll();

        var i = 0;
        while(i != lstPlagaImages.size()){
            entityPlagaImages = lstPlagaImages.get(i);

            var plaga_id_bbdd = new java.lang.String(entityPlagaImages.get("plaga_id"));
            var plaga_id_foto_bbdd = new java.lang.String(entityPlagaImages.get("plaga_foto_id"));
            //console.log("plaga_id_bbdd::: "+plaga_id_bbdd);
            //console.log("plaga_id_foto_bbdd::: "+plaga_id_foto_bbdd);
            if (plaga_id_bbdd == plaga_id_view){
                entityChangeRegistry = changeRegistryRepo.newEntity();
                entityChangeRegistry.set("table_name", "plaga_foto");
                entityChangeRegistry.set("event_type", "D");
                entityChangeRegistry.set("pk", plaga_id_foto_bbdd);
                changeRegistryRepo.save(entityChangeRegistry);
            }
            i++;
        }

        deletePlagaFoto(plaga_id_view);
    }

    function deletePlagaFoto(plaga_id_view) {
        //console.log("deletePlagaFoto");
        //console.log("plaga_id_view::: "+plaga_id_view);
        var plagaImagesRepo = ctx.get("repos").get("plagaImagesRepo");
        var lstPlagaImages = plagaImagesRepo.listAll();

        var i = 0;
        while(i != lstPlagaImages.size()){
            entityPlagaImages = lstPlagaImages.get(i);
            var plaga_id_bbdd = new java.lang.String(entityPlagaImages.get("plaga_id"));
            //console.log("plaga_id_bbdd::: "+plaga_id_bbdd);
            if (plaga_id_view == plaga_id_bbdd){
                //console.log("plaga_id_view == plaga_id_bbdd");
                var plaga_foto_id_bbdd = new java.lang.String(entityPlagaImages.get("plaga_foto_id"));
                //console.log("plaga_foto_id_bbdd::: "+plaga_foto_id_bbdd);
                //console.log("entityPlagaImages.get('plaga_foto_id')::: "+entityPlagaImages.get("plaga_foto_id"));
                plagaImagesRepo.doDelete(entityPlagaImages);
            }
            i++;
        }
     }

     function setSeveridad(tipo_plaga_id, severidad_id){
        //console.log("setSeveridad");
        //console.log("tipo_plaga_id::: "+tipo_plaga_id);
        //console.log("severidad_id::: "+severidad_id);

        var widgetTipoPlaga= vh.widget('tipo');
        var widgetSeveridad = vh.widget('severidad');
        //console.log("widgetSeveridad.getValue()::: "+widgetSeveridad.getValue());

        var componentTipoPlaga = widgetTipoPlaga.getComponent();
        var componentSeveridad = widgetSeveridad.getComponent();
        //console.log("label::: "+componentSeveridad.getOptions()[0].label);
        //console.log("label::: "+componentTipoPlaga.getOptions()[0].label);

        componentSeveridad.getOptions()[0].label = '';
        componentSeveridad.getOptions()[1].label = '';
        componentSeveridad.getOptions()[2].label = '';
        componentSeveridad.getOptions()[3].label = '';

        if (tipo_plaga_id == 1){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '< 5% de los  botones florales o bayas con larvas/mordeduras/puestas/glomérulos';
            componentSeveridad.getOptions()[2].label = '5-10% racimos con puesta (2ªgen) o 2-5%racimos con puesta(3ª gen)';
            componentSeveridad.getOptions()[3].label = '>10% racimos con puesta (2ªgen) o >5%racimos con puesta(3ª gen)';
        }else if (tipo_plaga_id == 2){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = 'D: < 5 larvas/cepa/ E:<7 larvas/cepa/ F: <10 larvas/cepa/ G:< 12 larvas/cepa';
            componentSeveridad.getOptions()[2].label = 'D: > 5 larvas/cepa/ E:>7 larvas/cepa/  F: Z10 larvas/cepa/  G: >12 larvas/cepa';
            componentSeveridad.getOptions()[3].label = '>20 % hojas con morderuras/glomérulos en bayas/> 12  larvas/cepa';
        }else if (tipo_plaga_id == 3){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<2% plantas (con brotes<10cm) con yemas dañadas por mordeduras circulares';
            componentSeveridad.getOptions()[2].label = '2-5% plantas (con brotes<10cm) con yemas dañadas por mordeduras circulares';
            componentSeveridad.getOptions()[3].label = '>5% plantas (con brotes<10cm) con yemas dañadas por mordeduras circulares';
        }else if (tipo_plaga_id == 4){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<2% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
            componentSeveridad.getOptions()[2].label = '2-5% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
            componentSeveridad.getOptions()[3].label = '>5% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
        }else if (tipo_plaga_id == 5){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<5% plantas con manchas poligonales en hoja color crema a rojo con o sin picnidios negros/lesiones o momificaciones de bayas';
            componentSeveridad.getOptions()[2].label = '5-10% plantas con manchas poligonales en hoja color crema a rojo con o sin picnidios negros/lesiones o momificaciones de bayas';
            componentSeveridad.getOptions()[3].label = '>10% plantas con manchas poligonales en hoja color crema a rojo con o sin picnidios negros/lesiones o momificaciones de bayas';
        }else if (tipo_plaga_id == 6){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<2% plantas con moho grisaceo  en los tejidos/heridas en bayas/pudimiento racimo';
            componentSeveridad.getOptions()[2].label = '2-5% plantas con moho grisaceo  en los tejidos/heridas en bayas/pudimiento racimo';
            componentSeveridad.getOptions()[3].label = '>5% plantas con moho grisaceo  en los tejidos/heridas en bayas/pudimiento racimo';
        }else if (tipo_plaga_id == 7){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<2% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
            componentSeveridad.getOptions()[2].label = '2-5% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
            componentSeveridad.getOptions()[3].label = '>5% hojas con presencia de acaro/agallas visibles/daños brotación yemas';
        }else if (tipo_plaga_id == 8){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<5% plantas con manchas aceitosas amarillentas en el haz/borra en envés/lesiones en brotes/borra en flores y granos';
            componentSeveridad.getOptions()[2].label = '5-10% plantas con manchas aceitosas amarillentas en el haz/borra en envés/lesiones en brotes/borra en flores y granos';
            componentSeveridad.getOptions()[3].label = '>10% plantas con manchas aceitosas amarillentas en el haz/borra en envés/lesiones en brotes/borra en flores y granos';
        }else if (tipo_plaga_id == 9){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<5% plantas con presencia de mosquito (<2 por hoja)/manchas color rojo con nervios verde en tintas/decoloraciones amarillentas en blancas con necrosis en borde/enrollamiento';
            componentSeveridad.getOptions()[2].label = '5-10% plantas con presencia de mosquito (>2 por hoja)/manchas color rojo con nervios verde en tintas/decoloraciones amarillentas en blancas con necrosis en borde/enrollamiento';
            componentSeveridad.getOptions()[3].label = '>10% plantas con presencia de mosquito (>2 por hoja)/manchas color rojo con nervios verde en tintas/decoloraciones amarillentas en blancas con necrosis en borde/enrollamiento';
        }else if (tipo_plaga_id == 10){
            componentSeveridad.getOptions()[0].label = 'Asintomático';
            componentSeveridad.getOptions()[1].label = '<5% plantas con polvo blanco en hoja/manchas oscuras sarmientos/polvo blanco en racimos';
            componentSeveridad.getOptions()[2].label = '5-10% plantas con  polvo blanco en hoja/manchas oscuras sarmientos/polvo blanco en racimos';
            componentSeveridad.getOptions()[3].label = '>10% plantas con polvo blanco en hoja/manchas oscuras sarmientos/polvo blanco en racimos';
        }

        //console.log("severidad_id::: "+severidad_id);
        widgetSeveridad.setValue(severidad_id!=null?severidad_id:'');


   }

   function setDescripcionSeveridad(severidad_id){
        //console.log("severidad_id::: "+severidad_id);
        var widgetDescripcionSeveridad = vh.widget('descripcion_severidad');
        var labelSeveridad = "";
        if (severidad_id!=null){
            var widgetSeveridad = vh.widget('severidad');
            var componentSeveridad = widgetSeveridad.getComponent();
            var labelSeveridad = componentSeveridad.getOptions()[severidad_id].label;
            //console.log("labelSeveridad::: "+labelSeveridad);
        }
        //widgetDescripcionSeveridad.setValue(labelSeveridad);
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

        var widgetMunicipio= vh.widget("c_provmuni_id");
          widgetMunicipio.setValue(valueReferencia.substring(0,5));
          updateCodigoMunicipio();

           var widgetProvincia= vh.widget("c_provincia_id");
           widgetProvincia.setValue(valueReferencia.substring(0,2));
           updateCodigoProvincia();



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

   function setReferencia(switcherReferenciaSigPac){
        //console.log("resetReferencia");
        if (switcherReferenciaSigPac == false){
            var widgetReferencia= vh.widget("c_refrec");
            if (widgetReferencia != null){
                widgetReferencia.setValue(null);
            }
        }
   }





 
