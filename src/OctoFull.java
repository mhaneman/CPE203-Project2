import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OctoFull implements EntityDynamic{
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

    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    public OctoFull( String id, Point position,
                  List<PImage> images, int resourceLimit, int resourceCount,
                  int actionPeriod, int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void nextImage()
    {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    public int getAnimationPeriod()
    {
        return this.animationPeriod;
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

                System.out.println("yep");
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
        octo.scheduleActions(world, imageStore, scheduler);
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

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), this.getClass());

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

    public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
        eventScheduler.scheduleEvent(this, createAnimationAction(0),
                getAnimationPeriod());
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

    public List<PImage> getImages() {
        return images;
    }
}
