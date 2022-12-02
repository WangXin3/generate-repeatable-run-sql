package io.wxx;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

import static io.wxx.Template.ADD_OR_DEL_COL_TEMPLATE;
import static io.wxx.Template.ADD_OR_DEL_INDEX_TEMPLATE;

/**
 * @author Xinxin.Wang
 * @since 2022/12/2 17:42
 */
public class DataGripGenerateRepeatableRunSql implements GenerateRepeatableRunSql{

    private static String DATA_PATH = "";
    private static String OUT_PATH = "";
    private static String TARGET_DB_NAME = "";

    @Override
    public void generate(String inputFilePath, String outputFilePath, String selectedItem, String targetDBName) {

        DATA_PATH = inputFilePath;
        OUT_PATH = outputFilePath;
        TARGET_DB_NAME = targetDBName;

        FileUtil.newFile(OUT_PATH);
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

    private void delIndex() {
        // drop index tbl_performance_activity_id_end_start_date_index on tbl_performance_activity;
        addOrDelIndexWriteToFile(false);
    }

    private void addIndex() {
        // create index tbl_performance_activity_id_end_start_date_index on tbl_performance_activity (performance_activity_id asc, end_date desc, start_date asc);
        addOrDelIndexWriteToFile(true);
    }

    private void delCol() {
        // alter table tbl_tenant_setting drop column indicator_radar_chart_enable;
        addOrDelColWriteToFile(false);
    }

    private void addCol() {
        // alter table tbl_calculation_formula add formula_type int null comment '公式类型 10-计算指标评分 20-测算绩效得分' after formula_description;
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

        List<String> list = FileUtil.readUtf8Lines(DATA_PATH);

        List<String> out = new ArrayList<>();
        for (String s : list) {
            if (StrUtil.isNotBlank(s)) {
                // 添加列 alter table tbl_calculation_formula add formula_type int null comment '公式类型 10-计算指标评分 20-测算绩效得分' after formula_description;
                // 删除列 alter table tbl_tenant_setting drop column indicator_radar_chart_enable;

                String[] split = s.split(" ");
                String tableName = split[2];
                String colName = split[isAdd ? 4 : 5];
                colName = StrUtil.removeSuffix(colName, ";");
                String procedureName = (Boolean.TRUE.equals(isAdd) ? "add_col_" : "del_col_") + colName;

                // 新增字段时需要加，删除时不需要
                String not = Boolean.TRUE.equals(isAdd) ? "NOT " : "";

                String data = StrUtil.indexedFormat(ADD_OR_DEL_COL_TEMPLATE, procedureName, not, addApostrophe(TARGET_DB_NAME),
                        addApostrophe(tableName), addApostrophe(colName), s, procedureName, procedureName);
                out.add(data);
                out.add("\n");
                out.add("# ----------------------------------------------");
            }
        }

        FileUtil.writeUtf8Lines(out, OUT_PATH);
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
        List<String> list = FileUtil.readUtf8Lines(DATA_PATH);

        List<String> out = new ArrayList<>();
        for (String s : list) {
            if (StrUtil.isNotBlank(s)) {

                // 添加索引 create index tbl_performance_activity_id_end_start_date_index on tbl_performance_activity (performance_activity_id asc, end_date desc, start_date asc);
                // 删除索引 drop index tbl_performance_activity_id_end_start_date_index on tbl_performance_activity;

                String[] split = s.split(" ");
                String indexName = split[2];
                String tableName = split[4];
                tableName = StrUtil.removeSuffix(tableName, ";");
                String procedureName = (Boolean.TRUE.equals(isAdd) ? "add_index_" : "del_index_") + indexName;

                // 新增索引时需要加，删除时不需要
                String not = Boolean.TRUE.equals(isAdd) ? "NOT " : "";

                String data = StrUtil.indexedFormat(ADD_OR_DEL_INDEX_TEMPLATE, procedureName, not, addApostrophe(TARGET_DB_NAME),
                        addApostrophe(tableName), addApostrophe(indexName), s, procedureName, procedureName);
                out.add(data);
                out.add("\n");
                out.add("# ----------------------------------------------");
            }
        }

        FileUtil.writeUtf8Lines(out, OUT_PATH);
    }
    private static String addApostrophe(String a) {
        return "'".concat(a).concat("'");
    }
}
