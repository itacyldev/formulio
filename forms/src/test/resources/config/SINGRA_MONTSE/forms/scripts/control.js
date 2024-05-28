    function cancelarInspeccion(c_accion_singra_id){
        //console.log("cancelarInspeccion");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);

        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_id));
        entityInspeccion.set("b_cancelada",new java.lang.Long(1));
        inspeccionRepo.save(entityInspeccion);
    }

    function activarInspeccion(c_accion_singra_id){
        //console.log("activarInspeccion");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);

        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_id));
        entityInspeccion.set("b_cancelada",new java.lang.Long(0));
        inspeccionRepo.save(entityInspeccion);
    }

    function cambiarInspeccionTelefonica(c_accion_singra_id){
        //console.log("cambiarInspeccionTelefonica");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);

        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_id));
        entityInspeccion.set("b_telefonico",new java.lang.Long(1));
        inspeccionRepo.save(entityInspeccion);

        cambiarInspeccionTelefonicaInsitu('0');
    }

    function cambiarInspeccionInsitu(c_accion_singra_id){
        //console.log("cambiarInspeccionInsitu");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);

        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_id));
        entityInspeccion.set("b_telefonico",new java.lang.Long(0));
        inspeccionRepo.save(entityInspeccion);
        cambiarInspeccionTelefonicaInsitu('1');
    }

    function cambiarInspeccionTelefonicaInsitu(valor){
        var viewContexts = vh.viewHolders();
        //console.log("viewContexts");
        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitemTelefonico'));
        lst = lst.flatMap(obj => obj.widgetContext.viewContext.statefulWidgets);
        lst = lst.filter(obj => obj.inputId.startsWith('b_telefonico')); //lista de inputwidget
        lst = lst.apply(obj => obj.inputView.check(valor));
    }

    function setInspeccionadaInspeccion(c_accion_singra_id){
        //console.log("setInspeccionadaInspeccion");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);

        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_id));
        entityInspeccion.set("b_firmado",new java.lang.Long(1));

         //console.log("b_telefonico::: "+entityInspeccion.get('b_telefonico'));

        setDFirma(entityInspeccion, "d_repr_nombre_instal", "d_repr_apellidos_instal", "d_firma_entidad");
        setDFirma(entityInspeccion, "d_nombre_inspector1", "d_apellidos_inspector1", "d_firma_t1");
        setDFirma(entityInspeccion, "d_nombre_inspector2", "d_apellidos_inspector2", "d_firma_t2");
        setDFirma(entityInspeccion, "d_nombre_inspector3", "d_apellidos_inspector3", "d_firma_t3");

        //console.log("d_firma_entidad::: "+entityInspeccion.get("d_firma_entidad"));
        //console.log("d_firma_t1::: "+entityInspeccion.get("d_firma_t1"));
        //console.log("d_firma_t2::: "+entityInspeccion.get("d_firma_t2"));
        //console.log("d_firma_t3::: "+entityInspeccion.get("d_firma_t3"));

        inspeccionRepo.save(entityInspeccion);

    }

    function setDFirma(entityInspeccion, str_nombre, str_apellidos, str_firma){
        //console.log("setDFirma");
        var nombre = reemplazarNuloConCadenaVacia(entityInspeccion.get(str_nombre));
        //console.log("nombre::: "+nombre);
        var apellidos = reemplazarNuloConCadenaVacia(entityInspeccion.get(str_apellidos));
        //console.log("apellidos::: "+apellidos);
        var d_firma = nombre + " " + apellidos;
        //console.log("d_firma::: "+d_firma);
        d_firma = d_firma.trimLeft();
        d_firma = d_firma.trimRight();
        //console.log("d_firma::: "+d_firma);
        entityInspeccion.set(str_firma,d_firma);
    }

    function reemplazarNuloConCadenaVacia(cadena) {
      if (cadena === null) {
        return "";
      } else {
        return cadena;
      }
    }

    function setInspeccionadoProductoMarca(c_pm_inspeccion_id){
        //console.log("setInspeccionadoProductoMarca");
        //console.log("c_pm_inspeccion_id::: "+c_pm_inspeccion_id);

        var pmInspeccionRepo = ctx.get("repos").get("pmInspeccionRepo");
        entityPmInspeccion = pmInspeccionRepo.findById(new java.lang.Long(c_pm_inspeccion_id));
        //console.log("b_inspeccionado::: "+entityPmInspeccion.get("b_inspeccionado"));
        entityPmInspeccion.set("b_inspeccionado",new java.lang.Long(1));

        var viewContexts = vh.viewHolders();
        //console.log("viewContexts");
        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitemProductoMarca'));
        lst = lst.flatMap(obj => obj.widgetContext.viewContext.statefulWidgets);
        lst = lst.filter(obj => obj.inputId.startsWith('respuestaLabel')); //lista de inputwidget
        //console.log("b_utilizado::: "+new java.lang.Long(lst.get(0).inputView.getCheckedRadioButtonId()));
        //console.log("b_volumen::: "+new java.lang.Long(lst.get(1).inputView.getCheckedRadioButtonId()));
        //console.log("b_conforme::: "+new java.lang.Long(lst.get(2).inputView.getCheckedRadioButtonId()));
        //console.log("b_integrado::: "+new java.lang.Long(lst.get(3).inputView.getCheckedRadioButtonId()));
        entityPmInspeccion.set("b_utilizado",new java.lang.Long(lst.get(0).inputView.getCheckedRadioButtonId()==0?1:lst.get(0).inputView.getCheckedRadioButtonId()==1?0:2));
        entityPmInspeccion.set("b_volumen",new java.lang.Long(lst.get(1).inputView.getCheckedRadioButtonId()==0?1:lst.get(1).inputView.getCheckedRadioButtonId()==1?0:2));
        entityPmInspeccion.set("b_conforme",new java.lang.Long(lst.get(2).inputView.getCheckedRadioButtonId()==0?1:lst.get(2).inputView.getCheckedRadioButtonId()==1?0:2));
        entityPmInspeccion.set("b_integrado",new java.lang.Long(lst.get(3).inputView.getCheckedRadioButtonId()==0?1:lst.get(3).inputView.getCheckedRadioButtonId()==1?0:2));


        pmInspeccionRepo.save(entityPmInspeccion);

        var c_accion_singra = entityPmInspeccion.get("c_accion_singra");
        var lstPmInspeccion = pmInspeccionRepo.listAll();

         var i = 0;
         var todasInspeccionadas = true;
         while(i != lstPmInspeccion.size() && todasInspeccionadas){
            entityPmInspeccion = lstPmInspeccion.get(i);
            var bInpeccionado = entityPmInspeccion.get("b_inspeccionado");
            //console.log("bInpeccionado::: "+bInpeccionado);
            if (bInpeccionado != 1){
                todasInspeccionadas = false;
            }
            i++;
         }

        if (todasInspeccionadas){
            setInspeccionadoProductoMarcaInspeccion(c_accion_singra);
        }
    }

     function updateReponsable(nombre_responsable, apellidos_responsable){
        //console.log("updateReponsable");
        //console.log("nombre_responsable:: "+nombre_responsable);
        //console.log("apellidos_responsable:: "+apellidos_responsable);

        var nombre = "";
        var apellidos = "";
        if (nombre_responsable != null){
            nombre = nombre_responsable;
        }
        if (apellidos_responsable != null){
            apellidos = apellidos_responsable;
        }
        //console.log("nombre:: "+nombre);
        //console.log("apellidos:: "+apellidos);

        var widgetResponsable = vh.widget("responsable");
        widgetResponsable.setValue(nombre+ " " +apellidos);
        //console.log("widgetResponsable:: "+widgetResponsable.getValue());

        var widgetFirmadoResponsable = vh.widget("firmado_responsable");
        widgetFirmadoResponsable.setValue(nombre+ " " +apellidos);
        //console.log("widgetFirmadoResponsable:: "+widgetFirmadoResponsable.getValue());

        // d_firma_entidad       //

     }

     function todosSi(){
        //console.log("todosSi");
        todos('0');
     }

     function todosNo(){
        //console.log("todosNo");
        todos('1');
     }

     function todosNA(){
         //console.log("todosNA");
         todos('2');
      }

     function todos(valor){
        var viewContexts = vh.viewHolders();
        //console.log("viewContexts");
        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitem'));
        lst = lst.flatMap(obj => obj.widgetContext.viewContext.statefulWidgets);
        lst = lst.filter(obj => obj.inputId.startsWith('respuestaLabel')); //lista de inputwidget
        lst = lst.apply(obj => obj.inputView.check(valor));
    }

    function todosSiInspeccion(c_accion_singra_view){
        //console.log("todosSiInspeccion");
        //console.log("c_accion_singra_view::: "+c_accion_singra_view);
        todosInspeccion(c_accion_singra_view, new java.lang.Long(1));

    }

    function todosNoInspeccion(c_accion_singra_view){
        //console.log("todosNoInspeccion");
        //console.log("c_accion_singra_view::: "+c_accion_singra_view);
        todosInspeccion(c_accion_singra_view, new java.lang.Long(0));
    }

    function todosNAInspeccion(c_accion_singra_view){
            //console.log("todosNoInspeccion");
            //console.log("c_accion_singra_view::: "+c_accion_singra_view);
            todosInspeccion(c_accion_singra_view, new java.lang.Long(2));
        }

    function todosInspeccion(c_accion_singra_view, valor){
        //console.log("todosInspeccion");
        //console.log("c_accion_singra_view::: "+c_accion_singra_view);
        //console.log("valor::: "+valor);

        var pmInspeccionRepo = ctx.get("repos").get("pmInspeccionRepo");
        var lstPmInspeccion = pmInspeccionRepo.listAll();
        var i = 0;
        while(i != lstPmInspeccion.size()){
            entityPmInspeccion = lstPmInspeccion.get(i);
            //console.log("c_pm_inspeccion_id::: "+entityPmInspeccion.get("c_pm_inspeccion_id"));
            var c_accion_singra_bbdd = entityPmInspeccion.get("c_accion_singra");
            //console.log("c_accion_singra_bbdd::: "+c_accion_singra_bbdd);
            if (c_accion_singra_bbdd == c_accion_singra_view){
                //console.log("iguales::: ");
                entityPmInspeccion.set("b_utilizado", valor);
                entityPmInspeccion.set("b_volumen", valor);
                entityPmInspeccion.set("b_conforme", valor);
                entityPmInspeccion.set("b_integrado", valor);
                entityPmInspeccion.set("b_inspeccionado", new java.lang.Long(1));

                pmInspeccionRepo.save(entityPmInspeccion);

                setRespuestaRespondida(entityPmInspeccion.get("c_cuestionario_respondido"), ""+valor);
            }
            i++;
        }
        setInspeccionadoProductoMarcaInspeccion(c_accion_singra_view);

    }

    function setRespuestaRespondida(c_cuestionario_respondido_view, valor){
        //console.log("setRespuestaRenpondida");
        //console.log("c_cuestionario_respondido_view::: "+c_cuestionario_respondido_view);

        var respuestaRespondidaRepo = ctx.get("repos").get("respuestaRespondidaRepo");

        var lstRespuestaRespondida = respuestaRespondidaRepo.listAll();
        var i = 0;

        while(i != lstRespuestaRespondida.size()){
            entityRespuestaRespondida = lstRespuestaRespondida.get(i);
            var c_cuestionario_respondido_bbdd = entityRespuestaRespondida.get("c_cuestionario_respondido");
            //console.log("c_cuestionario_respondido_bbdd::: "+c_cuestionario_respondido_bbdd);
            if (c_cuestionario_respondido_bbdd == c_cuestionario_respondido_view){
                //console.log("c_cuestionario_respondido_bbdd == c_cuestionario_respondido_view");
                //console.log("valor::: "+ valor);
                //console.log("entityRespuestaRespondida.get('c_respuesta_respondida_id')::: "+ entityRespuestaRespondida.get("c_respuesta_respondida_id"));
                entityRespuestaRespondida.set("d_respuesta", valor);
                respuestaRespondidaRepo.save(entityRespuestaRespondida);
            }
            i++;
        }
  }

    function setInspeccionadoProductoMarcaInspeccion(c_accion_singra){
        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra));
        entityInspeccion.set("b_inspeccionado_pm",new java.lang.Long(1));
        inspeccionRepo.save(entityInspeccion);
    }

    function setInspeccionadoCuestionario(c_accion_singra){
        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra));
        entityInspeccion.set("b_inspeccionado_cuestionario",new java.lang.Long(1));
        inspeccionRepo.save(entityInspeccion);
    }

    function updateInspeccionadoCuestionario(component){
        var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        var c_accion_singra_view = entity.get('c_accion_singra_id');
        //console.log("entity.get('c_accion_singra_id')::: "+entity.get('c_accion_singra_id'));
        if (c_accion_singra_view != null){
            entityInspeccion = inspeccionRepo.findById(new java.lang.Long(c_accion_singra_view));
            var b_inspeccionado_cuestionario = entityInspeccion.get("b_inspeccionado_cuestionario");
            //console.log("b_inspeccionado_cuestionario::: "+b_inspeccionado_cuestionario);
            component.setValueExpression(valueExpressionFactory.getInstance().create(""+b_inspeccionado_cuestionario));
        }
    }

    function countRegistros(component){
        //console.log("countRegistros");
        //console.log("entity.get('c_accion_singra_id')::: "+entity.get('c_accion_singra_id'));
        var c_accion_singra_view = entity.get('c_accion_singra_id');
        var docInspeccionRepo = ctx.get("repos").get("docInspeccionRepo");

        var lstDocInspeccion = docInspeccionRepo.listAll();
        var i = 0;
        var numRegistrosSanitarios = 0;
        var numRegistrosEmbotelladores = 0;
        var numMarcasYProductos = 0;
        var numFigurasCalidad = 0;

        while(i != lstDocInspeccion.size()){
            entityDocInspeccion = lstDocInspeccion.get(i);
            var c_accion_singra_bbdd = entityDocInspeccion.get("c_accion_singra");
            if (c_accion_singra_bbdd == c_accion_singra_view){
                if (entityDocInspeccion.get("c_tipo_doc") == 4){
                    numRegistrosSanitarios++;
                } else if (entityDocInspeccion.get("c_tipo_doc") == 10){
                    numRegistrosEmbotelladores++;
                } else if (entityDocInspeccion.get("c_tipo_doc") == 5){
                    numMarcasYProductos++;
                } else if (entityDocInspeccion.get("c_tipo_doc") == 6){
                    numFigurasCalidad++;
                }
            }
            i++;
        }
        //console.log("numRegistrosSanitarios::: "+numRegistrosSanitarios);
        //console.log("numRegistrosEmbotelladores::: "+numRegistrosEmbotelladores);
        //console.log("numMarcasYProductos::: "+numMarcasYProductos);
        //console.log("numFigurasCalidad::: "+numFigurasCalidad);

        var viewContexts = vh.viewHolders();

        lst = viewContexts.filter(obj => obj.holderId.startsWith('datalistitemRegistrosEmbotelladores'));
        //console.log(lst.size());
        var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setRenderExpression(valueExpressionFactory.getInstance().create(lst.size()>0));

    }

    /*function countRegistros2(component){
        //console.log("countRegistrosSanitarios");
        var c_accion_singra_view = entity.get('c_accion_singra_id');
        var docInspeccionRepo = ctx.get("repos").get("docInspeccionRepo");

        var lstDocInspeccion = docInspeccionRepo.listAll();
        var i = 0;
        var numRegistrosSanitarios = 0;
        var numRegistrosEmbotelladores = 0;
        var numMarcasYProductos = 0;
        var numFigurasCalidad = 0;

        while(i != lstDocInspeccion.size()){
           entityDocInspeccion = lstDocInspeccion.get(i);
           var c_accion_singra_bbdd = entityDocInspeccion.get("c_accion_singra");
           if (c_accion_singra_bbdd == c_accion_singra_view){
               if (entityDocInspeccion.get("c_tipo_doc") == 4){
                   numRegistrosSanitarios++;
               } else if (entityDocInspeccion.get("c_tipo_doc") == 10){
                   numRegistrosEmbotelladores++;
               } else if (entityDocInspeccion.get("c_tipo_doc") == 5){
                   numMarcasYProductos++;
               } else if (entityDocInspeccion.get("c_tipo_doc") == 6){
                   numFigurasCalidad++;
               }
           }
           i++;
        }
        //console.log("numRegistrosSanitarios::: "+numRegistrosSanitarios);
        //console.log("numRegistrosEmbotelladores::: "+numRegistrosEmbotelladores);
        //console.log("numMarcasYProductos::: "+numMarcasYProductos);
        //console.log("numFigurasCalidad::: "+numFigurasCalidad);

        //console.log("component.getId::: "+component.getId());
        var viewContexts = vh.viewHolders();
        var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setValueExpression(valueExpressionFactory.getInstance().create(numRegistrosSanitarios));
    }*/

    function countRegistrosSanitarios(component){
        //console.log("countRegistrosSanitarios");
        countElementos(component, 'datalistitemRegistrosSanitarios');
    }

    function countRegistrosEmbotelladores(component){
        //console.log("countRegistrosEmbotelladores");
        countElementos(component, 'datalistitemRegistrosEmbotelladores');
    }

    function countProductosYMarcasAutorizados(component){
        //console.log("countProductosYMarcasAutorizados");
        countElementos(component, 'datalistitemProductosYMarcasAutorizados');
    }

    function countFigurasDeCalidad(component){
        //console.log("countFigurasDeCalidad");
        countElementos(component, 'datalistitemFigurasCalidad');
    }

    function countElementos(component, datalist){
        var viewContexts = vh.viewHolders();
        lst = viewContexts.filter(obj => obj.holderId.startsWith(datalist));
        var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        component.setRenderExpression(valueExpressionFactory.getInstance().create(lst.size()==0));
    }

    function generarInforme(component){
        //console.log("generarInforme");
        //console.log("view.b_conforme::: "+view.get("b_conforme"));
        //console.log("view.firma_responsable::: "+view.get("firma_responsable"));
        /*var viewContexts = vh.viewHolders();
        //console.log("viewContexts");
        lst = viewContexts.filter(obj => obj.holderId.startsWith('formInspeccion'));
        lst = lst.flatMap(obj => obj.widgetContext.viewContext.statefulWidgets);
        lst = lst.filter(obj => obj.inputId.startsWith('b_conforme')); //lista de inputwidget
        //lst = lst.apply(obj => obj.inputView.check(valor));
        var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
        //component.setRenderExpression(valueExpressionFactory.getInstance().create(lst.size()==0));*/
    }

    function updateResponsable(c_accion_singra_id, nombre_responsable, apellidos_responsable){
        //console.log("updateResponsable");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);
        //console.log("nombre_responsable::: "+nombre_responsable);
        //console.log("apellidos_responsable::: "+apellidos_responsable);
        updateUsuario(c_accion_singra_id, "d_repr_nombre_instal", "d_repr_apellidos_instal", "d_firma_entidad", nombre_responsable, apellidos_responsable, "responsable", "fdo_responsable");
    }

    function updateInspector1(c_accion_singra_id, nombre_inspector, apellidos_inspector){
        //console.log("updateInspector1");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);
        //console.log("nombre_inspector::: "+nombre_inspector);
        //console.log("apellidos_inspector::: "+apellidos_inspector);
        updateUsuario(c_accion_singra_id, "d_nombre_inspector1", "d_apellidos_inspector1", "d_firma_t1", nombre_inspector, apellidos_inspector, "idTecnico1", "fdo_idTecnico1");
    }

    function updateInspector2(c_accion_singra_id, nombre_inspector, apellidos_inspector){
        //console.log("updateInspector2");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);
        //console.log("nombre_inspector::: "+nombre_inspector);
        //console.log("apellidos_inspector::: "+apellidos_inspector);
        updateUsuario(c_accion_singra_id, "d_nombre_inspector2", "d_apellidos_inspector2", "d_firma_t2", nombre_inspector, apellidos_inspector, "idTecnico2", "fdo_idTecnico2");
    }
    function updateInspector3(c_accion_singra_id, nombre_inspector, apellidos_inspector){
        //console.log("updateInspector3");
        //console.log("c_accion_singra_id::: "+c_accion_singra_id);
        //console.log("nombre_inspector::: "+nombre_inspector);
        //console.log("apellidos_inspector::: "+apellidos_inspector);
        updateUsuario(c_accion_singra_id, "d_nombre_inspector3", "d_apellidos_inspector3", "d_firma_t3", nombre_inspector, apellidos_inspector, "idTecnico3", "fdo_idTecnico3");
    }

    function updateUsuario(c_accion_singra_id, str_nombre, str_apellidos, str_firma, nombre_usuario, apellidos_usuario, str_tecnico, str_fdo_tecnico){
        //console.log("updateUsuario");
        if (nombre_usuario!=null || apellidos_usuario!=null){
            var nombre = '';
            if (nombre_usuario!=null){
                nombre = nombre_usuario;
            }
            var apellidos = '';
            if (apellidos_usuario!=null){
                apellidos = apellidos_usuario;
            }
            var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
            entityInspeccion = inspeccionRepo.findById(c_accion_singra_id);
            entityInspeccion.set(str_nombre,nombre);
            entityInspeccion.set(str_apellidos,apellidos);
            entityInspeccion.set(str_firma,nombre+ " " +apellidos);
            entityInspeccion.set("c_usuario_modif",nombre+ " " +apellidos);
            inspeccionRepo.save(entityInspeccion);
            var widgetInspector = vh.widget(str_tecnico);
            //console.log("str_tecnico::: "+str_tecnico+"= "+nombre+ " " +apellidos);
            if (widgetInspector != null){
                widgetInspector.setValue(nombre+ " " +apellidos);
            }
            var widgetFdoInspector = vh.widget(str_fdo_tecnico);
            //console.log("str_fdo_tecnico::: "+str_fdo_tecnico+"= "+nombre+ " " +apellidos);
            if (widgetFdoInspector != null){
                widgetFdoInspector.setValue(nombre + " " + apellidos);
            }
        }
    }

    function getCurrentDateFormatted(){
        //console.log("getCurrentDateFormatted");
        var now = new Date();
        var year = now.getFullYear().toString();
        var month = (now.getMonth() + 1).toString().padStart(2, '0');
        var day = now.getDate().toString().padStart(2, '0');
        var hour = now.getHours().toString().padStart(2, '0');
        var minute = now.getMinutes().toString().padStart(2, '0');
        var currentDateFormatted = year+month+day+'_'+hour+minute;
        return currentDateFormatted;
    }

    function setDFichero(c_accion_singra_id) {
        //console.log("setDFichero");
         var currentDateFormatted = getCurrentDateFormatted();
        //console.log("currentDateFormatted:: "+currentDateFormatted);
        var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
        entityInspeccion = inspeccionRepo.findById(c_accion_singra_id);
        //console.log("b_telefonico::: "+entityInspeccion.get('b_telefonico'));
        entityInspeccion.set("d_fichero","Inspeccion_"+c_accion_singra_id+"_"+currentDateFormatted+".pdf");
        inspeccionRepo.save(entityInspeccion);
   }

    function setDFicheroPrevia(c_accion_singra_id) {
           //console.log("setDFichero");
           var currentDateFormatted = getCurrentDateFormatted();
           //console.log("currentDateFormatted:: "+currentDateFormatted);
           var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
           entityInspeccion = inspeccionRepo.findById(c_accion_singra_id);
           entityInspeccion.set("d_fichero_prev","Inspeccion_"+c_accion_singra_id+"_"+currentDateFormatted+"_PREV.pdf");
           inspeccionRepo.save(entityInspeccion);
      }

   function setFirmaEntidad(c_accion_singra_id, firma_entidad) {
       //console.log("setFirmaEntidad");
       var inspeccionRepo = ctx.get("repos").get("inspeccionRepo");
       entityInspeccion = inspeccionRepo.findById(c_accion_singra_id);
       entityInspeccion.set("firma_entidad",firma_entidad);
       inspeccionRepo.save(entityInspeccion);
  }
























