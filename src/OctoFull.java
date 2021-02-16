import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OctoFull extends EntityOcto {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int actionPeriod;
    private int animationPeriod;

    public OctoFull( String id, Point position,
                  List<PImage> images, int resourceLimit,
                     int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public boolean moveToFull(WorldModel world,
                              Entity target, EventScheduler scheduler)
    {
        if (this.position.adjacent(target.getPosition()))
        {
            return true;
        }
        else
        {
            Point nextPos = nextPositionOcto(target.getPosition(), world);

            if (!this.position.equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public void transformFull(WorldModel world,
                              EventScheduler scheduler, ImageStore imageStore)
    {
        Entity octo = new OctoNotFull(this.id, this.position, this.images,
                this.resourceLimit, 0, this.actionPeriod, this.animationPeriod);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(octo);
        ((EntityAction)octo).scheduleActions(world, imageStore, scheduler);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), Atlantis.class);

        if (fullTarget.isPresent() &&
                moveToFull(world, fullTarget.get(), eventScheduler))
        {
            //at atlantis trigger animation
            ((EntityAction)fullTarget.get()).scheduleActions(world, imageStore, eventScheduler);

            //transform to unfull
            transformFull(world, eventScheduler, imageStore);
        }
        else
        {
            eventScheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
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
