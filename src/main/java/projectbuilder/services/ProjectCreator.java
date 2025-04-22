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
public class ProjectCreator {

    private volatile String currentOutput = "";
    private volatile boolean isPostRunning = false;

    public ResponseEntity<Map<String, Object>> catchOutputProject() {
        Map<String, Object> response = new HashMap<>();
        response.put("isPostRunning", isPostRunning);
        response.put("currentOutput", currentOutput);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Map<String, Object>> installAngular(String version) {
        Map<String, Object> response = new HashMap<>();
        try {
            String documentsPath = ProjectsUtil.returnNavigationToDocumentsFolder(response);
            if (documentsPath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("npm", "install", "@angular/cli@" + version);
            processBuilder.directory(new File(documentsPath));

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                response.put("message", "Angular CLI version " + version + " installed succesfully.");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "Failed to install Angular CLI version " + version + ". Please check the logs for more details.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (IOException | InterruptedException e) {
            response.put("message", "An error ocurred while installing Angular CLI");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> generateAngular(String stylesheet, String ssrEnabled, String nameProject) {
        Map<String, Object> response = new HashMap<>();
        isPostRunning = true;

        try {
            String documentsPath = ProjectsUtil.returnNavigationToDocumentsFolder(response);
            String angularProjectPath = documentsPath + File.separator + "projects-angular";
            File angularProjectsFolder = new File(angularProjectPath);
            if (!angularProjectsFolder.exists()) {
                angularProjectsFolder.mkdirs();
            }

            System.setProperty("user.dir", angularProjectPath);

            String angularCli = "ng new " + nameProject + " --style=" + stylesheet + " --ssr=" + ssrEnabled;
            ProcessBuilder ngNewProcessBuilder = new ProcessBuilder("cmd", "/c", angularCli);
            ngNewProcessBuilder.directory(angularProjectsFolder);

            Process ngNewProcess = ngNewProcessBuilder.start();

            // Thread para ler a saída do processo
            new Thread(() -> {
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(ngNewProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        System.out.println(line);
                        currentOutput = output.toString();
                    }
                } catch (IOException e) {
                    e.fillInStackTrace();
                } finally {
                    isPostRunning = false;
                }
            }).start();

            int ngNewExitCode = ngNewProcess.waitFor();

            if (ngNewExitCode == 0) {
                response.put("message", "Angular project created successfully!");
            } else {
                response.put("message", "Failed to create a Angular project. Exit code: " + ngNewExitCode);
            }

            response.put("isPostRunning", isPostRunning);
            response.put("currentOutput", currentOutput);
            return ResponseEntity.ok(response);

        } catch (IOException | InterruptedException e) {
            response.put("message", "Error to execute a command: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
