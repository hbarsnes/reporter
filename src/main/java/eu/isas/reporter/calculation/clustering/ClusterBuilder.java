package eu.isas.reporter.calculation.clustering;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.matches_iterators.PeptideMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.ProteinMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.PsmIterator;
import com.compomics.util.experiment.personalization.UrParameter;
import com.compomics.util.experiment.quantification.reporterion.ReporterIonQuantification;
import com.compomics.util.math.BasicMathFunctions;
import com.compomics.util.math.clustering.KMeansClustering;
import com.compomics.util.preferences.IdentificationParameters;
import com.compomics.util.waiting.WaitingHandler;
import eu.isas.peptideshaker.parameters.PSParameter;
import eu.isas.peptideshaker.utils.Metrics;
import eu.isas.reporter.calculation.QuantificationFeaturesGenerator;
import eu.isas.reporter.calculation.clustering.keys.PeptideClusterClassKey;
import eu.isas.reporter.calculation.clustering.keys.ProteinClusterClassKey;
import eu.isas.reporter.calculation.clustering.keys.PsmClusterClassKey;
import eu.isas.reporter.preferences.DisplayPreferences;
import eu.isas.reporter.quantificationdetails.PeptideQuantificationDetails;
import eu.isas.reporter.quantificationdetails.ProteinQuantificationDetails;
import eu.isas.reporter.quantificationdetails.PsmQuantificationDetails;
import eu.isas.reporter.settings.ClusteringSettings;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * Methods for building clusters based on a reporter project.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class ClusterBuilder {

    /**
     * The filtered protein keys indexed by cluster class key.
     */
    private HashMap<String, ArrayList<String>> filteredProteinKeys;
    /**
     * The clusters corresponding to every protein.
     */
    private HashMap<String, ArrayList<String>> proteinClusters;
    /**
     * The filtered protein keys indexed by the index used for clustering.
     */
    private HashMap<Integer, String> clusterProteinKeys;
    /**
     * The filtered peptide keys indexed by cluster class key.
     */
    private HashMap<String, ArrayList<String>> filteredPeptideKeys;
    /**
     * The clusters corresponding to every peptide.
     */
    private HashMap<String, ArrayList<String>> peptideClusters;
    /**
     * The filtered peptide keys indexed by the index used for clustering.
     */
    private HashMap<Integer, String> clusterPeptideKeys;
    /**
     * The filtered PSM keys indexed by cluster class key.
     */
    private HashMap<String, ArrayList<String>> filteredPsmKeys;
    /**
     * The clusters corresponding to every PSM.
     */
    private HashMap<String, ArrayList<String>> psmClusters;
    /**
     * The filtered PSM keys indexed by the index used for clustering.
     */
    private HashMap<Integer, String> clusterPsmKeys;
    /**
     * The matches keys of the ratios used for clustering.
     */
    private ArrayList<String> clusterKeys;
    /**
     * The ratios used for clustering.
     */
    private double[][] ratios;

    /**
     * Constructor.
     */
    public ClusterBuilder() {

    }

    /**
     * Clusters the profiles according to the given parameters.
     *
     * @param identification the identification
     * @param identificationParameters the identification parameters
     * @param metrics the PeptideShaker metrics
     * @param reporterIonQuantification the reporter ion quantification
     * @param quantificationFeaturesGenerator the quantification features
     * generator
     * @param displayPreferences the display preferences
     * @param loadData if true, the data is (re-)loaded
     * @param waitingHandler a waiting handler
     *
     * @return the k-means clustering of every class
     *
     * @throws SQLException if an SQLException occurs
     * @throws IOException if an IOException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws InterruptedException if an InterruptedException occurs
     * @throws MzMLUnmarshallerException if an MzMLUnmarshallerException occurs
     */
    public KMeansClustering clusterProfiles(Identification identification, IdentificationParameters identificationParameters, Metrics metrics, ReporterIonQuantification reporterIonQuantification, QuantificationFeaturesGenerator quantificationFeaturesGenerator, DisplayPreferences displayPreferences, boolean loadData, WaitingHandler waitingHandler) throws SQLException, IOException, ClassNotFoundException, InterruptedException, MzMLUnmarshallerException {

        waitingHandler.setSecondaryProgressCounterIndeterminate(true);

        ClusteringSettings clusteringSettings = displayPreferences.getClusteringSettings();

        // Load data if needed
        if (loadData) {
            waitingHandler.setWaitingText("Loading data (1/2). Please Wait...");
            loadData(identification, identificationParameters, metrics, clusteringSettings, reporterIonQuantification, quantificationFeaturesGenerator, waitingHandler);
            waitingHandler.setSecondaryProgressCounterIndeterminate(true);
            waitingHandler.setWaitingText("Clustering Data (2/2). Please Wait...");
        } else {
            waitingHandler.setWaitingText("Clustering Data. Please Wait...");
        }

        // Perform the clustering
        String[] keysArray = clusterKeys.toArray(new String[clusterKeys.size()]);
        KMeansClustering kMeansClutering = new KMeansClustering(ratios, keysArray, displayPreferences.getClusteringSettings().getKMeansClusteringSettings().getnClusters());
        kMeansClutering.kMeanCluster(waitingHandler);

        return kMeansClutering;
    }

    /**
     * Filters the proteins and indexes them according to the clustering
     * settings and stores the result in the attribute maps.
     *
     * @param identification the identification
     * @param identificationParameters the identification parameters
     * @param metrics the PeptideShaker metrics
     * @param clusteringSettings the clustering settings
     * @param reporterIonQuantification the reporter ion quantification
     * @param quantificationFeaturesGenerator the quantification features
     * generator
     * @param waitingHandler the waiting handler
     *
     * @throws SQLException if an exception occurs while interacting with the
     * database
     * @throws IOException if an exception occurs while reading or writing a
     * file
     * @throws ClassNotFoundException if a exception occurs while deserializing
     * an object
     * @throws InterruptedException if an threading exception occurs
     * @throws MzMLUnmarshallerException if an exception occurs while reading an
     * mzML file
     */
    public void loadData(Identification identification, IdentificationParameters identificationParameters, Metrics metrics, ClusteringSettings clusteringSettings, ReporterIonQuantification reporterIonQuantification, QuantificationFeaturesGenerator quantificationFeaturesGenerator, WaitingHandler waitingHandler) throws SQLException, IOException, ClassNotFoundException, InterruptedException, MzMLUnmarshallerException {

        HashSet<String> proteinKeys = identification.getProteinIdentification();
        HashSet<String> peptideKeys = identification.getPeptideIdentification();

        int nProteinClusters = clusteringSettings.getSelectedProteinClasses().size();
        int nPeptideClusters = clusteringSettings.getSelectedPeptideClasses().size();
        int nPsmClusters = clusteringSettings.getSelectedPsmClasses().size();
        int progressTotal = 1;
        if (nProteinClusters > 0) {
            progressTotal += proteinKeys.size();
        }
        if (nPeptideClusters > 0) {
            progressTotal += peptideKeys.size();
        }
        if (nPsmClusters > 0) {
            progressTotal += identification.getSpectrumIdentificationSize();
        }

        waitingHandler.resetPrimaryProgressCounter();
        waitingHandler.setPrimaryProgressCounterIndeterminate(false);
        waitingHandler.setMaxPrimaryProgressCounter(progressTotal);
        waitingHandler.increasePrimaryProgressCounter();

        PSParameter psParameter = new PSParameter();
        ArrayList<UrParameter> parameters = new ArrayList<UrParameter>(1);
        parameters.add(psParameter);

        ArrayList<String> sampleIndexes = new ArrayList<String>(reporterIonQuantification.getSampleIndexes());
        Collections.sort(sampleIndexes);

        Integer clusteringIndex = 0;
        clusterKeys = new ArrayList<String>(metrics.getnValidatedProteins());
        ArrayList<double[]> ratiosList = new ArrayList<double[]>(metrics.getnValidatedProteins());
        
        proteinClusters = new HashMap<String, ArrayList<String>>(nProteinClusters);
        clusterProteinKeys = new HashMap<Integer, String>(metrics.getnValidatedProteins());
        filteredProteinKeys = new HashMap<String, ArrayList<String>>(metrics.getnValidatedProteins());

        if (nProteinClusters > 0) {

            ProteinMatchesIterator proteinMatchesIterator;
            if (quantificationFeaturesGenerator.getQuantificationFeaturesCache().memoryCheck()) {
                proteinMatchesIterator = identification.getProteinMatchesIterator(metrics.getProteinKeys(), parameters, false, null, false, null, waitingHandler);
            } else {
                proteinMatchesIterator = identification.getProteinMatchesIterator(metrics.getProteinKeys(), parameters, true, parameters, true, parameters, waitingHandler);
            }

            while (proteinMatchesIterator.hasNext() && !waitingHandler.isRunCanceled()) {

                ProteinMatch proteinMatch = proteinMatchesIterator.next();
                String proteinKey = proteinMatch.getKey();
                psParameter = (PSParameter) identification.getProteinMatchParameter(proteinKey, psParameter);

                if (psParameter.getMatchValidationLevel().isValidated()) {
                    boolean found = false;
                    for (String keyName : clusteringSettings.getSelectedProteinClasses()) {
                        boolean inCluster = true;
                        ProteinClusterClassKey proteinClusterClassKey = clusteringSettings.getProteinClassKey(keyName);
                        if (proteinClusterClassKey.isStarred() && !psParameter.isStarred()) {
                            inCluster = false;
                        }
                        if (inCluster) {
                            ArrayList<String> clusterKeys = filteredProteinKeys.get(keyName);
                            if (clusterKeys == null) {
                                clusterKeys = new ArrayList<String>();
                                filteredProteinKeys.put(keyName, clusterKeys);
                            }
                            clusterKeys.add(proteinKey);
                            ArrayList<String> clusters = proteinClusters.get(proteinKey);
                            if (clusters == null) {
                                clusters = new ArrayList<String>(nProteinClusters);
                                proteinClusters.put(proteinKey, clusters);
                            }
                            clusters.add(keyName);
                            found = true;
                        }
                    }
                    if (found) {
                        ProteinQuantificationDetails quantificationDetails = quantificationFeaturesGenerator.getProteinMatchQuantificationDetails(proteinKey, waitingHandler);
                        double[] proteinRatios = new double[sampleIndexes.size()];

                        for (int sampleIndex = 0; sampleIndex < sampleIndexes.size(); sampleIndex++) {
                            Double ratio = quantificationDetails.getRatio(sampleIndexes.get(sampleIndex), reporterIonQuantification.getNormalizationFactors());
                            if (ratio != null) {
                                if (ratio != 0) {
                                    double logRatio = BasicMathFunctions.log(ratio, 2);
                                    proteinRatios[sampleIndex] = logRatio;
                                }
                            }
                        }

                        clusterKeys.add(proteinKey);
                        clusterProteinKeys.put(clusteringIndex, proteinKey);
                        ratiosList.add(proteinRatios);
                        clusteringIndex++;
                    }
                }
                waitingHandler.increasePrimaryProgressCounter();
            }
        }

        filteredPeptideKeys = new HashMap<String, ArrayList<String>>(metrics.getnValidatedProteins());
        clusterPeptideKeys = new HashMap<Integer, String>(metrics.getnValidatedProteins());
        peptideClusters = new HashMap<String, ArrayList<String>>(nPeptideClusters);

        if (nPeptideClusters > 0) {

            PeptideMatchesIterator peptideMatchesIterator; //@TODO: sort the peptides in some way?
            if (quantificationFeaturesGenerator.getQuantificationFeaturesCache().memoryCheck()) {
                peptideMatchesIterator = identification.getPeptideMatchesIterator(parameters, false, null, waitingHandler);
            } else {
                peptideMatchesIterator = identification.getPeptideMatchesIterator(parameters, true, parameters, waitingHandler);
            }

            while (peptideMatchesIterator.hasNext() && !waitingHandler.isRunCanceled()) {

                PeptideMatch peptideMatch = peptideMatchesIterator.next();
                Peptide peptide = peptideMatch.getTheoreticPeptide();
                String peptideKey = peptideMatch.getKey();
                psParameter = (PSParameter) identification.getPeptideMatchParameter(peptideKey, psParameter);

                if (psParameter.getMatchValidationLevel().isValidated()) {
                    boolean found = false;
                    for (String keyName : clusteringSettings.getSelectedPeptideClasses()) {
                        boolean inCluster = true;
                        PeptideClusterClassKey peptideClusterClassKey = clusteringSettings.getPeptideClassKey(keyName);
                        if (peptideClusterClassKey.isStarred() && !psParameter.isStarred()) {
                            inCluster = false;
                        }
                        if (inCluster && peptideClusterClassKey.isNotModified()) {
                            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                                if (modificationMatch.isVariable()) {
                                    inCluster = false;
                                    break;
                                }
                            }
                        }
                        if (inCluster && peptideClusterClassKey.getPossiblePtms() != null) {
                            boolean possiblePtms = false;
                            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                                if (modificationMatch.isVariable()) {
                                    if (peptideClusterClassKey.getPossiblePtmsAsSet().contains(modificationMatch.getTheoreticPtm())) {
                                        possiblePtms = true;
                                        break;
                                    }
                                }
                            }
                            if (!possiblePtms) {
                                inCluster = false;
                            }
                        }
                        if (inCluster && peptideClusterClassKey.getForbiddenPtms() != null) {
                            boolean forbiddenPtms = false;
                            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                                if (modificationMatch.isVariable()) {
                                    if (peptideClusterClassKey.getForbiddenPtmsAsSet().contains(modificationMatch.getTheoreticPtm())) {
                                        forbiddenPtms = true;
                                        break;
                                    }
                                }
                            }
                            if (forbiddenPtms) {
                                inCluster = false;
                            }
                        }
                        if (inCluster && peptideClusterClassKey.isNTerm() && peptide.isNterm(identificationParameters.getSequenceMatchingPreferences()).isEmpty()) {
                            inCluster = false;
                        }
                        if (inCluster && peptideClusterClassKey.isCTerm() && peptide.isCterm(identificationParameters.getSequenceMatchingPreferences()).isEmpty()) {
                            inCluster = false;
                        }
                        if (inCluster) {
                            ArrayList<String> clusterKeys = filteredPeptideKeys.get(keyName);
                            if (clusterKeys == null) {
                                clusterKeys = new ArrayList<String>();
                                filteredPeptideKeys.put(keyName, clusterKeys);
                            }
                            clusterKeys.add(peptideKey);
                            ArrayList<String> clusters = peptideClusters.get(peptideKey);
                            if (clusters == null) {
                                clusters = new ArrayList<String>(nProteinClusters);
                                peptideClusters.put(peptideKey, clusters);
                            }
                            clusters.add(keyName);
                            found = true;
                        }
                    }
                    if (found) {
                        PeptideQuantificationDetails quantificationDetails = quantificationFeaturesGenerator.getPeptideMatchQuantificationDetails(peptideMatch, waitingHandler);
                        double[] peptideRatios = new double[sampleIndexes.size()];

                        for (int sampleIndex = 0; sampleIndex < sampleIndexes.size(); sampleIndex++) {
                            Double ratio = quantificationDetails.getRatio(sampleIndexes.get(sampleIndex), reporterIonQuantification.getNormalizationFactors());
                            if (ratio != null) {
                                if (ratio != 0) {
                                    double logRatio = BasicMathFunctions.log(ratio, 2);
                                    peptideRatios[sampleIndex] = logRatio;
                                }
                            }
                        }

                        clusterKeys.add(peptideKey);
                        clusterPeptideKeys.put(clusteringIndex, peptideKey);
                        ratiosList.add(peptideRatios);
                        clusteringIndex++;
                    }
                }
                waitingHandler.increasePrimaryProgressCounter();
            }
        }

        filteredPsmKeys = new HashMap<String, ArrayList<String>>(metrics.getnValidatedProteins());
        clusterPsmKeys = new HashMap<Integer, String>(metrics.getnValidatedProteins());
        psmClusters = new HashMap<String, ArrayList<String>>(nPsmClusters);
        
        if (nPsmClusters > 0) {

            HashSet<String> neededFiles = new HashSet<String>();
            for (String keyName : clusteringSettings.getSelectedPeptideClasses()) {
                PsmClusterClassKey psmClusterClassKey = clusteringSettings.getPsmClassKey(keyName);
                if (psmClusterClassKey.getFile() == null) {
                    neededFiles.addAll(identification.getOrderedSpectrumFileNames());
                    break;
                }
                neededFiles.add(psmClusterClassKey.getFile());
            }

            for (String spectrumFile : neededFiles) {

                PsmIterator psmMatchesIterator = identification.getPsmIterator(spectrumFile, parameters, false, waitingHandler); //@TODO: sort the PSMs in some way?

                while (psmMatchesIterator.hasNext() && !waitingHandler.isRunCanceled()) {

                    SpectrumMatch spectrumMatch = psmMatchesIterator.next();
                    String spectrumKey = spectrumMatch.getKey();
                    psParameter = (PSParameter) identification.getSpectrumMatchParameter(spectrumKey, psParameter);

                    if (psParameter.getMatchValidationLevel().isValidated()) {
                        boolean found = false;
                        for (String keyName : clusteringSettings.getSelectedPeptideClasses()) {
                            boolean inCluster = true;
                            PsmClusterClassKey psmClusterClassKey = clusteringSettings.getPsmClassKey(keyName);
                            if (psmClusterClassKey.getFile() != null && !spectrumFile.equals(psmClusterClassKey.getFile())) {
                                inCluster = false;
                            }
                            if (inCluster && psmClusterClassKey.isStarred() && !psParameter.isStarred()) {
                                inCluster = false;
                            }
                            if (inCluster) {
                                ArrayList<String> clusterKeys = filteredPsmKeys.get(keyName);
                                if (clusterKeys == null) {
                                    clusterKeys = new ArrayList<String>();
                                    filteredPsmKeys.put(keyName, clusterKeys);
                                }
                                clusterKeys.add(spectrumKey);
                                ArrayList<String> clusters = psmClusters.get(spectrumKey);
                                if (clusters == null) {
                                    clusters = new ArrayList<String>(nProteinClusters);
                                    psmClusters.put(spectrumKey, clusters);
                                }
                                clusters.add(keyName);
                                found = true;
                            }
                        }
                        if (found) {
                            PsmQuantificationDetails quantificationDetails = quantificationFeaturesGenerator.getPSMQuantificationDetails(spectrumKey);
                            double[] psmRatios = new double[sampleIndexes.size()];

                            for (int sampleIndex = 0; sampleIndex < sampleIndexes.size(); sampleIndex++) {
                                Double ratio = quantificationDetails.getRatio(sampleIndexes.get(sampleIndex), reporterIonQuantification.getNormalizationFactors());
                                if (ratio != null) {
                                    if (ratio != 0) {
                                        double logRatio = BasicMathFunctions.log(ratio, 2);
                                        psmRatios[sampleIndex] = logRatio;
                                    }
                                }
                            }

                            clusterKeys.add(spectrumKey);
                            clusterPsmKeys.put(clusteringIndex, spectrumKey);
                        ratiosList.add(psmRatios);
                            clusteringIndex++;
                        }
                    }
                    waitingHandler.increasePrimaryProgressCounter();
                }
            }
        }
        ratios = ratiosList.toArray(new double[sampleIndexes.size()][ratiosList.size()]);
    }

    /**
     * Returns the protein keys retained after filtering.
     *
     * @return the protein keys retained after filtering
     */
    public Set<String> getFilteredProteins() {
        return proteinClusters.keySet();
    }

    /**
     * Returns the peptide keys retained after filtering.
     *
     * @return the peptide keys retained after filtering
     */
    public Set<String> getFilteredPeptides() {
        return peptideClusters.keySet();
    }

    /**
     * Returns the PSM keys retained after filtering.
     *
     * @return the PSM keys retained after filtering
     */
    public Set<String> getFilteredPsms() {
        return psmClusters.keySet();
    }

}