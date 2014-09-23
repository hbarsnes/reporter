/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.isas.reporter.gui.export;

import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.compomics.util.io.export.ExportScheme;
import eu.isas.reporter.export.report.ReporterExportFactory;
import eu.isas.reporter.gui.ReporterGUI;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * This dialog allows the user to select, add and edit reports
 *
 * @author Marc
 */
public class ReportDialog extends javax.swing.JDialog {
    
    /**
     * The export factory
     */
    private ReporterExportFactory exportFactory = ReporterExportFactory.getInstance();
    /**
     * The main gui instance
     */
    private ReporterGUI reporterGUI;
    /**
     * A simple progress dialog.
     */
    private static ProgressDialogX progressDialog;
    /**
     * List of the available export schemes
     */
    private ArrayList<String> exportSchemesNames;
    /**
     * Constructor 
     * 
     * @param reporterGUI the main gui instance
     */
    public ReportDialog(ReporterGUI reporterGUI) {
        super(reporterGUI, true);
        this.reporterGUI = reporterGUI;
        updateReportsList();
        initComponents();
        setLocationRelativeTo(reporterGUI);
        setVisible(true);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        exitButton = new javax.swing.JButton();
        customReportsPanel = new javax.swing.JPanel();
        reportsTableScrollPane = new javax.swing.JScrollPane();
        reportsTable = new javax.swing.JTable();
        exportReportButton = new javax.swing.JButton();
        selectReportTypeLabel = new javax.swing.JLabel();
        helpLabel = new javax.swing.JLabel();
        addReportLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(230, 230, 230));

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        customReportsPanel.setBackground(new java.awt.Color(230, 230, 230));
        customReportsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Quantification Report"));

        reportsTable.setModel(new ReportsTableModel());
        reportsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        reportsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportsTableMouseClicked(evt);
            }
        });
        reportsTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                reportsTableKeyReleased(evt);
            }
        });
        reportsTableScrollPane.setViewportView(reportsTable);

        exportReportButton.setText("Export");
        exportReportButton.setEnabled(false);
        exportReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportReportButtonActionPerformed(evt);
            }
        });

        selectReportTypeLabel.setText("Select a Report Type");

        helpLabel.setFont(helpLabel.getFont().deriveFont((helpLabel.getFont().getStyle() | java.awt.Font.ITALIC)));
        helpLabel.setText("Right click on a row in the table for additional options.");

        addReportLabel.setText("<html> <a href>Add new report type.</a> </html>");
        addReportLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addReportLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addReportLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addReportLabelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout customReportsPanelLayout = new javax.swing.GroupLayout(customReportsPanel);
        customReportsPanel.setLayout(customReportsPanelLayout);
        customReportsPanelLayout.setHorizontalGroup(
            customReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(customReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reportsTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, customReportsPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(addReportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(helpLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                        .addComponent(exportReportButton))
                    .addGroup(customReportsPanelLayout.createSequentialGroup()
                        .addComponent(selectReportTypeLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        customReportsPanelLayout.setVerticalGroup(
            customReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(customReportsPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(selectReportTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(customReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportReportButton)
                    .addComponent(helpLabel)
                    .addComponent(addReportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exitButton)
                    .addComponent(customReportsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customReportsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exitButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        try {
            exportFactory.saveFactory();
        } catch (Exception e) {
            reporterGUI.catchException(e);
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void reportsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportsTableMouseClicked

        if (evt != null && reportsTable.rowAtPoint(evt.getPoint()) != -1) {
            reportsTable.setRowSelectionInterval(reportsTable.rowAtPoint(evt.getPoint()), reportsTable.rowAtPoint(evt.getPoint()));
        }

        if (evt != null && evt.getButton() == MouseEvent.BUTTON3 && reportsTable.getSelectedRow() != -1) {
            String schemeName = (String) reportsTable.getValueAt(reportsTable.getSelectedRow(), 1);
            ExportScheme exportScheme = exportFactory.getExportScheme(schemeName);
//            editReportMenuItem.setVisible(exportScheme.isEditable());
//            removeReportMenuItem.setVisible(exportScheme.isEditable());
//            reportDocumentationPopupMenu.show(reportsTable, evt.getX(), evt.getY());
        }

        exportReportButton.setEnabled(reportsTable.getSelectedRow() != -1);
    }//GEN-LAST:event_reportsTableMouseClicked

    private void reportsTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_reportsTableKeyReleased
        reportsTableMouseClicked(null);
    }//GEN-LAST:event_reportsTableKeyReleased

    private void exportReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportReportButtonActionPerformed
        writeSelectedReport();
    }//GEN-LAST:event_exportReportButtonActionPerformed

    private void addReportLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addReportLabelMouseClicked
//        addReportMenuItemActionPerformed(null);
    }//GEN-LAST:event_addReportLabelMouseClicked

    private void addReportLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addReportLabelMouseEntered
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_addReportLabelMouseEntered

    private void addReportLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addReportLabelMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_addReportLabelMouseExited

    /**
     * Updates the reports list based on the information stored in the export
     * factory.
     */
    private void updateReportsList() {
        exportSchemesNames = new ArrayList<String>();
        exportSchemesNames.addAll(exportFactory.getDefaultExportSchemesNames());
        exportSchemesNames.addAll(exportFactory.getUserSchemesNames());
    }
    /**
     * Writes the selected report into a file.
     */
    private void writeSelectedReport() {

        // get the file to send the output to
        final File selectedFile = reporterGUI.getUserSelectedFile(".txt", "Tab separated text file (.txt)", "Export...", false);

        if (selectedFile != null) {
            progressDialog = new ProgressDialogX(this, reporterGUI,
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker-orange.gif")),
                    true);
            progressDialog.setTitle("Exporting Report. Please Wait...");

            final String filePath = selectedFile.getPath();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        progressDialog.setVisible(true);
                    } catch (IndexOutOfBoundsException e) {
                        // ignore
                    }
                }
            }, "ProgressDialog").start();

            new Thread("ExportThread") {
                @Override
                public void run() {

                    try {
                        String schemeName = (String) reportsTable.getValueAt(reportsTable.getSelectedRow(), 1);
                        ExportScheme exportScheme = exportFactory.getExportScheme(schemeName);
                        progressDialog.setTitle("Exporting. Please Wait...");
                        ReporterExportFactory.writeExport(exportScheme, selectedFile, reporterGUI.getExperiment().getReference(),
                                reporterGUI.getSample().getReference(), reporterGUI.getReplicateNumber(),
                                reporterGUI.getProjectDetails(), reporterGUI.getIdentification(), reporterGUI.getIdentificationFeaturesGenerator(),
                                reporterGUI.getQuantificationFeaturesGenerator(), reporterGUI.getReporterIonQuantification(), reporterGUI.getReporterPreferences(), reporterGUI.getSearchParameters(),
                                null, null, null, null, reporterGUI.getIdentificationDisplayPreferences().getnAASurroundingPeptides(),
                                reporterGUI.getAnnotationPreferences(), reporterGUI.getSequenceMatchingPreferences(), reporterGUI.getIdFilter(),
                                reporterGUI.getPtmScoringPreferences(), reporterGUI.getSpectrumCountingPreferences(), progressDialog);

                        boolean processCancelled = progressDialog.isRunCanceled();
                        progressDialog.setRunFinished();

                        if (!processCancelled) {
                            JOptionPane.showMessageDialog(reporterGUI, "Data copied to file:\n" + filePath, "Data Exported.", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (FileNotFoundException e) {
                        progressDialog.setRunFinished();
                        JOptionPane.showMessageDialog(reporterGUI,
                                "An error occurred while generating the output. Please make sure "
                                + "that the detination file is not opened by another application.", "Output Error.", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    } catch (Exception e) {
                        progressDialog.setRunFinished();
                        JOptionPane.showMessageDialog(reporterGUI, "An error occurred while generating the output.", "Output Error.", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    /**
     * Writes the documentation related to the selected report into a file.
     */
    private void writeDocumentationOfSelectedReport() {

        // get the file to send the output to
        final File selectedFile = reporterGUI.getUserSelectedFile(".txt", "Tab separated text file (.txt)", "Export Documentation...", false);

        if (selectedFile != null) {
            progressDialog = new ProgressDialogX(this, reporterGUI,
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                    Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker-orange.gif")),
                    true);

            new Thread(new Runnable() {
                public void run() {
                    try {
                        progressDialog.setVisible(true);
                    } catch (IndexOutOfBoundsException e) {
                        // ignore
                    }
                }
            }, "ProgressDialog").start();

            new Thread("ExportThread") {
                @Override
                public void run() {
                    boolean error = false;
                    try {
                        String schemeName = (String) reportsTable.getValueAt(reportsTable.getSelectedRow(), 1);
                        ExportScheme exportScheme = exportFactory.getExportScheme(schemeName);
                        ReporterExportFactory.writeDocumentation(exportScheme, selectedFile);
                    } catch (Exception e) {
                        error = true;
                        reporterGUI.catchException(e);
                    }
                    progressDialog.setRunFinished();

                    if (!error) {
                        JOptionPane.showMessageDialog(reporterGUI, "Documentation saved to \'" + selectedFile.getAbsolutePath() + "\'.",
                                "Documentation Saved", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }.start();
        }
    }

    /**
     * Table model for the reports table.
     */
    private class ReportsTableModel extends DefaultTableModel {

        public ReportsTableModel() {
        }

        @Override
        public int getRowCount() {
            if (exportSchemesNames == null) {
                return 0;
            }
            return exportSchemesNames.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return " ";
                case 1:
                    return "Name";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (column) {
                case 0:
                    return row + 1;
                case 1:
                    return exportSchemesNames.get(row);
                default:
                    return "";
            }
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            for (int i = 0; i < getRowCount(); i++) {
                if (getValueAt(i, columnIndex) != null) {
                    return getValueAt(i, columnIndex).getClass();
                }
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addReportLabel;
    private javax.swing.JPanel customReportsPanel;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton exportReportButton;
    private javax.swing.JLabel helpLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable reportsTable;
    private javax.swing.JScrollPane reportsTableScrollPane;
    private javax.swing.JLabel selectReportTypeLabel;
    // End of variables declaration//GEN-END:variables
}
