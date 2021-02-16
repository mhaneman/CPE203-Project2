public abstract class EntityAnimates extends EntityAction
{
    abstract int getAnimationPeriod();
    public Action createAnimationAction(int repeatCount)
    {
        return new Animation(this, null, null, repeatCount);
    }
}
