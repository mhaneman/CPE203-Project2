public abstract class EntityAction extends Entity
{
    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);
    abstract void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler);

    protected Action createActivityAction(WorldModel world,
                                       ImageStore imageStore)
    {
        return new Activity(this, world, imageStore);
    }
}
