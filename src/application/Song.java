// Brian Huang
// Tyler Latawiec
package application;

public class Song{

    private String title, artist, album, year;

    public Song(String newTitle, String newArtist, String newYear, String newAlbum)
    {
        this.title = newTitle;
        this.artist = newArtist;
        this.year = newYear;
        this.album = newAlbum;
    }

    public String getTitle(){
        return title;
    }
    public String getArtist(){
        return artist;
    }
    public String getYear(){
        return year;
    }
    public String getAlbum(){
        return album;
    }


    public void setTitle(String newTitle){
        this.title = newTitle;
    }
    public void setArtist(String newArtist){
        this.artist = newArtist;
    }
    public void setYear(String newYear){
        this.year = newYear;
    }
    public void setAlbum(String newAlbum){
        this.album = newAlbum;
    }
}