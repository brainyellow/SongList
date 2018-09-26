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
        
        ReadFromJSON();
        songTable.setItems(songs);
        songTable.refresh();
        songTable.getSortOrder().add(colTitle);
        songTable.getSortOrder().add(colArtist);
        songTable.getSelectionModel().selectFirst();

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
                    System.out.println("invalid year XD!!!!!");// error
            }
        } else {
            // error message : no title or artist
        }

        if (!isDuplicate(current)) {
            songs.add(current);
            songTable.setItems(songs);
            songTable.getSortOrder().add(colTitle);
            songTable.getSortOrder().add(colArtist);
            clearFields();

            Gson gson = new Gson();
            String jsonText = gson.toJson(songs);
            System.out.println(jsonText);
            WriteToJSON();
            Notification("Song Added", Color.web("#10d354"));
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
                System.out.println("invalid year LOL!");

            songTable.setItems(songs);
            songTable.getSortOrder().add(colTitle);
            songTable.getSortOrder().add(colArtist);
            
        }
        songTable.refresh();
        WriteToJSON();
    }

    // removes song from list
    public void removeSong(ActionEvent e) {
        songs.remove(current);
        songTable.setItems(songs);
        songTable.refresh();
        clearFields();
        WriteToJSON();
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
    		Gson gson = new GsonBuilder().create();
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
    public void Notification(String msg, Color color){
    	
    	
    	
    	notifRectangle.setFill(color);
    	notifText.setText(msg);
    	
    	
        TranslateTransition tRect = new TranslateTransition(Duration.millis(1000), notifRectangle);
        tRect.setFromY(0);
        tRect.setToY(41);
        tRect.setCycleCount(2);
        tRect.setAutoReverse(true);
        
        TranslateTransition tText = new TranslateTransition(Duration.millis(1000), notifText);
        tText.setFromY(-41);
        tText.setToY(0);
        tText.setCycleCount(2);
        tText.setAutoReverse(true);
        
        
        Timeline playtime = new Timeline(
        	    new KeyFrame(Duration.seconds(0), event -> tRect.play()),
        	    new KeyFrame(Duration.seconds(0), event -> tText.play()),
        	    new KeyFrame(Duration.seconds(1), event -> tRect.pause()),
        	    new KeyFrame(Duration.seconds(1), event -> tText.pause()),
        	    new KeyFrame(Duration.seconds(4), event -> tRect.play()),
        	    new KeyFrame(Duration.seconds(4), event -> tText.play())
        	);
        	playtime.playFromStart();
        	
 
        //tRect.play();
        //tText.play();
        
        

    }
}