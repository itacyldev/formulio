package es.jcyl.ita.frmdrd.dao.sources;

import java.util.List;

import es.jcyl.ita.frmdrd.dao.persister.Entity;

public class DBSourceDescriptor implements SourceDescriptor{

    private String path;
    private String user;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public List<Entity> getEntities() {
        return null;
    }
}
