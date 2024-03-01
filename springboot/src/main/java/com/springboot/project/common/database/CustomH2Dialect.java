package com.springboot.project.common.database;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.H2Dialect;

/**
 * In order to use the ifnull method when selecting
 * 
 * @author zdu
 *
 */
public class CustomH2Dialect extends H2Dialect {

    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
        functionRegistry.register("IFNULL", new StandardSQLFunction("IFNULL", StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("FOUND_ROWS", "COUNT(*) OVER()",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("IS_SORT_AT_BEFORE", "?1 < ?2",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern("LOCATE", "LOCATE(?1, ?2)",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss.ff3')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR",
                "TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(?1 AS NUMERIC(65, 4))",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS CHARACTER VARYING)",
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
        var tmpColumnNameAlias = tmpTableNameAlias + "_level";
        var getAncestorCountBuilder = new StringBuilder();
        getAncestorCountBuilder.append("(");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("WITH RECURSIVE `cte`(`id`, `" + tmpColumnNameAlias + "`) AS (");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("SELECT `id`, 0 as `" + tmpColumnNameAlias + "`");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("FROM `" + tableName + "`");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("WHERE `parent_id` IS NULL");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("UNION ALL");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`");
        getAncestorCountBuilder.append(",");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder
                .append("(`cte`.`" + tmpColumnNameAlias + "` + 1) as `" + tmpColumnNameAlias + "`");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`parent_id`");
        getAncestorCountBuilder.append(" ");

        for (var condition : conditions) {
            if (!condition.startsWith("`")) {
                throw new RuntimeException("condition must start with \"`\"");
            }
            getAncestorCountBuilder.append("AND `" + tmpTableNameAlias + "`." + condition + " ");
        }
        getAncestorCountBuilder.append(")");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder
                .append("SELECT IFNULL(SUM(`cte`.`" + tmpTableNameAlias
                        + "_level`), 0) as total_record FROM `cte` WHERE ?1 = `cte`.`id`");
        getAncestorCountBuilder.append(" ");
        getAncestorCountBuilder.append(")");
        return getAncestorCountBuilder.toString();
    }

    private String getDescendantCount(String tableName, String... conditions) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var tmpColumnNameAlias = tmpTableNameAlias + "_concat_id";
        var getDescendantCountBuilder = new StringBuilder();
        getDescendantCountBuilder.append("(");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WITH RECURSIVE `cte`(`id`, `" + tmpColumnNameAlias + "`) AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder
                .append("SELECT `id`, CONCAT(',', CONCAT(`id`, ',')) as `" + tmpColumnNameAlias + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `" + tableName + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE `parent_id` IS NULL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`, CONCAT(`cte`.`" + tmpColumnNameAlias
                + "`, " + "`" + tmpTableNameAlias + "`.`id`" + ", ',') as `"
                + tmpColumnNameAlias
                + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`parent_id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("AND `" + tmpTableNameAlias + "`.`is_deleted` = false");
        getDescendantCountBuilder.append(" ");

        for (var condition : conditions) {
            if (!condition.startsWith("`")) {
                throw new RuntimeException("condition must start with \"`\"");
            }
            getDescendantCountBuilder.append("AND `" + tmpTableNameAlias + "`." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder
                .append("SELECT COUNT(*) as total_record FROM `cte` WHERE LOCATE( CONCAT(',', CONCAT(?1, ',')), `cte`.`"
                        + tmpColumnNameAlias + "`) > 0 AND ?1 != `cte`.`id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(")");
        return getDescendantCountBuilder.toString();
    }

    private String isChild(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var tmpColumnNameAlias = tmpTableNameAlias + "_concat_id";
        var isChildBuilder = new StringBuilder();
        isChildBuilder.append("EXISTS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("WITH RECURSIVE `cte`(`id`, `" + tmpColumnNameAlias + "`) AS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `id`, CONCAT(',', CONCAT(`id`, ',')) as `" + tmpColumnNameAlias + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `" + tableName + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE `parent_id` IS NULL");
        isChildBuilder.append(" ");
        isChildBuilder.append("UNION ALL");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`, CONCAT(`cte`.`" + tmpColumnNameAlias
                + "`, CONCAT(" + "`" + tmpTableNameAlias + "`.`id`" + ", ',')) as `"
                + tmpColumnNameAlias
                + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`parent_id`");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        isChildBuilder.append(" ");
        isChildBuilder
                .append("SELECT * FROM `cte`");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE");
        isChildBuilder.append(" ");
        isChildBuilder.append("`cte`.`id` = ?1");
        isChildBuilder.append(" ");
        isChildBuilder.append("AND");
        isChildBuilder.append(" ");
        isChildBuilder.append("LOCATE( CONCAT(',', CONCAT(?2, ',')), `cte`.`"
                + tmpColumnNameAlias
                + "`) > 0");
        isChildBuilder.append(" ");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        return isChildBuilder.toString();
    }

    private String isNotDeleted(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var tmpColumnNameAlias = tmpTableNameAlias + "_concat_id";
        var isChildBuilder = new StringBuilder();
        isChildBuilder.append("EXISTS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("WITH RECURSIVE `cte`(`id`, `" + tmpColumnNameAlias + "`) AS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `id`, CONCAT(',' , CONCAT(`id`, ',')) as `" + tmpColumnNameAlias + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `" + tableName + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE `parent_id` IS NULL");
        isChildBuilder.append(" ");
        isChildBuilder.append("AND `is_deleted` = false");
        isChildBuilder.append(" ");
        isChildBuilder.append("UNION ALL");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`, CONCAT(`cte`.`" + tmpColumnNameAlias
                + "`, CONCAT(" + "`" + tmpTableNameAlias + "`.`id`" + ", ',')) as `"
                + tmpColumnNameAlias
                + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`parent_id`");
        isChildBuilder.append(" ");
        isChildBuilder.append("AND `" + tmpTableNameAlias + "`.`is_deleted` = false");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        isChildBuilder.append(" ");
        isChildBuilder
                .append("SELECT * FROM `cte` WHERE LOCATE( CONCAT(',', CONCAT(?1, ',')), `cte`.`"
                        + tmpColumnNameAlias + "`) > 0");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        return isChildBuilder.toString();
    }

    public CustomH2Dialect() {
        super();
    }

    public CustomH2Dialect(DatabaseVersion version) {
        super(version);
    }

    public CustomH2Dialect(DialectResolutionInfo info) {
        super(info);
    }
}