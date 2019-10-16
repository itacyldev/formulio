package es.jcyl.ita.frmdrd.dao.persister;

import es.jcyl.ita.frmdrd.dao.persister.jdbc.ParsedQuery;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntityMeta {

    String name;
    String[] idColumns;
    String[] columnTypes;
    String[] columnNames;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getIdColumns() {
        return idColumns;
    }

    public void setIdColumns(String[] idColumns) {
        this.idColumns = idColumns;
    }

    public String[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(String[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
}
