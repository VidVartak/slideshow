package com.nuclera.slideshow;
import com.nuclera.slideshow.events.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SlideshowStage extends Application{
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(SlideshowApplication.class).run();
    }

    @Override
    public void stop(){
        applicationContext.close();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }
}
