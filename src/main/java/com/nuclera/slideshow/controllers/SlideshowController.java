package com.nuclera.slideshow.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SlideshowController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField folderTextField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button restartButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> delay;

    @Value("${slideshow.millisPerSlide}")
    private int millisPerSlide;

    private boolean slideshowRunning = false;
    private boolean slideshowPaused = false;

    private List<Path> fileNames = new ArrayList<>();

    private File imageFolder;

    class SlideshowThread extends Thread
    {
        boolean paused = false;

        @Override
        public void run() {
            showAllSlides();
            resetShow();
        }

        public void resetShow() {
            progressBar.setProgress(0);
            imageView.setImage(null);
            slideshowRunning = false;
            paused=true;
            Platform.runLater(
                    () -> {
                        setButtonToPlay();
                    }
            );
        }

        private void showAllSlides() {
            for (int i = 0; i < fileNames.size(); i++) {
                try{
                    double progress= (double)(i+1) / (double) fileNames.size();
                    progressBar.setProgress(progress);
                    Image image = new Image( fileNames.get(i).toUri().toString());
                    imageView.setImage(image);
                    final int j = i+1;
                    log.info("displayed image #"+j);
                    Platform.runLater(
                            () -> {
                                statusLabel.setText("Showing: "+fileNames.get(j-1).getFileName().toString()+" ("+(j)+"/"+fileNames.size()+")");
                                statusLabel.setTextFill(Color.valueOf("Blue"));
                            }
                    );
                    Thread.currentThread().sleep(Integer.valueOf(delay.getValue())*1000);
                    synchronized (this)
                    {
                        while (paused){
                            Thread.sleep(100);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        void suspendShow()
        {
            paused=true;
            log.info("suspended show");
        }

        void resumeShow()
        {
            paused = false;
            log.info("resumed show");
        }
    }

    private SlideshowThread th;

    private static final Logger log = LoggerFactory.getLogger(SlideshowController.class);

    private boolean runShow(){
        boolean success=false;
        if (imageFolder==null){
            statusLabel.setText("Please press Browse to choose a folder where the images are");
            statusLabel.setTextFill(Color.valueOf("Red"));
        } else {
            th = new SlideshowThread();
            th.start();
            success=true;
        }
        return success;
    }

    private void addFileNameToArray(Path filePath){
        fileNames.add(filePath);
    }
    private void stopShow(){
        if (th!=null && th.isAlive())
            th.resetShow();
/*        progressBar.setProgress(0.0);
        imageView.setImage(null);
        th.suspendShow();
        th.interrupt();
  */  }

    private void pauseShow(){
        th.suspendShow();
    }

    private void resumeShow(){
        th.resumeShow();
        log.info("delay chosen:"+delay.getValue());
    }

    public void browseAction(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        imageFolder = directoryChooser.showDialog(stage);

        if (imageFolder!=null){
            folderTextField.setText(imageFolder.getAbsolutePath());
            log.info("imageFolder path:"+imageFolder.getAbsolutePath());
            fileNames = new ArrayList<>();
//            Pattern imageMIMEPattern = Pattern. compile("image/.*");
            Pattern imageMIMEPattern = Pattern.compile("image/jpeg");
            Path p = imageFolder.toPath();
            try {
                // find all files with mime type image/... in subdirectories up to a depth of 10
                Files.find(p, 1, (path, attributes) -> {
                    String contentType;
                    try {
                        contentType = Files.probeContentType(path);
                    } catch (IOException ex) {
                        return false;
                    }
                    return contentType != null && imageMIMEPattern.matcher(contentType).matches();
                }).forEach(this::addFileNameToArray);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            if (fileNames.size()==0){
                statusLabel.setText("Selected folder doesn't contain any JPEG files. Please choose another folder.");
                statusLabel.setTextFill(Color.valueOf("Red"));
            } else {
                statusLabel.setText("Press the play (>) button to start the slide show");
                statusLabel.setTextFill(Color.valueOf("Blue"));
                if (slideshowRunning) {
                    restartSlideshow();
                }
                playPause();
            }
        }
    }

    public void playPauseAction(ActionEvent actionEvent) {
        playPause();
    }

    private void playPause() {
        boolean showStartedSuccessfully=true;
        if (slideshowPaused||!slideshowRunning){
            if (!slideshowRunning){
                showStartedSuccessfully=runShow();
            } else {
                resumeShow();
            }
            if (showStartedSuccessfully) {
                slideshowRunning=true;
                setButtonToPause();
                slideshowPaused = false;
            }
        } else {
            setButtonToPlay();
            pauseShow();
            slideshowPaused = true;
        }
    }

    private void setButtonToPause() {
        playPauseButton.setText("||");
        playPauseButton.setTooltip(new Tooltip("Pause the Slideshow"));
    }

    private void setButtonToPlay() {
        playPauseButton.setText(">");
        playPauseButton.setTooltip(new Tooltip("Play the Slideshow"));
    }

    public void restartSlideshow(){
        progressBar.setProgress(0.0);
        slideshowRunning=false;
        setButtonToPlay();
        slideshowPaused=false;
        stopShow();
    }
    public void restartAction(ActionEvent actionEvent) {
        restartSlideshow();
    }

    public void exitAction(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

}
