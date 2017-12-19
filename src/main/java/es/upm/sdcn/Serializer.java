package es.upm.sdcn;

import java.io.*;

public class Serializer {

    public static byte[] fromObjectToByte(Object o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] serialized = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            serialized = bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
        return serialized;
    }

    public static Object fromByteToObject(byte[] serialized) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
        ObjectInput in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return o;
    }
}
