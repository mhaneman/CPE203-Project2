import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class EntityOcto extends EntityMoves
{
    protected int resourceLimit;
    public EntityOcto(String id, Point position, List<PImage> images, int resourceLimit, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    abstract Entity _transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    protected boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (_transform(world, scheduler, imageStore) != null)
        {
            Entity octo = _transform(world, scheduler, imageStore);
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(octo);
            ((EntityAction)octo).scheduleActions(world, imageStore, scheduler);

            return true;
        }

        return false;
    }

}
