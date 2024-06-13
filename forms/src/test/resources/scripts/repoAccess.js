var repoAccess = ctx.get("repos");

var repos = ctx.get("repos");
var lstRepos = repos.getRepos();
var repoIds = repos.getRepoIds();
//out.println(repoIds);
//out.println(lstRepos);

var contactsRepo = repos.get("contacts");

// ojo para acceder a packages no java. hay que poner prefijo "Packages"
// https://github.com/mozilla/rhino/blob/master/examples/liveConnect.js

// consulta
var lstContacts = contactsRepo.listAll();
out.println(lstContacts.size());
var entity = lstContacts.get(0);

// buscamos una entidad por el id
var loadedEntity = contactsRepo.findById(entity.getId())
myfunct(loadedEntity)
out.println(loadedEntity.get("first_name"))

// create entity
var newEntity = contactsRepo.newEntity();
newEntity.set("first_name", "mi primer entidad")
newEntity.set("last_name", "qué se yo!")
contactsRepo.save(newEntity)

// check
out.println("Antes. " + lstContacts.size());
out.println("Ahora. " + contactsRepo.count());




function myfunct(e){
    var meta = e.getMetadata();
    var str = "Entity type: " + meta.getName();
    str += "\nEntity has these properties: " + meta.getPropertyNames().join(", ");
    str += "\nWith Id: : "+ e.getId();
    str += "\n___________________________"
    out.println(str);
}
/*
// aceso a métodos de la lista:
out.println(lst.get(0))
out.println("Num records: " + lst.size())

// iteración sobre lista:
//lst.forEach(myfunct);

for(var i=0; i<lst.size() ; i++){
    //myfunct(lst.get(i));
}

// iterar sobre propiedades de un objeto:
//var entity = lst.get(0);
//for(var prop in entity){
//    out.println(prop)
//}
// ESTO NO FUNCOINA
//for(var prop of lst){
//    out.println(prop)
//}


// creamos una entidad

*/