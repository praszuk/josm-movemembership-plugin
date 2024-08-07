package org.openstreetmap.josm.plugins.movemembership.gui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.movemembership.MoveMembershipAction;

public class MoveMembershipView extends JFrame {
    final static int HEIGHT = 335;
    final static int WIDTH = 670;

    private final JButton sourceBtn = new JButton();
    private final JButton destinationBtn = new JButton();
    private final JButton moveBtn = new JButton(tr("Move"));
    private final JTable relationTable = new JTable();
    private final JLabel includeAllLabel = new JLabel(tr("Include all"));
    private final JLabel excludeAllLabel = new JLabel(tr("Exclude all"));

    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JMenuItem includeSelectionMenuItem = new JMenuItem(tr("Include selection"));
    private final JMenuItem excludeSelectionMenuItem = new JMenuItem(tr("Exclude selection"));


    public void initView() {
        setSize(WIDTH, HEIGHT);
        setTitle(MoveMembershipAction.TITLE);
        setLocationRelativeTo(MainApplication.getMainFrame());

        JPanel rootPanel = new JPanel(new BorderLayout());

        rootPanel.add(createSourceDestinationButtonsPanel(), BorderLayout.NORTH);
        rootPanel.add(createRelationTablePanel(), BorderLayout.CENTER);
        rootPanel.add(createMoveButtonPanel(), BorderLayout.SOUTH);

        add(rootPanel);
        setVisible(true);
        setAlwaysOnTop(true);
    }

    private JPanel createSourceDestinationButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel(tr("Source object") + ": "));
        panel.add(sourceBtn);

        panel.add(new JLabel(tr("Destination object") + ": "));
        panel.add(destinationBtn);

        return panel;
    }

    private JPanel createMoveButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        moveBtn.setEnabled(false);
        panel.add(moveBtn);

        return panel;
    }

    private JPanel createRelationTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel includeExcludeAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

        includeAllLabel.setForeground(Color.BLUE.darker());
        includeAllLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        excludeAllLabel.setForeground(Color.BLUE.darker());
        excludeAllLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        includeExcludeAllPanel.add(includeAllLabel);
        includeExcludeAllPanel.add(new JLabel(" | "));
        includeExcludeAllPanel.add(excludeAllLabel);

        panel.add(includeExcludeAllPanel, BorderLayout.NORTH);

        popupMenu.add(includeSelectionMenuItem);
        popupMenu.add(excludeSelectionMenuItem);
        relationTable.setComponentPopupMenu(popupMenu);

        panel.add(new JScrollPane(relationTable), BorderLayout.CENTER);

        return panel;
    }

    public void closeView() {
        setVisible(false);
        dispose();
    }

    public void moveBtnSetEnabled(boolean enabled) {
        moveBtn.setEnabled(enabled);
    }

    public void sourceBtnAddActionListener(ActionListener listener) {
        sourceBtn.addActionListener(listener);
    }
    public void destinationBtnAddActionListener(ActionListener listener) {
        destinationBtn.addActionListener(listener);
    }
    public void moveBtnAddActionListener(ActionListener listener) {
        moveBtn.addActionListener(listener);
    }
    public void includeAllLabelAddMouseListener(MouseListener lister) {
        includeAllLabel.addMouseListener(lister);
    }
    public void excludeAllLabelAddMouseListener(MouseListener lister) {
        excludeAllLabel.addMouseListener(lister);
    }
    public void includeTableSelectionAddActionListener(ActionListener listener) {
        includeSelectionMenuItem.addActionListener(listener);
    }
    public void excludeTableSelectionAddActionListener(ActionListener listener) {
        excludeSelectionMenuItem.addActionListener(listener);
    }
    public void addTableListSelectionListener(ListSelectionListener listener) {
        relationTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void sourceBtnSetText(String text) {
        sourceBtn.setText(text);
    }
    public void destinationBtnSetText(String text) {
        destinationBtn.setText(text);
    }

    public void setRelationTableModel(TableModel model) {
        relationTable.setModel(model);
    }

    public void setRelationTableSorter(TableRowSorter<TableModel> sorter) {
        relationTable.setRowSorter(sorter);
    }

    public int[] getSelectedRowIndexes() {
        return relationTable.getSelectedRows();
    }

    public void autoResizeColumns() {
        for (int columnIndex = 0; columnIndex < relationTable.getColumnCount() - 1; columnIndex++) {
            int maxCharactersLength = relationTable.getColumnModel()
                .getColumn(columnIndex)
                .getHeaderValue()
                .toString()
                .length();

            for (int rowIndex = 0; rowIndex < relationTable.getRowCount(); rowIndex++) {
                maxCharactersLength = Math.max(
                    maxCharactersLength,
                    relationTable.getModel().getValueAt(rowIndex, columnIndex).toString().length()
                );
            }
            int width = calculateColumnWidth(relationTable, maxCharactersLength + 3);
            relationTable.getColumnModel().getColumn(columnIndex).setMinWidth(width);
            relationTable.getColumnModel().getColumn(columnIndex).setMaxWidth(width);
        }
    }
    public static int calculateColumnWidth(JTable table, int charCount) {
        FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
        String sampleString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ- ";
        int stringWidth = fontMetrics.stringWidth(sampleString);
        int avgCharWidth = stringWidth / sampleString.length();
        return avgCharWidth * charCount;
    }
}
