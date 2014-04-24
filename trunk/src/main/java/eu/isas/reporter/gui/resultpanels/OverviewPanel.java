package eu.isas.reporter.gui.resultpanels;

import com.compomics.util.gui.GuiUtilities;
import com.compomics.util.gui.error_handlers.HelpDialog;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import eu.isas.peptideshaker.gui.tablemodels.ProteinTableModel;
import eu.isas.peptideshaker.preferences.SpectrumCountingPreferences.SpectralCountingMethod;
import eu.isas.reporter.gui.ReporterGUI;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Harald Barsnes
 * @author Marc Vaudel
 */
public class OverviewPanel extends javax.swing.JPanel {

    /**
     * The protein table column header tooltips.
     */
    private ArrayList<String> proteinTableToolTips;
    /**
     * The main gui class.
     */
    private ReporterGUI reporterGUI;
    /**
     * A simple progress dialog.
     */
    private ProgressDialogX progressDialog;
    
    /**
     * Creates a new OverviewPanel.
     * 
     * @param reporterGUI 
     */
    public OverviewPanel(ReporterGUI reporterGUI) {
        initComponents();
        this.reporterGUI = reporterGUI;
        
        setUpGui();
        formComponentResized(null);
    }
    
    /**
     * Sets up the GUI components
     */
    private void setUpGui() {
        // set main table properties
        proteinTable.getTableHeader().setReorderingAllowed(false);
        
        // correct the color for the upper right corner
        JPanel proteinCorner = new JPanel();
        proteinCorner.setBackground(proteinTable.getTableHeader().getBackground());
        proteinScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, proteinCorner);
        
        proteinTable.setAutoCreateRowSorter(true);
        
        // make sure that the user is made aware that the tool is doing something during sorting of the protein table
        proteinTable.getRowSorter().addRowSorterListener(new RowSorterListener() {

            @Override
            public void sorterChanged(RowSorterEvent e) {

                if (e.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
                    reporterGUI.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                    proteinTable.getTableHeader().setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

                    // change the peptide shaker icon to a "waiting version"
                    reporterGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter-orange.gif")));
                } else if (e.getType() == RowSorterEvent.Type.SORTED) {
                    reporterGUI.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    proteinTable.getTableHeader().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

                    // change the peptide shaker icon to a "waiting version"
                    reporterGUI.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")));
                }
            }
        });
        
        // make sure that the scroll panes are see-through
        proteinScrollPane.getViewport().setOpaque(false);
        
        setUpTableHeaderToolTips();
    }
    
    /**
     * Updates the display with the underlying data
     */
    public void updateDisplay() {
        

        progressDialog = new ProgressDialogX(reporterGUI,
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter-orange.gif")),
                true);
        progressDialog.setPrimaryProgressCounterIndeterminate(true);
        progressDialog.setTitle("Loading Overview. Please Wait...");

        new Thread(new Runnable() {
            public void run() {
                try {
                    progressDialog.setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    // ignore
                }
            }
        }, "ProgressDialog").start();

        new Thread("DisplayThread") {
            @Override
            public void run() {

                try {
                    
                    progressDialog.setPrimaryProgressCounterIndeterminate(true);
                    progressDialog.setTitle("Preparing Overview. Please Wait...");

                       ArrayList<String> proteinKeys = reporterGUI.getIdentificationFeaturesGenerator().getProcessedProteinKeys(progressDialog, reporterGUI.getFilterPreferences());
                    // update the table model
                    if (proteinTable.getRowCount() > 0) {
                        ((ProteinTableModel) proteinTable.getModel()).updateDataModel(reporterGUI.getIdentification(), reporterGUI.getIdentificationFeaturesGenerator(), reporterGUI.getDisplayFeaturesGenerator(), reporterGUI.getSearchParameters(), reporterGUI.getExceptionHandler(), proteinKeys);
                    } else {
                        ProteinTableModel proteinTableModel = new ProteinTableModel(reporterGUI.getIdentification(), reporterGUI.getIdentificationFeaturesGenerator(), reporterGUI.getDisplayFeaturesGenerator(), reporterGUI.getSearchParameters(), reporterGUI.getExceptionHandler(), proteinKeys);
                        proteinTable.setModel(proteinTableModel);
                    }

                    setTableProperties();
                    
                    ((DefaultTableModel) proteinTable.getModel()).fireTableDataChanged();

                    // update spectrum counting column header tooltip
                    if (reporterGUI.getSpectrumCountingPreferences().getSelectedMethod() == SpectralCountingMethod.EMPAI) {
                        proteinTableToolTips.set(proteinTable.getColumn("MS2 Quant.").getModelIndex(), "Protein MS2 Quantification - emPAI");
                    } else if (reporterGUI.getSpectrumCountingPreferences().getSelectedMethod() == SpectralCountingMethod.NSAF) {
                        proteinTableToolTips.set(proteinTable.getColumn("MS2 Quant.").getModelIndex(), "Protein MS2 Quantification - NSAF");
                    } else {
                        proteinTableToolTips.set(proteinTable.getColumn("MS2 Quant.").getModelIndex(), "Protein MS2 Quantification");
                    }

                    if (reporterGUI.getIdentificationDisplayPreferences().showScores()) {
                        proteinTableToolTips.set(proteinTable.getColumnCount() - 2, "Protein Score");
                    } else {
                        proteinTableToolTips.set(proteinTable.getColumnCount() - 2, "Protein Confidence");
                    }

                    
                    String title = ReporterGUI.TITLED_BORDER_HORIZONTAL_PADDING + "Proteins (";
                    try {
                        int nValidated = reporterGUI.getIdentificationFeaturesGenerator().getNValidatedProteins();
                        int nConfident = reporterGUI.getIdentificationFeaturesGenerator().getNConfidentProteins();
                        int nProteins = proteinTable.getRowCount();
                        if (nConfident > 0) {
                            title += nValidated + "/" + nProteins + " - " + nConfident + " confident, " + (nValidated - nConfident) + " doubtful";
                        } else {
                            title += nValidated + "/" + nProteins;
                        }
                    } catch (Exception eNValidated) {
                        reporterGUI.catchException(eNValidated);
                    }
                    title += ")" + ReporterGUI.TITLED_BORDER_HORIZONTAL_PADDING;

                    ((TitledBorder) proteinsLayeredPanel.getBorder()).setTitle(title);
                    proteinsLayeredPanel.repaint();

                    // updateProteinTableCellRenderers();

                    // enable the contextual export options
                    exportProteinsJButton.setEnabled(true);

                    reporterGUI.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    progressDialog.setRunFinished();

//                    new Thread(new Runnable() {
//                        public void run() {
//                            peptideShakerGUI.checkNewsFeed();
//                            peptideShakerGUI.showNotesNotification();
//                            updateSelection(true); // @TODO: this is sometimes too fast and results in Row index out of range...
//                            proteinTable.requestFocus();
//                        }
//                    }, "UpdateSelectionThread").start();

                } catch (Exception e) {
                    reporterGUI.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    reporterGUI.catchException(e);
                    progressDialog.setRunFinished();
                }
            }
        }.start();
    }
    
    
    /**
     * Sets up the table header tooltips.
     */
    private void setUpTableHeaderToolTips() {
        proteinTableToolTips = new ArrayList<String>();
        proteinTableToolTips.add(null);
        proteinTableToolTips.add("Starred");
        proteinTableToolTips.add("Protein Inference Class");
        proteinTableToolTips.add("Protein Accession Number");
        proteinTableToolTips.add("Protein Description");
        proteinTableToolTips.add("Protein Seqeunce Coverage (%) (Observed / Possible)");
        proteinTableToolTips.add("Number of Peptides (Validated / Total)");
        proteinTableToolTips.add("Number of Spectra (Validated / Total)");
        proteinTableToolTips.add("MS2 Quantification");
        proteinTableToolTips.add("Protein Molecular Weight (kDa)");

        if (reporterGUI.getIdentificationDisplayPreferences() != null && reporterGUI.getIdentificationDisplayPreferences().showScores()) {
            proteinTableToolTips.add("Protein Score");
        } else {
            proteinTableToolTips.add("Protein Confidence");
        }

        proteinTableToolTips.add("Validated");
    }

    /**
     * Set up the properties of the tables.
     */
    private void setTableProperties() {
        setProteinTableProperties();
    }

    /**
     * Set up the properties of the protein table.
     */
    private void setProteinTableProperties() {
        ProteinTableModel.setProteinTableProperties(proteinTable, reporterGUI.getSparklineColor(), reporterGUI.getSparklineColorNonValidated(), 
                reporterGUI.getSparklineColorNotFound(), reporterGUI.getSparklineColorDoubtful(), reporterGUI.getScoreAndConfidenceDecimalFormat(), 
                this.getClass(), reporterGUI.getMetrics().getMaxProteinKeyLength());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundLayeredPane = new javax.swing.JLayeredPane();
        overviewJPanel = new javax.swing.JPanel();
        overviewJSplitPane = new javax.swing.JSplitPane();
        proteinsJPanel = new javax.swing.JPanel();
        proteinsLayeredPane = new javax.swing.JLayeredPane();
        proteinsLayeredPanel = new javax.swing.JPanel();
        proteinScrollPane = new javax.swing.JScrollPane();
        proteinTable = new JTable() {
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        String tip = (String) proteinTableToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        proteinsHelpJButton = new javax.swing.JButton();
        exportProteinsJButton = new javax.swing.JButton();
        hideProteinsJButton = new javax.swing.JButton();
        contextMenuProteinsBackgroundPanel = new javax.swing.JPanel();
        ratioPlotsJPanel = new javax.swing.JPanel();
        ratioPlotsLayeredPane = new javax.swing.JLayeredPane();
        ratioPlotsTitledPanel = new javax.swing.JPanel();
        ratioPlotsInnerPanel = new javax.swing.JPanel();
        ratioPLotHelpJButton = new javax.swing.JButton();
        exportRatioPlotContextJButton = new javax.swing.JButton();
        hideRatioPlotJButton = new javax.swing.JButton();
        ratioPlotOptionsJButton = new javax.swing.JButton();
        contextMenuRatioPlotBackgroundPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        overviewJPanel.setBackground(new java.awt.Color(255, 255, 255));
        overviewJPanel.setOpaque(false);
        overviewJPanel.setPreferredSize(new java.awt.Dimension(900, 800));

        overviewJSplitPane.setBorder(null);
        overviewJSplitPane.setDividerLocation(300);
        overviewJSplitPane.setDividerSize(0);
        overviewJSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        overviewJSplitPane.setResizeWeight(0.5);
        overviewJSplitPane.setOpaque(false);

        proteinsJPanel.setOpaque(false);

        proteinsLayeredPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Proteins"));
        proteinsLayeredPanel.setOpaque(false);

        proteinScrollPane.setOpaque(false);

        proteinTable.setModel(new ProteinTableModel());
        proteinTable.setOpaque(false);
        proteinTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        proteinTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                proteinTableMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                proteinTableMouseReleased(evt);
            }
        });
        proteinTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                proteinTableMouseMoved(evt);
            }
        });
        proteinTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                proteinTableKeyReleased(evt);
            }
        });
        proteinScrollPane.setViewportView(proteinTable);

        javax.swing.GroupLayout proteinsLayeredPanelLayout = new javax.swing.GroupLayout(proteinsLayeredPanel);
        proteinsLayeredPanel.setLayout(proteinsLayeredPanelLayout);
        proteinsLayeredPanelLayout.setHorizontalGroup(
            proteinsLayeredPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 938, Short.MAX_VALUE)
            .addGroup(proteinsLayeredPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(proteinsLayeredPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(proteinScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 918, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        proteinsLayeredPanelLayout.setVerticalGroup(
            proteinsLayeredPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
            .addGroup(proteinsLayeredPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(proteinsLayeredPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(proteinScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        proteinsLayeredPanel.setBounds(0, 0, 950, 300);
        proteinsLayeredPane.add(proteinsLayeredPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        proteinsHelpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/help_no_frame_grey.png"))); // NOI18N
        proteinsHelpJButton.setToolTipText("Help");
        proteinsHelpJButton.setBorder(null);
        proteinsHelpJButton.setBorderPainted(false);
        proteinsHelpJButton.setContentAreaFilled(false);
        proteinsHelpJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/help_no_frame.png"))); // NOI18N
        proteinsHelpJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                proteinsHelpJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                proteinsHelpJButtonMouseExited(evt);
            }
        });
        proteinsHelpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proteinsHelpJButtonActionPerformed(evt);
            }
        });
        proteinsHelpJButton.setBounds(930, 0, 10, 19);
        proteinsLayeredPane.add(proteinsHelpJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        exportProteinsJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame_grey.png"))); // NOI18N
        exportProteinsJButton.setToolTipText("Copy to File");
        exportProteinsJButton.setBorder(null);
        exportProteinsJButton.setBorderPainted(false);
        exportProteinsJButton.setContentAreaFilled(false);
        exportProteinsJButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame_grey.png"))); // NOI18N
        exportProteinsJButton.setEnabled(false);
        exportProteinsJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame.png"))); // NOI18N
        exportProteinsJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportProteinsJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exportProteinsJButtonMouseExited(evt);
            }
        });
        exportProteinsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportProteinsJButtonActionPerformed(evt);
            }
        });
        exportProteinsJButton.setBounds(920, 0, 10, 19);
        proteinsLayeredPane.add(exportProteinsJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        hideProteinsJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hide_grey.png"))); // NOI18N
        hideProteinsJButton.setToolTipText("Hide Proteins (Shift+Ctrl+P)");
        hideProteinsJButton.setBorder(null);
        hideProteinsJButton.setBorderPainted(false);
        hideProteinsJButton.setContentAreaFilled(false);
        hideProteinsJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hide.png"))); // NOI18N
        hideProteinsJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hideProteinsJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hideProteinsJButtonMouseExited(evt);
            }
        });
        hideProteinsJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideProteinsJButtonActionPerformed(evt);
            }
        });
        hideProteinsJButton.setBounds(910, 0, 10, 19);
        proteinsLayeredPane.add(hideProteinsJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        contextMenuProteinsBackgroundPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contextMenuProteinsBackgroundPanelLayout = new javax.swing.GroupLayout(contextMenuProteinsBackgroundPanel);
        contextMenuProteinsBackgroundPanel.setLayout(contextMenuProteinsBackgroundPanelLayout);
        contextMenuProteinsBackgroundPanelLayout.setHorizontalGroup(
            contextMenuProteinsBackgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        contextMenuProteinsBackgroundPanelLayout.setVerticalGroup(
            contextMenuProteinsBackgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        contextMenuProteinsBackgroundPanel.setBounds(910, 0, 40, 19);
        proteinsLayeredPane.add(contextMenuProteinsBackgroundPanel, javax.swing.JLayeredPane.POPUP_LAYER);

        javax.swing.GroupLayout proteinsJPanelLayout = new javax.swing.GroupLayout(proteinsJPanel);
        proteinsJPanel.setLayout(proteinsJPanelLayout);
        proteinsJPanelLayout.setHorizontalGroup(
            proteinsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(proteinsLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
        );
        proteinsJPanelLayout.setVerticalGroup(
            proteinsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(proteinsLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        overviewJSplitPane.setTopComponent(proteinsJPanel);

        ratioPlotsJPanel.setOpaque(false);

        ratioPlotsTitledPanel.setBackground(new java.awt.Color(255, 255, 255));
        ratioPlotsTitledPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Ratio Plots"));
        ratioPlotsTitledPanel.setOpaque(false);

        ratioPlotsInnerPanel.setBackground(new java.awt.Color(255, 255, 255));
        ratioPlotsInnerPanel.setLayout(new javax.swing.BoxLayout(ratioPlotsInnerPanel, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout ratioPlotsTitledPanelLayout = new javax.swing.GroupLayout(ratioPlotsTitledPanel);
        ratioPlotsTitledPanel.setLayout(ratioPlotsTitledPanelLayout);
        ratioPlotsTitledPanelLayout.setHorizontalGroup(
            ratioPlotsTitledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ratioPlotsTitledPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ratioPlotsInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 918, Short.MAX_VALUE)
                .addContainerGap())
        );
        ratioPlotsTitledPanelLayout.setVerticalGroup(
            ratioPlotsTitledPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ratioPlotsTitledPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ratioPlotsInnerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );

        ratioPlotsTitledPanel.setBounds(0, 0, 950, 460);
        ratioPlotsLayeredPane.add(ratioPlotsTitledPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        ratioPLotHelpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/help_no_frame_grey.png"))); // NOI18N
        ratioPLotHelpJButton.setToolTipText("Help");
        ratioPLotHelpJButton.setBorder(null);
        ratioPLotHelpJButton.setBorderPainted(false);
        ratioPLotHelpJButton.setContentAreaFilled(false);
        ratioPLotHelpJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/help_no_frame.png"))); // NOI18N
        ratioPLotHelpJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratioPLotHelpJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratioPLotHelpJButtonMouseExited(evt);
            }
        });
        ratioPLotHelpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ratioPLotHelpJButtonActionPerformed(evt);
            }
        });
        ratioPLotHelpJButton.setBounds(930, 0, 10, 19);
        ratioPlotsLayeredPane.add(ratioPLotHelpJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        exportRatioPlotContextJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame_grey.png"))); // NOI18N
        exportRatioPlotContextJButton.setToolTipText("Copy to Clipboard");
        exportRatioPlotContextJButton.setBorder(null);
        exportRatioPlotContextJButton.setBorderPainted(false);
        exportRatioPlotContextJButton.setContentAreaFilled(false);
        exportRatioPlotContextJButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame_grey.png"))); // NOI18N
        exportRatioPlotContextJButton.setEnabled(false);
        exportRatioPlotContextJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/export_no_frame.png"))); // NOI18N
        exportRatioPlotContextJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportRatioPlotContextJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exportRatioPlotContextJButtonMouseExited(evt);
            }
        });
        exportRatioPlotContextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportRatioPlotContextJButtonActionPerformed(evt);
            }
        });
        exportRatioPlotContextJButton.setBounds(920, 0, 10, 19);
        ratioPlotsLayeredPane.add(exportRatioPlotContextJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        hideRatioPlotJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hide_grey.png"))); // NOI18N
        hideRatioPlotJButton.setToolTipText("Hide Coverage (Shift+Ctrl+E)");
        hideRatioPlotJButton.setBorder(null);
        hideRatioPlotJButton.setBorderPainted(false);
        hideRatioPlotJButton.setContentAreaFilled(false);
        hideRatioPlotJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/hide.png"))); // NOI18N
        hideRatioPlotJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hideRatioPlotJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hideRatioPlotJButtonMouseExited(evt);
            }
        });
        hideRatioPlotJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideRatioPlotJButtonActionPerformed(evt);
            }
        });
        hideRatioPlotJButton.setBounds(910, 0, 10, 19);
        ratioPlotsLayeredPane.add(hideRatioPlotJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        ratioPlotOptionsJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/contextual_menu_gray.png"))); // NOI18N
        ratioPlotOptionsJButton.setToolTipText("Coverage Options");
        ratioPlotOptionsJButton.setBorder(null);
        ratioPlotOptionsJButton.setBorderPainted(false);
        ratioPlotOptionsJButton.setContentAreaFilled(false);
        ratioPlotOptionsJButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/contextual_menu_black.png"))); // NOI18N
        ratioPlotOptionsJButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ratioPlotOptionsJButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ratioPlotOptionsJButtonMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ratioPlotOptionsJButtonMouseReleased(evt);
            }
        });
        ratioPlotOptionsJButton.setBounds(895, 5, 10, 19);
        ratioPlotsLayeredPane.add(ratioPlotOptionsJButton, javax.swing.JLayeredPane.POPUP_LAYER);

        contextMenuRatioPlotBackgroundPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contextMenuRatioPlotBackgroundPanelLayout = new javax.swing.GroupLayout(contextMenuRatioPlotBackgroundPanel);
        contextMenuRatioPlotBackgroundPanel.setLayout(contextMenuRatioPlotBackgroundPanelLayout);
        contextMenuRatioPlotBackgroundPanelLayout.setHorizontalGroup(
            contextMenuRatioPlotBackgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );
        contextMenuRatioPlotBackgroundPanelLayout.setVerticalGroup(
            contextMenuRatioPlotBackgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        contextMenuRatioPlotBackgroundPanel.setBounds(890, 0, 50, 19);
        ratioPlotsLayeredPane.add(contextMenuRatioPlotBackgroundPanel, javax.swing.JLayeredPane.POPUP_LAYER);

        javax.swing.GroupLayout ratioPlotsJPanelLayout = new javax.swing.GroupLayout(ratioPlotsJPanel);
        ratioPlotsJPanel.setLayout(ratioPlotsJPanelLayout);
        ratioPlotsJPanelLayout.setHorizontalGroup(
            ratioPlotsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ratioPlotsLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
        );
        ratioPlotsJPanelLayout.setVerticalGroup(
            ratioPlotsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ratioPlotsLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );

        overviewJSplitPane.setRightComponent(ratioPlotsJPanel);

        javax.swing.GroupLayout overviewJPanelLayout = new javax.swing.GroupLayout(overviewJPanel);
        overviewJPanel.setLayout(overviewJPanelLayout);
        overviewJPanelLayout.setHorizontalGroup(
            overviewJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(overviewJSplitPane)
                .addContainerGap())
        );
        overviewJPanelLayout.setVerticalGroup(
            overviewJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(overviewJSplitPane)
                .addContainerGap())
        );

        overviewJPanel.setBounds(0, 0, 990, 780);
        backgroundLayeredPane.add(overviewJPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 993, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(backgroundLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 993, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(backgroundLayeredPane, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void proteinTableMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proteinTableMouseExited
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_proteinTableMouseExited

    private void proteinTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proteinTableMouseReleased

//        if (evt != null) {
//            peptideShakerGUI.resetSelectedItems();
//        }
//
//        int row = proteinTable.getSelectedRow();
//        int column = proteinTable.getSelectedColumn();
//
//        int proteinIndex = -1;
//
//        if (row != -1) {
//            proteinIndex = proteinTable.convertRowIndexToModel(row);
//        }
//
//        if (evt == null || (evt.getButton() == MouseEvent.BUTTON1 && (proteinIndex != -1 && column != -1))) {
//
//            if (proteinIndex != -1) {
//
//                this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
//
//                // update the peptide selection
//                updatedPeptideSelection(proteinIndex);
//
//                // update the sequence coverage map
//                //updateSequenceCoverageMap(proteinIndex); // @TODO: will be called when a psm is selected anyway??
//
//                // remember the selection
//                newItemSelection();
//
//                this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//
//                if (column == proteinTable.getColumn("  ").getModelIndex()) {
//                    String proteinKey = proteinKeys.get(proteinIndex);
//                    PSParameter psParameter = new PSParameter();
//                    try {
//                        psParameter = (PSParameter) peptideShakerGUI.getIdentification().getProteinMatchParameter(proteinKey, new PSParameter());
//                    } catch (Exception e) {
//                        peptideShakerGUI.catchException(e);
//                    }
//                    if (!psParameter.isStarred()) {
//                        peptideShakerGUI.getStarHider().starProtein(proteinKey);
//                    } else {
//                        peptideShakerGUI.getStarHider().unStarProtein(proteinKey);
//                    }
//                }
//
//                // open protein link in web browser
//                if (column == proteinTable.getColumn("Accession").getModelIndex() && evt != null && evt.getButton() == MouseEvent.BUTTON1
//                        && ((String) proteinTable.getValueAt(row, column)).lastIndexOf("<html>") != -1) {
//
//                    String link = (String) proteinTable.getValueAt(row, column);
//                    link = link.substring(link.indexOf("\"") + 1);
//                    link = link.substring(0, link.indexOf("\""));
//
//                    this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
//                    BareBonesBrowserLaunch.openURL(link);
//                    this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//                }
//
//                // open the protein inference dialog
//                if (column == proteinTable.getColumn("PI").getModelIndex() && evt != null && evt.getButton() == MouseEvent.BUTTON1) {
//                    String proteinKey = proteinKeys.get(proteinIndex);
//                    new ProteinInferenceDialog(peptideShakerGUI, proteinKey, peptideShakerGUI.getIdentification());
//                }
//            }
//        } else if (evt.getButton() == MouseEvent.BUTTON3) {
//            if (proteinTable.columnAtPoint(evt.getPoint()) == proteinTable.getColumn("  ").getModelIndex()) {
//                selectJPopupMenu.show(proteinTable, evt.getX(), evt.getY());
//            }
//        }
    }//GEN-LAST:event_proteinTableMouseReleased

    private void proteinTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proteinTableMouseMoved
        int row = proteinTable.rowAtPoint(evt.getPoint());
        int column = proteinTable.columnAtPoint(evt.getPoint());

        proteinTable.setToolTipText(null);

        if (row != -1 && column != -1 && column == proteinTable.getColumn("Accession").getModelIndex() && proteinTable.getValueAt(row, column) != null) {

            String tempValue = (String) proteinTable.getValueAt(row, column);

            if (tempValue.lastIndexOf("<html>") != -1) {
                this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            } else {
                this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        } else if (column == proteinTable.getColumn("PI").getModelIndex() && proteinTable.getValueAt(row, column) != null) {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        } else if (column == proteinTable.getColumn("Description").getModelIndex() && proteinTable.getValueAt(row, column) != null) {
            if (GuiUtilities.getPreferredWidthOfCell(proteinTable, row, column) > proteinTable.getColumn("Description").getWidth()) {
                proteinTable.setToolTipText("" + proteinTable.getValueAt(row, column));
            }
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_proteinTableMouseMoved

    private void proteinTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_proteinTableKeyReleased
//        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN
//                || evt.getKeyCode() == KeyEvent.VK_PAGE_UP || evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
//            peptideShakerGUI.resetSelectedItems();
//            proteinTableMouseReleased(null);
//        }
    }//GEN-LAST:event_proteinTableKeyReleased

    private void proteinsHelpJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proteinsHelpJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_proteinsHelpJButtonMouseEntered

    private void proteinsHelpJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proteinsHelpJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_proteinsHelpJButtonMouseExited

    private void proteinsHelpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proteinsHelpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(reporterGUI, getClass().getResource("/helpFiles/OverviewTab.html"), null,  // @TODO: write help
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/help.GIF")), 
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")),
                "Proteins Help");

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_proteinsHelpJButtonActionPerformed

    private void exportProteinsJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportProteinsJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_exportProteinsJButtonMouseEntered

    private void exportProteinsJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportProteinsJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_exportProteinsJButtonMouseExited

    private void exportProteinsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportProteinsJButtonActionPerformed
        //copyTableContentToClipboardOrFile(TableIndex.PROTEIN_TABLE); // @TODO: reimplement me!
    }//GEN-LAST:event_exportProteinsJButtonActionPerformed

    private void hideProteinsJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hideProteinsJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_hideProteinsJButtonMouseEntered

    private void hideProteinsJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hideProteinsJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_hideProteinsJButtonMouseExited

    private void hideProteinsJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideProteinsJButtonActionPerformed

        // @TODO: reimplement me!
        
//        displayProteins = false;
//        peptideShakerGUI.setDisplayOptions(displayProteins, displayPeptidesAndPSMs, displayCoverage, displaySpectrum);
    }//GEN-LAST:event_hideProteinsJButtonActionPerformed

    private void ratioPLotHelpJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratioPLotHelpJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratioPLotHelpJButtonMouseEntered

    private void ratioPLotHelpJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratioPLotHelpJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratioPLotHelpJButtonMouseExited

    private void ratioPLotHelpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ratioPLotHelpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(reporterGUI, getClass().getResource("/helpFiles/OverviewTab.html"), null,  // @TODO: write help
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/help.GIF")), 
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/reporter.gif")),
                "Plot Help");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratioPLotHelpJButtonActionPerformed

    private void exportRatioPlotContextJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportRatioPlotContextJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_exportRatioPlotContextJButtonMouseEntered

    private void exportRatioPlotContextJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exportRatioPlotContextJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_exportRatioPlotContextJButtonMouseExited

    private void exportRatioPlotContextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportRatioPlotContextJButtonActionPerformed
//        try {
//            if (proteinTable.getSelectedRow() != -1) {
//
//                String proteinKey = proteinKeys.get(proteinTable.convertRowIndexToModel(proteinTable.getSelectedRow()));
//                Protein protein = sequenceFactory.getProtein(proteinKey);
//
//                String clipboardString = protein.getSequence();
//                StringSelection stringSelection = new StringSelection(clipboardString);
//                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//                clipboard.setContents(stringSelection, peptideShakerGUI);
//
//                JOptionPane.showMessageDialog(peptideShakerGUI, "Protein sequence copied to clipboard.", "Copied to Clipboard", JOptionPane.INFORMATION_MESSAGE);
//            }
//        } catch (Exception e) {
//            peptideShakerGUI.catchException(e);
//            e.printStackTrace();
//        }
    }//GEN-LAST:event_exportRatioPlotContextJButtonActionPerformed

    private void hideRatioPlotJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hideRatioPlotJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_hideRatioPlotJButtonMouseEntered

    private void hideRatioPlotJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hideRatioPlotJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_hideRatioPlotJButtonMouseExited

    private void hideRatioPlotJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideRatioPlotJButtonActionPerformed
//        displayCoverage = false;
//        peptideShakerGUI.setDisplayOptions(displayProteins, displayPeptidesAndPSMs, displayCoverage, displaySpectrum);
    }//GEN-LAST:event_hideRatioPlotJButtonActionPerformed

    private void ratioPlotOptionsJButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratioPlotOptionsJButtonMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ratioPlotOptionsJButtonMouseEntered

    private void ratioPlotOptionsJButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratioPlotOptionsJButtonMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ratioPlotOptionsJButtonMouseExited

    private void ratioPlotOptionsJButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ratioPlotOptionsJButtonMouseReleased
//        sequenceCoverageJPopupMenu.show(sequenceCoverageOptionsJButton, evt.getX(), evt.getY());
    }//GEN-LAST:event_ratioPlotOptionsJButtonMouseReleased

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        
        // resize the background panel
        backgroundLayeredPane.getComponent(0).setBounds(0, 0, backgroundLayeredPane.getWidth(), backgroundLayeredPane.getHeight());
        backgroundLayeredPane.revalidate();
        backgroundLayeredPane.repaint();

        
        // resize the layered panels
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                // move the icons
                proteinsLayeredPane.getComponent(0).setBounds(
                        proteinsLayeredPane.getWidth() - proteinsLayeredPane.getComponent(0).getWidth() - 10,
                        -3,
                        proteinsLayeredPane.getComponent(0).getWidth(),
                        proteinsLayeredPane.getComponent(0).getHeight());

                proteinsLayeredPane.getComponent(1).setBounds(
                        proteinsLayeredPane.getWidth() - proteinsLayeredPane.getComponent(1).getWidth() - 20,
                        -3,
                        proteinsLayeredPane.getComponent(1).getWidth(),
                        proteinsLayeredPane.getComponent(1).getHeight());

                proteinsLayeredPane.getComponent(2).setBounds(
                        proteinsLayeredPane.getWidth() - proteinsLayeredPane.getComponent(2).getWidth() - 32,
                        -5,
                        proteinsLayeredPane.getComponent(2).getWidth(),
                        proteinsLayeredPane.getComponent(2).getHeight());

                proteinsLayeredPane.getComponent(3).setBounds(
                        proteinsLayeredPane.getWidth() - proteinsLayeredPane.getComponent(3).getWidth() - 5,
                        -3,
                        proteinsLayeredPane.getComponent(3).getWidth(),
                        proteinsLayeredPane.getComponent(3).getHeight());

                // resize the plot area
                proteinsLayeredPane.getComponent(4).setBounds(0, 0, proteinsLayeredPane.getWidth(), proteinsLayeredPane.getHeight());
                proteinsLayeredPane.revalidate();
                proteinsLayeredPane.repaint();




                // move the icons
                ratioPlotsLayeredPane.getComponent(0).setBounds(
                        ratioPlotsLayeredPane.getWidth() - ratioPlotsLayeredPane.getComponent(0).getWidth() - 10,
                        -3,
                        ratioPlotsLayeredPane.getComponent(0).getWidth(),
                        ratioPlotsLayeredPane.getComponent(0).getHeight());

                ratioPlotsLayeredPane.getComponent(1).setBounds(
                        ratioPlotsLayeredPane.getWidth() - ratioPlotsLayeredPane.getComponent(1).getWidth() - 20,
                        -3,
                        ratioPlotsLayeredPane.getComponent(1).getWidth(),
                        ratioPlotsLayeredPane.getComponent(1).getHeight());

                ratioPlotsLayeredPane.getComponent(2).setBounds(
                        ratioPlotsLayeredPane.getWidth() - ratioPlotsLayeredPane.getComponent(2).getWidth() - 32,
                        -5,
                        ratioPlotsLayeredPane.getComponent(2).getWidth(),
                        ratioPlotsLayeredPane.getComponent(2).getHeight());

                ratioPlotsLayeredPane.getComponent(3).setBounds(
                        ratioPlotsLayeredPane.getWidth() - ratioPlotsLayeredPane.getComponent(3).getWidth() - 44,
                        0,
                        ratioPlotsLayeredPane.getComponent(3).getWidth(),
                        ratioPlotsLayeredPane.getComponent(3).getHeight());

                ratioPlotsLayeredPane.getComponent(4).setBounds(
                        ratioPlotsLayeredPane.getWidth() - ratioPlotsLayeredPane.getComponent(4).getWidth() - 5,
                        -3,
                        ratioPlotsLayeredPane.getComponent(4).getWidth(),
                        ratioPlotsLayeredPane.getComponent(4).getHeight());

                // resize the plot area
                ratioPlotsLayeredPane.getComponent(5).setBounds(0, 0, ratioPlotsLayeredPane.getWidth(), ratioPlotsLayeredPane.getHeight());
                ratioPlotsLayeredPane.revalidate();
                ratioPlotsLayeredPane.repaint();
            }
        });
        
    }//GEN-LAST:event_formComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane backgroundLayeredPane;
    private javax.swing.JPanel contextMenuProteinsBackgroundPanel;
    private javax.swing.JPanel contextMenuRatioPlotBackgroundPanel;
    private javax.swing.JButton exportProteinsJButton;
    private javax.swing.JButton exportRatioPlotContextJButton;
    private javax.swing.JButton hideProteinsJButton;
    private javax.swing.JButton hideRatioPlotJButton;
    private javax.swing.JPanel overviewJPanel;
    private javax.swing.JSplitPane overviewJSplitPane;
    private javax.swing.JScrollPane proteinScrollPane;
    private javax.swing.JTable proteinTable;
    private javax.swing.JButton proteinsHelpJButton;
    private javax.swing.JPanel proteinsJPanel;
    private javax.swing.JLayeredPane proteinsLayeredPane;
    private javax.swing.JPanel proteinsLayeredPanel;
    private javax.swing.JButton ratioPLotHelpJButton;
    private javax.swing.JButton ratioPlotOptionsJButton;
    private javax.swing.JPanel ratioPlotsInnerPanel;
    private javax.swing.JPanel ratioPlotsJPanel;
    private javax.swing.JLayeredPane ratioPlotsLayeredPane;
    private javax.swing.JPanel ratioPlotsTitledPanel;
    // End of variables declaration//GEN-END:variables

    
}
