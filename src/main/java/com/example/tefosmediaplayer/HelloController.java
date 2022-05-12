package com.example.tefosmediaplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class HelloController implements Initializable {
    @FXML
    private VBox root;

    @FXML
    private MediaView mvVideo;
    private MediaPlayer mpVideo;
    private Media mediaVideo;

    @FXML
    private Slider sliderTime;

    @FXML
    private HBox hboxControls;

    @FXML
    private Button buttonPPR1;

    @FXML
    private Button buttonStop;

    @FXML
    private HBox hboxVolume;

    @FXML
    private Label lbVolume;

    @FXML
    private Slider sliderVolume;

    @FXML
    private Label lbCurrentTime;

    @FXML
    private Label lbTotalTime;

    private boolean atEndOfVideo = false;
    private boolean isPlaying = true;
    private boolean isMuted = true;

    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivStop;
    private ImageView ivVolume;
    private ImageView ivMute;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final int imageViewSize = 25; //size of all icons


        //create image video string
        //create media and media player
        String src = getClass().getResource("/ambulance.mp4").toExternalForm();
        mediaVideo = new Media(src);
        mpVideo = new MediaPlayer(mediaVideo);
        mvVideo.setMediaPlayer(mpVideo);
        mpVideo.play(); //start the video on Start


        //create all icon Strings
        //create images for icons and set height and width for icons

        String playIco = getClass().getResource("/play.png").toExternalForm();
        Image imgPlay = new Image(playIco);
        ivPlay = new ImageView(imgPlay);
        ivPlay.setFitWidth(imageViewSize);
        ivPlay.setFitHeight(imageViewSize);


        String pauseIco = getClass().getResource("/pause.png").toExternalForm();
        Image imgPause = new Image(pauseIco);
        ivPause = new ImageView(imgPause);
        ivPause.setFitWidth(imageViewSize);
        ivPause.setFitHeight(imageViewSize);

        String stopIco = getClass().getResource("/stop.png").toExternalForm();
        Image imgStop = new Image(stopIco);
        ivStop = new ImageView(imgStop);
        ivStop.setFitWidth(imageViewSize);
        ivStop.setFitHeight(imageViewSize);

        String volumeIco = getClass().getResource("/volume.png").toExternalForm();
        Image imgVolume = new Image(volumeIco);
        ivVolume = new ImageView(imgVolume);
        ivVolume.setFitWidth(imageViewSize);
        ivVolume.setFitHeight(imageViewSize);

        String muteIco = getClass().getResource("/mute.png").toExternalForm();
        Image imgMute = new Image(muteIco);
        ivMute = new ImageView(imgMute);
        ivMute.setFitWidth(imageViewSize);
        ivMute.setFitHeight(imageViewSize);

        //set icons and values for button and volume slider for on start.
        buttonPPR1.setGraphic(ivPause);
        buttonPPR1.setBackground(null);
        lbVolume.setGraphic(ivVolume);
        buttonStop.setGraphic(ivStop);
        buttonStop.setBackground(null);
        sliderVolume.setValue(0.5);
        isMuted=false;

        //determine which icon show (play or pause) and the actions they perform
        buttonPPR1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button buttonPlay = (Button) actionEvent.getSource();
                if (atEndOfVideo){
                    sliderTime.setValue(0);
                    atEndOfVideo = false;
                    isPlaying = false;
                }
                if (isPlaying) {
                    buttonPlay.setGraphic(ivPlay);
                    mpVideo.pause();
                    isPlaying = false;
                } else {
                    buttonPlay.setGraphic(ivPause);
                    mpVideo.play();
                    isPlaying = true;
                }
            }
        });
        //stop video when stop button is pressed.
        //set slider time to beginning
        buttonStop.setOnMouseClicked(event -> {
            mpVideo.stop();
            sliderTime.setValue(0);
            isPlaying = false;
            buttonPPR1.setGraphic(ivPlay);
        });

        hboxVolume.getChildren().remove(sliderVolume); //hide volume slider on start

        mpVideo.volumeProperty().bindBidirectional(sliderVolume.valueProperty());

        //function to get current time from the video and display it on the label
        bindCurrentTimeLabel();


        //determine what icon shoes when starting the player
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mpVideo.setVolume(sliderVolume.getValue());

                if(mpVideo.getVolume() != 0.0){
                    lbVolume.setGraphic(ivVolume);
                    isMuted = false;
                } else{
                    lbVolume.setGraphic(ivMute);
                    isMuted = true;
                }
            }
        });

        //change volume icon to mute and vice versa when clicked
        lbVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( isMuted){
                    lbVolume.setGraphic(ivVolume);
                    sliderVolume.setValue(0.5);
                    isMuted = false;
                }else {
                    lbVolume.setGraphic(ivMute);
                    sliderVolume.setValue(0);
                    isMuted = true;
                }
            }
        });

        //show  volume slider when drag mouse over it
        lbVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (hboxVolume.lookup("#sliderVolume") == null){
                    hboxVolume.getChildren().add(sliderVolume);
                    sliderVolume.setValue(mpVideo.getVolume());
                }
            }
        });

        //hide vo;ume slider and show when hover over the volume section
        hboxVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hboxVolume.getChildren().remove(sliderVolume);
            }
        });

        //enable to drag slider to seek video
        mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                sliderTime.setMax(newValue.toSeconds());
                lbTotalTime.setText(getTime(newValue));
            }
        });

        sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasChanging, Boolean isChanging) {
                if(isChanging) {
                    mpVideo.seek(Duration.seconds(sliderTime.getValue()));
                }
            }
        });

        sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double currentTime = mpVideo.getCurrentTime().toSeconds();

                if(Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                    mpVideo.seek((Duration.seconds(newValue.doubleValue())));
                }
            }
        });

        //slider to move
        mpVideo.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                sliderTime.setValue(newValue.toSeconds());
            }
        });

        sliderTime.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mpVideo.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        sliderTime.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mpVideo.seek(Duration.seconds(sliderTime.getValue()));
            }
        });

        //set video time to be the length of the slide
        mpVideo.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = mediaVideo.getDuration();
                sliderTime.setMax(total.toSeconds());
            }
        });


        //change icon to play and stop video at end of video
        mpVideo.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                buttonPPR1.setGraphic(ivPlay);
                mpVideo.stop();
                sliderTime.setValue(0);
                isPlaying=false;
            }
        });

    }
    public void bindCurrentTimeLabel(){
        lbCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mpVideo.getCurrentTime()) + "/";
            }
        },mpVideo.currentTimeProperty()));
    }

    public String getTime(Duration time){
        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        if(hours > 0 ) return String.format("%d:%02d%02d",
                hours, minutes, seconds);

        else return String.format("%02d:%02d",
                minutes,seconds);
    }
}