package com.necsv.scanner.esg.view;

import java.util.Observable;

/**
 * Class that holds the aspect quotient, defined as content aspect ratio divided
 * by view aspect ratio.
 */
public class AspectQuotient extends Observable {

    /**
     * Aspect quotient
     */
    private float mAspectQuotient;

    // Public methods

    /**
     * Gets aspect quotient
     * 
     * @return The aspect quotient
     */
    public float get() {
        return mAspectQuotient;
    }

    /**
     * Updates and recalculates aspect quotient based on supplied view and
     * content dimensions.
     * 
     * @param viewWidth Width of view
     * @param viewHeight Height of view
     * @param contentWidth Width of content
     * @param contentHeight Height of content
     */
    public void updateAspectQuotient(float viewWidth, float viewHeight, float contentWidth,
            float contentHeight) {
        final float aspectQuotient = (contentWidth / contentHeight) / (viewWidth / viewHeight);

        if (aspectQuotient != mAspectQuotient) {
            mAspectQuotient = aspectQuotient;
            setChanged();
        }
    }

}
