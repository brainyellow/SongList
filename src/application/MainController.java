package application;

import javafx.scene.control.TableColumn;
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



    private String title, artist, album, year;

    ObservableList<Song> songs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
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
    }




    public void addSong(ActionEvent e){
        Song songHolder = new Song(titleField.getText(), artistField.getText(), yearField.getText(), albumField.getText());
        System.out.println(songHolder.getTitle());
        songs.add(songHolder);
        songTable.setItems(songs);

        

    }
    public void updateSong(ActionEvent e){
        
    }

    public void removeSong(ActionEvent e){
        
    }



}