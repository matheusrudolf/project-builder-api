package projectbuilder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import projectbuilder.services.ProjectCreator;
import projectbuilder.services.ProjectVerifyVersions;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/web-projects")
public class MainController {

    @Autowired
    ProjectCreator projectCreator;

    @Autowired
    ProjectVerifyVersions projectVerifyVersions;

    @GetMapping("/handle-output-creation")
    public ResponseEntity<Map<String, Object>> handelOutputCreation() {
        return projectCreator.catchOutputProject();
    }

    @GetMapping("/verify-node-version")
    public ResponseEntity<Map<String, Object>> getNodeVersion() {
        return projectVerifyVersions.handleNodeVersion();
    }

    @GetMapping("/verify-angular-version")
    public ResponseEntity<Map<String, Object>> getAngularVersion() {
        return projectVerifyVersions.handleAngularVersion();
    }

    @PostMapping("/install-angular-cli")
    public ResponseEntity<Map<String, Object>> installAngular(@RequestParam("version") String version) {
        return projectCreator.installAngular(version);
    }

    @PostMapping("/create-angular-project")
    public ResponseEntity<Map<String, Object>> createAngularProject(
            @RequestParam("stylesheet") String stylesheet,
            @RequestParam("ssrEnabled") String ssrEnabled,
            @RequestParam("nameProject") String nameProject) {
        return projectCreator.generateAngular(stylesheet, ssrEnabled, nameProject);
    }

}
