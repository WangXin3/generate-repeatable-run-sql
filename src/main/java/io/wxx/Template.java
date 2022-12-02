package io.wxx;

/**
 * @author Xinxin.Wang
 * @since 2022/12/2 17:49
 */
public class Template {

    public static final String ADD_OR_DEL_COL_TEMPLATE = "CREATE PROCEDURE {0}()\n" +
            "BEGIN\n" +
            "    IF ({1}EXISTS(SELECT 1\n" +
            "                   FROM information_schema.COLUMNS\n" +
            "                   WHERE TABLE_SCHEMA LIKE {2}\n" +
            "                     AND TABLE_NAME = {3}\n" +
            "                     AND COLUMN_NAME = {4}))\n" +
            "    THEN\n" +
            "        {5}\n" +
            "    END IF;\n" +
            "END;\n" +
            "\n" +
            "CALL {6}();\n" +
            "DROP PROCEDURE IF EXISTS {7};";

    public static final String ADD_OR_DEL_INDEX_TEMPLATE = "CREATE PROCEDURE {0}()\n" +
            "BEGIN\n" +
            "    IF ({1}EXISTS(SELECT 1\n" +
            "                   FROM information_schema.STATISTICS\n" +
            "                   WHERE TABLE_SCHEMA LIKE {2}\n" +
            "                     AND TABLE_NAME = {3}\n" +
            "                     AND INDEX_NAME = {4}))\n" +
            "    THEN\n" +
            "        {5}\n" +
            "    END IF;\n" +
            "END;\n" +
            "\n" +
            "CALL {6}();\n" +
            "DROP PROCEDURE IF EXISTS {7};";
}
