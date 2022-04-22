package org.schemaspy.util;

import org.schemaspy.Config;
import org.schemaspy.DbAnalyzer;
import org.schemaspy.model.Database;
import org.schemaspy.model.ForeignKeyConstraint;
import org.schemaspy.model.ImpliedForeignKeyConstraint;
import org.schemaspy.model.Table;
import org.schemaspy.view.MustacheCatalog;
import org.schemaspy.view.MustacheSchema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class AnalyzerUtils {

    private AnalyzerUtils() {
        // Util class
    }

    public static Map<String, Object> buildParametersMap(Database database, Collection<Table> tables, List<ImpliedForeignKeyConstraint> impliedConstraints) {
        final Map<String, Object> resultMap = new HashMap<>();

        final Config config = Config.getInstance();

        resultMap.put("database", database);
        resultMap.put("schema", new MustacheSchema(database.getSchema(), ""));
        resultMap.put("catalog", new MustacheCatalog(database.getCatalog(), ""));
        resultMap.put("xmlName", getXmlName(database));

        resultMap.put("tablesAmount", tables.stream().filter(t -> !t.isView()).count());
        resultMap.put("viewsAmount", tables.stream().filter(Table::isView).count());
        resultMap.put("constraintsAmount", DbAnalyzer.getForeignKeyConstraints(tables).size());
        resultMap.put("routinesAmount", database.getRoutines().size());
        resultMap.put("anomaliesAmount", getAllAnomaliesAmount(tables, impliedConstraints));

        resultMap.put("hideAnomalies", config.isHideAnomaliesEnabled());
        resultMap.put("hideOrphans", config.isHideOrphanTablesEnabled());

        return Collections.unmodifiableMap(resultMap);
    }

    private static long getAllAnomaliesAmount(Collection<Table> tables, List<? extends ForeignKeyConstraint> impliedConstraints) {
        long anomalies = DbAnalyzer.getTablesWithoutIndexes(new HashSet<>(tables)).size();

        anomalies += impliedConstraints.stream().filter(c -> !c.getChildTable().isView()).count();
        anomalies += DbAnalyzer.getTablesWithOneColumn(tables).stream().filter(t -> !t.isView()).count();
        anomalies += DbAnalyzer.getTablesWithIncrementingColumnNames(tables).stream().filter(t -> !t.isView()).count();
        anomalies += DbAnalyzer.getDefaultNullStringColumns(new HashSet<>(tables)).size();

        return anomalies;
    }

    private static String getXmlName(Database db) {
        StringBuilder description = new StringBuilder();

        description.append(db.getName());
        if (db.getSchema() != null) {
            description.append('.');
            description.append(db.getSchema().getName());
        } else if (db.getCatalog() != null) {
            description.append('.');
            description.append(db.getCatalog().getName());
        }

        return description.toString();
    }


}
