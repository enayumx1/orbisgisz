package org.h2gis.utilities;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SQL reserved words
 * @author Nicolas Fortin
 */
public class Constants {
    public static final Set<String> RESERVED_WORDS = new HashSet<String>(Arrays.asList("A", "ABORT", "ABS", "ABSOLUTE",
            "ACCESS", "ACTION", "ADA", "ADD", "ADMIN", "AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALSO", "ALTER",
            "ALWAYS", "ANALYSE", "ANALYZE", "AND", "ANY", "ARE", "ARRAY", "AS", "ASC", "ASENSITIVE", "ASSERTION",
            "ASSIGNMENT", "ASYMMETRIC", "AT", "ATOMIC", "ATTRIBUTE", "ATTRIBUTES", "AUDIT", "AUTHORIZATION",
            "AUTO_INCREMENT", "AVG", "AVG_ROW_LENGTH", "BACKUP", "BACKWARD", "BEFORE", "BEGIN", "BERNOULLI",
            "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BITVAR", "BLOB", "BOOL", "BOOLEAN", "BOTH",
            "BREADTH", "BREAK", "BROWSE", "BULK", "BY", "C", "CACHE", "CALL", "CALLED", "CARDINALITY", "CASCADE",
            "CASCADED", "CASE", "CAST", "CATALOG", "CATALOG_NAME", "CEIL", "CEILING", "CHAIN", "CHANGE", "CHAR",
            "CHAR_LENGTH", "CHARACTER", "CHARACTER_LENGTH", "CHARACTER_SET_CATALOG", "CHARACTER_SET_NAME",
            "CHARACTER_SET_SCHEMA", "CHARACTERISTICS", "CHARACTERS", "CHECK", "CHECKED", "CHECKPOINT", "CHECKSUM",
            "CLASS", "CLASS_ORIGIN", "CLOB", "CLOSE", "CLUSTER", "CLUSTERED", "COALESCE", "COBOL", "COLLATE",
            "COLLATION", "COLLATION_CATALOG", "COLLATION_NAME", "COLLATION_SCHEMA", "COLLECT", "COLUMN", "COLUMN_NAME",
            "COLUMNS", "COMMAND_FUNCTION", "COMMAND_FUNCTION_CODE", "COMMENT", "COMMIT", "COMMITTED", "COMPLETION",
            "COMPRESS", "COMPUTE", "CONDITION", "CONDITION_NUMBER", "CONNECT", "CONNECTION", "CONNECTION_NAME",
            "CONSTRAINT", "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONSTRAINTS", "CONSTRUCTOR",
            "CONTAINS", "CONTAINSTABLE", "CONTINUE", "CONVERSION", "CONVERT", "COPY", "CORR", "CORRESPONDING", "COUNT",
            "COVAR_POP", "COVAR_SAMP", "CREATE", "CREATEDB", "CREATEROLE", "CREATEUSER", "CROSS", "CSV", "CUBE",
            "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR",
            "CURSOR_NAME", "CYCLE", "DATA", "DATABASE", "DATABASES", "DATE", "DATETIME", "DATETIME_INTERVAL_CODE",
            "DATETIME_INTERVAL_PRECISION", "DAY", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND",
            "DAYOFMONTH", "DAYOFWEEK", "DAYOFYEAR", "DBCC", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT",
            "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINED", "DEFINER", "DEGREE", "DELAY_KEY_WRITE", "DELAYED",
            "DELETE", "DELIMITER", "DELIMITERS", "DENSE_RANK", "DENY", "DEPTH", "DEREF", "DERIVED", "DESC",
            "DESCRIBE", "DESCRIPTOR", "DESTROY", "DESTRUCTOR", "DETERMINISTIC", "DIAGNOSTICS", "DICTIONARY",
            "DISABLE", "DISCONNECT", "DISK", "DISPATCH", "DISTINCT", "DISTINCTROW", "DISTRIBUTED", "DIV", "DO",
            "DOMAIN", "DOUBLE", "DROP", "DUAL", "DUMMY", "DUMP", "DYNAMIC", "DYNAMIC_FUNCTION", "DYNAMIC_FUNCTION_CODE",
            "EACH", "ELEMENT", "ELSE", "ELSEIF", "ENABLE", "ENCLOSED", "ENCODING", "ENCRYPTED", "END", "END-EXEC",
            "ENUM", "EQUALS", "ERRLVL", "ESCAPE", "ESCAPED", "EVERY", "EXCEPT", "EXCEPTION", "EXCLUDE", "EXCLUDING",
            "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTING", "EXISTS", "EXIT", "EXP", "EXPLAIN", "EXTERNAL", "EXTRACT",
            "FALSE", "FETCH", "FIELDS", "FILE", "FILLFACTOR", "FILTER", "FINAL", "FIRST", "FLOAT", "FLOAT4", "FLOAT8",
            "FLOOR", "FLUSH", "FOLLOWING", "FOR", "FORCE", "FOREIGN", "FORTRAN", "FORWARD", "FOUND", "FREE", "FREETEXT",
            "FREETEXTTABLE", "FREEZE", "FROM", "FULL", "FULLTEXT", "FUNCTION", "FUSION", "G", "GENERAL", "GENERATED",
            "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GRANTED", "GRANTS", "GREATEST", "GROUP", "GROUPING", "HANDLER",
            "HAVING", "HEADER", "HEAP", "HIERARCHY", "HIGH_PRIORITY", "HOLD", "HOLDLOCK", "HOST", "HOSTS", "HOUR",
            "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IDENTIFIED", "IDENTITY", "IDENTITY_INSERT",
            "IDENTITYCOL", "IF", "IGNORE", "ILIKE", "IMMEDIATE", "IMMUTABLE", "IMPLEMENTATION", "IMPLICIT",
            "IN", "INCLUDE", "INCLUDING", "INCREMENT", "INDEX", "INDICATOR", "INFILE", "INFIX", "INHERIT",
            "INHERITS", "INITIAL", "INITIALIZE", "INN", "INOUT", "INPUT", "INSENSITI", "INSER", "INS", "INSTANCE",
            "INST", "INSTE", "INT", "INT2", "INT4", "INT", "INT", "INT", "INTER", "IN", "INV", "IS", "ISA", "ISNULL",
            "ITERATE", "K", "KEY", "KE", "KEYS", "LANCOMPIL", "LANGUAGE", "LAST", "LATERAL", "L", "LEAST", "LEAV",
            "LEF", "LENGTH", "LES", "LEVEL", "LIMIT", "LINES", "LN", "LOCAL", "LOCALTI", "LOCATI", "LOCATO", "LOC",
            "LOGIN", "LONG", "LONG", "LOOP", "LOW_PRI", "LOWE", "M", "M", "MATCH", "MATCHE", "MAX", "MA", "M", "MEDIU",
            "MEDI", "MEDIUM", "MEMBER", "MESSAGE", "MESSAGE_", "MESS", "METH", "MIDDLEI", "MIN", "MIN_", "MINU",
            "MINUTE", "MINUT", "MINVAL", "MLSL", "MOD", "MOD", "MODIFIES", "MODULE", "MONTH", "MONTHNAME", "MORE",
            "MULTISET", "MUMPS", "MYISAM", "NAME", "NATIONAL", "NA", "NCHAR", "NESTI", "NEW", "NO", "NO_", "NOAUDIT",
            "NOCOM", "NOCREATEDB", "NOCR", "NOINH", "NOLOGIN", "NONCLUSTE", "NONE", "NORMA", "NOSUPER", "NOT", "N",
            "NOTIFY", "NOTNUL", "NOWAIT", "NUL", "NULLABLE", "NUL", "NULLS", "NUMERI", "OBJECT", "OCTETS", "OFF",
            "OFFLINE", "OFFSETS", "OIDS", "ON", "ONLY", "OPENDATAS", "OPENQUERY", "OPENXML", "OPERATO", "OPTIMIZE",
            "OPTIONALLY", "O", "OR", "ORDERING", "OTHERS", "OUT", "OUTFILE", "OVER", "OVERLAPS", "OVERRIDING",
            "PACK_KEYS", "PAD", "PARAMETER", "PARAMETER_N", "PARAMETE", "PARAMETER_SPE", "PARAMETE", "PARAMETER_",
            "PARAMET", "PART", "PART", "PASCAL", "PA", "PATH", "PERCENT", "PERCENTILE", "PERCEN", "PLACING", "PLI",
            "PO", "POSTFIX", "PRECE", "PRE", "PREF", "PREORDER", "PREPARE", "PRESERVE", "PRINT", "PRIOR", "PRIVILEGES",
            "PROCEDURAL", "PROCESS", "PUBLIC", "P", "QUOTE", "RAID0", "RANGE", "RANK", "RAW", "READ", "READTEXT",
            "RECHEC", "RECONFIGURE", "RECUR", "REF", "REFERENC", "REGEXP", "REGR", "REGR_COU", "REGR_INTERCEPT",
            "REG", "REGR_SLOPE", "REGR_SXX", "REG", "REINDEX", "RELEASE", "REL", "RENAME", "REPEATABLE", "REPLACE",
            "REQUIRE", "RESIGNAL", "REST", "RESTORE", "RES", "RETURN", "RETURNE", "RETURNE", "RETURNED", "RETURNS",
            "RIGHT", "ROLE", "ROLLUP", "ROUTIN", "ROUTINE_NAME", "ROW", "ROW_NU", "ROWCOUNT", "ROWID", "ROWS", "R",
            "SAVE", "SAVEP", "SCALE", "SCHEMA", "SCHE", "SCOPE", "SCOPE_NAME", "SCOPE_S", "SCROLL", "SECOND", "SE",
            "SECTION", "SELECT", "SENSITI", "SEPARATOR", "SERI", "SERVER_N", "SESSION", "SET", "SET", "SETS", "SE",
            "SHA", "SH", "SHUTDO", "SIGNAL", "SIMI", "SIMP", "SIZE", "SOME", "SONAME", "SPACE", "SPATIA", "SPECIFIC",
            "SPECIFIC", "SPEC", "SQL", "SQL_", "SQL_BI", "SQL_CA", "SQL_LOG_", "SQL_LOG_", "SQL_LOW_P", "SQL",
            "SQL_SMAL", "SQL_", "SQLCA", "SQLERR", "SQLEXC", "SQLSTAT", "SQLWA", "SQRT", "STABLE", "STARTIN",
            "STATE", "STATIC", "STAT", "STDDEV_", "STDDEV_S", "STDIN", "STOR", "STR", "STRICT", "STRUCTUR",
            "STYLE", "SUBLI", "SUBMU", "SUBSTR", "SUCC", "SUM", "SU", "SYMMET", "SYNON", "SYSDA", "SYSID",
            "SYSTEM", "SYSTEM", "TABLE", "TABLE", "TABLES", "TAB", "TEMP", "TEMPORA", "TERMINA", "TERMINA",
            "TEXT", "THAN", "THEN", "TIME", "TIME", "TIMEZO", "TIME", "TINY", "TINYINT", "TO", "T", "TOP", "T",
            "TRAN", "TRANSACTI", "TRA", "TRANSA", "TR", "TRAN", "TRANS", "TRANSLA", "TREAT", "TRIGGER_", "TRIGG",
            "TRIGGER_", "TRIM", "TRUNCA", "TRUSTE", "TSEQ", "TYPE", "UID", "UNBOU", "UNCO", "UNDER", "UNEN",
            "UNION", "UNIQ", "UNKNOWN", "UNLI", "UNLOCK", "UNNEST", "UNTIL", "UPDATETE", "UPPER", "USAGE", "USE",
            "USE", "US", "USER_D", "USER_", "USER_DEFI", "USING", "UTC_TIME", "U", "VACUUM", "VA", "VALIDAT",
            "VALUE", "VAR_POP", "VARBI", "VARCHAR", "VARCHA", "VARIA", "VARIABL", "VARYING", "VIEW", "WAITFOR",
            "WHE", "WHILE", "WIDTH", "WINDOW", "WITHI", "WIT", "WORK", "WRIT", "X509", "YEA", "ZERO"));
}
