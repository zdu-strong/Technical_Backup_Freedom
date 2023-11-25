package com.springboot.project.common.database;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.MySQLServerConfiguration;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

/**
 * In order to use the ifnull method when selecting. In order to use the
 * found_rows method to get the total number of items in group by.
 * 
 * @author zdu
 *
 */
public class CustomMySQLDialect extends MySQLDialect {

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
                "SUBSTRING(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s.%f'), 1, 23)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR_MONTH",
                "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("FORMAT_DATE_AS_YEAR", "DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y')",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(?1 AS DECIMAL(65,4))",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS NCHAR)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
        functionRegistry.registerPattern("IS_NOT_DELETED_OF_ORGANIZE",
                isNotDeleted("organize_entity"),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern(
                "IS_CHILD_OF_ORGANIZE",
                isChild("organize_entity"),
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
        getDescendantCountBuilder.append("WITH RECURSIVE `cte` AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `id`, `parent_id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `" + tableName + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE `id` = ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`, `"+tmpTableNameAlias+"`.`parent_id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("ON `cte`.`parent_id` = `" + tmpTableNameAlias + "`.`id`");
        getDescendantCountBuilder.append(" ");
        for (var condition : conditions) {
            if (!condition.startsWith("`")) {
                throw new RuntimeException("condition must start with \"`\"");
            }
            getDescendantCountBuilder.append("AND `" + tmpTableNameAlias + "`." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT COUNT(*) as total_record FROM `cte`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append(")");
        return getDescendantCountBuilder.toString();
    }

    private String getDescendantCount(String tableName, String... conditions) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var getDescendantCountBuilder = new StringBuilder();
        getDescendantCountBuilder.append("(");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WITH RECURSIVE `cte` AS (");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `" + tableName + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("WHERE `id` = ?1");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("UNION ALL");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT `" + tmpTableNameAlias + "`.`id`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`parent_id`");
        getDescendantCountBuilder.append(" ");
        for (var condition : conditions) {
            if (!condition.startsWith("`")) {
                throw new RuntimeException("condition must start with \"`\"");
            }
            getDescendantCountBuilder.append("AND `" + tmpTableNameAlias + "`." + condition + " ");
        }
        getDescendantCountBuilder.append(")");
        getDescendantCountBuilder.append(" ");
        getDescendantCountBuilder.append("SELECT COUNT(*) as total_record FROM `cte`");
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
        isChildBuilder.append("WITH RECURSIVE `cte` AS (");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `id`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `" + tableName + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE `id` = ?1");
        isChildBuilder.append(" ");
        isChildBuilder.append("UNION ALL");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT `" + tmpTableNameAlias + "`.`parent_id` as `id`");
        isChildBuilder.append(" ");
        isChildBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        isChildBuilder.append(" ");
        isChildBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`id`");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        isChildBuilder.append(" ");
        isChildBuilder.append("SELECT * FROM `cte`");
        isChildBuilder.append(" ");
        isChildBuilder.append("WHERE `cte`.`id` = ?2");
        isChildBuilder.append(" ");
        isChildBuilder.append(")");
        return isChildBuilder.toString();
    }

    private String isNotDeleted(String tableName) {
        var tmpTableNameAlias = tableName + "_tmp_alias";
        var isNotDeletedBuilder = new StringBuilder();
        isNotDeletedBuilder.append("'EXISTS'");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("(");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WITH RECURSIVE `cte` AS (");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT `id`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("FROM `" + tableName + "`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WHERE `id` = ?1");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("`is_deleted` = false");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("UNION ALL");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT IFNULL(`" + tmpTableNameAlias + "`.`parent_id`, 'NULL') as `id`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("FROM `cte` INNER JOIN `" + tableName + "` `" + tmpTableNameAlias + "`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("ON `cte`.`id` = `" + tmpTableNameAlias + "`.`id`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("AND");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("`" + tmpTableNameAlias + "`.`is_deleted` = false");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("SELECT * FROM `cte`");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append("WHERE `cte`.`id` = 'NULL'");
        isNotDeletedBuilder.append(" ");
        isNotDeletedBuilder.append(")");
        return isNotDeletedBuilder.toString();
    }

    public CustomMySQLDialect() {
        super();
    }

    public CustomMySQLDialect(DatabaseVersion version) {
        super(version);
    }

    public CustomMySQLDialect(DatabaseVersion version, int bytesPerCharacter) {
        super(version, bytesPerCharacter);
    }

    public CustomMySQLDialect(DatabaseVersion version, MySQLServerConfiguration serverConfiguration) {
        super(version, serverConfiguration);
    }

    public CustomMySQLDialect(DatabaseVersion version, int bytesPerCharacter, boolean noBackslashEscapes) {
        super(version, bytesPerCharacter, noBackslashEscapes);
    }

    public CustomMySQLDialect(DialectResolutionInfo info) {
        super(info);
    }

}
