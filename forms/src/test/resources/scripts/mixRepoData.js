


function mixRepoData (){
    // get memo repository
    var repo = ctx.get("repos").get("memoRepoTest");

    for (var i=0; i< 10; i++){
        var entity = repo.newEntity();
        entity.set("f1",Math.random());
        repo.save(entity);
    }
}
