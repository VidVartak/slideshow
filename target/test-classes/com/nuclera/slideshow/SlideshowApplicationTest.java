package com.nuclera.slideshow;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxAssert.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

public class SlideshowApplicationTest extends TestFXBase{
    final String EXIT_BUTTON_ID="#exitButton";
    final String PLAY_PAUSE_BUTTON_ID="#playPauseButton";
//    @Mock
//    DirectoryChooser directoryChooser = Mockito.mock(DirectoryChooser.class);

    @Test(expected=FxRobotException.class)
    public void clickOnBogusElement(){
        System.out.println("Fake button test running");
        clickOn("#fakeButton");
    }

    @Test
    public void clickOnPlayPause(){
        System.out.println("Play/Pause button test running");
        clickOn(PLAY_PAUSE_BUTTON_ID);
        FxAssert.verifyThat("#statusLabel", (Label label)->{
            String text = label.getText();
            return text.contains("Please press Browse to choose a folder where the images are");
        });
    }

    @Test
    public void checkStopButtonExists(){
        System.out.println("Stop button existance test running");
        FxAssert.verifyThat("#restartButton", (Button button)->{
            String text = button.getText();
            return text.contains("■");
        });
    }

    @Test
    public void checkBrowseButtonExists(){
        System.out.println("Browse button existance test running");
        FxAssert.verifyThat("#browseButton", (Button button)->{
            String text = button.getText();
            return text.contains("Browse");
        });
    }

    @Test
    public void checkExitButtonExists(){
        System.out.println("Exit button existance test running");
        FxAssert.verifyThat("#exitButton", (Button button)->{
            String text = button.getText();
            return text.contains("Exit");
        });
    }

    //    @Test
    public void clickOnExit(){
        System.out.println("Exit button test running");
        sleep(5000);
        clickOn(EXIT_BUTTON_ID);
    }

    public void testBrowseButton(){
        //This test is not currently working due to "cannot mock directoryChooser" error.
        System.out.println("Browse button test running");
        clickOn("#browseButton");
//        Mockito.when(directoryChooser.showDialog(Mockito.anyObject())).thenReturn(new File("dummy"));
    }
}
