public abstract class EntityOcto extends EntityMoves
{
    protected Point nextPositionOcto(Point destPos, WorldModel worldModel)
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

}
