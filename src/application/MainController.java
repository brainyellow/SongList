package application;

import javafx.scene.control.TableColumn;

import com.google.gson.Gson;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


public class MainController{
    @FXML public TextField titleField;
    @FXML public TextField artistField;
    @FXML public TextField yearField;
    @FXML public TextField albumField;

    @FXML TableView<Song> songTable;
    @FXML TableColumn<Song, String> colTitle;
    @FXML TableColumn<Song, String> colArtist;
    @FXML TableColumn<Song, String> colYear;
    @FXML TableColumn<Song, String> colAlbum;


    private Song current;
    private Song songHolder;
    private String title, artist, album, year;

    ObservableList<Song> songs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
    	
    	// setting correct value types of the columns
        colTitle.setCellValueFactory(
            new PropertyValueFactory<Song,String>("title")
        );
        colArtist.setCellValueFactory(
            new PropertyValueFactory<Song,String>("artist")
        );
        colYear.setCellValueFactory(
            new PropertyValueFactory<Song,String>("year")
        );
        colAlbum.setCellValueFactory(
            new PropertyValueFactory<Song,String>("album")
        );

        // Listener for Table Selections, when a row in the table is selected, the song fields are updated
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

    public void addSong(ActionEvent e){
        if(titleField.getText() != null || artistField.getText() != null)
        {
            songHolder = new Song(titleField.getText(), artistField.getText());
            
            
            if(albumField.getText() != null)
            {
                songHolder.setAlbum(albumField.getText());
            }
            if(yearField.getText() != null)
            {
                if(isNum(yearField.getText()))
                    songHolder.setYear(yearField.getText());
                else
                    System.out.println("invalid year");//error
            } 
        }
        else
        {
            //error message : no title or artist
        }
        
        
        
        
        songs.add(songHolder);
        songTable.setItems(songs);
        clearFields();
        
        Gson gson = new Gson();
        String jsonText = gson.toJson(songs);
        System.out.println(jsonText);
    }
    public void updateSong(ActionEvent e){

        current.setTitle(titleField.getText());
        current.setArtist(artistField.getText());
        if(isNum(yearField.getText()))
            current.setYear(yearField.getText());
        else
            System.out.println("invalid year");
        current.setAlbum(albumField.getText());
        songTable.setItems(songs);
        songTable.refresh();
    }

    public void removeSong(ActionEvent e){
        songs.remove(current);
        songTable.setItems(songs);
        songTable.refresh();
        clearFields();
    }

    private void clearFields(){
        titleField.clear();
        artistField.clear();
        yearField.clear();
        albumField.clear();
    }

    private boolean isNum(String str)
    {   
        try  
        {  
            Integer.parseInt(str);      //testing if number
            return true;
        }  
        catch(NumberFormatException nfe)  
        {  
            // invalid year
            return false;
        }  
    }
    
    
}