package es.jcyl.ita.formic.core.context;


import java.util.Date;


public abstract class AbstractBaseContext implements Context {

    private Date creationDate;
    private String prefix;

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

}
