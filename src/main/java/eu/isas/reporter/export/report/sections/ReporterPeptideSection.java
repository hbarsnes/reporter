package eu.isas.reporter.export.report.sections;

import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.features.IdentificationFeaturesGenerator;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches_iterators.PeptideMatchesIterator;
import com.compomics.util.experiment.identification.peptide_shaker.PSParameter;
import com.compomics.util.experiment.identification.utils.PeptideUtils;
import com.compomics.util.experiment.io.biology.protein.ProteinDetailsProvider;
import com.compomics.util.experiment.io.biology.protein.SequenceProvider;
import com.compomics.util.experiment.mass_spectrometry.SpectrumProvider;
import com.compomics.util.experiment.personalization.UrParameter;
import com.compomics.util.experiment.quantification.reporterion.ReporterIonQuantification;
import com.compomics.util.io.export.ExportFeature;
import com.compomics.util.io.export.ExportWriter;
import com.compomics.util.io.export.writers.ExcelWriter;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.waiting.WaitingHandler;
import eu.isas.peptideshaker.export.sections.PsPeptideSection;
import eu.isas.reporter.calculation.QuantificationFeaturesGenerator;
import com.compomics.util.io.export.features.ReporterExportFeature;
import com.compomics.util.io.export.features.peptideshaker.PsFragmentFeature;
import com.compomics.util.io.export.features.peptideshaker.PsIdentificationAlgorithmMatchesFeature;
import com.compomics.util.io.export.features.peptideshaker.PsPeptideFeature;
import com.compomics.util.io.export.features.peptideshaker.PsPsmFeature;
import eu.isas.reporter.export.report.ReporterReportStyle;
import com.compomics.util.io.export.features.reporter.ReporterPeptideFeature;
import com.compomics.util.io.export.features.reporter.ReporterPsmFeatures;
import eu.isas.reporter.settings.ReporterSettings;
import eu.isas.reporter.quantificationdetails.PeptideQuantificationDetails;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math.MathException;

/**
 * This class outputs the peptide related quantification export features.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class ReporterPeptideSection {

    /**
     * The peptide identification features to export.
     */
    private ArrayList<ExportFeature> identificationFeatures = new ArrayList<>();
    /**
     * The peptide quantification features to export.
     */
    private ArrayList<ReporterExportFeature> quantificationFeatures = new ArrayList<>();
    /**
     * The PSM subsection if needed.
     */
    private ReporterPsmSection psmSection = null;
    /**
     * Boolean indicating whether the line shall be indexed.
     */
    private boolean indexes;
    /**
     * Boolean indicating whether column headers shall be included.
     */
    private boolean header;
    /**
     * The writer used to send the output to file.
     */
    private ExportWriter writer;
    /**
     * Style for the reporter output.
     */
    private ReporterReportStyle reporterStyle;

    /**
     * Constructor.
     *
     * @param exportFeatures the features to export in this section
     * @param indexes indicates whether the line index should be written
     * @param header indicates whether the table header should be written
     * @param writer the writer which will write to the file
     */
    public ReporterPeptideSection(
            ArrayList<ExportFeature> exportFeatures,
            boolean indexes,
            boolean header,
            ExportWriter writer
    ) {

        ArrayList<ExportFeature> psmFeatures = new ArrayList<>();

        for (ExportFeature exportFeature : exportFeatures) {

            if (exportFeature instanceof PsPeptideFeature) {
                identificationFeatures.add(exportFeature);
            } else if (exportFeature instanceof ReporterPeptideFeature) {
                quantificationFeatures.add((ReporterExportFeature) exportFeature);
            } else if (exportFeature instanceof ReporterPsmFeatures
                    || exportFeature instanceof PsPsmFeature
                    || exportFeature instanceof PsIdentificationAlgorithmMatchesFeature
                    || exportFeature instanceof PsFragmentFeature) {
                psmFeatures.add(exportFeature);
            } else {
                throw new IllegalArgumentException(
                        "Export feature of type "
                        + exportFeature.getClass()
                        + " not recognized."
                );
            }

        }

        if (!psmFeatures.isEmpty()) {
            psmSection = new ReporterPsmSection(
                    psmFeatures,
                    indexes,
                    header,
                    writer
            );
        }

        this.indexes = indexes;
        this.header = header;
        this.writer = writer;

        if (writer instanceof ExcelWriter) {
            reporterStyle = ReporterReportStyle.getReportStyle((ExcelWriter) writer);
        }

    }

    /**
     * Writes the desired section.
     *
     * @param identification the identification of the project
     * @param identificationFeaturesGenerator the identification features
     * generator of the project
     * @param sequenceProvider the sequence provider
     * @param spectrumProvider the spectrum provider
     * @param proteinDetailsProvider the protein details provider
     * @param quantificationFeaturesGenerator the quantification features
     * generator containing the quantification information
     * @param reporterIonQuantification the reporter ion quantification object
     * containing the quantification configuration
     * @param reporterSettings the reporter settings
     * @param identificationParameters the identification parameters
     * @param keys the keys of the protein matches to output
     * @param nSurroundingAA the number of surrounding amino acids to export
     * @param linePrefix the line prefix to use
     * @param validatedOnly whether only validated matches should be exported
     * @param decoys whether decoy matches should be exported as well
     * @param waitingHandler the waiting handler
     *
     * @throws java.sql.SQLException exception thrown whenever an error occurred
     * while interacting with the database
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while interacting with a file
     * @throws java.lang.ClassNotFoundException exception thrown whenever an
     * error occurred while deserializing an object
     * @throws java.lang.InterruptedException exception thrown whenever a
     * threading error occurred
     * @throws org.apache.commons.math.MathException exception thrown whenever
     * an error occurred while transforming the ratios
     */
    public void writeSection(
            Identification identification,
            IdentificationFeaturesGenerator identificationFeaturesGenerator,
            SequenceProvider sequenceProvider,
            SpectrumProvider spectrumProvider,
            ProteinDetailsProvider proteinDetailsProvider,
            QuantificationFeaturesGenerator quantificationFeaturesGenerator,
            ReporterIonQuantification reporterIonQuantification,
            ReporterSettings reporterSettings,
            IdentificationParameters identificationParameters,
            long[] keys,
            int nSurroundingAA,
            String linePrefix,
            boolean validatedOnly,
            boolean decoys,
            WaitingHandler waitingHandler
    ) throws IOException, IllegalArgumentException, SQLException,
            ClassNotFoundException, InterruptedException, MathException {

        if (waitingHandler != null) {
            waitingHandler.setSecondaryProgressCounterIndeterminate(true);
        }

        if (header) {
            writeHeader(reporterIonQuantification);
        }

        if (keys == null) {
            keys = identification.getPeptideIdentification().stream()
                    .mapToLong(Long::longValue)
                    .toArray();
        }

        int line = 1;
        PSParameter psParameter = new PSParameter();
        ArrayList<UrParameter> parameters = new ArrayList<>(1);
        parameters.add(psParameter);

        if (waitingHandler != null) {
            waitingHandler.setWaitingText("Exporting. Please Wait...");
            waitingHandler.resetSecondaryProgressCounter();
            waitingHandler.setMaxSecondaryProgressCounter(keys.length);
        }

        PeptideMatchesIterator peptideMatchesIterator = identification.getPeptideMatchesIterator(keys, waitingHandler);
        PeptideMatch peptideMatch;

        while ((peptideMatch = peptideMatchesIterator.next()) != null) {

            if (waitingHandler != null) {
                if (waitingHandler.isRunCanceled()) {
                    return;
                }
                waitingHandler.increaseSecondaryProgressCounter();
            }

            long peptideKey = peptideMatch.getKey();

            psParameter = (PSParameter) identification.getPeptideMatch(peptideKey).getUrParam(psParameter);

            if (!validatedOnly || psParameter.getMatchValidationLevel().isValidated()) {

                peptideMatch = identification.getPeptideMatch(peptideKey);

                if (decoys || !PeptideUtils.isDecoy(peptideMatch.getPeptide(), sequenceProvider)) {

                    boolean first = true;

                    if (indexes) {

                        if (linePrefix != null) {
                            writer.write(linePrefix);
                        }

                        writer.write(line + "");
                        first = false;

                    }

                    for (ExportFeature exportFeature : identificationFeatures) {

                        if (!first) {
                            writer.addSeparator();
                        } else {
                            first = false;
                        }

                        PsPeptideFeature peptideFeature = (PsPeptideFeature) exportFeature;

                        writer.write(PsPeptideSection.getfeature(
                                identification,
                                identificationFeaturesGenerator,
                                sequenceProvider,
                                proteinDetailsProvider,
                                identificationParameters,
                                nSurroundingAA,
                                linePrefix,
                                peptideMatch,
                                peptideFeature,
                                validatedOnly,
                                decoys,
                                waitingHandler)
                        );

                    }

                    ArrayList<String> sampleIndexes = new ArrayList<>(reporterIonQuantification.getSampleIndexes());
                    Collections.sort(sampleIndexes);

                    for (ExportFeature exportFeature : quantificationFeatures) {

                        ReporterPeptideFeature peptideFeature = (ReporterPeptideFeature) exportFeature;

                        if (peptideFeature.hasChannels()) {

                            for (String sampleIndex : sampleIndexes) {

                                if (!first) {
                                    writer.addSeparator();
                                } else {
                                    first = false;
                                }

                                writer.write(
                                        getFeature(
                                                spectrumProvider,
                                                quantificationFeaturesGenerator,
                                                reporterIonQuantification,
                                                peptideMatch,
                                                peptideFeature,
                                                sampleIndex,
                                                waitingHandler
                                        ),
                                        reporterStyle
                                );
                            }
                        } else {

                            if (!first) {
                                writer.addSeparator();
                            } else {
                                first = false;
                            }

                            writer.write(
                                    getFeature(
                                            spectrumProvider,
                                            quantificationFeaturesGenerator,
                                            reporterIonQuantification,
                                            peptideMatch,
                                            peptideFeature,
                                            "",
                                            waitingHandler
                                    ),
                                    reporterStyle
                            );
                        }
                    }

                    if (psmSection != null) {

                        writer.newLine();
                        String psmSectionPrefix = "";

                        if (linePrefix != null) {
                            psmSectionPrefix += linePrefix;
                        }

                        psmSectionPrefix += line + ".";
                        writer.increaseDepth();

                        psmSection.writeSection(
                                identification,
                                identificationFeaturesGenerator,
                                sequenceProvider,
                                spectrumProvider,
                                proteinDetailsProvider,
                                quantificationFeaturesGenerator,
                                reporterIonQuantification,
                                reporterSettings,
                                identificationParameters,
                                peptideMatch.getSpectrumMatchesKeys(),
                                psmSectionPrefix,
                                nSurroundingAA,
                                validatedOnly,
                                decoys,
                                null
                        );

                        writer.decreaseDepth();
                    }

                    line++;
                    writer.newLine();

                }
            }
        }
    }

    /**
     * Returns the report component corresponding to a feature at a given
     * channel.
     *
     * @param spectrumProvider the spectrum provider
     * @param quantificationFeaturesGenerator the quantification features
     * generator
     * @param reporterIonQuantification the reporter ion quantification object
     * containing the quantification configuration
     * @param peptideMatch the peptide match
     * @param peptideFeatures the peptide feature to export
     * @param sampleIndex the index of the sample in case the feature is channel
     * dependent, ignored otherwise
     * @param waitingHandler the waiting handler
     *
     * @return the report component corresponding to a feature at a given
     * channel
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
    public static String getFeature(
            SpectrumProvider spectrumProvider,
            QuantificationFeaturesGenerator quantificationFeaturesGenerator,
            ReporterIonQuantification reporterIonQuantification,
            PeptideMatch peptideMatch,
            ReporterPeptideFeature peptideFeatures,
            String sampleIndex,
            WaitingHandler waitingHandler
    ) throws SQLException, IOException, ClassNotFoundException, InterruptedException {

        switch (peptideFeatures) {

            case raw_ratio:

                PeptideQuantificationDetails quantificationDetails
                        = quantificationFeaturesGenerator.getPeptideMatchQuantificationDetails(
                                spectrumProvider,
                                peptideMatch,
                                waitingHandler
                        );

                return quantificationDetails.getRawRatio(sampleIndex).toString();

            case normalized_ratio:

                quantificationDetails = quantificationFeaturesGenerator.getPeptideMatchQuantificationDetails(
                        spectrumProvider,
                        peptideMatch,
                        waitingHandler
                );

                return quantificationDetails.getRatio(sampleIndex, reporterIonQuantification.getNormalizationFactors()).toString();

            default:
                return "Not implemented";
        }
    }

    /**
     * Writes the title of the section.
     *
     * @param reporterIonQuantification the reporter ion quantification object
     * containing the quantification configuration
     *
     * @throws java.io.IOException exception thrown whenever an error occurred
     * while interacting with a file
     */
    public void writeHeader(ReporterIonQuantification reporterIonQuantification) throws IOException {

        boolean needSecondLine = false;
        ArrayList<String> sampleIndexes = new ArrayList<>(reporterIonQuantification.getSampleIndexes());
        Collections.sort(sampleIndexes);

        boolean firstColumn = true;

        if (indexes) {
            writer.writeHeaderText("");
            writer.addSeparator();
        }

        for (ExportFeature exportFeature : identificationFeatures) {

            if (firstColumn) {
                firstColumn = false;
            } else {
                writer.addSeparator();
            }

            writer.writeHeaderText(exportFeature.getTitle());
        }

        for (ReporterExportFeature exportFeature : quantificationFeatures) {

            if (firstColumn) {
                firstColumn = false;
            } else {
                writer.addSeparator();
            }

            writer.writeHeaderText(exportFeature.getTitle(), reporterStyle);

            if (exportFeature.hasChannels()) {

                for (int i = 1; i < sampleIndexes.size(); i++) {

                    if (firstColumn) {
                        firstColumn = false;
                    } else {
                        writer.addSeparator();
                    }

                    writer.writeHeaderText(" ", reporterStyle); // Space used for the excel style
                }

                needSecondLine = true;

            }

        }

        if (needSecondLine) {

            writer.newLine();
            firstColumn = true;

            if (indexes) {
                writer.addSeparator();
            }

            for (ExportFeature exportFeature : identificationFeatures) {

                if (firstColumn) {
                    firstColumn = false;
                } else {
                    writer.writeHeaderText("");
                    writer.addSeparator();
                }

            }

            for (ReporterExportFeature exportFeature : quantificationFeatures) {

                if (exportFeature.hasChannels()) {

                    for (String sampleIndex : sampleIndexes) {

                        if (firstColumn) {
                            firstColumn = false;
                        } else {
                            writer.writeHeaderText("", reporterStyle);
                            writer.addSeparator();
                        }

                        writer.write(reporterIonQuantification.getSample(sampleIndex), reporterStyle);

                    }

                } else {

                    if (firstColumn) {
                        firstColumn = false;
                    } else {
                        writer.writeHeaderText("", reporterStyle);
                        writer.addSeparator();
                    }

                }
            }
        }

        writer.newLine();
    }
}
