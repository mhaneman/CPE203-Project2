import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Obstacle extends Entity {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;

    public Obstacle(String id, Point position, List<PImage> images)
    {
        super(id, position, images);
    }
}
