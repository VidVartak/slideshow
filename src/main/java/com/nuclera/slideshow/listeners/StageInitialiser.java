package com.nuclera.slideshow.listeners;

import com.nuclera.slideshow.events.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageInitialiser implements ApplicationListener<StageReadyEvent> {
    @Value("classpath:/slideshow.fxml")
    private Resource slideshowResource;

    private String stageTitle;

    public StageInitialiser(@Value("${stage.title}") String stageTitle,
            ApplicationContext applicationContext) {
        this.stageTitle = stageTitle;
        this.applicationContext = applicationContext;
    }

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(slideshowResource.getURL());
            fxmlLoader.setControllerFactory(myClass -> applicationContext.getBean(myClass));
            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            stage.setScene(new Scene(parent, 975, 760));
            stage.setTitle(stageTitle);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
