/*
Action: ideally what our various entities might do in our virutal world
 */

final class Action
{
   private ActionKind kind;
   private Entity entity;
   private WorldModel world;
   private ImageStore imageStore;
   private int repeatCount;

   public Action(ActionKind kind, Entity entity, WorldModel world,
      ImageStore imageStore, int repeatCount)
   {
      this.kind = kind;
      this.entity = entity;
      this.world = world;
      this.imageStore = imageStore;
      this.repeatCount = repeatCount;
   }

   public void executeAction(EventScheduler scheduler)
   {
      switch (this.kind)
      {
      case ACTIVITY:
         executeActivityAction(scheduler);
         break;

      case ANIMATION:
         executeAnimationAction(scheduler);
         break;
      }
   }

   private void executeAnimationAction(EventScheduler scheduler)
   {
      this.entity.nextImage();

      if (this.repeatCount != 1)
      {
         scheduler.scheduleEvent(this.entity,
            this.entity.createAnimationAction(
                    Math.max(this.repeatCount - 1, 0)),
            this.entity.getAnimationPeriod());
      }
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
