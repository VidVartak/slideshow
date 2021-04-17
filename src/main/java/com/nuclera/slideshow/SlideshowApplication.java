package com.nuclera.slideshow;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SlideshowApplication{

    public static void main(String[] args) {
        Application.launch(SlideshowStage.class, args);
    }

}
