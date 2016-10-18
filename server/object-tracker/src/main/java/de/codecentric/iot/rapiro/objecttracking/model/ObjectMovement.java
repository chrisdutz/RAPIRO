package de.codecentric.iot.rapiro.objecttracking.model;

/**
 * Created by christoferdutz on 17.10.16.
 */
public class ObjectMovement {

    private DetectedObject[] newObjects;
    private DetectedObject[] movedObjects;
    private DetectedObject[] removedObjects;

    public DetectedObject[] getNewObjects() {
        return newObjects;
    }

    public void setNewObjects(DetectedObject[] newObjects) {
        this.newObjects = newObjects;
    }

    public DetectedObject[] getMovedObjects() {
        return movedObjects;
    }

    public void setMovedObjects(DetectedObject[] movedObjects) {
        this.movedObjects = movedObjects;
    }

    public DetectedObject[] getRemovedObjects() {
        return removedObjects;
    }

    public void setRemovedObjects(DetectedObject[] removedObjects) {
        this.removedObjects = removedObjects;
    }

}
