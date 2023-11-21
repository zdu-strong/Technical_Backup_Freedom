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
        this.registerFunctionOfIS_CHILD_ORGANIZE(functionRegistry, basicTypeRegistry);
        this.registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS(functionRegistry, basicTypeRegistry);
    }

    private void registerFunctionOfIS_CHILD_ORGANIZE(SqmFunctionRegistry functionRegistry,
            BasicTypeRegistry basicTypeRegistry) {
        var maxRecursionLevel = 10;
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("EXISTS (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("SELECT `organize`.`id`");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("FROM `organize_entity` `organize`");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = "organize" + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append("LEFT JOIN  `organize_entity` `" + secondTableName + "`");
            isChildStringBuilder.append(" ");
            isChildStringBuilder
                    .append("ON `" + firstTableName + "`.`parent_id` = `" + secondTableName + "`.`id`");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append("WHERE `organize`.`id` = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("AND");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("IN");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("`organize`.`id`");
        isChildStringBuilder.append(",");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("`organize`.`parent_id`");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append(",");
            isChildStringBuilder.append(" ");
            isChildStringBuilder.append("`" + secondTableName + "`.`parent_id`");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        functionRegistry.registerPattern(
                "IS_CHILD_ORGANIZE",
                isChildStringBuilder.toString(),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));

    }

    private void registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS(SqmFunctionRegistry functionRegistry,
            BasicTypeRegistry basicTypeRegistry) {
        var maxRecursionLevel = 10;
        var isNotDeleteOfOrganizeAndAncestorsStringBuilder = new StringBuilder();
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("EXISTS (");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("SELECT `organize`.`id`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("FROM `organize_entity` `organize`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = "organize" + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = "organize_" + i;
            isNotDeleteOfOrganizeAndAncestorsStringBuilder
                    .append("LEFT JOIN `organize_entity` `" + secondTableName + "`");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder
                    .append("ON `" + firstTableName + "`.`parent_id` = `" + secondTableName + "`.`id`");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("AND");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`" + secondTableName + "`.`is_deleted` = false");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        }
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("WHERE `organize`.`id` = ?1");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("AND");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`organize`.`is_deleted` = false");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("AND");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("'parent_id_NULL'");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("IN");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`organize`.`id`");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(",");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder
                .append("IFNULL(`organize`.`parent_id`, 'parent_id_NULL')");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = "organize_" + i;
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(",");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("IFNULL(");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`" + secondTableName + "`.`parent_id`");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(", ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(CASE WHEN `" + secondTableName
                    + "`.`id` IS NULL THEN 'CHILD_ORGANIZE_ORGANIZE_ID_NULL' ELSE 'parent_id_NULL' END)");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        }
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        functionRegistry.registerPattern("IS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS",
                isNotDeleteOfOrganizeAndAncestorsStringBuilder.toString(),
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
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