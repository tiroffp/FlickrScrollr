import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.MouseEvent;
import java.util.LinkedList;
import java.util.Objects;

public class Display extends PApplet {
    LinkedList<PGraphics> images = new LinkedList<>();
    //placement of top photo in app
    int placement = 0;
    //Page being searched for next photo
    private int pageNumber = 1; //INVARIANT never negative
    //Flickr4Java instance that is being used to to search for photos
    private Flickr flickr = new Flickr("7842c0631891898b5830354d8d8f655c", "fdd3276cb2c16494", new REST());

    //Measurements for size of app window
    private static final int APP_WIDTH = 1024;
    private static final int APP_HEIGHT = 1024;
    private static final String SEARCH_TAG_0 = "Boston";


    //initialize the app size
    public void settings() {
        size(APP_WIDTH,APP_HEIGHT);
    }
    //sets the initial display,
    public void setup() {
        background(0);
        PGraphics initImage = createGraphics(1000, 50);
        initImage.beginDraw();
        initImage.textSize(16);
        initImage.textAlign(CENTER);
        initImage.text("Scroll down to see images", 500, 25);
        initImage.endDraw();
        images.push(initImage);
    }
    // retrieve the first photo found under the given tags
    public Photo retrievePhoto(String... args) throws FlickrException {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setAccuracy(1);
        searchParameters.setTags(args);
        PhotoList<Photo> list = new PhotoList<>();
        while(list.isEmpty()) {
            list = flickr.getPhotosInterface().search(searchParameters, 1, pageNumber);
            pageNumber += 1;
        }
        return list.get(0);
    }
    // draw the current display of the app
    public void draw() {
        background(0);
        stroke(255);
        int buffer = 0;
        for(PImage curr : images) {
            image(curr, (APP_WIDTH / 2) - (curr.width / 2), placement + buffer);
            buffer = buffer + curr.height + 100;
        }
    }

    @Override
    // Move the current photos down and load a new photo if needed
    public void mouseWheel(MouseEvent event) {
        int e = event.getCount();
        placement += e * 10;
        if(placement > 100) {
            try {
                println("loading");
                Photo photo = retrievePhoto(SEARCH_TAG_0);
                PImage newImage = loadImage(photo.getLargeUrl(), photo.getOriginalFormat());
                PGraphics newGraphic = createGraphics(newImage.width, newImage.height);
                newGraphic.beginDraw();
                newGraphic.textSize(16);
                newGraphic.background(newImage);
                if(!Objects.isNull(photo.getTitle())) {
                    newGraphic.text(photo.getTitle(), 5, 20);
                }
                if(!Objects.isNull(photo.getOwner()) && !Objects.isNull(photo.getOwner().getUsername())) {
                    newGraphic.text(photo.getOwner().getUsername(), 5, 50);
                }
                newGraphic.endDraw();
                images.push(newGraphic);
                placement = -newImage.height;
            } catch (FlickrException e1) {
                PGraphics errorImage = createGraphics(100, 10);
                errorImage.beginDraw();
                errorImage.text("No photos to display", 0,0);
                errorImage.endDraw();
                images.push(errorImage);
                placement = -100;
            }

        }
    }
    // main method of the app
    static public void main(String... args) {
        PApplet.main("Display");
    }
}
