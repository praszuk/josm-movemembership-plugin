package org.openstreetmap.josm.plugins.movemembership;

import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOneSelectedPrimitive;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.move;
import static org.openstreetmap.josm.tools.I18n.tr;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class MoveMembershipController {
    private final MoveMembershipView view;
    private final MoveMembershipModel model;


    public MoveMembershipController(MoveMembershipView view, MoveMembershipModel model) {
        this.view = view;
        this.model = model;

        initViewListeners();
        initModelListeners();
    }

    private void initModelListeners() {
        model.addPropertyChangeListener(MoveMembershipModel.PROPERTY_CHANGED, evt -> updateButtons());
    }

    private void initViewListeners() {
        view.sourceBtnAddActionListener(actionEvent -> model.setSource(getOneSelectedPrimitive()));
        view.destinationBtnAddActionListener(actionEvent -> model.setDestination(getOneSelectedPrimitive()));
        view.moveBtnAddActionListener(actionEvent -> moveBtnClicked());
    }

    private void moveBtnClicked() {
        move(model.getSource(), model.getDestination());
        view.closeView();
    }

    private void updateButtons() {
        view.sourceBtnSetText(getTextForPrimitiveBtn(model.getSource()));
        view.destinationBtnSetText(getTextForPrimitiveBtn(model.getDestination()));
        view.moveBtnSetEnabled(model.getSource() != null && model.getDestination() != null);
    }

    public void initGui() {
        view.initView();
        updateButtons();
    }

    public static String getTextForPrimitiveBtn(OsmPrimitive primitive) {
        if (primitive == null) {
            return tr("<Select OSM object and click here>");
        }
        String name = primitive.getName() != null ? primitive.getName() : "";
        return String.format("[%s] %o (%s)",  primitive.getType().toString(), primitive.getId(), name);
    }
}
