import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OctoFull extends EntityOcto {


    public OctoFull( String id, Point position, List<PImage> images,
                     int resourceLimit, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(position, Atlantis.class);

        if (fullTarget.isPresent() &&
                moveTo(world, fullTarget.get(), eventScheduler))
        {
            //at atlantis trigger animation
            ((EntityAction)fullTarget.get()).scheduleActions(world, imageStore, eventScheduler);

            //transform to unfull
            transform(world, eventScheduler, imageStore);
        }
        else
        {
            eventScheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    actionPeriod);
        }
    }

    @Override
    Entity _transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return new OctoNotFull(this.id, this.position, this.images,
                this.resourceLimit, 0, this.actionPeriod, this.animationPeriod);
    }

    @Override
    void _moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
    }

    @Override
    boolean _nextPosition(WorldModel worldModel, Point newPos, Optional<Entity> occupant)
    {
        return worldModel.isOccupied(newPos);
    }
}
