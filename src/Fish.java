import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Fish extends EntityAction {
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

    private int animationPeriod;

    public Fish(String id, Point position,
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
        throw new UnsupportedOperationException(
                String.format("getAnimationPeriod not supported for %s",
                        this.getClass()));
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
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        eventScheduler.unscheduleAllEvents(this);

        Entity crab = new Crab(getId() + CRAB_ID_SUFFIX, pos, imageStore.getImageList(CRAB_KEY),
                0, 0, getActionPeriod() / CRAB_PERIOD_SCALE,
                CRAB_ANIMATION_MIN + rand.nextInt(CRAB_ANIMATION_MAX - CRAB_ANIMATION_MIN));

        world.addEntity(crab);
        ((EntityAction)crab).scheduleActions(world, imageStore, eventScheduler);
    }

    public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());

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
