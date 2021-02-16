import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


public abstract class Entity
{


    abstract PImage getCurrentImage();
    abstract int getActionPeriod();
    abstract Point getPosition();
    abstract int getImageIndex();
    abstract List<PImage> getImages();
    abstract void setPosition(Point position);
    abstract void nextImage();
}
