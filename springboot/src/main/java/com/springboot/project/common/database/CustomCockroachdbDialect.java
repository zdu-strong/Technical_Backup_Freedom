package com.springboot.project.common.database;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.dialect.CockroachDialect;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.PostgreSQLDriverKind;

public class CustomCockroachdbDialect extends CockroachDialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
        functionRegistry.register("IFNULL", new StandardSQLFunction("COALESCE", StandardBasicTypes.LONG));
        functionRegistry.registerPattern("FOUND_ROWS", "COUNT(*) OVER()",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("IS_SORT_AT_BEFORE", "?1 < ?2",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern("LOCATE", "POSITION(?2 IN ?1)",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM-DD HH24:MI:SS.MS')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM-DD HH24:MI:SS')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM-DD HH24:MI')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM-DD HH24')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM-DD')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH",
                "to_char(?1 AT TIME ZONE ?2, 'YYYY-MM')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR", "to_char(?1 AT TIME ZONE ?2, 'YYYY')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(?1 AS DECIMAL)",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS TEXT)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("IS_NOT_DELETED_OF_ORGANIZE",
                isNotDeleted("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern(
                "IS_CHILD_OF_ORGANIZE",
                isChild("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern(
                "GET_ANCESTOR_COUNT_OF_ORGANIZE",
                getAncestorCount("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern(
                "GET_DESCENDANT_COUNT_OF_ORGANIZE",
                getDescendantCount("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
    }

    private String getAncestorCount(String tableName, String... conditions) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var getDescendantCountBuilder = new StringBuilder();
        getDescendantCountBuilder.append("(");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WITH RECURSIVE cte AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT id, parent_id");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM " + tableName + "");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE id = ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder
                .append("SELECT " + tmpTableNameAlias + ".id, " + tmpTableNameAlias + ".parent_id");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM cte INNER JOIN " + tableName + " " + tmpTableNameAlias + "");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("ON cte.parent_id = " + tmpTableNameAlias + ".id");
        getDescendantCountBuilder.append(" ");
        for (var condition : conditions) {
            if (!condition.startsWith("")) {
                throw new RuntimeException("condition must start with \"\"");
            }
            getDescendantCountBuilder.append("AND " + tmpTableNameAlias + "." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT COUNT(*) as total_record FROM cte");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE cte.id != ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(")");
        return getDescendantCountBuilder.toString();
    }

    private String getDescendantCount(String tableName, String... conditions) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var getDescendantCountBuilder = new StringBuilder();
        getDescendantCountBuilder.append("(");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WITH RECURSIVE cte AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT id");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM " + tableName + "");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE id = ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT " + tmpTableNameAlias + ".id");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM cte INNER JOIN " + tableName + " " + tmpTableNameAlias + "");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("ON cte.id = " + tmpTableNameAlias + ".parent_id");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("AND");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(tmpTableNameAlias + ".is_deleted = false");
        getDescendantCountBuilder.append(" ");
        for (var condition : conditions) {
            if (!condition.startsWith("")) {
                throw new RuntimeException("condition must start with \"\"");
            }
            getDescendantCountBuilder.append("AND " + tmpTableNameAlias + "." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT COUNT(*) as total_record FROM cte");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE cte.id != ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(")");
        return getDescendantCountBuilder.toString();
    }

    private String isChild(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var isChildBuilder = new StringBuilder();
        isChildBuilder.append("EXISTS");
        isChildBuilder.append(" ");
        isChildBuilder.append("(");
        isChildBuilder.append(" ");
        isChildBuilder.append("WITH RECURSIVE cte AS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT id");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM " + tableName + "");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE id = ?1");
        isChildBuilder.append(" ");
        isChildBuilder.append("UNION ALL");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT " + tmpTableNameAlias + ".parent_id as id");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM cte INNER JOIN " + tableName + " " + tmpTableNameAlias + "");
        isChildBuilder.append(" ");
        isChildBuilder.append("ON cte.id = " + tmpTableNameAlias + ".id");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT * FROM cte");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE cte.id = ?2");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        return isChildBuilder.toString();
    }

    private String isNotDeleted(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var isNotDeletedBuilder = new StringBuilder();
        isNotDeletedBuilder.append("EXISTS");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("(");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WITH RECURSIVE cte AS (");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT id");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("FROM " + tableName + "");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WHERE id = ?1");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("is_deleted = false");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("UNION ALL");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT IFNULL(" + tmpTableNameAlias + ".parent_id, 'NULL') as id");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("FROM cte INNER JOIN " + tableName + " " + tmpTableNameAlias + "");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("ON cte.id = " + tmpTableNameAlias + ".id");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("" + tmpTableNameAlias + ".is_deleted = false");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT * FROM cte");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WHERE cte.id = 'NULL'");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        return isNotDeletedBuilder.toString();
    }

    public CustomCockroachdbDialect() {
        super();
    }

    public CustomCockroachdbDialect(DialectResolutionInfo info) {
        super(info);
    }

    public CustomCockroachdbDialect(DialectResolutionInfo info, String versionString) {
        super(info, versionString);
    }

    public CustomCockroachdbDialect(DatabaseVersion version) {
        super(version);
    }

    public CustomCockroachdbDialect(DatabaseVersion version, PostgreSQLDriverKind driverKind) {
        super(version, driverKind);
    }

}
