package org.openstreetmap.josm.plugins.movemembership;


import static org.openstreetmap.josm.data.osm.OsmPrimitive.getParentRelations;
import static org.openstreetmap.josm.plugins.movemembership.ImportUtils.importOsmFile;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOsmPrimitivePositionsInRelation;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.getOsmPrimitiveRolesInRelation;
import static org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction.move;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.data.osm.AbstractPrimitive;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.testutils.JOSMTestRules;

public class MoveTest {
    @RegisterExtension
    static JOSMTestRules rule = new JOSMTestRules();

    @BeforeEach
    public void setUp() {
        UndoRedoHandler.getInstance().clean();
    }

    @AfterEach
    public void tearDown() {
        UndoRedoHandler.getInstance().clean();
    }

    @Test
    void testMoveManyRelations() {
        DataSet testDataSet = importOsmFile(new File("test/data/three_bus_routes_two_to_move.osm"), "");
        assert testDataSet != null;

        OsmPrimitive source = testDataSet.getPrimitives(p -> p.hasKey("area")).stream().findFirst().orElseThrow();
        OsmPrimitive destination = testDataSet.getPrimitives(p -> p.hasTag("highway", "bus_stop") && p.hasTag("name", "Test stop")).stream().findFirst().orElseThrow();

        Assertions.assertEquals(2, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(1, getParentRelations(List.of(destination)).size());

        move(
            source,
            destination,
            getParentRelations(List.of(source)).stream().map(AbstractPrimitive::getUniqueId).collect(Collectors.toList())
        );

        Assertions.assertTrue(getParentRelations(List.of(source)).isEmpty());
        Assertions.assertEquals(3, getParentRelations(List.of(destination)).size());
    }

    @Test
    void testMoveFilteredRelations() {
        DataSet testDataSet = importOsmFile(new File("test/data/three_bus_routes_two_to_move.osm"), "");
        assert testDataSet != null;

        OsmPrimitive source = testDataSet.getPrimitives(p -> p.hasKey("area")).stream().findFirst().orElseThrow();
        OsmPrimitive destination = testDataSet.getPrimitives(p -> p.hasTag("highway", "bus_stop") && p.hasTag("name", "Test stop")).stream().findFirst().orElseThrow();

        Assertions.assertEquals(2, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(1, getParentRelations(List.of(destination)).size());

        long relationIdToMove = getParentRelations(List.of(source)).stream().findFirst().orElseThrow().getUniqueId();
        move(source, destination, List.of(relationIdToMove));

        Assertions.assertEquals(1, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(2 ,getParentRelations(List.of(destination)).size());
    }

    @Test
    void testMoveMultiplePositionsAndRolesInOneRelation() {
        DataSet testDataSet = importOsmFile(new File("test/data/multiple_positions_and_roles.osm"), "");
        assert testDataSet != null;

        OsmPrimitive source = testDataSet.getPrimitives(p -> p.hasKey("area")).stream().findFirst().orElseThrow();
        OsmPrimitive destination = testDataSet.getPrimitives(p -> p.hasTag("highway", "bus_stop") && p.hasTag("name", "Test stop")).stream().findFirst().orElseThrow();

        Relation relation = getParentRelations(List.of(source)).stream().findFirst().orElseThrow();
        List<Integer> sourceMemberPositions = getOsmPrimitivePositionsInRelation(relation, source);
        List<String> sourceMemberRoles = getOsmPrimitiveRolesInRelation(relation, source);

        move(source, destination, List.of(relation.getUniqueId()));

        Assertions.assertTrue(getParentRelations(List.of(source)).isEmpty());
        Assertions.assertEquals(1, getParentRelations(List.of(destination)).size());

        List<Integer> destinationMemberPositions = getOsmPrimitivePositionsInRelation(relation, destination);
        List<String> destinationMemberRoles = getOsmPrimitiveRolesInRelation(relation, destination);
        Assertions.assertEquals(sourceMemberPositions, destinationMemberPositions);
        Assertions.assertEquals(sourceMemberRoles, destinationMemberRoles);
    }

    @Test
    void testMoveCommandUndoRedo() {
        DataSet testDataSet = importOsmFile(new File("test/data/three_bus_routes_two_to_move.osm"), "");
        assert testDataSet != null;

        OsmPrimitive source = testDataSet.getPrimitives(p -> p.hasKey("area")).stream().findFirst().orElseThrow();
        OsmPrimitive destination = testDataSet.getPrimitives(p -> p.hasTag("highway", "bus_stop") && p.hasTag("name", "Test stop")).stream().findFirst().orElseThrow();

        Assertions.assertEquals(2, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(1, getParentRelations(List.of(destination)).size());

        long relationIdToMove = getParentRelations(List.of(source)).stream().findFirst().orElseThrow().getUniqueId();
        move(source, destination, List.of(relationIdToMove));

        Assertions.assertEquals(1, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(2 ,getParentRelations(List.of(destination)).size());

        SequenceCommand moveCommand = (SequenceCommand) UndoRedoHandler.getInstance().getLastCommand();
        Assertions.assertEquals("Move object memberships", moveCommand.getName());

        UndoRedoHandler.getInstance().undo();

        Assertions.assertFalse(UndoRedoHandler.getInstance().hasUndoCommands());
        Assertions.assertEquals(2, getParentRelations(List.of(source)).size());
        Assertions.assertEquals(1 ,getParentRelations(List.of(destination)).size());
    }

    @Test
    void testMoveCanceledBySourceHasNoRelationsOrSourceOrDestinationIsNull() {
        DataSet testDataSet = new DataSet();
        OsmPrimitive source = new Way();
        OsmPrimitive destination = new Way();
        testDataSet.addPrimitive(source);
        testDataSet.addPrimitive(destination);

        move(source, destination, List.of());
        move(null, destination, List.of());
        move(source, null, List.of());
        move(null, null, List.of());

        Assertions.assertNull(UndoRedoHandler.getInstance().getLastCommand());
    }
}
