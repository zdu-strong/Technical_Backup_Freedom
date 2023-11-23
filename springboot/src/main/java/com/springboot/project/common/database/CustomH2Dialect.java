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
        functionRegistry.register("IFNULL", new StandardSQLFunction("IFNULL", StandardBasicTypes.LONG));
        functionRegistry.registerPattern("FOUND_ROWS", "COUNT(*) OVER()",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("IS_SORT_AT_BEFORE", "?1 < ?2",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern("LOCATE", "LOCATE(?2, ?1)",
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
                "GET_DESCENDANT_COUNT_OF_ORGANIZE",
                getDescendantCount("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
    }

    private String getDescendantCount(String tableName, String... conditions) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var getDescendantCountBuilder = new StringBuilder();
        getDescendantCountBuilder.append("(");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WITH RECURSIVE `cte`(`id`, `" + tmpTableNameAlias + "_concat_id`) AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `id`, CONCAT(`id`, ',') as `" + tmpTableNameAlias + "_concat_id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `" + tableName + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE `parent_id` IS NULL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`, CONCAT(`cte`.`" + tmpTableNameAlias
                + "_concat_id`, " + "`" + tmpTableNameAlias + "`.`id`" + ", ',') as `"
                + tmpTableNameAlias
                + "_concat_id`");
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
            getDescendantCountBuilder.append("`" + tmpTableNameAlias + "`." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder
                .append("SELECT COUNT(*) as total_record FROM `cte` WHERE LOCATE( CONCAT(?1, ','), `cte`.`"
                        + tmpTableNameAlias + "_concat_id`) > 0 AND ?1 != `cte`.`id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(")");
        return getDescendantCountBuilder.toString();
    }

    private String isChild(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var maxRecursionLevel = 20;
        var isChildBuilder = new StringBuilder();
        isChildBuilder.append("EXISTS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `" + tableName + "` `" + tmpTableNameAlias + "`");
        isChildBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = tmpTableNameAlias + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = tmpTableNameAlias + "_" + i;
            isChildBuilder.append("LEFT JOIN  `" + tableName + "` `" + secondTableName + "`");
            isChildBuilder.append(" ");
            isChildBuilder
                    .append("ON `" + firstTableName + "`.`parent_id` = `" + secondTableName + "`.`id`");
            isChildBuilder.append(" ");
        }
        isChildBuilder.append("WHERE `" + tmpTableNameAlias + "`.`id` = ?1");
        isChildBuilder.append(" ");
        isChildBuilder.append("AND");
        isChildBuilder.append(" ");
        isChildBuilder.append("?2");
        isChildBuilder.append(" ");
        isChildBuilder.append("IN");
        isChildBuilder.append(" ");
        isChildBuilder.append("(");
        isChildBuilder.append(" ");
        isChildBuilder.append("`" + tmpTableNameAlias + "`.`id`");
        isChildBuilder.append(",");
        isChildBuilder.append(" ");
        isChildBuilder.append("`" + tmpTableNameAlias + "`.`parent_id`");
        isChildBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = tmpTableNameAlias + "_" + i;
            isChildBuilder.append(",");
            isChildBuilder.append(" ");
            isChildBuilder.append("`" + secondTableName + "`.`parent_id`");
            isChildBuilder.append(" ");
        }
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        return isChildBuilder.toString();
    }

    private String isNotDeleted(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var maxRecursionLevel = 20;
        var isNotDeletedBuilder = new StringBuilder();
        isNotDeletedBuilder.append("EXISTS (");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("FROM `" + tableName + "` `" + tmpTableNameAlias + "`");
        isNotDeletedBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = tmpTableNameAlias + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = tmpTableNameAlias + "_" + i;
            isNotDeletedBuilder
                    .append("LEFT JOIN `" + tableName + "` `" + secondTableName + "`");
            isNotDeletedBuilder.append(" ");
            isNotDeletedBuilder
                    .append("ON `" + firstTableName + "`.`parent_id` = `" + secondTableName + "`.`id`");
            isNotDeletedBuilder.append(" ");
            isNotDeletedBuilder.append("AND");
            isNotDeletedBuilder.append(" ");
            isNotDeletedBuilder.append("`" + secondTableName + "`.`is_deleted` = false");
            isNotDeletedBuilder.append(" ");
        }
        isNotDeletedBuilder.append("WHERE `" + tmpTableNameAlias + "`.`id` = ?1");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("`" + tmpTableNameAlias + "`.`is_deleted` = false");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("'PARENT_ID_NULL'");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("IN");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("(");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("`" + tmpTableNameAlias + "`.`id`");
        isNotDeletedBuilder.append(",");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder
                .append("IFNULL(`" + tmpTableNameAlias + "`.`parent_id`, 'PARENT_ID_NULL')");
        isNotDeletedBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = tmpTableNameAlias + "_" + i;
            isNotDeletedBuilder.append(",");
            isNotDeletedBuilder.append(" ");
            isNotDeletedBuilder
                    .append("IFNULL(`" + secondTableName + "`.`parent_id`, (CASE WHEN `" + secondTableName
                            + "`.`id` IS NULL THEN 'CHILD_ID_NULL' ELSE 'PARENT_ID_NULL' END) )");
            isNotDeletedBuilder.append(" ");
        }
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        return isNotDeletedBuilder.toString();
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