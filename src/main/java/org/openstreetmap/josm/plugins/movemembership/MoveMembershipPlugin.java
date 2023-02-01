package org.openstreetmap.josm.plugins.movemembership;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class MoveMembershipPlugin extends Plugin {
    public MoveMembershipPlugin(PluginInformation info) {
        super(info);
        MainMenu.add(MainApplication.getMenu().selectionMenu, new MoveMembershipsAction());
    }
}
