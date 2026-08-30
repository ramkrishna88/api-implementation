package com.example.configdemo;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BuildInfoController {

    private BuildInfo buildInfo;

    @GetMapping("/build-info")
    public String getBuildInfo() {
        return "Build ID: " + buildInfo.getId()
                + ", Version: " + buildInfo.getVersion()
                + ", Name: " + buildInfo.getName();
    }
}
