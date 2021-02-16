public abstract class EntityAnimates extends EntityAction
{
    abstract Action createAnimationAction(int repeatCount);
    abstract int getAnimationPeriod();
}
