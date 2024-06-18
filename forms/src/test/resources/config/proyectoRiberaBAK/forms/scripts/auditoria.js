 function addAuditoria (){
    var repo = ctx.get("repos").get("auditoriaRepo");
    var entity = repo.newEntity();
    var d = new Date();

    var date = new Date();
    var dateStr =
      ("00" + (date.getMonth() + 1)).slice(-2) + "/" +
      ("00" + date.getDate()).slice(-2) + "/" +
      date.getFullYear() + " " +
      ("00" + date.getHours()).slice(-2) + ":" +
      ("00" + date.getMinutes()).slice(-2) + ":" +
      ("00" + date.getSeconds()).slice(-2);

      entity.set("fecha", dateStr);

      repo.save(entity);
      var newAuditoria = ctx.get("newAuditoria");
      newAuditoria = entity.getId();
}

 function getAuditoria(component){
    var repo = ctx.get("repos").get("auditoriaRepo");
    var lstAuditorias = repo.listAll();
    console.log("lstAuditorias.size()::: " + lstAuditorias.size());
    var entityAuditoria = lstAuditorias.get(lstAuditorias.size() - 1);
    console.log("entityAuditoria.getId()::: " + entityAuditoria.getId());
    console.log("This is a component " + component.getId());
    var valueExpressionFactory = Packages.es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
    component.setValueExpression(valueExpressionFactory.getInstance().create(""+entityAuditoria.getId()));
 }

 function showObservaciones(widget){
    //console.log("This is a widget " + widget.getId());
    console.log("showObservaciones");
 }

/*function listPreguntaAuditoria (){
    var preguntaAuditoriaRepo = ctx.get("repos").get("preguntaAuditoriaRepo");
    var auditoriaRepo = ctx.get("repos").get("auditoriaRepo");

    var lstAuditorias = auditoriaRepo.listAll();
    var entityAuditoria = lstAuditorias.get(lstAuditorias.size() - 1);

    var listPreguntasAuditorias = preguntaAuditoriaRepo.findById(entityAuditoria.getId());
}*/
