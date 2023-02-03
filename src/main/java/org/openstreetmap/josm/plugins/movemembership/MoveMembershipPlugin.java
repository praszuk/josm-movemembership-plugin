package org.openstreetmap.josm.plugins.movemembership;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class MoveMembershipPlugin extends Plugin {
    private final MoveMembershipsAction moveMembershipsAction;
    public MoveMembershipPlugin(PluginInformation info) {
        super(info);
        moveMembershipsAction = new MoveMembershipsAction();
        MainMenu.add(MainApplication.getMenu().selectionMenu, moveMembershipsAction);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame, newFrame);
        moveMembershipsAction.setEnabled(newFrame != null);
    }
}
