package org.openstreetmap.josm.plugins.movemembership;

import static org.openstreetmap.josm.data.osm.OsmPrimitive.getParentRelations;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.ChangeMembersCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;


public class MoveMembershipAction extends JosmAction {
    static final String DESCRIPTION = tr("Move object relations memberships to another object.");
    static final String TITLE = tr("Move object memberships");

    public MoveMembershipAction() {
        super(
            TITLE,
            (ImageProvider) null,
            DESCRIPTION,
            Shortcut.registerShortcut(
                "movemembership:show",
                tr("Open MoveMembership dialog"),
                KeyEvent.CHAR_UNDEFINED,
                Shortcut.NONE
            ),
            true,
            TITLE,
            false
        );
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        MoveMembershipModel model = new MoveMembershipModel();
        // Pre-selection
        Collection<OsmPrimitive> selection = getLayerManager().getEditDataSet().getSelected();
        if (selection.size() == 2){
            OsmPrimitive[] primitives = selection.toArray(OsmPrimitive[]::new);
            model.setSource(primitives[0]);
            model.setDestination(primitives[1]);
        }
        new MoveMembershipController(new MoveMembershipView(), model).initGui();
    }

    /**
     * @return selected 1 primitive or null if there is no selection or selection > 1
     */
    public static OsmPrimitive getOneSelectedPrimitive() {
        Collection<OsmPrimitive> primitives = MainApplication.getLayerManager().getEditDataSet().getSelected();
        if (primitives.isEmpty()){
            Logging.info("No primitive selected");
            return null;
        }
        else if (primitives.size() > 1){
            Logging.info("Selected more than 1 primitive.");
            return null;
        }

        return primitives.stream().findFirst().get();
    }

    public static List<Integer> getOsmPrimitivePositionsInRelation(Relation relation, OsmPrimitive primitive) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < relation.getMembers().size(); i++){
            if (relation.getMember(i).getMember().equals(primitive)) {
                positions.add(i);
            }
        }
        return positions;
    }

    public static List<String> getOsmPrimitiveRolesInRelation(Relation relation, OsmPrimitive primitive) {
        List<String> roles = new ArrayList<>();
        for (int i = 0; i < relation.getMembers().size(); i++){
            if (relation.getMember(i).getMember().equals(primitive)) {
                roles.add(relation.getMember(i).getRole());
            }
        }
        return roles;
    }

    public static void move(OsmPrimitive source, OsmPrimitive destination, List<Long> relationIds){
        if (source == null || destination == null){
            Logging.warn("Move action canceled. Source or destination object is null!");
            return;
        }

        Set<Relation> sourceRelations = getParentRelations(List.of(source))
            .stream()
            .filter(relation -> relationIds.contains(relation.getUniqueId()))
            .collect(Collectors.toSet());

        if (sourceRelations.isEmpty()) {
            Logging.info("Move action canceled. Source object is not a member of any relation!");
            return;
        }

        List<Command> commands = new ArrayList<>();
        for (Relation sourceRel : sourceRelations){
            List<RelationMember> newMembers = sourceRel.getMembers();

            for (int i = 0; i < sourceRel.getMembers().size(); i++){
                RelationMember relationMember = sourceRel.getMember(i);

                if (relationMember.getMember().equals(source)){
                    newMembers.set(i, new RelationMember(relationMember.getRole(), destination));
                }
            }

            commands.add(new ChangeMembersCommand(sourceRel, newMembers));
        }

        SequenceCommand cmd = new SequenceCommand(tr("Move object memberships"), commands);
        UndoRedoHandler.getInstance().add(cmd);
    }
}
