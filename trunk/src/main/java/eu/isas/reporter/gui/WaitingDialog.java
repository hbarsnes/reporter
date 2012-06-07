package eu.isas.reporter.gui;

import eu.isas.reporter.Reporter;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This dialog will display feedback to the user while the data is being
 * processed
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class WaitingDialog extends javax.swing.JDialog {

    /**
     * Boolean indicating whether the run has been canceled.
     */
    private boolean runCancelled = false;
    /**
     * Boolean indicating whether the run is finished.
     */
    private boolean runFinished = false;
    /**
     * Convenience date format.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
    /**
     * The calculator processing the data.
     */
    private Reporter reporter;
    /**
     * The progress bar position.
     */
    private int progress;
    /**
     * The main ReporterGUI.
     */
    private ReporterGUI reporterGUI;
    
    /**
     * Constructor for the waiting dialog.
     *
     * @param reporterGUI The parent frame
     * @param modal boolean indicating whether the dialog is modal
     * @param reporter The calculator processing the data
     */
    public WaitingDialog(ReporterGUI reporterGUI, boolean modal, Reporter reporter) {
        super(reporterGUI, modal);
        this.reporterGUI = reporterGUI;
        this.reporter = reporter;
        initComponents();
        this.setTitle(this.getTitle() + " - " + reporter.getExperiment().getReference());
        reportArea.setText("Reporter File Import:\n\n");
    }

    /**
     * Sets the run finished.
     */
    public void setRunFinished() {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        
        progressBar.setValue(progressBar.getMaximum());
        progressBar.setStringPainted(true);
        progressBar.setString("Calculation Completed.");
        
        // change the peptide shaker icon back to the default version
        reporterGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")));
        
        if (runCancelled) {
            closeButton.setText("Close");
        } else {
            closeButton.setText("Open");
        }
        
        reportArea.append("\nTo view the results, click the Open button.");
        saveButton.setEnabled(true);
        runFinished = true;
    }

    /**
     * Sets the run canceled.
     */
    public void setRunCancelled() {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                progressBar.setString("Calculation Cancelled!");
                
                // change the peptide shaker icon back to the default version
                reporterGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")));
            }
        });

        closeButton.setText("Close");
        saveButton.setEnabled(true);
        runCancelled = true;
    }

    /**
     * Sets a text on the progress bar.
     *
     * @param text text to be written on the progress bar
     */
    public void setProgressbarText(String text) {
        progressBar.setStringPainted(true);
        progressBar.setString(text);
    }

    /**
     * Sets the maximal value for the progress bar.
     *
     * @param max the maximal value for the progress bar
     */
    public void setProgressBarMaximum(int max) {
        progressBar.setIndeterminate(false);
        progressBar.setMaximum(max);
        progress = 0;
        progressBar.setValue(progress);
    }

    /**
     * Increments the progress bar position.
     */
    public void incrementProgressBar() {
        progressBar.setValue(++progress);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        waitingPanel = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        reportArea = new javax.swing.JTextArea();
        saveButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporter Calculation");

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        waitingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Processing Progress"));
        waitingPanel.setOpaque(false);

        progressBar.setIndeterminate(true);

        reportArea.setBackground(new java.awt.Color(254, 254, 254));
        reportArea.setColumns(20);
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setRows(5);
        jScrollPane1.setViewportView(reportArea);

        javax.swing.GroupLayout waitingPanelLayout = new javax.swing.GroupLayout(waitingPanel);
        waitingPanel.setLayout(waitingPanelLayout);
        waitingPanelLayout.setHorizontalGroup(
            waitingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, waitingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(waitingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE))
                .addContainerGap())
        );
        waitingPanelLayout.setVerticalGroup(
            waitingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(waitingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addContainerGap())
        );

        saveButton.setText("Save Report");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Cancel");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waitingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {closeButton, saveButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(waitingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(saveButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if (runCancelled) {
            this.dispose();
        } else if (runFinished) {
            reporter.updateResults();
            this.dispose();
        } else {
            runCancelled = true;
            closeButton.setText("Close");
            saveButton.setEnabled(true);
            appendReport("Run cancelled.");
        }
}//GEN-LAST:event_closeButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        File outputFile = null;
        JFileChooser fc = new JFileChooser();
        int result = fc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFile = fc.getSelectedFile();
            if (outputFile.exists()) {
                int choice = JOptionPane.showConfirmDialog(this,
                        new String[]{"The file " + outputFile.getName() + " already exists!", "Overwrite?"},
                        "File Already Exists", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
                    return;
                }
            } else {
                return;
            }
        }
        if (outputFile != null) {
            saveReport(outputFile);
        }
}//GEN-LAST:event_saveButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextArea reportArea;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel waitingPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Returns true if the run has been cancelled.
     *
     * @return true if the run has been cancelled
     */
    public boolean isRunCancelled() {
        return runCancelled;
    }

    /**
     * Appends a string to the report displayed with the current date.
     *
     * @param report the new report
     */
    public void appendReport(String report) {
        Date date = new Date();
        reportArea.append(date + "\t" + report + "\n");
    }

    /**
     * Saves the report in the given file.
     *
     * @param aFile file to save the report in
     */
    private void saveReport(File aFile) {
        StringBuffer output = new StringBuffer();
        String host = " @ ";

        try {
            host += InetAddress.getLocalHost().getHostName();


        } catch (UnknownHostException uhe) {
            // Disregard. It's not so bad if we can not report this.
        }

        // Write the file header.
        output.append("# ------------------------------------------------------------------"
                + "\n# SearchGUI Report File"
                + "\n#"
                + "\n# Originally saved by: " + System.getProperty("user.name") + host
                + "\n#                  on: " + sdf.format(new Date())
                + "\n#                  as: " + aFile.getName()
                + "\n# ------------------------------------------------------------------\n");

        output.append(reportArea.getText() + "\n");

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(aFile));
            bw.write(output.toString());
            bw.flush();
            JOptionPane.showMessageDialog(this, "Settings written to file '" + aFile.getAbsolutePath() + "'.", "Settings Saved", JOptionPane.INFORMATION_MESSAGE);


        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, new String[]{"Error writing report to file:", ioe.getMessage()}, "Save Failed", JOptionPane.ERROR_MESSAGE);

        } finally {
            if (bw != null) {
                try {
                    bw.close();

                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(this, new String[]{"Error writing report to file:", ioe.getMessage()}, "Save Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
