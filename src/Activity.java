public class Activity implements Action{
    private Entity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(Entity entity, WorldModel world,
                  ImageStore imageStore, int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
                executeActivityAction(scheduler);
    }

    private void executeActivityAction(EventScheduler scheduler)
    {
        switch (this.entity.getKind())
        {
            case OCTO_FULL:
                this.entity.executeOctoFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case OCTO_NOT_FULL:
                this.entity.executeOctoNotFullActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case FISH:
                this.entity.executeFishActivity(this.world, this.imageStore,
                        scheduler);
                break;

            case CRAB:
                this.entity.executeCrabActivity(this.world,
                        this.imageStore, scheduler);
                break;

            case QUAKE:
                this.entity.executeQuakeActivity(this.world, this.imageStore,
                        scheduler);
                break;

            case SGRASS:
                this.entity.executeSgrassActivity(this.world, this.imageStore,
                        scheduler);
                break;

            case ATLANTIS:
                this.entity.executeAtlantisActivity(this.world, this.imageStore,
                        scheduler);
                break;

            default:
                throw new UnsupportedOperationException(
                        String.format("executeActivityAction not supported for %s",
                                this.entity.getKind()));
        }
    }
}
