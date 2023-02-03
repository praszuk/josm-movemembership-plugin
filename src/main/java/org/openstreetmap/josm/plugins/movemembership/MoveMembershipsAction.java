package org.openstreetmap.josm.plugins.movemembership;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.ChangeMembersCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.Shortcut;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.openstreetmap.josm.data.osm.OsmPrimitive.getParentRelations;
import static org.openstreetmap.josm.tools.I18n.tr;


public class MoveMembershipsAction extends JosmAction {
    static final String DESCRIPTION = tr("Move object relations memberships to another object.");
    static final String TITLE = tr("Move object memberships");

    private OsmPrimitive source;
    private OsmPrimitive destination;

    public MoveMembershipsAction() {
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

    /**
     * @return selected 1 primitive or null if there is no selection or selection > 1
     */
    private OsmPrimitive getOneSelectedPrimitive() {
        Collection<OsmPrimitive> primitives = getLayerManager().getEditDataSet().getSelected();
        if (primitives.size() == 0){
            Logging.info("No primitive selected");
            return null;
        }
        else if (primitives.size() > 1){
            Logging.info("Selected more than 1 primitive.");
            return null;
        }


        return primitives.stream().findFirst().get();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // Pre-selection
        Collection<OsmPrimitive> selection = getLayerManager().getEditDataSet().getSelected();
        if (selection.size() == 2){
            OsmPrimitive[] primitives = selection.toArray(OsmPrimitive[]::new);
            this.source = primitives[0];
            this.destination = primitives[1];
        }

        new MoveMembershipsGUI(this);
    }

    protected void move(){
        if (source == null || destination == null){
            Logging.warn("Move action canceled. Source or destination object is null!");
            return;
        }
        Set<Relation> sourceRelations = getParentRelations(List.of(source));

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

        System.out.println(sourceRelations);
    }

    protected boolean selectSourcePrimitive(){
        OsmPrimitive primitive = this.getOneSelectedPrimitive();
        if (primitive != null){
            this.source = primitive;
            return true;
        }
        return false;
    }

    protected boolean selectDestinationPrimitive(){
        OsmPrimitive primitive = this.getOneSelectedPrimitive();
        if (primitive != null){
            this.destination = primitive;
            return true;
        }
        return false;
    }

    public OsmPrimitive getDestination() {
        return destination;
    }

    public OsmPrimitive getSource() {
        return source;
    }
}
