// Brian Huang
// Tyler Latawiec
package application;

public class Song{

    private String title, artist, album, year;

    public Song(String title, String artist)
    {
        this.title = title;
        this.artist = artist;
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


    public void setTitle(String title){
        this.title = title;
    }
    public void setArtist(String artist){
        this.artist = artist;
    }
    public void setYear(String year){
        this.year = year;
    }
    public void setAlbum(String album){
        this.album = album;
    }
}