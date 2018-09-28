// Tyler Latawiec
// Brian Huang
package application;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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

    
    // List of songs
    private ObservableList<Song> songs = FXCollections.observableArrayList();
    private ObservableList<Song> songsCopy = FXCollections.observableArrayList();

    //Used for adding songs/checking for duplicates
    private Song current;

    // controls used for animations
    private TranslateTransition tRect;
    private TranslateTransition tText;
    private Timeline playtime;
    
    private boolean undoReady = false;


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
        tRect = new TranslateTransition(Duration.millis(600), notifRectangle);
        tRect.setFromY(0);
        tRect.setToY(30);
        tRect.setCycleCount(2);
        tRect.setAutoReverse(true);
        tText = new TranslateTransition(Duration.millis(600), notifText);
        tText.setFromY(0);
        tText.setToY(30);
        tText.setCycleCount(2);
        tText.setAutoReverse(true);
        
        // Timeline for timing of drop down notification 
        playtime = new Timeline(
        	    new KeyFrame(Duration.seconds(0), event -> tRect.play()),
        	    new KeyFrame(Duration.seconds(0), event -> tText.play()),
        	    new KeyFrame(Duration.seconds(.6), event -> tRect.pause()), // Rectangle and label will stop at bottom of drop
        	    new KeyFrame(Duration.seconds(.6), event -> tText.pause()),
        	    new KeyFrame(Duration.seconds(3), event -> tRect.play()),   // Rectangle and label will rise to top
        	    new KeyFrame(Duration.seconds(3), event -> tText.play())
        	);
      
    }
    
    // adds song to list
    public void addSong(ActionEvent e) {
        if (!titleField.getText().isEmpty() && !artistField.getText().isEmpty()) {
            
            current = new Song(titleField.getText().trim(), artistField.getText().trim());
            
            if (!isDuplicate(current)) {
                if (albumField.getText() != null) {

                    current.setAlbum(albumField.getText().trim());
                }
                if ((yearField.getText() != null && isNum(yearField.getText()) || yearField.getText().isEmpty())) {    
                	
                    songsCopy.setAll(songs);
                    undoReady = true;

                	current.setYear(yearField.getText().trim());
                    songs.add(current);

                    songTable.setItems(songs);
                    songTable.getSortOrder().add(colTitle);
                    songTable.getSortOrder().add(colArtist);
                    songTable.getSelectionModel().select(current);

                    Gson gson = new Gson();
                    String jsonText = gson.toJson(songs);
                    System.out.println(jsonText);
                    WriteToJSON();

                    Notification("Song Added", Color.web("#10d354"));
                    }
                else {
                    Notification("Invalid Year", Color.web("#eda634"));
                    yearField.clear();
                }
            }
            else {
            	yearField.clear();
            	albumField.clear();
                Notification("Song is already in your library", Color.web("#eda634"));
            }
        }
        else
            Notification("No Title or Artist entered", Color.web("#eda634"));  
    }
    // updates currently selected song
    public void updateSong(ActionEvent e) {
    	if (songTable.getSelectionModel().getSelectedItem() == null)
    	{
    		Notification("No song selected to update", Color.web("#eda634"));
    	}
    	else if (titleField.getText().isEmpty() || artistField.getText().isEmpty()) {
    		Notification("Cannot delete a title or artist field", Color.web("#eda634"));
    		titleField.setText(current.getTitle());
    		artistField.setText(current.getArtist());
    	}
    	else
    	{
    		// updatedSong will hold the values in Artist and Title fields
            Song updatedSong = new Song(titleField.getText(), artistField.getText());

            // It is then checked if it is a duplicate, if not, it will proceed the update
            if (!isDuplicate(updatedSong) || (updatedSong.getTitle().equalsIgnoreCase(current.getTitle()) && updatedSong.getArtist().equalsIgnoreCase(current.getArtist()))) {
                songsCopy.setAll(songs);
                undoReady = true;

                current.setTitle(titleField.getText().trim());
                current.setArtist(artistField.getText().trim());
                current.setAlbum(albumField.getText().trim());
                
                if (isNum(yearField.getText()) || yearField.getText().isEmpty()){

                    current.setYear(yearField.getText().trim());
                    songTable.setItems(songs);
                    songTable.getSortOrder().add(colTitle);
                    songTable.getSortOrder().add(colArtist);
                    
                    songTable.refresh();
                    WriteToJSON();
                    Notification("Song details updated", Color.web("#10d354"));
                }
                else{
                    if (current.getYear() != null)
                    	yearField.setText(current.getYear());
                    else
                    	yearField.clear();
                    Notification("Invalid year", Color.web("#eda634"));
                }
            }
            else{
                Notification("Cannot Update, song is already in your library", Color.web("#eda634"));
            }
    	}
    }

    // removes song from list
    public void removeSong(ActionEvent e) {
        songsCopy.setAll(songs);
        undoReady = true;

        songs.remove(current);

        songTable.setItems(songs);
        songTable.refresh();

        clearFields();
        WriteToJSON();

        songTable.getSelectionModel().selectNext();
        current = songTable.getSelectionModel().getSelectedItem();
        
		if (current != null) {
			titleField.setText(current.getTitle());
			artistField.setText(current.getArtist());
			yearField.setText(current.getYear());
			albumField.setText(current.getAlbum());
		}
        
        
        Notification("Song Removed", Color.web("#f44242"));
    }
    
    public void undoAction(ActionEvent e) {
    	
    	if (undoReady){
    		songs.setAll(songsCopy);
        	songTable.setItems(songsCopy);
        	songTable.refresh();
        	
            Notification("Last Action Reverted", Color.web("#eda634"));

            undoReady = false;
    	}
    	else {
    		Notification("Already performed Undo action", Color.web("#eda634"));
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
    
    public void WriteToJSON(){

    	try (Writer writer = new FileWriter("Songs.json")) {
    		Gson gson = new GsonBuilder().setPrettyPrinting().create();
    		gson.toJson(songs, writer);
    	}
    	catch(Exception e){
    		System.out.println("Could not write JSON LOL!");
    	}

    }

    public void ReadFromJSON(){
        try(Reader reader = new FileReader("Songs.json")){
            List<Song> listOfSongs;

            Gson gson = new Gson();
            listOfSongs = gson.fromJson(reader, new TypeToken<List<Song>>(){}.getType());
            ObservableList<Song> asList = FXCollections.observableArrayList(listOfSongs);
            songs = asList;
        }
        catch(Exception noFile){
            System.out.println(noFile);
        }
    }

    //Error animation
    public void Notification(String msg, Color color) {

        notifRectangle.setFill(color);
        notifText.setText(msg);

        tRect.stop();
        tText.stop();
        playtime.playFromStart();
    }
}