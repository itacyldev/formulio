package es.jcyl.ita.frmdrd.dao.engine.jdbc;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import es.jcyl.ita.fwk4.base.dao.PaginatedList;
import es.jcyl.ita.uqserv.jobs.model.JobExecutionMode;
import es.jcyl.ita.uqserv.qengine.datasource.DataSourceLocator;

package es.jcyl.ita.uqserv.qengine;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * @since 15/10/2019.
 */

public class QueryEngineSpring {

    private enum DB_PROVIDER {
        SQLITE, ORACLE
    };

    private static final String SQL_PAGINACION_INI = "SELECT * from ( SELECT ROWNUM rn, c.* FROM (";
    private static final String SQL_PAGINACION_FIN = ") c) WHERE  rn BETWEEN ? AND ?";
    private static final String SQLLITE_PAGINACION_END = " LIMIT ? OFFSET ?";

    private static final String SQL_NAMED_PAGINACION_FIN = ") c) WHERE  rn BETWEEN :pgini AND :pgend";
    private static final String SQLLITE_NAMED_PAGINACION_FIN = " LIMIT :pageSize OFFSET :offset";

    private static final String SQL_COUNT_INI = "SELECT COUNT(1) FROM ( ";
    private static final Object SQL_COUNT_END = " ) ";

    private static final Log LOGGER = LogFactory.getLog(JdbcQueryEngine.class);

    Map<String, JdbcTemplate> templates = new HashMap<String, JdbcTemplate>();
    Map<String, NamedParameterJdbcTemplate> namedTemplates = new HashMap<String, NamedParameterJdbcTemplate>();

    private JobExecutionMode executionMode = JobExecutionMode.FG;

    public JdbcQueryEngine() {
    }

    public int executeUpdate(String sql, String jndiName,
                             Map<String, Object> params) {
        assert (StringUtils
                .isNotBlank(sql)) : "El parámetro sql no puede ser nulo";
        assert (StringUtils.isNotBlank(
                jndiName)) : "El parámetro jndiName no puede ser nulo";
        assert (params != null) : "El array de parámetros no puede ser nulo.";

        NamedParameterJdbcTemplate tmt = getNamedTemplate(jndiName);
        return tmt.update(sql, params);
    }

    public int executeUpdate(String sql, String jndiName, Object[] params) {
        assert (StringUtils
                .isNotBlank(sql)) : "El parámetro sql no puede ser nulo";
        assert (StringUtils.isNotBlank(
                jndiName)) : "El parámetro jndiName no puede ser nulo";
        assert (params != null) : "El array de parámetros no puede ser nulo.";

        JdbcTemplate tmt = getTemplate(jndiName);
        return tmt.update(sql, params);
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName) {
        return execute(query, jndiName, new Object[] {}, -1, 0);
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName, int pageSize, int offset) {
        return execute(query, jndiName, new Object[] {}, pageSize, offset);
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName, Object[] params) {
        return execute(query, jndiName, params, -1, 0);
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName, Object[] params, int pageSize, int offset) {
        assert (StringUtils
                .isNotBlank(query)) : "El parámetro query no puede ser nulo";
        assert (StringUtils.isNotBlank(
                jndiName)) : "El parámetro jndiName no puede ser nulo";
        assert (params != null) : "El array de parámetros no puede ser nulo.";

        JdbcTemplate tmt = getTemplate(jndiName);
        DB_PROVIDER dbProvider = detectProvider(jndiName);

        PaginatedList retorno = new PaginatedList();
        List<Map<String, Object>> resultSet = null;

        int totalResult = 0;
        LOGGER.debug(query);
        if (pageSize != -1) {
            totalResult = count(query, jndiName, params);

            StringBuilder sqlPag = new StringBuilder(query);
            ArrayList<Object> combinedArray = new ArrayList(
                    Arrays.asList(params));

            setPaginationFilter(sqlPag, offset, pageSize, combinedArray,
                    dbProvider);

            // execute query
            resultSet = tmt.queryForList(sqlPag.toString(),
                    combinedArray.toArray(new Object[0]));

        } else {
            resultSet = tmt.queryForList(query, params);
            totalResult = resultSet.size();
        }

        // crear lista paginada a partir de resultset
        retorno.setPaginationInfo(offset, pageSize, totalResult);
        retorno.addAll(resultSet);

        return retorno;
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName, Map<String, Object> params) {
        return execute(query, jndiName, params, -1, 0);
    }

    public PaginatedList<Map<String, Object>> execute(String query,
                                                      String jndiName, Map<String, Object> params, int pageSize,
                                                      int offset) {
        assert (StringUtils
                .isNotBlank(query)) : "El parámetro query no puede ser nulo";
        assert (StringUtils.isNotBlank(
                jndiName)) : "El parámetro jndiName no puede ser nulo";
        assert (params != null) : "El mapa de parámetros no puede ser nulo.";

        NamedParameterJdbcTemplate tmt = getNamedTemplate(jndiName);
        DB_PROVIDER dbProvider = detectProvider(jndiName);

        PaginatedList retorno = new PaginatedList();
        List<Map<String, Object>> resultSet = null;

        int totalResult = 0;
        LOGGER.debug(query);
        // copiar mapa de parámetros
        Map<String, Object> queryParams = new HashMap<String, Object>(params);

        if (pageSize != -1) {
            totalResult = count(query, jndiName, queryParams);
            StringBuilder sqlPag = new StringBuilder(query);

            setNamedPaginationFilter(sqlPag, offset, pageSize, queryParams,
                    dbProvider);

            resultSet = tmt.queryForList(sqlPag.toString(), queryParams);

        } else {
            resultSet = tmt.queryForList(query, queryParams);

            totalResult = resultSet.size();
        }

        // crear lista paginada a partir de resultset
        retorno.setPaginationInfo(offset, pageSize, totalResult);
        retorno.addAll(resultSet);

        return retorno;
    }

    private DB_PROVIDER detectProvider(String jndiName) {
        // TODO Auto-generated method stub
        if (jndiName.contains("sqlite")) {
            return DB_PROVIDER.SQLITE;
        } else {
            return DB_PROVIDER.ORACLE;
        }
    }

    private void setPaginationFilter(StringBuilder sqlPag, int offset,
                                     int pageSize, ArrayList<Object> combinedArray,
                                     DB_PROVIDER provider) {
        if (provider == DB_PROVIDER.ORACLE) {
            sqlPag.insert(0, SQL_PAGINACION_INI);
            sqlPag.append(SQL_PAGINACION_FIN);

            // create parameter array
            combinedArray.addAll(Arrays.asList(
                    new Object[] { (offset + 1), (offset + pageSize) }));
        } else {
            sqlPag.append(SQLLITE_PAGINACION_END);
            // create parameter array
            combinedArray.addAll(
                    Arrays.asList(new Object[] { pageSize, (offset + 1) }));
        }
        combinedArray.toArray(new Object[0]);
    }

    private void setNamedPaginationFilter(StringBuilder sqlPag, int offset,
                                          int pageSize, Map<String, Object> queryParams,
                                          DB_PROVIDER provider) {
        if (provider == DB_PROVIDER.ORACLE) {
            sqlPag.insert(0, SQL_PAGINACION_INI);
            sqlPag.append(SQL_NAMED_PAGINACION_FIN);

            // parámetros de paginación nombrados
            queryParams.put("pgini", (offset + 1));
            queryParams.put("pgend", (offset + pageSize));

        } else {
            sqlPag.append(SQLLITE_NAMED_PAGINACION_FIN);

            // parámetros de paginación nombrados
            queryParams.put("pageSize", pageSize);
            queryParams.put("offset", (offset + 1));
        }
    }

    protected int count(String query, String jndiName, Object[] params) {
        JdbcTemplate jdbcTemplate = getTemplate(jndiName);

        StringBuilder countSQL = new StringBuilder(SQL_COUNT_INI);
        countSQL.append(query).append(SQL_COUNT_END);

        int numResults = jdbcTemplate.queryForObject(countSQL.toString(),
                params, Integer.class);

        return numResults;
    }

    protected int count(String query, String jndiName,
                        Map<String, Object> params) {
        NamedParameterJdbcTemplate jdbcTemplate = getNamedTemplate(jndiName);

        StringBuilder countSQL = new StringBuilder(SQL_COUNT_INI);
        countSQL.append(query).append(SQL_COUNT_END);

        int numResults = jdbcTemplate.queryForObject(countSQL.toString(),
                params, Integer.class);

        return numResults;
    }

    /**
     * Creación de plantilla que permite el uso de parámetros identificados por
     * nombre
     *
     * @param jndiName
     * @return
     */
    private NamedParameterJdbcTemplate getNamedTemplate(String jndiName) {
        if (!this.namedTemplates.containsKey(jndiName.toUpperCase())) {
            DataSource ds = DataSourceLocator.getDataSource(jndiName,
                    this.executionMode);
            NamedParameterJdbcTemplate tmt = new NamedParameterJdbcTemplate(ds);
            this.namedTemplates.put(jndiName.toUpperCase(), tmt);
        }
        return this.namedTemplates.get(jndiName.toUpperCase());
    }

    private JdbcTemplate getTemplate(String jndiName) {
        if (!this.templates.containsKey(jndiName.toUpperCase())) {
            DataSource ds = DataSourceLocator.getDataSource(jndiName,
                    this.executionMode);
            JdbcTemplate tmt = new JdbcTemplate(ds);
            this.templates.put(jndiName.toUpperCase(), tmt);
        }
        return this.templates.get(jndiName.toUpperCase());
    }

    public JobExecutionMode getExecutionMode() {
        return this.executionMode;
    }

    public void setExecutionMode(JobExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

}
