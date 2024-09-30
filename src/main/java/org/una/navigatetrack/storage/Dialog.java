package org.una.navigatetrack.storage;

import javax.swing.*;
import java.awt.event.*;

public class Dialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel MessageLabel;

    public Dialog() {
        setContentPane(contentPane);

        this.setSize(300, 150);
        this.setTitle("Message");

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setMessage(String message) {
        MessageLabel.setText("<html>" + message + "</html>");
        this.setResizable(false);
        this.pack();
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void setCancelButtonVisible(boolean visible) {
        buttonCancel.setVisible(visible);
        this.pack();
    }

    public static void main(String[] args) {
        Dialog dialog = new Dialog();

        JOptionPane.showMessageDialog(null, "No se pudo crear el directorio.", "Error", JOptionPane.ERROR_MESSAGE);

        dialog.setMessage("Contenido de texto largo .............. <br> .......... ....... ......");
        dialog.setCancelButtonVisible(true);
        System.exit(0);
    }
}
