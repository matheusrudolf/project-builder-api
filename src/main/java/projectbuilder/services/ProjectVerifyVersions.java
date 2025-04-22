package projectbuilder.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import projectbuilder.utils.ProjectsUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProjectVerifyVersions {

    public ResponseEntity<Map<String, Object>> handleNodeVersion() {
        Map<String, Object> response = new HashMap<>();

        try {
            String documentsPath = ProjectsUtil.returnNavigationToDocumentsFolder(response);
            if (documentsPath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("node", "-v");
            processBuilder.directory(new File(documentsPath));

            Process process = processBuilder.start();
            return returnMessageFromVerifyingVersions(process, response, "Node.js", "https://nodejs.org/en");

        } catch (IOException | InterruptedException e) {
            response.put("message", "An error occurred while checking for Node.js installation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> handleAngularVersion() {
        Map<String, Object> response = new HashMap<>();

        try {
            String documentsPath = ProjectsUtil.returnNavigationToDocumentsFolder(response);
            if (documentsPath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            ProcessBuilder processAngularVersion = new ProcessBuilder();
            processAngularVersion.command("ng.cmd", "--version");
            processAngularVersion.directory(new File(documentsPath));

            Process processAngularBuffer = processAngularVersion.start();
            return returnMessageFromVerifyingVersions(processAngularBuffer, response, "Angular", "https://angular.dev/");

        } catch (IOException | InterruptedException e) {
            response.put("message", "An error occurred while checking for Angular installation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private ResponseEntity<Map<String, Object>> returnMessageFromVerifyingVersions(Process process, Map<String, Object> response, String topic, String link)
            throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();
        String outputString = output.toString().trim();

        if (exitCode == 0) {
            response.put("message", topic + " is installed. Version: " + output.toString());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", topic + " is not installed. Go to " + link + ", and get a " + topic + " version is compatible.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
