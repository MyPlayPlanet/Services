package net.myplayplanet.services.connection.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SerializeHelper {
    /**
     * @param data {@link Byte}Array which should be Deserialized to T
     * @param <T>  The Type you want to get
     * @return The Content deserialized to your Type
     */
    public static <T> T deserializeToType(byte[] data) {
        if (data == null) {
            return null;
        }

        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert is != null;
            return (T) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
