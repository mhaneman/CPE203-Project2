import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


interface Entity
{
    void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
    Action createAnimationAction(int repeatCount);
    int getAnimationPeriod();

    PImage getCurrentImage();
    int getActionPeriod();
    Point getPosition();
    int getImageIndex();
    List<PImage> getImages();
    void setPosition(Point position);
    void nextImage();

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
}
