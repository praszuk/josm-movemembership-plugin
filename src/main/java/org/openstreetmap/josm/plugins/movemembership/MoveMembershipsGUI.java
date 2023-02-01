package org.openstreetmap.josm.plugins.movemembership;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.MainApplication;

import javax.swing.*;

import java.awt.*;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MoveMembershipsGUI extends JFrame {
    final static int HEIGHT = 135;
    final static int WIDTH = 670;
    final static int MAX_NAME_CHARACTERS = 10;
    private final MoveMembershipsAction moveMembershipsAction;
    private final JButton sourceBtn;
    private final JButton destinationBtn;
    private final JButton moveBtn;

    public MoveMembershipsGUI(MoveMembershipsAction moveMembershipsAction) {
        super();

        this.moveMembershipsAction = moveMembershipsAction;

        setSize(WIDTH, HEIGHT);
        setTitle(MoveMembershipsAction.TITLE);
        setLocationRelativeTo(MainApplication.getMainFrame());

        JPanel root = new JPanel(new GridBagLayout());

        JPanel setupPanel = new JPanel(new GridLayout(2, 2));

        setupPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel sourceLabel = new JLabel(tr("Source object") + ": ");
        JLabel destinationLabel = new JLabel(tr("Destination object") + ": ");

        sourceBtn = new JButton();
        destinationBtn = new JButton();

        sourceBtn.addActionListener(actionEvent -> {
            boolean isSuccess = moveMembershipsAction.selectSourcePrimitive();
            if (isSuccess){
                updateSourceBtn();
            }
            updateMoveBtnLock();
        });
        destinationBtn.addActionListener(actionEvent -> {
            boolean isSuccess = moveMembershipsAction.selectDestinationPrimitive();
            if (isSuccess){
                updateDestinationBtn();
            }
            updateMoveBtnLock();
        });

        setupPanel.add(sourceLabel);
        setupPanel.add(sourceBtn);

        setupPanel.add(destinationLabel);
        setupPanel.add(destinationBtn);

        updateDestinationBtn();
        updateSourceBtn();

        JPanel actionPanel = new JPanel();
        moveBtn = new JButton(tr("Move"));
        moveBtn.setEnabled(false);

        moveBtn.addActionListener(actionEvent -> {
            moveMembershipsAction.move();
            close();
        });
        actionPanel.add(moveBtn);

        updateMoveBtnLock();

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 5);

        root.add(setupPanel, c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 2;
        root.add(actionPanel, c);

        add(root);
        setVisible(true);
        setAlwaysOnTop(true);
    }

    private void updateMoveBtnLock() {
        moveBtn.setEnabled(moveMembershipsAction.getSource() != null && moveMembershipsAction.getDestination() != null);
    }

    private void close() {
        setVisible(false);
        dispose();
    }

    void updateSourceBtn() {
        OsmPrimitive srcPrimitive = moveMembershipsAction.getSource();
        if (moveMembershipsAction.getSource() == null) {
            sourceBtn.setText(tr("<Add source object from selection>"));
        } else {
            String name = srcPrimitive.getName();
            if (name != null){
                name = name.substring(0, Math.min(MAX_NAME_CHARACTERS, name.length()));
            }else {
                name = "";
            }
            sourceBtn.setText(String.format(
                    "[%s] %o (%s)",
                    srcPrimitive.getType().toString(),
                    srcPrimitive.getId(),
                    name)
            );
        }
    }

    void updateDestinationBtn() {
        OsmPrimitive dstPrimitive = moveMembershipsAction.getDestination();
        if (moveMembershipsAction.getDestination() == null) {
            destinationBtn.setText(tr("<Add destination object from selection>"));
        } else {
            String name = dstPrimitive.getName();
            if (name != null){
                name = name.substring(0, Math.min(MAX_NAME_CHARACTERS, name.length()));
            }else {
                name = "";
            }
            destinationBtn.setText(String.format(
                    "[%s] %o (%s)",
                    dstPrimitive.getType().toString(),
                    dstPrimitive.getId(),
                    name)
            );
        }
    }

}
