package application;

import java.io.File;
import java.io.FileInputStream;

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

            FileInputStream fileStr = new FileInputStream(file); 
               int size = (int)file.length();
               fileStr.skip(size - 128); 
               byte[] last128 = new byte[128]; 
               fileStr.read(last128); 
               String id3 = new String(last128); 
               String tag = id3.substring(0, 3); 
               if (tag.equals("TAG")) { 
                  System.out.println("Title: " + id3.substring(3, 32)); 
                  System.out.println("Artist: " + id3.substring(33, 62)); 
                  System.out.println("Album: " + id3.substring(63, 91)); 
                  System.out.println("Year: " + id3.substring(93, 97)); 
               } 
               else {
                  System.out.println(" does not contain" 
                     + " ID3 info.");  }
            
        }
        else{
            System.out.println("Canceled");
        }
    }

}