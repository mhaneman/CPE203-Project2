import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Atlantis extends EntityAnimates {
    public static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int actionPeriod;
    private int animationPeriod;

    public Atlantis(String id, Point position,
                  List<PImage> images,
                  int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void executeActivity(WorldModel world,
                                        ImageStore imageStore, EventScheduler eventScheduler)
    {

        eventScheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }



    public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                getAnimationPeriod());
    }

    public int getAnimationPeriod()
    {
        return this.animationPeriod;
    }

    public int getActionPeriod() {
        return actionPeriod;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public List<PImage> getImages() {
        return images;
    }
}
