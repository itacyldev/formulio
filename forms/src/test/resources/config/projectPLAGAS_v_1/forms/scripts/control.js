    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
    var entityRecinto;

    function updateHabitat(component){
        console.log("updateHabitat");
        updateCampo(component, "habitat");
    }

    function updateGrupo(component){
        console.log("updateGrupo");
        updateCampo(component, "grupo");
    }

    function updateUtilizacion(component){
        console.log("updateUtilizacion");
        updateCampo(component, "utilizacion");
    }

    function updateSistExplotacion(component){
        console.log("updateSistExplotacion");
        updateCampo(component, "sis_explotacion");
    }

    function updateActuacionParcela(component){
        console.log("updateActuacionParcela");
        updateCampo(component, "actuacion_parcela");
    }

    function updateTipoLaboreo(component){
        console.log("updateTipoLaboreo");
        updateCampo(component, "tipo_laboreo");
    }

    function updateAltura(component){
        console.log("updateAltura");
        entityRecinto = setEntityRecinto();

        if (entityRecinto != null){
            var altura = new java.math.BigDecimal(entityRecinto.get("altura")).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+altura.toString()));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateDensidad(component){
        console.log("updateDensidad");
        if (entityRecinto != null){
            var densidad = new java.math.BigDecimal(entityRecinto.get("densidad")).setScale(2, java.math.RoundingMode.HALF_UP);
             component.setValueExpression(valueExpressionFactory.getInstance().create(""+densidad.toString()));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateCampo(component, campo){
        console.log("updateCampo");
        entityRecinto = setEntityRecinto();

        if (entityRecinto != null){
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityRecinto.get(campo)));
        }else{
            component.setValueExpression(null);
        }
    }

    function setEntityRecinto(){
        console.log("setEntityRecinto");
        var recinto_id_view = view.get("recinto_id");
        console.log("recinto_id_view::: "+recinto_id_view);
        var recinto_id_bbdd = entity.get("recinto_id");
        console.log("recinto_id_bbdd::: "+recinto_id_bbdd);
        if (recinto_id_view == null){
            recinto_id_view = entity.get("operador_id");
        }

        entityRecinto = null;
        if (recinto_id_view != null){
            var recintoRepo = ctx.get("repos").get("recintoRepo");
            var lstRecintos = recintoRepo.listAll();
            var i = 0;
             var encontrado = false;
            while(i != lstRecintos.size()){
                console.log("i: " + i);
                entityRecinto = lstRecintos.get(i);
                var recinto_id_bbdd = entityRecinto.get("recinto_id");
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

    function getNumUnidadMuestral(transector_id){
        console.log("getNumUnidadMuestral");
        var num_unidad_muestral = 1;
        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");

        var lstUnidadesMuestrales = unidadMuestralRepo.listAll();
        var i = lstUnidadesMuestrales.size();
        console.log(" lstUnidadesMuestrales.size():::" +i)
        var encontrado = false;
        while(i != 0){
            entityUnidadMuestral = lstUnidadesMuestrales.get(i-1);
            var transector_id_bbdd = entityUnidadMuestral.get("transector_id");
            if (transector_id_bbdd == transector_id){
                encontrado = true;
                break;
            }
            i--;
        }

        if (encontrado){
            num_unidad_muestral = entityUnidadMuestral.get("num_unidad_muestral")+1;
        }
        console.log("num_unidad_muestral:::" +num_unidad_muestral)
        return num_unidad_muestral;
    }
    
    function anadirUnidadMuestral(transector_id, huraTotal, activaTotal, cobVegTotal, danoTotal){
        console.log("anadirUnidadMuestral");

        var transector_id = new java.lang.Long(entity.get("transector_id"));

        console.log("transector_id::: "+transector_id);
        
        /*console.log("view.get('huraTotal')::: "+view.get("huraTotal"));
        console.log("view.get('activaTotal')::: "+view.get("activaTotal"));
        console.log("view.get('cobVegTotal')::: "+view.get("cobVegTotal"));
        console.log("view.get('danoTotal')::: "+view.get("danoTotal"));
        */
        console.log("huraTotal::: "+huraTotal);
        console.log("activaTotal::: "+activaTotal);
        console.log("cobVegTotal::: "+cobVegTotal);
        console.log("danoTotal::: "+danoTotal);

        var unidadMuestralRepo = ctx.get("repos").get("unidadMuestralRepo");
        var entityUnidadMuestral = unidadMuestralRepo.newEntity();
        entityUnidadMuestral.set("transector_id", transector_id);
        entityUnidadMuestral.set("num_unidad_muestral", new java.lang.Long(getNumUnidadMuestral(transector_id)));
        /*entityUnidadMuestral.set("hura_id", view.get("huraTotal"));
        entityUnidadMuestral.set("activa_id", view.get("activaTotal"));
        entityUnidadMuestral.set("cob_veg_id", view.get("cobVegTotal"));
        entityUnidadMuestral.set("dano_id", view.get("danoTotal"));*/
        if (huraTotal != null){
            entityUnidadMuestral.set("hura_id", new java.lang.Long(huraTotal));
        }

        if (activaTotal != null){
            entityUnidadMuestral.set("activa_id", new java.lang.Long(activaTotal));
        }

        if (cobVegTotal != null){
            entityUnidadMuestral.set("cob_veg_id", new java.lang.Long(cobVegTotal));
        }

        if (danoTotal != null){
            entityUnidadMuestral.set("dano_id", new java.lang.Long(danoTotal));
        }

        unidadMuestralRepo.save(entityUnidadMuestral);
    }
