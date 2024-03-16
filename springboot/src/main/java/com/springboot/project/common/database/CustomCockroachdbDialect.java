package com.springboot.project.common.database;

import org.hibernate.boot.model.FunctionContributions;
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
        functionRegistry.registerPattern("IFNULL", "COALESCE(CAST(CAST(?1 AS TEXT) AS DECIMAL), ?2)",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("FOUND_ROWS", "COUNT(*) OVER()",
                basicTypeRegistry.resolve(StandardBasicTypes.LONG));
        functionRegistry.registerPattern("IS_SORT_AT_BEFORE", "?1 < ?2",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.registerPattern("LOCATE", "POSITION(?1 in ?2)",
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
        functionRegistry.registerPattern("CONVERT_TO_BIG_DECIMAL", "CAST(CAST(?1 AS TEXT) AS DECIMAL)",
                basicTypeRegistry.resolve(StandardBasicTypes.BIG_DECIMAL));
        functionRegistry.registerPattern("CONVERT_TO_STRING", "CAST(?1 AS TEXT)",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));
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
