import java.util.List;
import java.util.Optional;
import java.util.Random;

import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


final class Entity
{
   private static final String QUAKE_ID = "quake";
   private static final int QUAKE_ACTION_PERIOD = 1100;
   private static final int QUAKE_ANIMATION_PERIOD = 100;
   private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;
   public static final int ATLANTIS_ANIMATION_REPEAT_COUNT = 7;

   public static final String QUAKE_KEY = "quake";
   private static final String FISH_ID_PREFIX = "fish -- ";
   private static final int FISH_CORRUPT_MIN = 20000;
   private static final int FISH_CORRUPT_MAX = 30000;

   public static final String CRAB_KEY = "crab";
   public static final String CRAB_ID_SUFFIX = " -- crab";
   public static final int CRAB_PERIOD_SCALE = 4;
   public static final int CRAB_ANIMATION_MIN = 50;
   public static final int CRAB_ANIMATION_MAX = 150;

   private static final String FISH_KEY = "fish";
   private static final Random rand = new Random();

   private EntityKind kind;
   private String id;
   private Point position;
   private List<PImage> images;
   private int imageIndex;
   private int resourceLimit;
   private int resourceCount;
   private int actionPeriod;

   public int getActionPeriod() {
      return actionPeriod;
   }

   public EntityKind getKind() {
      return kind;
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

   public List<PImage> getImages() {
      return images;
   }

   private int animationPeriod;

   public Entity(EntityKind kind, String id, Point position,
      List<PImage> images, int resourceLimit, int resourceCount,
      int actionPeriod, int animationPeriod)
   {
      this.kind = kind;
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
      this.resourceLimit = resourceLimit;
      this.resourceCount = resourceCount;
      this.actionPeriod = actionPeriod;
      this.animationPeriod = animationPeriod;
   }

   public static Entity createAtlantis(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.ATLANTIS, id, position, images,
              0, 0, 0, 0);
   }

   public static Entity createOctoFull(String id, int resourceLimit,
                                       Point position, int actionPeriod, int animationPeriod,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_FULL, id, position, images,
              resourceLimit, resourceLimit, actionPeriod, animationPeriod);
   }

   public static Entity createOctoNotFull(String id, int resourceLimit,
                                          Point position, int actionPeriod, int animationPeriod,
                                          List<PImage> images)
   {
      return new Entity(EntityKind.OCTO_NOT_FULL, id, position, images,
              resourceLimit, 0, actionPeriod, animationPeriod);
   }

   public static Entity createObstacle(String id, Point position,
                                       List<PImage> images)
   {
      return new Entity(EntityKind.OBSTACLE, id, position, images,
              0, 0, 0, 0);
   }

   public static Entity createFish(String id, Point position, int actionPeriod,
                                   List<PImage> images)
   {
      return new Entity(EntityKind.FISH, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public static Entity createCrab(String id, Point position,
                                   int actionPeriod, int animationPeriod, List<PImage> images)
   {
      return new Entity(EntityKind.CRAB, id, position, images,
              0, 0, actionPeriod, animationPeriod);
   }

   public static Entity createQuake(Point position, List<PImage> images)
   {
      return new Entity(EntityKind.QUAKE, QUAKE_ID, position, images,
              0, 0, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
   }

   public static Entity createSgrass(String id, Point position, int actionPeriod,
                                     List<PImage> images)
   {
      return new Entity(EntityKind.SGRASS, id, position, images, 0, 0,
              actionPeriod, 0);
   }

   public void nextImage()
   {
      this.imageIndex = (this.imageIndex + 1) % this.images.size();
   }

   public int getAnimationPeriod()
   {
      switch (this.kind)
      {
         case OCTO_FULL:
         case OCTO_NOT_FULL:
         case CRAB:
         case QUAKE:
         case ATLANTIS:
            return this.animationPeriod;
      default:
         throw new UnsupportedOperationException(
            String.format("getAnimationPeriod not supported for %s",
            this.kind));
      }
   }

   public boolean moveToFull(WorldModel world,
                             Entity target, EventScheduler scheduler)
   {
      if (this.position.adjacent(target.position))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionOcto(target.position, world);

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

   public boolean moveToNotFull(WorldModel world,
                                Entity target, EventScheduler scheduler)
   {
      if (this.position.adjacent(target.position))
      {
         this.resourceCount += 1;
         world.removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionOcto(target.position, world);

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
      Entity octo = createOctoNotFull(this.id, this.resourceLimit,
         this.position, this.actionPeriod, this.animationPeriod,
         this.images);

      world.removeEntity(this);
      scheduler.unscheduleAllEvents(this);

      world.addEntity(octo);
      octo.scheduleActions(world, imageStore, scheduler);
   }

   public boolean transformNotFull(WorldModel world,
                                   EventScheduler scheduler, ImageStore imageStore)
   {
      if (this.resourceCount >= this.resourceLimit)
      {
         Entity octo = createOctoFull(this.id, this.resourceLimit,
            this.position, this.actionPeriod, this.animationPeriod,
            this.images);

         world.removeEntity(this);
         scheduler.unscheduleAllEvents(this);

         world.addEntity(octo);
         octo.scheduleActions(world, imageStore, scheduler);

         return true;
      }

      return false;
   }

   public Action createActivityAction(WorldModel world,
                                      ImageStore imageStore)
   {
      return new Activity(this, world, imageStore, 0);
   }

   public Action createAnimationAction(int repeatCount)
   {
      return new Animation(this, null, null, repeatCount);
   }

   public void executeOctoFullActivity(WorldModel world,
                                       ImageStore imageStore, EventScheduler eventScheduler)
   {
      Optional<Entity> fullTarget = world.findNearest(getPosition(),
         EntityKind.ATLANTIS);

      if (fullTarget.isPresent() &&
         moveToFull(world, fullTarget.get(), eventScheduler))
      {
         //at atlantis trigger animation
         fullTarget.get().scheduleActions(world, imageStore, eventScheduler);

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

   public void executeOctoNotFullActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
   {
      Optional<Entity> notFullTarget = world.findNearest(getPosition(),
         EntityKind.FISH);

      if (!notFullTarget.isPresent() ||
         !moveToNotFull(world, notFullTarget.get(), eventScheduler) ||
         !transformNotFull(world, eventScheduler, imageStore))
      {
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
      }
   }

   public void executeFishActivity(WorldModel world,
                                   ImageStore imageStore, EventScheduler eventScheduler)
   {
      Point pos = getPosition();  // store current position before removing

      world.removeEntity(this);
      eventScheduler.unscheduleAllEvents(this);

      Entity crab = createCrab(getId() + CRAB_ID_SUFFIX,
              pos, getActionPeriod() / CRAB_PERIOD_SCALE,
              CRAB_ANIMATION_MIN +
                      rand.nextInt(CRAB_ANIMATION_MAX - CRAB_ANIMATION_MIN),
              imageStore.getImageList(CRAB_KEY));

      world.addEntity(crab);
      crab.scheduleActions(world, imageStore, eventScheduler);
   }

   public void executeCrabActivity(WorldModel world,
                                   ImageStore imageStore, EventScheduler eventScheduler)
   {
      Optional<Entity> crabTarget = world.findNearest(
              getPosition(), EntityKind.SGRASS);
      long nextPeriod = getActionPeriod();

      if (crabTarget.isPresent())
      {
         Point tgtPos = crabTarget.get().getPosition();

         if (world.moveToCrab(this, crabTarget.get(), eventScheduler))
         {
            Entity quake = createQuake(tgtPos,
               imageStore.getImageList(QUAKE_KEY));

            world.addEntity(quake);
            nextPeriod += getActionPeriod();
            quake.scheduleActions(world, imageStore, eventScheduler);
         }
      }

      eventScheduler.scheduleEvent(this,
         createActivityAction(world, imageStore),
         nextPeriod);
   }

   public void executeSgrassActivity(WorldModel world,
                                     ImageStore imageStore, EventScheduler eventScheduler)
   {
      Optional<Point> openPt = world.findOpenAround(getPosition());

      if (openPt.isPresent())
      {
         Entity fish = createFish(FISH_ID_PREFIX + getId(),
                 openPt.get(), FISH_CORRUPT_MIN +
                         rand.nextInt(FISH_CORRUPT_MAX - FISH_CORRUPT_MIN),
                 imageStore.getImageList(FISH_KEY));
         world.addEntity(fish);
         fish.scheduleActions(world, imageStore, eventScheduler);
      }

      eventScheduler.scheduleEvent(this,
         createActivityAction(world, imageStore),
         getActionPeriod());
   }

   public void executeAtlantisActivity(WorldModel world,
                                       ImageStore imageStore, EventScheduler eventScheduler)
   {
      eventScheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   public void executeQuakeActivity(WorldModel world,
                                    ImageStore imageStore, EventScheduler eventScheduler)
   {
      eventScheduler.unscheduleAllEvents(this);
      world.removeEntity(this);
   }

   public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
   {
      switch (getKind())
      {
      case OCTO_FULL:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         eventScheduler.scheduleEvent(this, createAnimationAction(0),
            getAnimationPeriod());
         break;

      case OCTO_NOT_FULL:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         eventScheduler.scheduleEvent(this,
            createAnimationAction(0), getAnimationPeriod());
         break;

      case FISH:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         break;

      case CRAB:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         eventScheduler.scheduleEvent(this,
            createAnimationAction(0), getAnimationPeriod());
         break;

      case QUAKE:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         eventScheduler.scheduleEvent(this,
            createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
            getAnimationPeriod());
         break;

      case SGRASS:
         eventScheduler.scheduleEvent(this,
            createActivityAction(world, imageStore),
            getActionPeriod());
         break;
      case ATLANTIS:
         eventScheduler.scheduleEvent(this,
                    createAnimationAction(ATLANTIS_ANIMATION_REPEAT_COUNT),
                    getAnimationPeriod());
            break;

      default:
      }
   }

   public Point nextPositionCrab(Point destPos, WorldModel worldModel)
   {
      int horiz = Integer.signum(destPos.x - getPosition().x);
      Point newPos = new Point(getPosition().x + horiz,
         getPosition().y);

      Optional<Entity> occupant = worldModel.getOccupant(newPos);

      if (horiz == 0 ||
         (occupant.isPresent() && !(occupant.get().getKind() == EntityKind.FISH)))
      {
         int vert = Integer.signum(destPos.y - getPosition().y);
         newPos = new Point(getPosition().x, getPosition().y + vert);
         occupant = worldModel.getOccupant(newPos);

         if (vert == 0 ||
            (occupant.isPresent() && !(occupant.get().getKind() == EntityKind.FISH)))
         {
            newPos = getPosition();
         }
      }

      return newPos;
   }

   public Point nextPositionOcto(Point destPos, WorldModel worldModel)
   {
      int horiz = Integer.signum(destPos.x - getPosition().x);
      Point newPos = new Point(getPosition().x + horiz,
         getPosition().y);

      if (horiz == 0 || worldModel.isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - getPosition().y);
         newPos = new Point(getPosition().x,
            getPosition().y + vert);

         if (vert == 0 || worldModel.isOccupied(newPos))
         {
            newPos = getPosition();
         }
      }

      return newPos;
   }

   public PImage getCurrentImage()
   {

      if (this instanceof Entity)
      {
         return ((Entity)this).getImages().get(((Entity)this).getImageIndex());
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         this));
      }
   }
}
