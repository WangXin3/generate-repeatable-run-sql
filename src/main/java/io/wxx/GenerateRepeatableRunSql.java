package io.wxx;

/**
 * @author Xinxin.Wang
 * @since 2022/12/2 17:41
 */
public interface GenerateRepeatableRunSql {
    void generate(String inputFilePath, String outputFilePath, String selectedItem, String targetDBName);
}
