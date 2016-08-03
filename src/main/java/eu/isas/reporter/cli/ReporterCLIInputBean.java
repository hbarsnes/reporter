package eu.isas.reporter.cli;

import com.compomics.software.cli.CommandLineUtils;
import com.compomics.software.cli.CommandParameter;
import com.compomics.util.Util;
import com.compomics.util.experiment.identification.parameters_cli.IdentificationParametersInputBean;
import com.compomics.util.preferences.IdentificationParameters;
import eu.isas.peptideshaker.scoring.MatchValidationLevel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.ujmp.core.collections.ArrayIndexList;

/**
 * This class is used to verify that a command line is valid and parse its
 * parameters.
 *
 * @author Marc Vaudel
 */
public class ReporterCLIInputBean {

    /**
     * The file containing the identification results.
     */
    private File peptideShakerFile;
    /**
     * File where to save the output.
     */
    private File outputFile = null;
    /**
     * The file containing the isotope correction factors.
     */
    private File isotopesFile = null;
    /**
     * The number of threads.
     */
    private int nThreads = Runtime.getRuntime().availableProcessors();
    /**
     * The identification parameters input.
     */
    private IdentificationParametersInputBean identificationParametersInputBean;
    /**
     * The path settings.
     */
    private PathSettingsCLIInputBean pathSettingsCLIInputBean;
    /**
     * The name of the reporter ion method as set in the isotopic distribution file.
     */
    private String reporterMethod = null;
    /**
     * The reporter ion tolerance.
     */
    private Double reporterIonTolerance = null;
    /**
     * Boolean indicating whether the most accurate reporter ion should be used.
     */
    private Boolean mostAccurate = null;
    /**
     * Boolean indicating whether the quantification peaks are in the same spectra as the identification peaks.
     */
    private Boolean sameSpectra = null;
    /**
     * The precursor window m/z tolerance.
     */
    private Double precMzTolerance = null;
    /**
     * Boolean indicating whether the precursor window m/z tolerance is in ppm.
     */
    private Boolean precMzTolerancePpm = null;
    /**
     * The precursor window RT tolerance in seconds.
     */
    private Double precRtTolerance = null;
    /**
     * Boolean indicating whether peptides presenting null intensities should be ignored.
     */
    private Boolean ignoreNull = null;
    /**
     * Boolean indicating whether peptides presenting missed cleavages should be ignored.
     */
    private Boolean ignoreMc = null;
    /**
     * The percentile for ratio window estimation.
     */
    private Double percentile = null;
    /**
     * The resolution for ratio estimation.
     */
    private Double resolution = null;
    /**
     * The minimum number of unique peptides to consider only those for a protein group ratio estimation.
     */
    private Integer minUnique = null;
    /**
     * The list of ptms to ignore.
     */
    private ArrayList<String> ignoredPtms = null;
    /**
     * The validation level for a PSM to be considered for quantification.
     */
    private Integer validationPsm = null;
    /**
     * The validation level for a peptide to be considered for quantification.
     */
    private Integer validationPeptide = null;
    /**
     * The validation level for a protein to be considered for quantification.
     */
    private Integer validationProtein = null;

    /**
     * Parses the arguments of a command line.
     *
     * @param aLine the command line
     * 
     * @throws IOException thrown if an error occurred while reading the FASTA
     * file
     * @throws ClassNotFoundException thrown if the search parameters cannot be
     * converted
     */
    public ReporterCLIInputBean(CommandLine aLine) throws IOException, ClassNotFoundException {

        // PeptideShaker file
        String arg = aLine.getOptionValue(ReporterCLIParameters.ID.id);
        peptideShakerFile = new File(arg);

        // The output file
        if (aLine.hasOption(ReporterCLIParameters.OUT.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.OUT.id);
            outputFile = new File(arg);
        }

        // The isotopes file
        if (aLine.hasOption(ReporterCLIParameters.ISOTOPES.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.ISOTOPES.id);
            isotopesFile = new File(arg);
        }

        // get the number of threads
        if (aLine.hasOption(ReporterCLIParameters.THREADS.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.THREADS.id);
            nThreads = new Integer(arg);
        }

        // get the reporter ion method
        if (aLine.hasOption(ReporterCLIParameters.METHOD.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.METHOD.id);
            reporterMethod = arg;
        }

        // get the reporter ion m/z tolerance
        if (aLine.hasOption(ReporterCLIParameters.ION_TOL.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.ION_TOL.id);
            Double input = new Double(arg);
            reporterIonTolerance = input;
        }

        // get the most accurate option
        if (aLine.hasOption(ReporterCLIParameters.MOST_ACCURATE.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.MOST_ACCURATE.id);
            Integer input = new Integer(arg);
            mostAccurate = input.equals(1);
        }

        // get the same spectra option
        if (aLine.hasOption(ReporterCLIParameters.SAME_SPECTRA.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.SAME_SPECTRA.id);
            Integer input = new Integer(arg);
            sameSpectra = input.equals(1);
        }

        // get the precursor ion m/z tolerance
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_MZ_TOL.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_MZ_TOL.id);
            Double input = new Double(arg);
            precMzTolerance = input;
        }

        // get the precursor ion m/z tolerance unit
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_MZ_TOL_PPM.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_MZ_TOL_PPM.id);
            Integer input = new Integer(arg);
            precMzTolerancePpm = input.equals(1);
        }

        // get the precursor ion RT tolerance
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_RT_TOL.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_RT_TOL.id);
            Double input = new Double(arg);
            precRtTolerance = input;
        }
        
        // get the ignore null option
        if (aLine.hasOption(ReporterCLIParameters.IGNORE_NULL.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.IGNORE_NULL.id);
            Integer input = new Integer(arg);
            ignoreNull = input.equals(1);
        }
        
        // get the ignore missed cleavages option
        if (aLine.hasOption(ReporterCLIParameters.IGNORE_MC.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.IGNORE_MC.id);
            Integer input = new Integer(arg);
            ignoreMc = input.equals(1);
        }

        // get the percentile option for ratio estimation
        if (aLine.hasOption(ReporterCLIParameters.PERCENTILE.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.PERCENTILE.id);
            Double input = new Double(arg);
            percentile = input;
        }

        // get the resolution option for ratio estimation
        if (aLine.hasOption(ReporterCLIParameters.RESOLUTION.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.RESOLUTION.id);
            Double input = new Double(arg);
            resolution = input;
        }

        // get the number of unique peptides
        if (aLine.hasOption(ReporterCLIParameters.MIN_UNIQUE.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.MIN_UNIQUE.id);
            minUnique = new Integer(arg);
        }

        // get the ignored PTMs
        if (aLine.hasOption(ReporterCLIParameters.IGNORE_PTMS.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.IGNORE_PTMS.id);
            ignoredPtms = CommandLineUtils.splitInput(arg);
        }

        // get the PSM validation level
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PSM.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PSM.id);
            validationPsm = new Integer(arg);
        }

        // get the peptide validation level
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PEPTIDE.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PEPTIDE.id);
            validationPeptide = new Integer(arg);
        }

        // get the peptide validation level
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PROTEIN.id)) {
            arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PROTEIN.id);
            validationProtein = new Integer(arg);
        }

        // identification parameters
        identificationParametersInputBean = new IdentificationParametersInputBean(aLine);

        // path settings
        pathSettingsCLIInputBean = new PathSettingsCLIInputBean(aLine);

    }

    /**
     * Verifies that the command line is valid.
     *
     * @param aLine the command line to validate
     *
     * @return a boolean indicating whether the command line is valid.
     */
    public static boolean isValidStartup(CommandLine aLine) {

        // PeptideShaker file
        if (!aLine.hasOption(ReporterCLIParameters.ID.id) || ((String) aLine.getOptionValue(ReporterCLIParameters.ID.id)).equals("")) {
            System.out.println(System.getProperty("line.separator") + "PeptideShaker file not specified." + System.getProperty("line.separator"));
            return false;
        } else {
            String arg = aLine.getOptionValue(ReporterCLIParameters.ID.id);
            File input = new File(arg);
            if (!input.exists()) {
                System.out.println(System.getProperty("line.separator") + "PeptideShaker file \'" + input.getName() + "\' not found." + System.getProperty("line.separator"));
            }
            String extension = Util.getExtension(input);
            if (!extension.equals("cpsx") || !extension.equals("zip")) {
                System.out.println(System.getProperty("line.separator") + "Format \'" + extension + "\' not supported for PeptideShaker file." + System.getProperty("line.separator"));
            }
        }

        // The isotopes file
        if (aLine.hasOption(ReporterCLIParameters.ISOTOPES.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.ISOTOPES.id);
            File input = new File(arg);
            if (!input.exists()) {
                System.out.println(System.getProperty("line.separator") + "Isotope correction factors file \'" + input.getName() + "\' not found." + System.getProperty("line.separator"));
            }
            String extension = Util.getExtension(input);
            if (!extension.equals("xml")) {
                System.out.println(System.getProperty("line.separator") + "Format \'" + extension + "\' not supported for Isotope correction factors file." + System.getProperty("line.separator"));
            }
        }

        // The number of threads
        if (aLine.hasOption(ReporterCLIParameters.THREADS.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.THREADS.id);
            if (!CommandParameter.isPositiveInteger(ReporterCLIParameters.THREADS.id, arg, false)) {
                return false;
            }
        }
        
        // The ion tolerance
        if (aLine.hasOption(ReporterCLIParameters.ION_TOL.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.ION_TOL.id);
            if (!CommandParameter.isPositiveDouble(ReporterCLIParameters.ION_TOL.id, arg, false)) {
                return false;
            }
        }
        
        // Most accurate option
        if (aLine.hasOption(ReporterCLIParameters.MOST_ACCURATE.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.MOST_ACCURATE.id);
            if (!CommandParameter.isBooleanInput(ReporterCLIParameters.MOST_ACCURATE.id, arg)) {
                return false;
            }
        }
        
        // Same spectra option
        if (aLine.hasOption(ReporterCLIParameters.SAME_SPECTRA.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.SAME_SPECTRA.id);
            if (!CommandParameter.isBooleanInput(ReporterCLIParameters.SAME_SPECTRA.id, arg)) {
                return false;
            }
        }
        
        // The precursor ion m/z tolerance
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_MZ_TOL.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_MZ_TOL.id);
            if (!CommandParameter.isPositiveDouble(ReporterCLIParameters.PREC_WINDOW_MZ_TOL.id, arg, false)) {
                return false;
            }
        }
        
        // The precursor ion m/z tolerance unit
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_MZ_TOL_PPM.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_MZ_TOL_PPM.id);
            if (!CommandParameter.isBooleanInput(ReporterCLIParameters.PREC_WINDOW_MZ_TOL_PPM.id, arg)) {
                return false;
            }
        }
        
        // The precursor ion RT tolerance
        if (aLine.hasOption(ReporterCLIParameters.PREC_WINDOW_RT_TOL.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.PREC_WINDOW_RT_TOL.id);
            if (!CommandParameter.isPositiveDouble(ReporterCLIParameters.PREC_WINDOW_RT_TOL.id, arg, false)) {
                return false;
            }
        }
        
        // The ignore null option
        if (aLine.hasOption(ReporterCLIParameters.IGNORE_NULL.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.IGNORE_NULL.id);
            if (!CommandParameter.isBooleanInput(ReporterCLIParameters.IGNORE_NULL.id, arg)) {
                return false;
            }
        }
        
        // The ignore missed cleavages option
        if (aLine.hasOption(ReporterCLIParameters.IGNORE_MC.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.IGNORE_MC.id);
            if (!CommandParameter.isBooleanInput(ReporterCLIParameters.IGNORE_MC.id, arg)) {
                return false;
            }
        }
        
        // The percentile
        if (aLine.hasOption(ReporterCLIParameters.PERCENTILE.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.PERCENTILE.id);
            if (!CommandParameter.isPositiveDouble(ReporterCLIParameters.PERCENTILE.id, arg, false)) {
                return false;
            }
        }

        // The resolution option for ratio estimation
        if (aLine.hasOption(ReporterCLIParameters.RESOLUTION.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.RESOLUTION.id);
            if (!CommandParameter.isPositiveDouble(ReporterCLIParameters.RESOLUTION.id, arg, false)) {
                return false;
            }
        }

        // The number of unique peptides
        if (aLine.hasOption(ReporterCLIParameters.MIN_UNIQUE.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.MIN_UNIQUE.id);
            if (!CommandParameter.isInteger(ReporterCLIParameters.MIN_UNIQUE.id, arg)) {
                return false;
            }
        }

        // The validation levels
        ArrayList<String> validationLevels = new ArrayList<String>(3);
        validationLevels.add("0");
        validationLevels.add("1");
        validationLevels.add("2");
        // PSM
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PSM.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PSM.id);
            if (!CommandParameter.isInList(ReporterCLIParameters.VALIDATION_PSM.id, arg, validationLevels)) {
                return false;
            }
        }
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PEPTIDE.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PEPTIDE.id);
            if (!CommandParameter.isInList(ReporterCLIParameters.VALIDATION_PEPTIDE.id, arg, validationLevels)) {
                return false;
            }
        }
        // Protein
        if (aLine.hasOption(ReporterCLIParameters.VALIDATION_PROTEIN.id)) {
            String arg = aLine.getOptionValue(ReporterCLIParameters.VALIDATION_PROTEIN.id);
            if (!CommandParameter.isInList(ReporterCLIParameters.VALIDATION_PROTEIN.id, arg, validationLevels)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Returns the PeptideShaker file.
     * 
     * @return the PeptideShaker file
     */
    public File getPeptideShakerFile() {
        return peptideShakerFile;
    }

    /**
     * Returns the output file where to save the project, null if not set.
     * 
     * @return the output file
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * Returns the isotope correction file, null if not set.
     * 
     * @return the isotope correction file
     */
    public File getIsotopesFile() {
        return isotopesFile;
    }

    /**
     * Returns the number of threads to use.
     * 
     * @return the number of threads to use
     */
    public int getnThreads() {
        return nThreads;
    }

    /**
     * Returns the name of the reporter methods provided by the user.
     * 
     * @return the name of the reporter method
     */
    public String getReporterMethod() {
        return reporterMethod;
    }

    /**
     * Returns the tolerance used for the reporter ion matching in the spectrum.
     * 
     * @return the tolerance used for the reporter ion matching in the spectrum
     */
    public Double getReporterIonTolerance() {
        return reporterIonTolerance;
    }

    /**
     * Indicates whether the most accurate reporter ion should be used.
     * 
     * @return a boolean indicating whether the most accurate reporter ion should be used
     */
    public Boolean getMostAccurate() {
        return mostAccurate;
    }

    /**
     * Indicates whether the quantification peaks are in the same spectra as the identification peaks.
     * 
     * @return a boolean indicating whether the quantification peaks are in the same spectra as the identification peaks
     */
    public Boolean getSameSpectra() {
        return sameSpectra;
    }

    /**
     * Returns the m/z tolerance to use for the precursor ion window.
     * 
     * @return the m/z tolerance to use for the precursor ion window
     */
    public Double getPrecMzTolerance() {
        return precMzTolerance;
    }

    /**
     * Indicates whether the precursor ion window m/z tolerance is in ppm.
     * 
     * @return a boolean indicating whether the window precursor ion m/z tolerance is in ppm
     */
    public Boolean getPrecMzTolerancePpm() {
        return precMzTolerancePpm;
    }

    /**
     * Returns the RT tolerance to use for the precursor ion window in seconds.
     * 
     * @return the RT tolerance to use for the precursor ion window in seconds
     */
    public Double getPrecRtTolerance() {
        return precRtTolerance;
    }

    /**
     * Returns the identification parameters.
     *
     * @return the identification parameters
     */
    public IdentificationParameters getIdentificationParameters() {
        return identificationParametersInputBean.getIdentificationParameters();
    }

    /**
     * Returns the identification parameters file.
     *
     * @return the identification parameters file
     */
    public File getIdentificationParametersFile() {
        if (identificationParametersInputBean.getDestinationFile() != null) {
            return identificationParametersInputBean.getDestinationFile();
        } else {
            return identificationParametersInputBean.getInputFile();
        }
    }

    /**
     * Returns the path settings provided by the user.
     *
     * @return the path settings provided by the user
     */
    public PathSettingsCLIInputBean getPathSettingsCLIInputBean() {
        return pathSettingsCLIInputBean;
    }

    /**
     * Returns a boolean indicating whether peptides presenting null intensities should be ignored.
     * 
     * @return a boolean indicating whether peptides presenting null intensities should be ignored
     */
    public Boolean getIgnoreNull() {
        return ignoreNull;
    }

    /**
     * Returns a boolean indicating whether peptides presenting missed cleavages should be ignored.
     * 
     * @return a boolean indicating whether peptides presenting missed cleavages should be ignored
     */
    public Boolean getIgnoreMc() {
        return ignoreMc;
    }

    /**
     * Returns the percentile for ratio window estimation.
     * 
     * @return the percentile for ratio window estimation
     */
    public Double getPercentile() {
        return percentile;
    }

    /**
     * Returns the resolution for ratio estimation.
     * 
     * @return the resolution for ratio estimation
     */
    public Double getResolution() {
        return resolution;
    }

    /**
     * Returns the minimum number of unique peptides to consider only those for a protein group ratio estimation.
     * 
     * @return the minimum number of unique peptides to consider only those for a protein group ratio estimation
     */
    public Integer getMinUnique() {
        return minUnique;
    }

    /**
     * Returns the list of ptms to ignore.
     * 
     * @return the list of ptms to ignore
     */
    public ArrayList<String> getIgnoredPtms() {
        return ignoredPtms;
    }

    /**
     * Returns the validation level to use for PSMs.
     * 
     * @return the validation level to use for PSMs
     */
    public Integer getValidationPsm() {
        return validationPsm;
    }

    /**
     * Returns the validation level to use for peptides.
     * 
     * @return the validation level to use for peptides
     */
    public Integer getValidationPeptide() {
        return validationPeptide;
    }

    /**
     * Returns the validation level to use for proteins.
     * 
     * @return the validation level to use for proteins
     */
    public Integer getValidationProtein() {
        return validationProtein;
    }
    
}
