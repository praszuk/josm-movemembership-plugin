package org.openstreetmap.josm.plugins.movemembership.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class MoveMembershipModel {
    public static final String PROPERTY_CHANGED = "PROPERTY_CHANGED";

    private OsmPrimitive source;
    private OsmPrimitive destination;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public OsmPrimitive getSource() {
        return source;
    }

    public void setSource(OsmPrimitive source) {
        this.source = source;
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGED, null, source);
    }

    public OsmPrimitive getDestination() {
        return destination;
    }

    public void setDestination(OsmPrimitive destination) {
        this.destination = destination;
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGED, null, destination);
    }

    public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(name, listener);
    }
}
