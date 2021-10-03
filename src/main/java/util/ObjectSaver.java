package util;

import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A helper class that contains object saving static methods for Serializable objects.
 * @author Acemad
 */
public class ObjectSaver {

    /**
     * Save a given object to the given path. Creates the file if it does not exist and overwrites it if it does.
     * @param object The object to save
     * @param filePath Path of the file where to save the object
     */
    public static void saveObjectToFile(Object object, String filePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Load and deserialize an object file, given by its path, and cast it to the appropriate type, then return it
     * @param filePath Path of the serialized object file
     * @param type Class<T> representing the type of the object
     * @param <T> Type of the object to deserialize
     * @return The actual instance of the serialized object
     */
    public static <T> T loadFromFile(String filePath, Class<T> type) {
        T object = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            object = type.cast(objectInputStream.readObject());
            objectInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return object;
    }

}
