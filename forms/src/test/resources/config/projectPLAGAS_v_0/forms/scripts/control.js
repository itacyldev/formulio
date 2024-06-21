    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
    var entityRecinto;

    function updateHabitat(component){
        //console.log("updateHabitat");
        updateCampo(component, "habitat");
    }

    function updateGrupo(component){
        //console.log("updateGrupo");
        updateCampo(component, "grupo");
    }

    function updateUtilizacion(component){
        //console.log("updateUtilizacion");
        updateCampo(component, "utilizacion");
    }

    function updateSistExplotacion(component){
        //console.log("updateSistExplotacion");
        updateCampo(component, "sis_explotacion");
    }

    function updateActuacionParcela(component){
        //console.log("updateActuacionParcela");
        updateCampo(component, "actuacion_parcela");
    }

    function updateTipoLaboreo(component){
        //console.log("updateTipoLaboreo");
        updateCampo(component, "tipo_laboreo");
    }

    function updateAltura(component){
        //console.log("updateAltura");
        entityRecinto = setEntityRecinto();

        if (entityRecinto != null){
            var altura = new java.math.BigDecimal(entityRecinto.get("altura")).setScale(2, java.math.RoundingMode.HALF_UP);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+altura.toString()));
        }else{
            component.setValueExpression(null);
        }
    }

    function updateDensidad(component){
        //console.log("updateDensidad");
        if (entityRecinto != null){
            var densidad = new java.math.BigDecimal(entityRecinto.get("densidad")).setScale(2, java.math.RoundingMode.HALF_UP);
             component.setValueExpression(valueExpressionFactory.getInstance().create(""+densidad.toString()));
        }else{
            component.setValueExpression(null);
        }
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
        var recinto_id_view = view.get("recinto_id");
        //console.log("recinto_id_view::: "+recinto_id_view);
        var recinto_id_bbdd = entity.get("recinto_id");
        //console.log("recinto_id_bbdd::: "+recinto_id_bbdd);
        if (recinto_id_view == null){
            recinto_id_view = entity.get("operador_id");
        }

        entityRecinto = null;
        if (recinto_id_view != null){
            var recintoRepo = ctx.get("repos").get("recintoRepo");
            var lstRecintos = recintoRepo.listAll();
            var i = 0;

            while(i != lstRecintos.size()){
                //console.log("i: " + i);
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
    
    /*function aplicarHuraTotaLinde(){
        console.log("aplicarHuraTotaLinde");
        var huraTotal = view.get("huraTotal");
        console.log("huraTotal::: "+huraTotal);

        var visitaRepo = ctx.get("repos").get("visitaRepo");
        var visita_id = new java.lang.Long(view.get("visita_id"))
        var entityVisita = visitaRepo.findById(visita_id);

        var i = 1;
        while(i != 6){
            var id_huras_transecto_linde = "huras_transecto_linde_"+i;
            console.log("id_huras_transecto_linde::: "+id_huras_transecto_linde);
            console.log("view.get('id_huras_transecto_linde')::: "+view.get(id_huras_transecto_linde));

            if (view.get(id_huras_transecto_linde) == null){
                entityVisita.set(id_huras_transecto_linde, huraTotal);
            }
            i++;
        }
        visitaRepo.save(entityVisita);
    }*/

    function aplicarHuraTotaLinde(et){
        console.log("aplicarHuraTotaLinde");
        var huraTotal = view.get("huraTotal");
        console.log("huraTotal::: "+huraTotal);
        var visitaRepo = ctx.get("repos").get("visitaRepo");
        var visita_id = new java.lang.Long(view.get("visita_id"))
        var entityVisita = visitaRepo.findById(visita_id);

        var i = 1;
        while(i != 6){
            var id_huras_transecto_linde = "huras_transecto_linde_"+i;
            console.log("id_huras_transecto_linde::: "+id_huras_transecto_linde);
            console.log("view.get('id_huras_transecto_linde')::: "+view.get(id_huras_transecto_linde));

            if (view.get(id_huras_transecto_linde) == null){
                Packages.es.jcyl.ita.formic.forms.scripts.ScriptEntityUtils.set(entityVisita, id_huras_transecto_linde,huraTotal);
            }
            i++;
        }
        visitaRepo.save(entityVisita);
    }

    function limpiarDatos(et){
        console.log("aplicarHuraTotaLinde");
        var visitaRepo = ctx.get("repos").get("visitaRepo");
        var visita_id = new java.lang.Long(view.get("visita_id"))
        var entityVisita = visitaRepo.findById(visita_id);
        console.log("visita_id::: "+visita_id);
        console.log("entityVisita.get(visita_id)::: "+entityVisita.get("visita_id"));

        if (entityVisita != null){
            var i = 1;
            while(i != 34){
                var id_huras_transecto_linde = "huras_transecto_linde_"+i;
                console.log("id_huras_transecto_linde::: "+id_huras_transecto_linde);
                Packages.es.jcyl.ita.formic.forms.scripts.ScriptEntityUtils.set(entityVisita, id_huras_transecto_linde, null);
                i++;
            }
            visitaRepo.save(entityVisita);
        }
    }



    /*function updateHurasTransectoLinde1(component){
        console.log("updateHurasTransectoLinde1");
        console.log("view.get('huras_transecto_linde_1')::: "+view.get("huras_transecto_linde_1"));
        if (view.get("huras_transecto_linde_1") == null){
            var huraTotal = view.get("huraTotal");
            console.log("huraTotal::: "+huraTotal);
            var visitaRepo = ctx.get("repos").get("visitaRepo");
            var visita_id = new java.lang.Long(view.get("visita_id"))
            var entityVisita = visitaRepo.findById(visita_id);
            entityVisita.set("huras_transecto_linde_1", huraTotal);
            visitaRepo.save(entityVisita);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+huraTotal));
        }
       }*/

    /*function updateOnAfter(widget){
         console.log("updateHurasTransectoLinde1");
         if (widget.getValue() != null and new java.lang.Long(widget.getValue()) == 1){
            widget.getInputView().setTextColor(-500136);
            console.log("Entro");
         }
         console.log("This is a widget: " + widget.getComponentId());
         console.log("This is a widget: " + widget.getValue());
         /*console.log("This is a widget: " + widget.getInputView());

         //console.log("This is a widget: " + widget.getComponent().getId());
         //widget.getInputView();
         widget.setBackgroundColor(-500136);
         widget.getInputView().setTextColor(-500136);
         //widget.getInputView().setText("Hello world from rhino JS!");

    }*/
