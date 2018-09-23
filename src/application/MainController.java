package application;

import java.io.File;

import javax.swing.JFileChooser;

import javafx.event.ActionEvent;

public class MainController{

    JFileChooser jf = new JFileChooser();

    public void AddButtonClick(ActionEvent e){
        int ret = jf.showOpenDialog(null);

        if (ret == 0){
            System.out.println("File chosen");
            File file = jf.getSelectedFile();
            System.out.println("Selected file location: " + file.getPath());
        }
        else{
            System.out.println("Canceled");
        }
    }

}