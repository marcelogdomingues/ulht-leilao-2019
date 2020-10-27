import java.io.*;

public class Serializable implements java.io.Serializable {

    static void serialize(Object toSerialize, String fileName) throws IOException {
        FileOutputStream file = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(toSerialize);
        out.close();
        file.close();
    }

    static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(file);
        Object deserialized = in.readObject();
        in.close();
        file.close();
        return deserialized;
    }

}
