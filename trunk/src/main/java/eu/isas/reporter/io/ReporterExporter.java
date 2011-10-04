package eu.isas.reporter.io;

import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.Protein;
import com.compomics.util.experiment.biology.ions.ReporterIon;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.advocates.SearchEngine;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.quantification.reporterion.ReporterIonQuantification;
import com.compomics.util.experiment.quantification.reporterion.quantification.PeptideQuantification;
import com.compomics.util.experiment.quantification.reporterion.quantification.ProteinQuantification;
import com.compomics.util.experiment.quantification.reporterion.quantification.PsmQuantification;
import com.compomics.util.gui.dialogs.ProgressDialogX;
import eu.isas.reporter.myparameters.IgnoredRatios;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 * This class will export the quantification results as csv files
 *
 * @author Marc Vaudel
 */
public class ReporterExporter {

    /**
     * suffix for psm file
     */
    private final static String spectraSuffix = "_Reporter_spectra.txt";
    /**
     * suffix for peptide file
     */
    private final static String peptidesSuffix = "_Reporter_peptides.txt";
    /**
     * suffix for protein file
     */
    private final static String proteinsSuffix = "_Reporter_proteins.txt";
    /**
     * separator used to create the csv files
     */
    private String separator;
    /**
     * The experiment conducted
     */
    private MsExperiment experiment;
    /**
     * the reporter ions used in the method method
     */
    private ArrayList<ReporterIon> ions;
    /**
     * The reference reporter ion
     */
    private int reference;

    /**
     * Constructor
     *
     * @param experiment    The experiment conducted
     * @param separator     The separator to use
     */
    public ReporterExporter(MsExperiment experiment, String separator) {
        this.separator = separator;
        this.experiment = experiment;
    }

    /**
     * Exports the quantification results into csv files
     *
     * @param quantification    The quantification achieved
     * @param identification    The corresponding identification
     * @param location          The folder where to save the files
     */
    public void exportResults(ReporterIonQuantification quantification, Identification identification, String location, ProgressDialogX progressDialog) throws Exception {


        ions = quantification.getReporterMethod().getReporterIons();
        reference = quantification.getReferenceLabel();

        File spectraFile = new File(location, experiment.getReference() + spectraSuffix);
        File peptidesFile = new File(location, experiment.getReference() + peptidesSuffix);
        File proteinsFile = new File(location, experiment.getReference() + proteinsSuffix);

        if (spectraFile.exists()) {
            int outcome = JOptionPane.showConfirmDialog(null, new String[]{"Existing output file found", "Overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (outcome != JOptionPane.YES_OPTION) {
                return;
            }
        }
        Writer spectraOutput = new BufferedWriter(new FileWriter(spectraFile));
        String content = "Protein" + separator + "Sequence" + separator + "Variable Modifications" + separator + "Spectrum File" + separator + "Identification Spectrum" + separator + "Quantification spectrum" + separator
                + "Identification File" + separator + "Mass Error" + separator + "Mascot E-Value" + separator
                + "OMSSA E-Value" + separator + "X!Tandem E-Value" + separator + getRatiosLabels(quantification) + separator + getIntensitiesLabels() + "\n";
        spectraOutput.write(content);
        SpectrumMatch spectrumMatch;
        Peptide peptide;
        boolean first;
        HashMap<String, ArrayList<Integer>> modificationMap;
        String identificationSpectrum, quantificationSpectrum, spectrumFile, idFile;
        PsmQuantification psmQuantification;
        for (String psmKey : quantification.getPsmIDentificationToQuantification().keySet()) {
            spectrumMatch = identification.getSpectrumMatch(psmKey);
            spectrumFile = Spectrum.getSpectrumFile(psmKey);
            identificationSpectrum = Spectrum.getSpectrumTitle(psmKey);
            peptide = spectrumMatch.getBestAssumption().getPeptide();
            idFile = "";
            first = true;
            for (int se : spectrumMatch.getAdvocates()) {
                if (!first) {
                    idFile += ", ";
                } else {
                    first = false;
                }
                if (spectrumMatch.getFirstHit(se).getPeptide().isSameAs(peptide)) {
                    idFile += spectrumMatch.getFirstHit(se).getFile();
                }
            }
            for (String spectrumKey : quantification.getPsmIDentificationToQuantification().get(psmKey)) {
                quantificationSpectrum = Spectrum.getSpectrumTitle(spectrumKey);
                content = "";
                first = true;
                for (String protein : peptide.getParentProteins()) {
                    if (!first) {
                        content += ", ";
                    } else {
                        first = false;
                    }
                    content += protein;
                }
                content += separator;
                content += peptide.getSequence() + separator;
                modificationMap = new HashMap<String, ArrayList<Integer>>();
                for (ModificationMatch modMatch : peptide.getModificationMatches()) {
                    if (!modificationMap.containsKey(modMatch.getTheoreticPtm())) {
                        modificationMap.put(modMatch.getTheoreticPtm(), new ArrayList<Integer>());
                    }
                    modificationMap.get(modMatch.getTheoreticPtm()).add(modMatch.getModificationSite());
                }
                first = true;
                for (String mod : modificationMap.keySet()) {
                    if (!first) {
                        content += ", ";
                    } else {
                        first = false;
                    }
                    content += mod + " (";
                    boolean first2 = true;
                    for (int position : modificationMap.get(mod)) {
                        if (!first2) {
                            content += ", ";
                        } else {
                            first2 = false;
                        }
                        content += position;
                    }
                    content += ")";
                }
                content += separator;
                content += spectrumFile + separator;
                content += identificationSpectrum + separator;
                content += quantificationSpectrum + separator;
                content += idFile + separator;
                content += spectrumMatch.getBestAssumption().getDeltaMass() + separator;

                if (spectrumMatch.getFirstHit(Advocate.MASCOT) != null
                        && spectrumMatch.getFirstHit(Advocate.MASCOT).getPeptide().isSameAs(peptide)) {
                    content += spectrumMatch.getFirstHit(Advocate.MASCOT).getEValue();
                }
                content += separator;
                if (spectrumMatch.getFirstHit(Advocate.OMSSA) != null
                        && spectrumMatch.getFirstHit(Advocate.OMSSA).getPeptide().isSameAs(peptide)) {
                    content += spectrumMatch.getFirstHit(Advocate.OMSSA).getEValue();
                }
                content += separator;
                if (spectrumMatch.getFirstHit(Advocate.XTANDEM) != null
                        && spectrumMatch.getFirstHit(Advocate.XTANDEM).getPeptide().isSameAs(peptide)) {
                    content += spectrumMatch.getFirstHit(Advocate.XTANDEM).getEValue();
                }
                content += separator;

                psmQuantification = quantification.getSpectrumMatch(spectrumKey);
                content += getRatios(psmQuantification);
                content += getIntensities(psmQuantification);
                content += "\n";
                spectraOutput.write(content);
            }
        }
        spectraOutput.close();

        Writer peptidesOutput = new BufferedWriter(new FileWriter(peptidesFile));
        content = "Protein(s)" + separator + "Sequence" + separator + "Variable Modification(s)" + separator + "number of Spectra" + separator + getRatiosLabels(quantification) + "\n";
        peptidesOutput.write(content);
        PeptideMatch peptideMatch;
        PeptideQuantification peptideQuantification;
        for (String peptideKey : quantification.getPeptideQuantification()) {
            peptideMatch = identification.getPeptideMatch(peptideKey);
            peptide = peptideMatch.getTheoreticPeptide();
            content = "";
            first = true;
            for (String protein : peptide.getParentProteins()) {
                if (!first) {
                    content += ", ";
                } else {
                    first = false;
                }
                content += protein;
            }
            content += separator;
            content += peptide.getSequence() + separator;

            modificationMap = new HashMap<String, ArrayList<Integer>>();
            for (ModificationMatch modMatch : peptide.getModificationMatches()) {
                if (!modificationMap.containsKey(modMatch.getTheoreticPtm())) {
                    modificationMap.put(modMatch.getTheoreticPtm(), new ArrayList<Integer>());
                }
                modificationMap.get(modMatch.getTheoreticPtm()).add(modMatch.getModificationSite());
            }
            first = true;
            for (String mod : modificationMap.keySet()) {
                if (!first) {
                    content += ", ";
                } else {
                    first = false;
                }
                content += mod + " (";
                boolean first2 = true;
                for (int position : modificationMap.get(mod)) {
                    if (!first2) {
                        content += ", ";
                    } else {
                        first2 = false;
                    }
                    content += position;
                }
                content += ")";
            }
            content += separator;
            peptideQuantification = quantification.getPeptideMatch(peptideKey);
            content += peptideQuantification.getPsmQuantification().size() + separator;
            content += getRatios(peptideQuantification) + separator;
            content += "\n";
            peptidesOutput.write(content);
        }
        peptidesOutput.close();

        Writer proteinsOutput = new BufferedWriter(new FileWriter(proteinsFile));
        content = "possible protein(s)" + separator + "Number of identified Peptides" + separator + "Number of quantified peptides" + separator + getRatiosLabels(quantification) + "\n";
        proteinsOutput.write(content);
        ProteinMatch proteinMatch;
        ProteinQuantification proteinQuantification;
        for (String proteinKey : quantification.getProteinQuantification()) {
            proteinMatch = identification.getProteinMatch(proteinKey);
            content = "";
            first = true;
            for (String protein : ProteinMatch.getAccessions(proteinKey)) {
                if (!first) {
                    content += ", ";
                } else {
                    first = false;
                }
                content += protein;
            }
            content += separator;
            content += proteinMatch.getPeptideMatches().size() + separator;
            
            proteinQuantification = quantification.getProteinMatch(proteinKey);
            content += proteinQuantification.getPeptideQuantification().size() + separator;
            content += getRatios(proteinQuantification);
            content += "\n";
            
            proteinsOutput.write(content);
        }
        proteinsOutput.close();
    }

    /**
     * Returns the labels of the computed ratios
     *
     * @param quantification the quantification achieved
     * @return String of the different labels
     */
    private String getRatiosLabels(ReporterIonQuantification quantification) {
        String result = "";
        boolean first = true;
        for (ReporterIon ion : ions) {
            if (!first) {
                result += separator;
            } else {
                first = false;
            }
            result += quantification.getSample(ion.getIndex()).getReference() + "/" + quantification.getSample(reference).getReference();
        }
        return result;
    }

    /**
     * returns the ratios of the selected protein quantification
     *
     * @param proteinQuantification the selected protein quantification
     * @return  the corresponding ratios
     */
    private String getRatios(ProteinQuantification proteinQuantification) {
        String result = "";
        boolean first = true;
        for (ReporterIon ion : ions) {
            if (!first) {
                result += separator;
            } else {
                first = false;
            }
            try {
                result += proteinQuantification.getRatios().get(ion.getIndex()).getRatio();
            } catch (Exception e) {
                result += "NA";
            }
        }
        return result;
    }

    /**
     * returns the ratios of the selected peptide quantification
     *
     * @param peptideQuantification the selected peptide quantification
     * @return  the corresponding ratios
     */
    private String getRatios(PeptideQuantification peptideQuantification) {
        String result = "";
        boolean first = true;
        for (ReporterIon ion : ions) {
            if (!first) {
                result += separator;
            } else {
                first = false;
            }
            try {
                result += peptideQuantification.getRatios().get(ion.getIndex()).getRatio();
            } catch (Exception e) {
                result += "NA";
            }
        }
        return result;
    }

    /**
     * returns the ratios of the selected spectrum quantification
     *
     * @param spectrumQuantification the selected spectrum quantification
     * @return  the corresponding ratios
     */
    private String getRatios(PsmQuantification spectrumQuantification) {
        String result = "";
        for (ReporterIon ion : ions) {
            result += spectrumQuantification.getRatios().get(ion.getIndex()).getRatio() + separator;
        }
        return result;
    }

    /**
     * returns the intensities of the selected spectrum quantification
     *
     * @param spectrumQuantification the selected spectrum quantification
     * @return  the corresponding intensities
     */
    private String getIntensities(PsmQuantification spectrumQuantification) {
        String result = "";
        boolean first = true;
        for (ReporterIon ion : ions) {
            if (!first) {
                result += separator;
            } else {
                first = false;
            }
            IonMatch match = spectrumQuantification.getReporterMatches().get(ion.getIndex());
            if (match != null) {
                result += match.peak.intensity;
            } else {
                result += 0;
            }
        }
        return result;
    }

    /**
     * Returns the labels of the different ions
     *
     * @return the labels of the different ions
     */
    private String getIntensitiesLabels() {
        String result = "";
        boolean first = true;
        for (ReporterIon ion : ions) {
            if (!first) {
                result += separator;
            } else {
                first = false;
            }
            result += ion.getIndex();
        }
        return result;
    }
}