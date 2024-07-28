package org.openstreetmap.josm.plugins.movemembership;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.DefaultTableModel;

public class RelationTableModel extends DefaultTableModel {
    public static final String COL_MOVE_CHECKBOX  = tr("Move");
    public static final String COL_ID  = tr("Id");
    public static final String COL_ROLE = tr("Role");
    public static final String COL_POSITION = tr("Position");
    public static final String COL_TYPE = tr("Type");
    public static final String COL_NAME = tr("Name");

    public static final ArrayList<String> COLUMNS = new ArrayList<>(
        Arrays.asList(COL_MOVE_CHECKBOX, COL_ID, COL_ROLE, COL_POSITION, COL_TYPE, COL_NAME)
    );

    public RelationTableModel() {
        COLUMNS.forEach(this::addColumn);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == COLUMNS.indexOf(COL_MOVE_CHECKBOX);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COLUMNS.indexOf(COL_MOVE_CHECKBOX)){
            return Boolean.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public void addRow(boolean moveCheckbox, long relationId, String roles, String positions, String type, String name) {
        super.addRow(new Object[]{moveCheckbox, relationId, roles, positions, type, name});
    }

    public List<Long> getCheckedRelationIds() {
        return getDataVector().stream()
            .filter(vector -> (boolean) vector.get(COLUMNS.indexOf(COL_MOVE_CHECKBOX)))
            .map(vector -> (long) vector.get(COLUMNS.indexOf(COL_ID)))
            .collect(Collectors.toList());
    }
}