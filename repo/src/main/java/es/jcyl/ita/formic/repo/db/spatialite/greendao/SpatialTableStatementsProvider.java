package es.jcyl.ita.formic.repo.db.spatialite.greendao;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.TableStatements;

import es.jcyl.ita.formic.repo.db.sqlite.greendao.TableStatementProvider;
import es.jcyl.ita.formic.repo.db.sqlite.sql.SQLBuilder;
import es.jcyl.ita.formic.repo.meta.EntityMeta;

/**
 * Provides an Statement using EntityMeta information. Solves the problem of SQL specific creation
 * for the Spatialite queries.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class SpatialTableStatementsProvider extends TableStatements implements TableStatementProvider {

    private EntityMeta meta;
    private Database db;

    public SpatialTableStatementsProvider() {
        super(null, null, null, null);
    }

    private SpatialTableStatementsProvider(Database db, EntityMeta meta) {
        super(db, meta.getName(), meta.getPropertyNames(), meta.getIdPropertiesName());
        this.meta = meta;
        this.db = db;
    }

    public DatabaseStatement getInsertOrReplaceStatement() {
        if (this.insertOrReplaceStatement == null) {
//            String sql = SqlUtils.createSqlInsert("INSERT OR REPLACE INTO ", tablename, this.allColumns);
            String sql = SQLBuilder.createInsertInto("INSERT OR REPLACE INTO ", meta);
            DatabaseStatement newInsertOrReplaceStatement = this.db.compileStatement(sql);
            synchronized (this) {
                if (this.insertOrReplaceStatement == null) {
                    this.insertOrReplaceStatement = newInsertOrReplaceStatement;
                }
            }
            if (this.insertOrReplaceStatement != newInsertOrReplaceStatement) {
                newInsertOrReplaceStatement.close();
            }
        }
        return this.insertOrReplaceStatement;
    }

    @Override
    public void setDB(Database db) {
        this.db = db;
    }

    @Override
    public void setEntityMeta(EntityMeta meta) {
        this.meta = meta;
    }

    @Override
    public TableStatements build() {
        // the provider acts
        return new SpatialTableStatementsProvider(this.db, this.meta);
    }
}
