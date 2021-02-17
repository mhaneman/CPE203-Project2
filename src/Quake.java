import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class Quake extends EntityAnimates {
    private static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

    public Quake(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    void executeActivity(WorldModel world,
                                     ImageStore imageStore, EventScheduler eventScheduler)
    {

        eventScheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }

    void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler)
    {
        eventScheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                actionPeriod);
        eventScheduler.scheduleEvent(this,
                createAnimationAction(QUAKE_ANIMATION_REPEAT_COUNT),
                animationPeriod);
    }


}
