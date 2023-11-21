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
        registerFunctionOfIS_CHILD_ORGANIZE(functionRegistry, basicTypeRegistry);
        registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS(functionRegistry, basicTypeRegistry);
    }

    private void registerFunctionOfIS_CHILD_ORGANIZE(SqmFunctionRegistry functionRegistry,
            BasicTypeRegistry basicTypeRegistry) {
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("IN");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("WITH RECURSIVE `cte` AS (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("SELECT `id`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("FROM `organize_entity`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("WHERE `id` = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("UNION ALL");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("SELECT `organize`.`parent_id`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("FROM `cte` INNER JOIN `organize_entity` `organize`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("ON `cte`.`id` = `organize`.`id`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("SELECT * FROM `cte`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        functionRegistry.registerPattern(
                "IS_CHILD_ORGANIZE",
                isChildStringBuilder.toString(),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
    }

    private void registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS(SqmFunctionRegistry functionRegistry,
            BasicTypeRegistry basicTypeRegistry) {
        var isNotDeleteOfOrganizeAndAncestorsStringBuilder = new StringBuilder();
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("'NULL'");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("IN");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("WITH RECURSIVE `cte` AS (");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("SELECT `id`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("FROM `organize_entity`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("WHERE `id` = ?1");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("AND");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`is_deleted` = false");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("UNION ALL");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("SELECT IFNULL(`organize`.`parent_id`, 'NULL')");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("FROM `cte` INNER JOIN `organize_entity` `organize`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("ON `cte`.`id` = `organize`.`id`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("AND");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`organize`.`is_deleted` = false");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("SELECT * FROM `cte`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        functionRegistry.registerPattern("IS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS",
                isNotDeleteOfOrganizeAndAncestorsStringBuilder.toString(),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
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
