    function createRecinto(component){
        var pk_uid = view.get("recinto_id");
        var c_poligono = view.get("c_poligono");
        var c_parcela = view.get("c_parcela");
        var c_recinto = view.get("c_recinto");
        var acta_id = new java.lang.Long(view.get("acta_id"));
        //console.log("pk_uid == "+pk_uid);
        //console.log("acta_id == "+acta_id);

        var recintoRepo = ctx.get("repos").get("recintoRepo");

        var lstRecintos = recintoRepo.listAll();
        var i = 0;
        var encontrado = 0;
        while(i != lstRecintos.size()){
            var entityRecinto = lstRecintos.get(i);
            var pk_uid_bbdd = new java.lang.Long(entityRecinto.get("pk_uid"));
            var acta_id_bbdd = new java.lang.Long(entityRecinto.get("acta_id"));
            //console.log("pk_uid_bbdd == "+pk_uid_bbdd);
            //console.log("acta_id_bbdd == "+acta_id_bbdd);
            if (pk_uid_bbdd == pk_uid && acta_id_bbdd == acta_id){
                encontrado = 1;
            }
            i++;
        }

        if (encontrado == 0){
            //console.log("entityRecinto == null");
            var entityRecinto = recintoRepo.newEntity();
            entityRecinto.set("recinto_id", pk_uid);
            entityRecinto.set("pk_uid", pk_uid);
            entityRecinto.set("acta_id", acta_id);
            entityRecinto.set("c_poligono", c_poligono);
            entityRecinto.set("c_parcela", c_parcela);
            entityRecinto.set("c_recinto", c_recinto);
            recintoRepo.save(entityRecinto);
        }
    }

