package org.openstreetmap.josm.plugins.movemembership.gui;

import static org.openstreetmap.josm.data.osm.OsmPrimitive.getParentRelations;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOneSelectedPrimitive;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOsmPrimitivePositionsInRelation;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOsmPrimitiveRolesInRelation;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.move;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.util.HighlightHelper;

public class MoveMembershipController {
    private final MoveMembershipView view;
    private final MoveMembershipModel model;
    private final RelationTableModel relationTableModel;


    public MoveMembershipController(MoveMembershipView view, MoveMembershipModel model) {
        this.view = view;
        this.model = model;
        this.relationTableModel = new RelationTableModel();

        initViewListeners();
        initModelListeners();
    }

    private void initModelListeners() {
        model.addPropertyChangeListener(MoveMembershipModel.PROPERTY_CHANGED, evt -> {
            updateButtons();
            updateRelationTable();
        });
    }

    private void initViewListeners() {
        view.sourceBtnAddActionListener(actionEvent -> model.setSource(getOneSelectedPrimitive()));
        view.destinationBtnAddActionListener(actionEvent -> model.setDestination(getOneSelectedPrimitive()));
        view.moveBtnAddActionListener(actionEvent -> moveBtnClicked());
        view.includeAllLabelAddMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                relationTableModel.setRowInclusion(
                    IntStream.range(0, relationTableModel.getRowCount()).toArray(),
                    true
                );
            }
        });
        view.excludeAllLabelAddMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                relationTableModel.setRowInclusion(
                    IntStream.range(0, relationTableModel.getRowCount()).toArray(),
                    false
                );
            }
        });
        view.includeTableSelectionAddActionListener(
            actionEvent -> relationTableModel.setRowInclusion(view.getSelectedRowIndexes(), true)
        );
        view.excludeTableSelectionAddActionListener(
            actionEvent -> relationTableModel.setRowInclusion(view.getSelectedRowIndexes(), false)
        );
        view.addTableListSelectionListener(listSelectionEvent -> highlightSelectedRelations());
    }

    private void moveBtnClicked() {
        move(model.getSource(), model.getDestination(), relationTableModel.getIncludedRelationIds());
        view.closeView();
    }

    private void updateButtons() {
        view.sourceBtnSetText(getTextForPrimitiveBtn(model.getSource()));
        view.destinationBtnSetText(getTextForPrimitiveBtn(model.getDestination()));
        view.moveBtnSetEnabled(model.getSource() != null && model.getDestination() != null);
    }

    private void highlightSelectedRelations() {
        int[] selectedRowIndexes = view.getSelectedRowIndexes();
        List<Long> selectedRelationIds = relationTableModel.getRelationIdsFromRowIndexes(selectedRowIndexes);
        List<OsmPrimitive> selectedRelations = getParentRelations(List.of(model.getSource())).stream()
            .filter(relation -> selectedRelationIds.contains(relation.getUniqueId()))
            .collect(Collectors.toList());

        HighlightHelper.clearAllHighlighted();
        new HighlightHelper().highlight(selectedRelations);
    }

    private void updateRelationTable() {
        relationTableModel.getDataVector().removeAllElements();
        if (model.getSource() != null) {
            getParentRelations(List.of(model.getSource())).forEach(relation -> relationTableModel.addRow(
                    true,
                    relation.getUniqueId(),
                    String.join(",", getOsmPrimitiveRolesInRelation(relation, model.getSource())),
                    getOsmPrimitivePositionsInRelation(relation, model.getSource()).stream()
                        .map(posIndex -> String.valueOf(posIndex + 1))
                        .collect(Collectors.joining(",")),
                    relation.get("type"),
                    relation.getName()
            ));
            view.autoResizeColumns();
        }

    }
    public void initGui() {
        view.initView();
        view.setRelationTableModel(relationTableModel);
        view.setRelationTableSorter(relationTableModel.getTableRowSorter());
        updateButtons();
        updateRelationTable();
    }

    public static String getTextForPrimitiveBtn(OsmPrimitive primitive) {
        if (primitive == null) {
            return tr("<Select OSM object and click here>");
        }
        String name = primitive.getName() != null ? primitive.getName() : "";
        return String.format("[%s] %o (%s)",  primitive.getType().toString(), primitive.getId(), name);
    }
}
