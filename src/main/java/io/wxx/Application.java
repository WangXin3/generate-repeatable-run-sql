package io.wxx;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

/**
 * @author Xinxin.Wang
 * @since 2022/9/1 15:34
 */
public class Application {
    public static final String DATA_GRIP = "DataGrip";
    public static final String NAVI_CAT = "Navicat";

    JFrame f = new JFrame("可重复执行SQL转换");

    JPanel jp1 = new JPanel();
    JPanel jp2 = new JPanel();
    JPanel jp3 = new JPanel();
    JPanel jp4 = new JPanel();
    JPanel jp5 = new JPanel();
    JPanel jp6 = new JPanel();

    JButton p1Button = new JButton("选择输入文件");
    JLabel p1Jl1 = new JLabel("输入文件：");
    JLabel p1Jl2 = new JLabel("请选择输入文件");

    JLabel p2Jl1 = new JLabel("请选择输入文件sql类型：");
    JComboBox<String> p2Jb = new JComboBox<>();

    JButton p3Button = new JButton("选择输出路径");
    JLabel p3Jl1 = new JLabel("输出路径：");
    JLabel p3Jl2 = new JLabel("请选择输出路径，默认输出到输入文件路径");

    JButton p4Button = new JButton("执行");

    JLabel p5Jl1 = new JLabel("请选择比对工具：");
    JComboBox<String> p5Jb = new JComboBox<>();

    JLabel p6Jl1 = new JLabel("请输入目标库名：");
    JTextField p6jtf1 = new JTextField(20);
    JLabel p6Jl2 = new JLabel("* 当比对工具为DataGrip时必填");

    String cacheDirPath = "";
    String inputFileName = "";
    String inputPath = "";
    String outputPath = "";

    public void run() {
        jp1.add(p1Button);
        jp1.add(p1Jl1);
        jp1.add(p1Jl2);

        String property = System.getProperty("java.class.path");
        p1Button.addActionListener(e -> {
            cacheDirPath = StrUtil.isBlank(cacheDirPath) ? property : cacheDirPath;
            JFileChooser jfc = new JFileChooser(cacheDirPath);
            jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return !FileUtil.isFile(f) || f.getName().endsWith(".sql");
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showSaveDialog(f);

            try {
                File file = jfc.getSelectedFile();
                String sourceFilePath = file.getAbsolutePath();
                p1Jl2.setText(sourceFilePath);
                inputFileName = file.getName();
                inputPath = file.getParent();
                cacheDirPath = file.getParent();
            } catch (Exception var6) {
                JOptionPane.showMessageDialog(f, "没有选中任何文件", "提示", JOptionPane.WARNING_MESSAGE);
            }

        });
        p2Jb.addItem("请选择");
        p2Jb.addItem("添加字段");
        p2Jb.addItem("删除字段");
        p2Jb.addItem("添加索引");
        p2Jb.addItem("删除索引");
        jp2.add(p2Jl1);
        jp2.add(p2Jb);

        p5Jb.addItem(DATA_GRIP);
        p5Jb.addItem(NAVI_CAT);
        jp5.add(p5Jl1);
        jp5.add(p5Jb);

        p6Jl2.setForeground(Color.RED);
        jp6.add(p6Jl1);
        jp6.add(p6jtf1);
        jp6.add(p6Jl2);

        jp3.add(p3Button);
        jp3.add(p3Jl1);
        jp3.add(p3Jl2);
        p3Button.addActionListener(e -> {
            cacheDirPath = StrUtil.isBlank(cacheDirPath) ? property : cacheDirPath;
            JFileChooser jfc = new JFileChooser(cacheDirPath);
            jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return FileUtil.isDirectory(f);
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.showSaveDialog(f);

            try {
                File file = jfc.getSelectedFile();
                String sourceFilePath = file.getAbsolutePath();
                p3Jl2.setText(sourceFilePath);
                outputPath = sourceFilePath;
                cacheDirPath = sourceFilePath;
            } catch (Exception var6) {
                JOptionPane.showMessageDialog(f, "没有选中任何文件夹", "提示", JOptionPane.WARNING_MESSAGE);
            }

        });
        jp4.add(p4Button);
        p4Button.addActionListener(e -> {
            String selectedItem = (String) p2Jb.getSelectedItem();
            String comparisonUtil = (String) p5Jb.getSelectedItem();
            String targetDBName = p6jtf1.getText();
            if (StrUtil.isBlank(inputPath)) {
                JOptionPane.showMessageDialog(f, "请选择输入文件", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (StrUtil.isBlank(selectedItem) || "请选择".equals(selectedItem)) {
                JOptionPane.showMessageDialog(f, "请选择输入文件sql类型", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DATA_GRIP.equals(comparisonUtil) && StrUtil.isBlank(targetDBName)) {
                JOptionPane.showMessageDialog(f, "当比对工具为DataGrip时目标库名必填", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String outputFilePath;
            if (StrUtil.isBlank(outputPath)) {
                outputFilePath = inputPath + "\\" + selectedItem + "_" + inputFileName;
            } else {
                outputFilePath = outputPath + "\\" + selectedItem + "_" + inputFileName;
            }

            // 生成文件
            GenerateRepeatableRunSql generateRepeatableRunSql = getImpl(comparisonUtil);
            if (generateRepeatableRunSql == null) {
                JOptionPane.showMessageDialog(f, "没有该对比工具处理类", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            generateRepeatableRunSql.generate(p1Jl2.getText(), outputFilePath, selectedItem, targetDBName);

            JOptionPane.showMessageDialog(f, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        jp1.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp2.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp3.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp5.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp6.setLayout(new FlowLayout(FlowLayout.LEFT));

        f.add(jp1);
        f.add(jp2);
        f.add(jp5);
        f.add(jp6);
        f.add(jp3);
        f.add(jp4);

        f.setLayout(new GridLayout(6, 1));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setBounds(600, 300, 600, 220);
        f.setResizable(false);
        f.setVisible(true);
    }

    public static GenerateRepeatableRunSql getImpl(String comparisonUtil) {
        if (DATA_GRIP.equals(comparisonUtil)) {
            return new DataGripGenerateRepeatableRunSql();
        }
        if (NAVI_CAT.equals(comparisonUtil)) {
            return new NavicatGenerateRepeatableRunSql();
        }
        return null;
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }
}
