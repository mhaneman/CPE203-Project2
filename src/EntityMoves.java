import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class EntityMoves extends EntityAnimates
{
    public EntityMoves(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                animationPeriod);
    }

    protected abstract boolean _nextPosition(WorldModel worldModel, Point newPos, Optional<Entity> occupant);

    protected Point nextPosition(Point destPos, WorldModel worldModel)
    {
        int horiz = Integer.signum(destPos.x - position.x);
        Point newPos = new Point(position.x + horiz, position.y);

        Optional<Entity> occupant = worldModel.getOccupant(newPos);

        if (horiz == 0 || _nextPosition(worldModel, newPos, occupant))
        {
            int vert = Integer.signum(destPos.y - position.y);
            newPos = new Point(position.x, position.y + vert);
            occupant = worldModel.getOccupant(newPos);

            if (vert == 0 || _nextPosition(worldModel, newPos, occupant))
            {
                newPos = position;
            }
        }

        return newPos;
    }

    protected abstract void _moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position))
        {
            _moveTo(world, target, scheduler);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(target.position, world);

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
}
