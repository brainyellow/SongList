// Tyler Latawiec
// Brian Huang
package application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.*;

public class MainController {
    @FXML
    private TextField titleField;
    @FXML
    private TextField artistField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField albumField;

    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> colTitle;
    @FXML
    private TableColumn<Song, String> colArtist;

    @FXML
    private Rectangle notifRectangle;
    @FXML
    private Label notifText;
    @FXML
    private Rectangle windowBar;

    @FXML
    private Button undoButton;

    // List of songs
    private ObservableList<Song> songs = FXCollections.observableArrayList();
    private ObservableList<Song> songsCopy = FXCollections.observableArrayList();

    // Used for adding songs/checking for duplicates, variable current (Song) will change according to what song is selected in the table
    private Song current;

    // controls used for animations
    private TranslateTransition tRect;
    private TranslateTransition tText;
    private Timeline playtime;

    private boolean undoReady = false;

    // Web color strings, used for our notification system
    private final String NotifyGreen = "#10d354";       // Green  - indicates successful action
    private final String NotifyOrange = "#eda634";      // Orange - indicates a problem with an action
    private final String NotifyRed = "#f44242";         // Red    - indicates removal of a song

    private int selectIndex;


    @FXML
    public void initialize() {

        // setting correct data types of the columns of TableView
        colTitle.setCellValueFactory(new PropertyValueFactory<Song, String>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<Song, String>("artist"));

        // Listener for Table Selections, when a row in the table is selected, the song
        // fields are updated
        songTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                current = newSelection;
                titleField.setText(current.getTitle());
                artistField.setText(current.getArtist());
                yearField.setText(current.getYear());
                albumField.setText(current.getAlbum());
            }
        });

        // Read from JSON and sort songs in table
        ReadFromJSON();
        songTable.setItems(songs);
        songTable.refresh();
        songTable.getSortOrder().add(colTitle);
        songTable.getSortOrder().add(colArtist);
        songTable.getSelectionModel().selectFirst();

        // Initialize notification animations
        tRect = new TranslateTransition(Duration.millis(600), notifRectangle);  // animation for rectange (600ms)
        tRect.setFromY(0);          
        tRect.setToY(30);               // drop down 30 pixels
        tRect.setCycleCount(2);         // will reverse one time (for it to rise up)
        tRect.setAutoReverse(true);
        tText = new TranslateTransition(Duration.millis(600), notifText);
        tText.setFromY(0);
        tText.setToY(30);
        tText.setCycleCount(2);
        tText.setAutoReverse(true);

        // Timeline for timing of drop down notification
        playtime = new Timeline(new KeyFrame(Duration.seconds(0), event -> tRect.play()),
                new KeyFrame(Duration.seconds(0), event -> tText.play()),
                new KeyFrame(Duration.seconds(.6), event -> tRect.pause()),     // Rectangle and label will stop at bottom of drop
                new KeyFrame(Duration.seconds(.6), event -> tText.pause()),
                                                                                // We intend to give the user 3 seconds to read notification ( .6sec -> 3.6sec )
                new KeyFrame(Duration.seconds(3.6), event -> tRect.play()), 
                new KeyFrame(Duration.seconds(3.6), event -> tText.play()));    // Rectangle and label will rise to top

    }

    // Method to add song to list
    public void addSong(ActionEvent e) {
        if (!titleField.getText().isEmpty() && !artistField.getText().isEmpty()) {  // if both fields contain text

            // dupCheck holds title and artist 
            Song dupCheck = new Song(titleField.getText().trim(), artistField.getText().trim());

            if (!isDuplicate(dupCheck)) {
                //check if current is not part of library
                current = dupCheck;
                current.setAlbum(albumField.getText().trim());

                if ((isNum(yearField.getText()) || yearField.getText().isEmpty())) {     //if year field is blank or is a number 

                    // setting undo state in songsCopy
                    songsCopy.setAll(songs);
                    undoReady = true;
                    undoButton.setDisable(false);

                    // Setting year in new potential song
                    current.setYear(yearField.getText().trim());
                    // Add song into current list of songs
                    songs.add(current);

                    // Refreshing and resorting table
                    RefreshSongs();
                    songTable.getSelectionModel().select(current);

                    // Showing custom notification for success
                    Notification("Song Added", Color.web(NotifyGreen));

                } else {    // If year isn't a valid number, show notification and clear year field
                    Notification("Invalid Year", Color.web(NotifyOrange));
                    yearField.clear();
                }
            } else {  // if potential song would be a duplicate
                Notification("Song is already in your library", Color.web(NotifyOrange));
            }
        } else  // if a song or artist field is blank, cannot add to library since requirements are not met
            Notification("No Title or Artist entered", Color.web(NotifyOrange));   // Display notification to show problem
    }

    // Method for updating song
    public void updateSong(ActionEvent e) {

        if (songTable.getSelectionModel().getSelectedItem() == null) {          // if no song is selected in the table (shouldn't be possible but just in case)
            Notification("No song selected to update", Color.web(NotifyOrange));
        } 
        else if (titleField.getText().isEmpty() || artistField.getText().isEmpty()) {       // if user tries to delete a song's title or artist
            Notification("Cannot delete a title or artist field", Color.web(NotifyOrange));
            titleField.setText(current.getTitle());
            artistField.setText(current.getArtist());
        } 
        else {
            // updatedSong (Song object) will hold the values in Artist and Title fields
            Song updatedSong = new Song(titleField.getText(), artistField.getText());

            // It is then checked if it is a duplicate, if not, it will proceed the update
            if (!isDuplicate(updatedSong) || (updatedSong.getTitle().equalsIgnoreCase(current.getTitle())
                    && updatedSong.getArtist().equalsIgnoreCase(current.getArtist()))) {

 
                // Will replace every song in the copy list to the current list
                // (Updating an attribute of a song and then replacing with the copy list (an undo action) had no result, going through and making a true copy of each
                // song in the list was the only solution we could come to for our undo action to work)
                songsCopy.clear();
                for (Song s : songs){
                    songsCopy.add(new Song(s.getTitle(), s.getArtist(), s.getYear(), s.getAlbum() ));
                }
                undoReady = true;
                undoButton.setDisable(false);


                // Changing attributes of currently selected song to what the user has in the respected fields
                current.setTitle(titleField.getText().trim());
                current.setArtist(artistField.getText().trim());
                current.setAlbum(albumField.getText().trim());



                if (isNum(yearField.getText()) || yearField.getText().isEmpty()) {      // if text in year field is a number, or is blank, proceed with update

                    current.setYear(yearField.getText().trim());

                    RefreshSongs();
                    selectIndex = songTable.getSelectionModel().getSelectedIndex();

                    Notification("Song details updated", Color.web(NotifyGreen));
                } else {  
                    // if user's year field is invalid, other attributes will be updated but year will update to blank
                    
                    yearField.clear();

                    RefreshSongs();
                    selectIndex = songTable.getSelectionModel().getSelectedIndex();

                    Notification("Song details updated, however year was invalid", Color.web(NotifyOrange));
                }
            } else {       // if users updated title and artist field matches a different song in library
                Notification("Cannot Update, song is already in your library", Color.web(NotifyOrange));
            }
        }
    }

    // removes song from list
    public void removeSong(ActionEvent e) {
        if (current != null) {

            songsCopy.setAll(songs);
            undoReady = true;
            undoButton.setDisable(false);
            selectIndex = songTable.getSelectionModel().getSelectedIndex();
            songs.remove(current);
            current = null;

            songTable.getSelectionModel().select(selectIndex);
            current = songTable.getSelectionModel().getSelectedItem();

            RefreshSongs();
            clearFields();

            Notification("Song Removed", Color.web(NotifyRed));
        } else {
            Notification("No song to remove", Color.web(NotifyOrange));
        }

        if (current != null) {
            titleField.setText(current.getTitle());
            artistField.setText(current.getArtist());
            yearField.setText(current.getYear());
            albumField.setText(current.getAlbum());
        }
    }

    public void undoAction(ActionEvent e) {

        if (undoReady) {
            songs.clear();
            for (Song s: songsCopy) 
                songs.add(new Song(s.getTitle(), s.getArtist(), s.getYear(), s.getAlbum()));

            RefreshSongs();
            songTable.getSelectionModel().select(selectIndex);
            current = songTable.getSelectionModel().getSelectedItem();
            if (current != null) {
                titleField.setText(current.getTitle());
                artistField.setText(current.getArtist());
                yearField.setText(current.getYear());
                albumField.setText(current.getAlbum());
            }
            Notification("Last Action Reverted", Color.web(NotifyGreen));

            undoReady = false;
            undoButton.setDisable(true);
            if (songsCopy.size() <= 0) {
                clearFields();
                current = null;
            }
        } else {
            Notification("Undo failed", Color.web(NotifyOrange));
            clearFields();
        }

    }

    // clears text fields
    private void clearFields() {
        titleField.clear();
        artistField.clear();
        yearField.clear();
        albumField.clear();
    }

    // Checks if argument is a number
    private boolean isNum(String str) {
        try {
            Integer.parseInt(str); // testing if number
            return true;
        } catch (NumberFormatException nfe) {
            return false; // invalid number (or year in this case)
        }
    }

    // checks if a song is a duplicate of another song currently in the list
    private boolean isDuplicate(Song check) {
        for (Song s : songs) {
            if (s.getTitle().equalsIgnoreCase(check.getTitle()))
                if (s.getArtist().equalsIgnoreCase(check.getArtist()))
                    return true;
        }
        return false;
    }

    public void WriteToJSON() {

        try (Writer writer = new FileWriter("Songs.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(songs, writer);
        } catch (Exception e) {
            System.out.println("Could not make JSON file");
        }

    }

    public void ReadFromJSON() {
        try (Reader reader = new FileReader("Songs.json")) {
            List<Song> listOfSongs;

            Gson gson = new Gson();
            listOfSongs = gson.fromJson(reader, new TypeToken<List<Song>>() {}.getType());
            ObservableList<Song> asList = FXCollections.observableArrayList(listOfSongs);
            songs = asList;
        } catch (Exception noFile) {
            System.out.println(noFile);
        }
    }

    // Notification animation method, with the arguments color and message for the notification to display
    public void Notification(String msg, Color color) {

        notifRectangle.setFill(color);
        notifText.setText(msg);

        tRect.stop();       // stop commands, incase a notification is already showing, a new notification will start with the latest 
        tText.stop();       // action performed for better responsiveness for the user
        playtime.playFromStart();
    }

    // Method used for when a user action is successful, will refresh table with most recent songs and write to json
    public void RefreshSongs(){     
        songTable.setItems(songs);
        songTable.getSortOrder().add(colTitle);
        songTable.getSortOrder().add(colArtist);
        songTable.refresh();

        WriteToJSON();
    }
}