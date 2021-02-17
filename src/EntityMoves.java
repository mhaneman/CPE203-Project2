import java.util.Optional;

public abstract class EntityMoves extends EntityAnimates
{
    protected void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                getAnimationPeriod());
    }

    abstract boolean _nextPosition(WorldModel worldModel, Point newPos, Optional<Entity> occupant);

    public Point nextPosition(Point destPos, WorldModel worldModel)
    {
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz,
                getPosition().y);

        Optional<Entity> occupant = worldModel.getOccupant(newPos);

        if (horiz == 0 || _nextPosition(worldModel, newPos, occupant))
        {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);
            occupant = worldModel.getOccupant(newPos);

            if (vert == 0 || _nextPosition(worldModel, newPos, occupant))
            {
                newPos = getPosition();
            }
        }

        return newPos;
    }

    abstract void _moveTo(WorldModel world, Entity target, EventScheduler scheduler);
    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition()))
        {
            _moveTo(world, target, scheduler);
            return true;
        }
        else
        {
            Point nextPos = nextPosition(target.getPosition(), world);

            if (!this.getPosition().equals(nextPos))
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
