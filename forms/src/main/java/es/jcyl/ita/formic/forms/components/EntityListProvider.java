package es.jcyl.ita.frmdrd.ui.components;

import java.util.List;

import es.jcyl.ita.crtrepo.Entity;

public interface EntityListProvider {

    public void setEntities(List<Entity> entities);

    public List<Entity> getEntities();

}
