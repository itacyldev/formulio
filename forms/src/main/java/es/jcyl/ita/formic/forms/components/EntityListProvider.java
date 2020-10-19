package es.jcyl.ita.formic.forms.components;

import java.util.List;

import es.jcyl.ita.formic.repo.Entity;


public interface EntityListProvider {

    public void setEntities(List<Entity> entities);

    public List<Entity> getEntities();

}
