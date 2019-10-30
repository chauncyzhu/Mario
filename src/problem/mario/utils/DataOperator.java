package problem.mario.utils;

import com.google.gson.Gson;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

public class DataOperator {
    public static void saveObjectByObjectOutput(Object o, File file) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(o);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObjectByObjectInput(File file) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            Object o = inputStream.readObject();
            inputStream.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveObjectByJson(Object o, File file) {
        String json = new Gson().toJson(o);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getObjectByJson(File file, Class c) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            StringBuilder stringBuilder = new StringBuilder();
            int l = 0;
            byte[] bs = new byte[1024];
            while ((l = fileInputStream.read(bs)) != -1) {
                stringBuilder.append(new String(bs, 0, l));
            }
            fileInputStream.close();
            return new Gson().fromJson(stringBuilder.toString(), c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String ObjectToString(Object o) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.close();
            byteArrayOutputStream.close();
            return new BASE64Encoder().encode(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object StringToObject(String str) {
        try {
            byte[] bs = new BASE64Decoder().decodeBuffer(str);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bs);
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            Object o = inputStream.readObject();
            inputStream.close();
            byteArrayInputStream.close();
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
