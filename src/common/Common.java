package common;

import java.io.*;

public class Common {
    public static void main(String[] args) {
//        File fin = new File("C:\\Users\\������\\Desktop\\MOST_WANTED.txt"); ��
//        File fout = new File("C:\\Users\\������\\Desktop\\hello.txt");   д
//        byte[] res = file2Byte(fin);
//        byte2File(res, fout);
    }

    private static final int DEFAULT_BUFFER_SIZE = 10 * 1024 * 1024; // 10M������

    /**
     * ���ֽ�����д��ָ���ļ�
     *
     * @param in
     *            ��д���ֽ�����
     * @param out
     *            Ŀ���ļ�
     * @return �Ƿ�д��ɹ�
     */
    public static boolean byte2File(byte[] in, File out) {
        try {
            FileOutputStream fout = new FileOutputStream(out);
            fout.write(in);
            fout.close();
        } catch (Exception e) {
            System.out.println(e.toString() + " �ֽ�ת��ʧ��");
            return false;
        }
        return true;
    }

    /**
     * ��ָ���ļ�ת�����ֽ�����
     *
     * @param file
     *            ��ת���ļ�
     * @return ת������ֽ�����
     */
    public static byte[] file2Byte(File file) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int len = 0;
        try {
            FileInputStream fin = new FileInputStream(file);
            len = fin.read(buffer);
            fin.close();
        } catch (Exception e) {
            System.out.println(e.toString() + " �ļ��޷�ת�����ֽ�");
        }
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++)
            ret[i] = buffer[i];
        return ret;
    }

    /**
     * ����ָ���ļ���Ŀ���ļ�
     *
     * @param in
     *            �����Ƶ�Դ�ļ�
     * @param out
     *            Ŀ���ļ�
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
            System.out.println(e.toString() + " �ļ�ת��ʧ��");
            return false;
        }
        return true;
    }

    /**
     * ���ַ���д��ָ���ļ�(��ָ���ĸ�·�����ļ��в�����ʱ��������޶�ȥ�������Ա�֤����ɹ���)
     *
     * @param res
     *            ԭ�ַ���
     * @param filePath
     *            �ļ�·��
     * @return �ɹ����
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
            char buf[] = new char[DEFAULT_BUFFER_SIZE]; // �ַ�������
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.toString() + " �ļ������ڻ��ʧ�ܣ�");
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
     * �ı��ļ�ת��Ϊָ��������ַ���
     *
     * @param file
     *            �ı��ļ�
     * @param encoding
     *            ��������
     * @return ת������ַ���
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
            // ��������д�������
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
