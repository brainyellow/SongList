// Tyler Latawiec
// Brian Huang
package application;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;

import com.google.gson.Gson;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Rectangle;

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
    private Rectangle topBar;

    
    // List of songs
    private ObservableList<Song> songs = FXCollections.observableArrayList();

    //Used for adding songs/checking for dupicates
    private Song current;


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
    }
    
    // adds song to list
    public void addSong(ActionEvent e) {
        if (titleField.getText() != null && artistField.getText() != null) {
            current = new Song(titleField.getText(), artistField.getText());

            if (albumField.getText() != null) {
                current.setAlbum(albumField.getText());
            }
            if (yearField.getText() != null) {
                if (isNum(yearField.getText()))
                current.setYear(yearField.getText());
                else
                    System.out.println("invalid year");// error
            }
        } else {
            // error message : no title or artist
        }

        if (!isDuplicate(current)) {
            songs.add(current);
            songTable.setItems(songs);
            clearFields();

            Gson gson = new Gson();
            String jsonText = gson.toJson(songs);
            System.out.println(jsonText);
        }
    }
    // updates currently selected song
    public void updateSong(ActionEvent e) {

        // updatedSong will hold the values in Artist and Title fields
        Song updatedSong = new Song(titleField.getText(), artistField.getText());

        // It is then checked if it is a duplicate, if not, it will proceed the update
        if (!isDuplicate(updatedSong)) {

            current.setTitle(titleField.getText());
            current.setArtist(artistField.getText());

            current.setAlbum(albumField.getText());

            if (isNum(yearField.getText()))
            current.setYear(yearField.getText());
            else
                System.out.println("invalid year");

            songTable.setItems(songs);
            
        }
        songTable.refresh();
    }

    // removes song from list
    public void removeSong(ActionEvent e) {
        songs.remove(current);
        songTable.setItems(songs);
        songTable.refresh();
        clearFields();
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
            Integer.parseInt(str);  // testing if number
            return true;
        } catch (NumberFormatException nfe) {
            return false;           // invalid number (or year in this case)
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

}