var repoAccess = ctx.get("repos");

var repos = ctx.get("repos");
var lstRepos = repos.getRepos();
var repoIds = repos.getRepoIds();

var contactsRepo = repos.get("contacts");

// consulta
var lstContacts = contactsRepo.listAll();
out.println(lstContacts.size());
var entity = lstContacts.get(0);

myfunct(entity)

function myfunct(e){
    var meta = e.getMetadata();
    var str = "Entity type: " + meta.getName();
    str += "\nEntity has these properties: " + meta.getPropertyNames().join(", ");
    str += "\nWith Id: : "+ e.getId();
    str += "\n___________________________"
    out.println(str);
}
