 function mixRepoData (){
    // get memo repository
    var provs = [5,9,24,34,37,40,42,47,49]
    var repo = ctx.get("repos").get("resumenIncis");
    var repoProv = ctx.get("repos").get("provRepo");
    for (var i=0; i < 10; i++){
        var entity = repo.newEntity();
        entity.set("f1",Math.random());
        var prov = repoProv.findById(provs[i]);
        
        if(prov != null){
            entity.set("f2",prov.get("name"))
        }
        repo.save(entity);

    }
}