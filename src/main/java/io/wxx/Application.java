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
    JFrame f = new JFrame("可重复执行SQL转换");

    JPanel jp1 = new JPanel();
    JPanel jp2 = new JPanel();
    JPanel jp3 = new JPanel();
    JPanel jp4 = new JPanel();

    JButton p1Button = new JButton("选择输入文件");
    JLabel p1Jl1 = new JLabel("输入文件：");
    JLabel p1Jl2 = new JLabel("请选择输入文件");

    JLabel p2Jl1 = new JLabel("请选择输入文件sql类型：");
    JComboBox<String> p2Jb = new JComboBox<>();

    JButton p3Button = new JButton("选择输出路径");
    JLabel p3Jl1 = new JLabel("输出路径：");
    JLabel p3Jl2 = new JLabel("请选择输出路径，默认输出到输入文件路径");

    JButton p4Button = new JButton("执行");

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
            if (StrUtil.isBlank(inputPath)) {
                JOptionPane.showMessageDialog(f, "请选择输入文件", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (StrUtil.isBlank(selectedItem) || "请选择".equals(selectedItem)) {
                JOptionPane.showMessageDialog(f, "请选择输入文件sql类型", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String outputFilePath;
            if (StrUtil.isBlank(outputPath)) {
                outputFilePath = inputPath + "\\" + selectedItem + "_" + inputFileName;
            } else {
                outputFilePath = outputPath + "\\" + selectedItem + "_" + inputFileName;
            }

            // 生成文件
            GenerateRepeatableRunSql.generate(p1Jl2.getText(), outputFilePath, selectedItem);

            JOptionPane.showMessageDialog(f, "执行成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        jp1.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp2.setLayout(new FlowLayout(FlowLayout.LEFT));
        jp3.setLayout(new FlowLayout(FlowLayout.LEFT));

        f.add(jp1);
        f.add(jp2);
        f.add(jp3);
        f.add(jp4);

        f.setLayout(new GridLayout(4, 1));
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setBounds(600, 300, 600, 180);
        f.setResizable(false);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        Application application = new Application();
        application.run();
    }
}
