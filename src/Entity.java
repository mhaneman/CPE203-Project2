import java.util.List;
import processing.core.PImage;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */


public abstract class Entity
{
    abstract Point getPosition();
    abstract void setPosition(Point position);
    abstract int getImageIndex();
    abstract void setImageIndex(int imageIndex);
    abstract List<PImage> getImages();
    abstract int getActionPeriod();

    protected void nextImage()
    {
        this.setImageIndex((this.getImageIndex() + 1) % this.getImages().size());
    }
    protected PImage getCurrentImage()
    {
        return this.getImages().get(this.getImageIndex());
    }
}
