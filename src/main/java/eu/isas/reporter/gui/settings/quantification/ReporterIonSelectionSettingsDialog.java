package eu.isas.reporter.gui.settings.quantification;

import com.compomics.util.experiment.quantification.reporterion.ReporterMethod;
import com.compomics.util.gui.renderers.AlignedListCellRenderer;
import eu.isas.reporter.settings.ReporterIonSelectionSettings;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 * The preferences dialog.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class ReporterIonSelectionSettingsDialog extends javax.swing.JDialog {

    /**
     * Boolean indicating whether the editing of the settings has been canceled.
     */
    private boolean canceled = false;
    /**
     * The reporter method used.
     */
    private ReporterMethod reporterMethod = null;

    /**
     * Creates a new ReporterIonSelectionSettingsDialog.
     *
     * @param parentDialog the parent dialog
     * @param reporterIonSelectionSettings the settings to display
     * @param reporterMethod the reporter method used
     * @param editable boolean indicating whether the settings can be edited
     */
    public ReporterIonSelectionSettingsDialog(JDialog parentDialog, ReporterIonSelectionSettings reporterIonSelectionSettings, ReporterMethod reporterMethod, boolean editable) {
        super(parentDialog, true);
        this.reporterMethod = reporterMethod;
        initComponents();
        setUpGui(editable);
        populateGUI(reporterIonSelectionSettings);
        setLocationRelativeTo(parentDialog);
        setVisible(true);
    }

    /**
     * Creates a new ReporterIonSelectionSettingsDialog. The dialog will not
     * check whether the reporter ion m/z tolerance is sufficient to distinguish
     * ions.
     *
     * @param parentDialog the parent dialog
     * @param reporterIonSelectionSettings the settings to display
     * @param editable boolean indicating whether the settings can be edited
     */
    public ReporterIonSelectionSettingsDialog(JDialog parentDialog, ReporterIonSelectionSettings reporterIonSelectionSettings, boolean editable) {
        this(parentDialog, reporterIonSelectionSettings, null, editable);
    }

    /**
     * Set up the GUI.
     *
     * @param editable boolean indicating whether the settings can be edited
     */
    private void setUpGui(boolean editable) {

        //@TODO: Set editable or not
        
        ionSelectionComboBox.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
    }

    /**
     * Fills the GUI with the given settings.
     *
     * @param reporterIonSelectionSettings the settings to display
     */
    private void populateGUI(ReporterIonSelectionSettings reporterIonSelectionSettings) {

        ionToleranceTxt.setText(reporterIonSelectionSettings.getReporterIonsMzTolerance() + "");
        if (reporterIonSelectionSettings.isMostAccurate()) {
            ionSelectionComboBox.setSelectedIndex(0);
        } else {
            ionSelectionComboBox.setSelectedIndex(1);
        }
        if (reporterIonSelectionSettings.isSameSpectra()) {
            sameSpectra.setSelected(true);
            precursorMatching.setSelected(false);
        } else {
            sameSpectra.setSelected(false);
            precursorMatching.setSelected(true);
            mzTolTxt.setText(reporterIonSelectionSettings.getPrecursorMzTolerance() + "");
            rtTolTxt.setText(reporterIonSelectionSettings.getPrecursorRTTolerance() + "");
        }
        updateSameSpectrumMatchingSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        reporterLocationButtonGroup = new javax.swing.ButtonGroup();
        backgroundPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        spectrumAnalysisPanel = new javax.swing.JPanel();
        reporterIonMzToleranceLabel = new javax.swing.JLabel();
        ionToleranceTxt = new javax.swing.JTextField();
        ionSelectionLabel = new javax.swing.JLabel();
        ionSelectionComboBox = new javax.swing.JComboBox();
        reporterLocationPanel = new javax.swing.JPanel();
        sameSpectra = new javax.swing.JRadioButton();
        precursorMatching = new javax.swing.JRadioButton();
        mzToleranceLabel = new javax.swing.JLabel();
        mzTolTxt = new javax.swing.JTextField();
        ppmCmb = new javax.swing.JComboBox();
        rtToleranceLabel = new javax.swing.JLabel();
        rtTolTxt = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporter Ion Selection Settings");
        setResizable(false);

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        spectrumAnalysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Spectrum Analysis"));
        spectrumAnalysisPanel.setOpaque(false);

        reporterIonMzToleranceLabel.setText("Reporter Tolerance (m/z)");

        ionToleranceTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ionSelectionLabel.setText("Ion Selection");

        ionSelectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Most Accurate", "Most Intense" }));

        javax.swing.GroupLayout spectrumAnalysisPanelLayout = new javax.swing.GroupLayout(spectrumAnalysisPanel);
        spectrumAnalysisPanel.setLayout(spectrumAnalysisPanelLayout);
        spectrumAnalysisPanelLayout.setHorizontalGroup(
            spectrumAnalysisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spectrumAnalysisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(spectrumAnalysisPanelLayout.createSequentialGroup()
                        .addComponent(reporterIonMzToleranceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ionToleranceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(spectrumAnalysisPanelLayout.createSequentialGroup()
                        .addComponent(ionSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ionSelectionComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        spectrumAnalysisPanelLayout.setVerticalGroup(
            spectrumAnalysisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spectrumAnalysisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reporterIonMzToleranceLabel)
                    .addComponent(ionToleranceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(spectrumAnalysisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ionSelectionLabel)
                    .addComponent(ionSelectionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        reporterLocationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reporter Location"));
        reporterLocationPanel.setOpaque(false);

        reporterLocationButtonGroup.add(sameSpectra);
        sameSpectra.setSelected(true);
        sameSpectra.setText("Same Spectra");
        sameSpectra.setIconTextGap(10);
        sameSpectra.setOpaque(false);
        sameSpectra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sameSpectraActionPerformed(evt);
            }
        });

        reporterLocationButtonGroup.add(precursorMatching);
        precursorMatching.setText("Precursor Matching");
        precursorMatching.setIconTextGap(10);
        precursorMatching.setOpaque(false);
        precursorMatching.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precursorMatchingActionPerformed(evt);
            }
        });

        mzToleranceLabel.setText("m/z tolerance");

        mzTolTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ppmCmb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ppm", "m/z" }));

        rtToleranceLabel.setText("RT tolerance (s)");

        rtTolTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rtTolTxt.setText("10");
        rtTolTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rtTolTxtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout reporterLocationPanelLayout = new javax.swing.GroupLayout(reporterLocationPanel);
        reporterLocationPanel.setLayout(reporterLocationPanelLayout);
        reporterLocationPanelLayout.setHorizontalGroup(
            reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reporterLocationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sameSpectra, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(precursorMatching, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(reporterLocationPanelLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rtToleranceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(mzToleranceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rtTolTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(reporterLocationPanelLayout.createSequentialGroup()
                                .addComponent(mzTolTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ppmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        reporterLocationPanelLayout.setVerticalGroup(
            reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reporterLocationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sameSpectra)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(precursorMatching)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mzToleranceLabel)
                    .addComponent(mzTolTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ppmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(reporterLocationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rtToleranceLabel)
                    .addComponent(rtTolTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reporterLocationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spectrumAnalysisPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spectrumAnalysisPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reporterLocationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Save the data and close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (validateInput()) {
            dispose();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void sameSpectraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sameSpectraActionPerformed
        updateSameSpectrumMatchingSelection();
    }//GEN-LAST:event_sameSpectraActionPerformed

    private void precursorMatchingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precursorMatchingActionPerformed
        sameSpectraActionPerformed(null);
    }//GEN-LAST:event_precursorMatchingActionPerformed

    private void rtTolTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rtTolTxtActionPerformed
        // @TODO: validate the input
    }//GEN-LAST:event_rtTolTxtActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox ionSelectionComboBox;
    private javax.swing.JLabel ionSelectionLabel;
    private javax.swing.JTextField ionToleranceTxt;
    private javax.swing.JTextField mzTolTxt;
    private javax.swing.JLabel mzToleranceLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox ppmCmb;
    private javax.swing.JRadioButton precursorMatching;
    private javax.swing.JLabel reporterIonMzToleranceLabel;
    private javax.swing.ButtonGroup reporterLocationButtonGroup;
    private javax.swing.JPanel reporterLocationPanel;
    private javax.swing.JTextField rtTolTxt;
    private javax.swing.JLabel rtToleranceLabel;
    private javax.swing.JRadioButton sameSpectra;
    private javax.swing.JPanel spectrumAnalysisPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Indicates whether the user canceled the editing.
     * 
     * @return a boolean indicating whether the user canceled the editing
     */
    public boolean isCanceled() {
        return canceled;
    }
    
    /**
     * Updates the selection of the spectrum matching selection
     */
    private void updateSameSpectrumMatchingSelection() {
        // enable or disable the precursor matching options
        mzToleranceLabel.setEnabled(precursorMatching.isSelected());
        mzTolTxt.setEnabled(precursorMatching.isSelected());
        ppmCmb.setEnabled(precursorMatching.isSelected());
        rtToleranceLabel.setEnabled(precursorMatching.isSelected());
        rtTolTxt.setEnabled(precursorMatching.isSelected());
    }

    /**
     * Methods which validates the user input (returns false in case of wrong
     * input).
     *
     * @return true if the input can be processed
     */
    private boolean validateInput() {

        // check the ion torerance
        Double input;
        try {
            input = new Double(ionToleranceTxt.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please input a number for the ion tolerance.", "Ion Tolerance Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (input <= 0) {
            JOptionPane.showMessageDialog(this, "Please input a positive number for the ion tolerance.", "Ion Tolerance Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (reporterMethod != null) {
            for (String reagent1 : reporterMethod.getReagentNames()) {
                for (String reagent2 : reporterMethod.getReagentNames()) {
                    if (!reagent1.equals(reagent2) && Math.abs(reporterMethod.getReagent(reagent1).getReporterIon().getTheoreticMass()
                            - reporterMethod.getReagent(reagent2).getReporterIon().getTheoreticMass()) <= input) {
                        JOptionPane.showMessageDialog(this, 
                                "The selected ion tolerance does not make it possible to distinguish " + reagent1 + " and " + reagent2 + ".", 
                                "Ion Tolerance Error", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            }
        }

        // check the precursor matching
        if (precursorMatching.isSelected()) {
            try {
                input = new Double(mzTolTxt.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Please input a number for the precursor m/z tolerance.", "Matching Tolerance Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (input <= 0) {
                JOptionPane.showMessageDialog(this, "Please input a positive number for the precursor m/z tolerance.", "Matching Tolerance Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                input = new Double(rtTolTxt.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Please input a number for the precursor RT tolerance.", "Matching Tolerance Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (input <= 0) {
                JOptionPane.showMessageDialog(this, "Please input a positive number for the precursor RT tolerance.", "Matching Tolerance Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the settings as set by the user.
     *
     * @return the settings as set by the user
     */
    public ReporterIonSelectionSettings getReporterIonSelectionSettings() {
        
        ReporterIonSelectionSettings reporterIonSelectionSettings = new ReporterIonSelectionSettings();
        Double ionTolerance = new Double(ionToleranceTxt.getText().trim());
        reporterIonSelectionSettings.setReporterIonsMzTolerance(ionTolerance);
        reporterIonSelectionSettings.setMostAccurate(ionSelectionComboBox.getSelectedIndex() == 0);

        if (precursorMatching.isSelected()) {
            reporterIonSelectionSettings.setSameSpectra(false);
            Double matchingMzTolerance = new Double(mzTolTxt.getText().trim());
            reporterIonSelectionSettings.setPrecursorMzTolerance(matchingMzTolerance);
            Double matchingRtTolerance = new Double(rtTolTxt.getText().trim());
            reporterIonSelectionSettings.setPrecursorMzTolerance(matchingRtTolerance);
            reporterIonSelectionSettings.setPrecursorMzPpm(ppmCmb.getSelectedIndex() == 0);
        } else {
            reporterIonSelectionSettings.setSameSpectra(true);
        }

        return reporterIonSelectionSettings;
    }
}
