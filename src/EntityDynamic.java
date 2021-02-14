import processing.core.PImage;

import java.util.List;

public interface EntityDynamic extends Entity{



    void nextImage();
    int getAnimationPeriod();
    Action createAnimationAction(int repeatCount);
}
