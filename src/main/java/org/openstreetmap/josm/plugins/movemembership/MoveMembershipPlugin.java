package org.openstreetmap.josm.plugins.movemembership;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class MoveMembershipPlugin extends Plugin {
    private final MoveMembershipAction moveMembershipAction;

    public MoveMembershipPlugin(PluginInformation info) {
        super(info);

        moveMembershipAction = new MoveMembershipAction();
        MainMenu.add(MainApplication.getMenu().selectionMenu, moveMembershipAction);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame, newFrame);

        moveMembershipAction.setEnabled(newFrame != null);
    }
}
