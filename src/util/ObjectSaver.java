package util;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * A helper class that contains object saving static methods for Serializable objects.
 * @author Acemad
 */
public class ObjectSaver {

    /**
     * Save a given object to the given path. Creates the file if it does not exist and overwrites it if it does.
     * @param object
     * @param filePath
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

}
