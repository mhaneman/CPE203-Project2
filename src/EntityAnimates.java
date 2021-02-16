public abstract class EntityAnimates extends EntityAction
{
    abstract int getAnimationPeriod();
    protected Action createAnimationAction(int repeatCount)
    {
        return new Animation(this, repeatCount);
    }
}
