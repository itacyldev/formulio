
    function setUnidadKG (){
        //console.log("setUnidadKG");

        var tipoMorcillaInstalacionRepo = ctx.get("repos").get("tipoMorcillaInstalacionRepo");

        var lstTipoMorcillaInstalacion = tipoMorcillaInstalacionRepo.listAll();
        //console.log("lstTipoMorcillaInstalacion.size(): " + lstTipoMorcillaInstalacion.size());

        var i = 0;
        while(i != lstTipoMorcillaInstalacion.size()){
            //console.log("i: " + i);
            var entityTipoMorcillaInstalacion = lstTipoMorcillaInstalacion.get(i);

            var peso_unitario_kg =  entityTipoMorcillaInstalacion.get("peso_unitario_kg");
            //console.log("peso_unitario_kg: " + peso_unitario_kg);
            if (peso_unitario_kg != 0){
                var unidad_kg = 1/peso_unitario_kg;
                //console.log("unidad_kg: " + unidad_kg);
                entityTipoMorcillaInstalacion.set("unidad_kg", unidad_kg);
                tipoMorcillaInstalacionRepo.save(entityTipoMorcillaInstalacion)
            }
            i++;
        }
    }

