import processing.core.PImage;

import java.util.List;

public abstract class EntityAction extends Entity
{
    protected int actionPeriod;
    public EntityAction(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images);

        this.actionPeriod = actionPeriod;
    }

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
    abstract void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);

    protected Action createActivityAction(WorldModel world,
                                       ImageStore imageStore)
    {
        return new Activity(this, world, imageStore);
    }
}
