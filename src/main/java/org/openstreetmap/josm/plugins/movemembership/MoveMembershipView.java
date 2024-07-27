package org.openstreetmap.josm.plugins.movemembership;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openstreetmap.josm.gui.MainApplication;

public class MoveMembershipView extends JFrame {
    final static int HEIGHT = 135;
    final static int WIDTH = 670;

    private final JButton sourceBtn = new JButton();
    private final JButton destinationBtn = new JButton();
    private final JButton moveBtn = new JButton(tr("Move"));

    public void initView() {
        setSize(WIDTH, HEIGHT);
        setTitle(MoveMembershipAction.TITLE);
        setLocationRelativeTo(MainApplication.getMainFrame());

        JPanel root = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 5);

        root.add(createSourceDestinationButtonsPanel(), c);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 2;
        root.add(createMoveButtonPanel(), c);

        add(root);
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
        JPanel panel = new JPanel();

        moveBtn.setEnabled(false);
        panel.add(moveBtn);

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

    public void sourceBtnSetText(String text) {
        sourceBtn.setText(text);
    }
    public void destinationBtnSetText(String text) {
        destinationBtn.setText(text);
    }
}
