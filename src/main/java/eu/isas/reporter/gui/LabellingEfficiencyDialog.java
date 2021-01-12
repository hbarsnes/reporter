package eu.isas.reporter.gui;

import com.compomics.util.Util;
import com.compomics.util.db.object.ObjectsCache;
import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.biology.proteins.Peptide;
import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches_iterators.PeptideMatchesIterator;
import com.compomics.util.experiment.identification.peptide_shaker.PSParameter;
import com.compomics.util.experiment.identification.utils.ModificationUtils;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.experiment.personalization.UrParameter;
import com.compomics.util.gui.file_handling.FileAndFileFilter;
import com.compomics.util.gui.file_handling.FileChooserUtil;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.compomics.util.io.file.LastSelectedFolder;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.advanced.SequenceMatchingParameters;
import com.compomics.util.parameters.identification.search.ModificationParameters;
import eu.isas.peptideshaker.utils.PsdbParent;
import eu.isas.reporter.Reporter;
import eu.isas.reporter.io.ProjectImporter;
import java.awt.Dialog;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Labeling efficiency dialog.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class LabellingEfficiencyDialog extends javax.swing.JDialog {

    /**
     * The parent frame.
     */
    private java.awt.Frame parentFrame;
    /**
     * The normal icon.
     */
    private Image normalIcon;
    /**
     * The waiting icon.
     */
    private Image waitingIcon;
    /**
     * The last selected folder.
     */
    private LastSelectedFolder lastSelectedFolder;
    /**
     * A simple progress dialog.
     */
    private ProgressDialogX progressDialog;
    /**
     * The psdb parent used to manage the data.
     */
    private PsdbParent psdbParent;
    /**
     * The mgf files loaded.
     */
    private ArrayList<File> mgfFiles = new ArrayList<File>();
    /**
     * The modification factory.
     */
    private ModificationFactory modificationFactory = ModificationFactory.getInstance();
    /**
     * The cache to use for identification and quantification objects.
     */
    private ObjectsCache cache;
    /**
     * List of the sorted modifications.
     */
    private ArrayList<String> sortedModifications;
    /**
     * Map of the labeling efficiency: PTM name | efficiency
     */
    private HashMap<String, Double> labellingEfficiency;

    /**
     * Constructor with a dialog as owner.
     *
     * @param owner the owner of the dialog
     * @param parentFrame the parent frame
     * @param normalIcon the normal icon
     * @param waitingIcon the waiting icon
     * @param lastSelectedFolder the last selected folder
     */
    public LabellingEfficiencyDialog(Dialog owner, JFrame parentFrame, Image normalIcon, Image waitingIcon, LastSelectedFolder lastSelectedFolder) {

        super(owner, true);

        this.parentFrame = parentFrame;
        this.normalIcon = normalIcon;
        this.waitingIcon = waitingIcon;

        initComponents();
        setUpGui();
    }

    /**
     * Set up the GUI.
     */
    private void setUpGui() {
        setTableProperties();
    }

    /**
     * Set up the properties of the tables.
     */
    private void setTableProperties() {

        efficiencyTable.getColumn(" ").setMaxWidth(30);
        efficiencyTable.getColumn("Name").setMaxWidth(100);
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
        closeButton = new javax.swing.JButton();
        fileSelectiontPanel2 = new javax.swing.JPanel();
        spectrumFilesLabel2 = new javax.swing.JLabel();
        txtSpectraFileLocation2 = new javax.swing.JTextField();
        idFilesLabel2 = new javax.swing.JLabel();
        txtIdFileLocation2 = new javax.swing.JTextField();
        addIdFilesButton2 = new javax.swing.JButton();
        addSpectraFilesJButton2 = new javax.swing.JButton();
        databaseFileLabel2 = new javax.swing.JLabel();
        fastaTxt2 = new javax.swing.JTextField();
        addDbButton2 = new javax.swing.JButton();
        efficiencyPanel = new javax.swing.JPanel();
        efficiencyTableScrollPane = new javax.swing.JScrollPane();
        efficiencyTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        fileSelectiontPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Files Selection"));
        fileSelectiontPanel2.setOpaque(false);

        spectrumFilesLabel2.setText("Spectrum File(s)");

        txtSpectraFileLocation2.setEditable(false);
        txtSpectraFileLocation2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        idFilesLabel2.setText("Project File");

        txtIdFileLocation2.setEditable(false);
        txtIdFileLocation2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtIdFileLocation2.setText("Please import a project");

        addIdFilesButton2.setText("Browse");
        addIdFilesButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIdFilesButton2ActionPerformed(evt);
            }
        });

        addSpectraFilesJButton2.setText("Browse");
        addSpectraFilesJButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSpectraFilesJButton2ActionPerformed(evt);
            }
        });

        databaseFileLabel2.setText("Database File");

        fastaTxt2.setEditable(false);
        fastaTxt2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        addDbButton2.setText("Browse");
        addDbButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDbButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fileSelectiontPanel2Layout = new javax.swing.GroupLayout(fileSelectiontPanel2);
        fileSelectiontPanel2.setLayout(fileSelectiontPanel2Layout);
        fileSelectiontPanel2Layout.setHorizontalGroup(
            fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSelectiontPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectrumFilesLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databaseFileLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idFilesLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdFileLocation2)
                    .addComponent(txtSpectraFileLocation2, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(fastaTxt2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addSpectraFilesJButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addIdFilesButton2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addDbButton2))
                .addContainerGap())
        );
        fileSelectiontPanel2Layout.setVerticalGroup(
            fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSelectiontPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idFilesLabel2)
                    .addComponent(txtIdFileLocation2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addIdFilesButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spectrumFilesLabel2)
                    .addComponent(txtSpectraFileLocation2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addSpectraFilesJButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileSelectiontPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseFileLabel2)
                    .addComponent(fastaTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addDbButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        efficiencyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Labelling Efficiency"));
        efficiencyPanel.setOpaque(false);

        efficiencyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        efficiencyTableScrollPane.setViewportView(efficiencyTable);

        javax.swing.GroupLayout efficiencyPanelLayout = new javax.swing.GroupLayout(efficiencyPanel);
        efficiencyPanel.setLayout(efficiencyPanelLayout);
        efficiencyPanelLayout.setHorizontalGroup(
            efficiencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(efficiencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(efficiencyTableScrollPane)
                .addContainerGap())
        );
        efficiencyPanelLayout.setVerticalGroup(
            efficiencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(efficiencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(efficiencyTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(efficiencyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton))
                    .addComponent(fileSelectiontPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileSelectiontPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(efficiencyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(closeButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Add the identification files.
     * 
     * @param evt 
     */
    private void addIdFilesButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIdFilesButton2ActionPerformed

        String psdbFileFilterDescription = "PeptideShaker (.psdb)";
        //String zipFileFilterDescription = "Zipped PeptideShaker (.zip)"; // @TODO: support zip files
        String lastSelectedFolderPath = lastSelectedFolder.getLastSelectedFolder();
        //        FileAndFileFilter selectedFileAndFilter = Util.getUserSelectedFile(this, new String[]{".psdb", ".zip"},
        //                new String[]{psdbFileFilterDescription, zipFileFilterDescription}, "Select Identification File(s)", lastSelectedFolderPath, null, true, false, false, 0);
        FileAndFileFilter selectedFileAndFilter = FileChooserUtil.getUserSelectedFile(this, new String[]{".psdb"},
                new String[]{psdbFileFilterDescription}, "Select Identification File(s)", lastSelectedFolderPath, null, true, false, false, 0);

        if (selectedFileAndFilter != null) {

            File selectedFile = selectedFileAndFilter.getFile();
            lastSelectedFolder.setLastSelectedFolder(selectedFile.getParent());

            if (selectedFile.getName().endsWith(".zip")) {
                //importPeptideShakerZipFile(selectedFile); // @TODO: support zip files
            } else if (selectedFile.getName().endsWith(".psdb")) {
                importPeptideShakerFile(selectedFile);
                //                reporterGui.getUserPreferences().addRecentProject(selectedFile); // @TOOD: implement me?
                //                reporterGui.updateRecentProjectsList();
            } else {
                JOptionPane.showMessageDialog(this, "Not a PeptideShaker file (.psdb).", "Unsupported File.", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_addIdFilesButton2ActionPerformed

    /**
     * Add the spectrum files.
     * 
     * @param evt 
     */
    private void addSpectraFilesJButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSpectraFilesJButton2ActionPerformed

        // @TODO: add mgf validation etc like for PeptideShaker
        JFileChooser fileChooser = new JFileChooser(lastSelectedFolder.getLastSelectedFolder());
        fileChooser.setDialogTitle("Select Spectra File(s)");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setMultiSelectionEnabled(true);

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File myFile) {
                return myFile.getName().toLowerCase().endsWith(".mgf")
                        || myFile.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Supported formats: .mgf";
            }
        };

        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showDialog(this.getParent(), "Add");

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                for (File newFile : fileChooser.getSelectedFiles()) {
                    if (newFile.isDirectory()) {
                        File[] tempFiles = newFile.listFiles();
                        for (File file : tempFiles) {
                            if (file.getName().toLowerCase().endsWith(".mgf")) {
                                if (!mgfFiles.contains(file)) {
                                    mgfFiles.add(file);
                                    psdbParent.getProjectDetails().addSpectrumFile(file);
                                }
                            }
                        }
                    } else if (newFile.getName().toLowerCase().endsWith(".mgf")) {
                        if (!mgfFiles.contains(newFile)) {
                            mgfFiles.add(newFile);
                            psdbParent.getProjectDetails().addSpectrumFile(newFile);
                            spectrumFactory.addSpectra(newFile, null); // @TODO: add progress dialog!!
                        }
                    }

                    lastSelectedFolder.setLastSelectedFolder(newFile.getPath());
                }

                txtSpectraFileLocation2.setText(mgfFiles.size() + " file(s) selected");
            }
        } catch (Exception e) {
            progressDialog.setRunFinished();
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while reading the mgf file.", "Mgf Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_addSpectraFilesJButton2ActionPerformed

    /**
     * Set the FASTA file.
     * 
     * @param evt 
     */
    private void addDbButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDbButton2ActionPerformed
        JFileChooser fileChooser;

        //        if (searchParameters != null && searchParameters.getFastaFile() != null && searchParameters.getFastaFile().exists()) {
        //            fileChooser = new JFileChooser(searchParameters.getFastaFile());
        //        } else {
        //            fileChooser = new JFileChooser(peptideShakerGUI.getLastSelectedFolder());
        //        }
        fileChooser = new JFileChooser(lastSelectedFolder.getLastSelectedFolder());

        fileChooser.setDialogTitle("Select FASTA File(s)");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File myFile) {
                return myFile.getName().toLowerCase().endsWith("fasta")
                        || myFile.getName().toLowerCase().endsWith("fast")
                        || myFile.getName().toLowerCase().endsWith("fas")
                        || myFile.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Supported formats: FASTA (.fasta)";
            }
        };

        fileChooser.setFileFilter(filter);
        int returnVal = fileChooser.showDialog(this.getParent(), "Open");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fastaFile = fileChooser.getSelectedFile();
            lastSelectedFolder.setLastSelectedFolder(fastaFile.getAbsolutePath());
            fastaTxt2.setText(fastaFile.getName());
            psdbParent.getProjectDetails().setFastaFile(fastaFile);
        }
    }//GEN-LAST:event_addDbButton2ActionPerformed

    /**
     * Close the dialog.
     * 
     * @param evt 
     */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addDbButton2;
    private javax.swing.JButton addIdFilesButton2;
    private javax.swing.JButton addSpectraFilesJButton2;
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel databaseFileLabel2;
    private javax.swing.JPanel efficiencyPanel;
    private javax.swing.JTable efficiencyTable;
    private javax.swing.JScrollPane efficiencyTableScrollPane;
    private javax.swing.JTextField fastaTxt2;
    private javax.swing.JPanel fileSelectiontPanel2;
    private javax.swing.JLabel idFilesLabel2;
    private javax.swing.JLabel spectrumFilesLabel2;
    private javax.swing.JTextField txtIdFileLocation2;
    private javax.swing.JTextField txtSpectraFileLocation2;
    // End of variables declaration//GEN-END:variables

    /**
     * Method used to import a psdb file.
     *
     * @param psFile a psdb file
     */
    private void importPeptideShakerFile(final File psFile) {

        progressDialog = new ProgressDialogX(this, parentFrame,
                normalIcon,
                waitingIcon, true);

        progressDialog.setPrimaryProgressCounterIndeterminate(true);
        progressDialog.setTitle("Importing Project. Please Wait...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("ImportThread") {
            @Override
            public void run() {

                try {
                    psdbParent = new PsdbParent(Reporter.getMatchesFolder());
                    psdbParent.setPsdbFile(psFile);
                    ProjectImporter projectImporter = new ProjectImporter(LabellingEfficiencyDialog.this);
                    projectImporter.importPeptideShakerProject(psdbParent, mgfFiles, progressDialog);
                    projectImporter.importReporterProject(psdbParent, progressDialog);

                    if (progressDialog.isRunCanceled()) {
                        progressDialog.setRunFinished();
                        progressDialog.dispose();
                        return;
                    }

                    txtSpectraFileLocation2.setText(psdbParent.getProjectDetails().getSpectrumFileNames().size() + " files loaded"); //@TODO: allow editing
                    fastaTxt2.setText(psdbParent.getProjectDetails().getFastaFile());
                    txtIdFileLocation2.setText(psdbParent.getPsdbFile().getName());

                    cache = new ObjectsCache();

                    estimateLabellingEfficiency();
                    refresh();

                    progressDialog.setRunFinished();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.setRunCanceled();
                    JOptionPane.showMessageDialog(LabellingEfficiencyDialog.this,
                            "An error occurred while estimating the labelling efficiency.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.start();
    }

    /**
     * Estimates the labeling efficiency for the variable modifications in the
     * psdb parent.
     *
     *
     * @throws java.sql.SQLException exception thrown whenever an error occurred
     * while interacting with the database
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while interacting with a file
     * @throws java.lang.ClassNotFoundException exception thrown whenever an
     * error occurred while deserializing an object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading error occurred
     */
    private void estimateLabellingEfficiency() throws SQLException, IOException, ClassNotFoundException, InterruptedException {

        IdentificationParameters identificationParameters = psdbParent.getIdentificationParameters();
        SequenceProvider sequenceProvider = psdbParent.getSequenceProvider();
        SequenceMatchingParameters proteinSequenceMatchingPreferences = identificationParameters.getSequenceMatchingParameters();
        ModificationParameters modificationParameters = identificationParameters.getSearchParameters().getModificationParameters();
        sortedModifications = new ArrayList<>(modificationParameters.getAllNotFixedModifications());
        Collections.sort(sortedModifications);
        HashMap<String, Integer> nModifiedMap = new HashMap<>(sortedModifications.size()),
                nPossibleMap = new HashMap<>(sortedModifications.size());
        ArrayList<Modification> modifications = new ArrayList<>(sortedModifications.size());
        for (String ptmName : sortedModifications) {
            nModifiedMap.put(ptmName, 0);
            nPossibleMap.put(ptmName, 0);
            modifications.add(modificationFactory.getModification(ptmName));
        }

        Identification identification = psdbParent.getIdentification();

        PSParameter psParameter = new PSParameter();
        ArrayList<UrParameter> parameters = new ArrayList<UrParameter>(1);
        parameters.add(psParameter);

        PeptideMatchesIterator peptideMatchesIterator = identification.getPeptideMatchesIterator(progressDialog);
        PeptideMatch peptideMatch;

        while ((peptideMatch = peptideMatchesIterator.next()) != null) {

            Peptide peptide = peptideMatch.getPeptide();

            for (Modification modification : modifications) {

                String modificationName = modification.getName();
                int nNew = ModificationUtils.getPossibleModificationSites(peptide, modification, sequenceProvider, proteinSequenceMatchingPreferences).length;
                if (nNew > 0) {
                    Integer nPossible = nPossibleMap.get(modificationName);
                    nPossible += nNew;
                    nPossibleMap.put(modificationName, nPossible);
                }
                nNew = 0;
                if (peptide.getNVariableModifications() > 0) {
                    for (ModificationMatch modificationMatch : peptide.getVariableModifications()) {
                        if (modificationMatch.getModification().equals(modificationName)) {
                            nNew++;
                        }
                    }
                }
                if (nNew > 0) {
                    Integer nModified = nModifiedMap.get(modificationName);
                    nModified += nNew;
                    nModifiedMap.put(modificationName, nModified);
                }
            }
        }

        for (String ptmName : sortedModifications) {

            Integer nModified = nModifiedMap.get(ptmName);
            Integer nPossible = nPossibleMap.get(ptmName);
            Double efficiency = nModified.doubleValue() / nPossible;
            labellingEfficiency.put(ptmName, efficiency);

        }
    }

    /**
     * Updates the combo box and table values based on the currently selected
     * quantification method.
     */
    private void refresh() {

        efficiencyTable.setModel(new LabellingEfficiencyTableModel());

        setTableProperties();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                efficiencyTable.revalidate();
                efficiencyTable.repaint();
            }
        });
    }

    /**
     * Table model for the sample to reporter ion assignment.
     */
    private class LabellingEfficiencyTableModel extends DefaultTableModel {

        @Override
        public int getRowCount() {
            if (sortedModifications == null || psdbParent == null) {
                return 0;
            }
            return sortedModifications.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return " ";
                case 1:
                    return "Name";
                case 2:
                    return "Efficiency";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            String ptmName = sortedModifications.get(row);
            switch (column) {
                case 0:
                    return (row + 1);
                case 1:
                    return ptmName;
                case 2:
                    Double efficiency = labellingEfficiency.get(ptmName);
                    Double efficiencyInPercent = Util.roundDouble(100.0 * efficiency, 2);
                    return efficiencyInPercent;
                default:
                    return "";
            }
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
