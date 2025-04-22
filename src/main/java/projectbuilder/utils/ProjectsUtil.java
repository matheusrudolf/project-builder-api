package projectbuilder.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Map;

public class ProjectsUtil {

    public ProjectsUtil() {}

    public static String returnNavigationToDocumentsFolder(Map<String, Object> response) {
        String documentsPath = System.getProperty("user.home") + File.separator + "Documents";
        File documentsFolder = new File(documentsPath);
        if (!documentsFolder.exists()) {
            response.put("message", "'Documents' folder not found on the system.");
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            return null;
        }

        return documentsPath;
    }

}
