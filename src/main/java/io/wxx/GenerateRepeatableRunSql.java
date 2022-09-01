package io.wxx;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xinxin.Wang
 * @since 2022/9/1 15:35
 */
public class GenerateRepeatableRunSql {

    private GenerateRepeatableRunSql() {
    }

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

    private static String dataPath = "";
    private static String outPath = "";

    /**
     * 删除索引
     */
    private static void delIndex() {
        addOrDelIndexWriteToFile(false);
    }

    /**
     * 添加索引
     */
    private static void addIndex() {
        addOrDelIndexWriteToFile(true);
    }


    /**
     * 将语句写到文件
     *
     * @param isAdd /
     */
    private static void addOrDelIndexWriteToFile(boolean isAdd) {
        // {0} 存储过程名 add_index_
        // {1} 数据库名
        // {2} 表名
        // {3} 索引名
        // {4} 执行语句
        // {5} 存储过程名
        // {6} 存储过程名
        List<String> list = FileUtil.readUtf8Lines(dataPath);

        List<String> out = new ArrayList<>();
        for (String s : list) {
            if (StrUtil.isNotBlank(s)) {

                // 添加索引 ALTER TABLE `xxx`.`xxx` ADD INDEX `xxx`(`xxx`) USING BTREE;
                // 删除索引 ALTER TABLE `xxx`.`xxx` DROP INDEX `xxx`;

                String[] split = s.split("`");
                String schemaName = split[1];
                String tableName = split[3];
                String indexName = split[5];
                String procedureName = (Boolean.TRUE.equals(isAdd) ? "add_index_" : "del_index_") + indexName;

                // 新增字段时需要加，删除时不需要
                String not = Boolean.TRUE.equals(isAdd) ? "NOT " : "";

                String data = StrUtil.indexedFormat(ADD_OR_DEL_INDEX_TEMPLATE, procedureName, not, addApostrophe(schemaName),
                        addApostrophe(tableName), addApostrophe(indexName), s, procedureName, procedureName);
                out.add(data);
                out.add("\n");
                out.add("# ----------------------------------------------");
            }
        }

        FileUtil.writeUtf8Lines(out, outPath);
    }

    /**
     * 删除列
     */
    private static void delCol() {
        addOrDelColWriteToFile(false);
    }

    /**
     * 添加列
     */
    private static void addCol() {
        addOrDelColWriteToFile(true);
    }


    /**
     * 将语句写到文件
     *
     * @param isAdd /
     */
    private static void addOrDelColWriteToFile(Boolean isAdd) {
        // {0} 存储过程名 add_col_01
        // {1} 数据库名
        // {2} 表名
        // {3} 列名
        // {4} 执行语句
        // {5} 存储过程名
        // {6} 存储过程名
        List<String> list = FileUtil.readUtf8Lines(dataPath);

        List<String> out = new ArrayList<>();
        for (String s : list) {
            if (StrUtil.isNotBlank(s)) {

                // 添加列 ALTER TABLE `xxx`.`xxx` ADD COLUMN `xxx` tinyint NULL DEFAULT NULL COMMENT 'xxx' AFTER `xxx`;
                // 删除列 ALTER TABLE `xxx`.`xxx` DROP COLUMN `xxx`;

                String[] split = s.split("`");
                String schemaName = split[1];
                String tableName = split[3];
                String colName = split[5];
                String procedureName = (Boolean.TRUE.equals(isAdd) ? "add_col_" : "del_col_") + colName;

                // 新增字段时需要加，删除时不需要
                String not = Boolean.TRUE.equals(isAdd) ? "NOT " : "";

                String data = StrUtil.indexedFormat(ADD_OR_DEL_COL_TEMPLATE, procedureName, not, addApostrophe(schemaName),
                        addApostrophe(tableName), addApostrophe(colName), s, procedureName, procedureName);
                out.add(data);
                out.add("\n");
                out.add("# ----------------------------------------------");
            }
        }

        FileUtil.writeUtf8Lines(out, outPath);
    }

    private static String addApostrophe(String a) {
        return "'".concat(a).concat("'");
    }

    public static void generate(String inputFilePath, String outputFilePath, String selectedItem) {
        dataPath = inputFilePath;
        outPath = outputFilePath;

        FileUtil.newFile(outPath);
        switch (selectedItem) {
            case "添加字段":
                addCol();
                break;
            case "删除字段":
                delCol();
                break;
            case "添加索引":
                addIndex();
                break;
            case "删除索引":
                delIndex();
                break;
            default:
                break;
        }
    }
}