package common;

import java.io.*;

public class Common {
    public static void main(String[] args) {
//        File fin = new File("C:\\Users\\于宗鹏\\Desktop\\MOST_WANTED.txt"); 读
//        File fout = new File("C:\\Users\\于宗鹏\\Desktop\\hello.txt");   写
//        byte[] res = file2Byte(fin);
//        byte2File(res, fout);
    }

    private static final int DEFAULT_BUFFER_SIZE = 10 * 1024 * 1024; // 10M缓冲区

    /**
     * 将字节序列写入指定文件
     *
     * @param in
     *            待写入字节数组
     * @param out
     *            目标文件
     * @return 是否写入成功
     */
    public static boolean byte2File(byte[] in, File out) {
        try {
            FileOutputStream fout = new FileOutputStream(out);
            fout.write(in);
            fout.close();
        } catch (Exception e) {
            System.out.println(e.toString() + " 字节转换失败");
            return false;
        }
        return true;
    }

    /**
     * 将指定文件转化成字节数组
     *
     * @param file
     *            待转化文件
     * @return 转化后的字节数组
     */
    public static byte[] file2Byte(File file) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int len = 0;
        try {
            FileInputStream fin = new FileInputStream(file);
            len = fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            System.out.println(e.toString() + " 文件无法转化成字节");
        }
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++)
            ret[i] = buffer[i];
        return ret;
    }

    /**
     * 复制指定文件到目标文件
     *
     * @param in
     *            待复制的源文件
     * @param out
     *            目标文件
     * @return
     */
    public static boolean file2File(File in, File out) {
        try {
            FileInputStream fin = new FileInputStream(in);
            FileOutputStream fout = new FileOutputStream(out);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int len = 0;
            while ((len = fin.read(buffer)) != -1) {
                fout.write(buffer, 0, len);
            }
            fin.close();
            fout.close();
        } catch (Exception e) {
            System.out.println(e.toString() + " 文件转换失败");
            return false;
        }
        return true;
    }

    /**
     * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
     *
     * @param res
     *            原字符串
     * @param filePath
     *            文件路径
     * @return 成功标记
     */
    public static boolean string2File(String res, String filePath) {
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[DEFAULT_BUFFER_SIZE]; // 字符缓冲区
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString() + " 文件不存在或打开失败！");
            return false;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 文本文件转换为指定编码的字符串
     *
     * @param file
     *            文本文件
     * @param encoding
     *            编码类型
     * @return 转换后的字符串
     * @throws IOException
     */
    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file));
            } else {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            }
            // 将输入流写入输出流
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return writer.toString();
    }
}
