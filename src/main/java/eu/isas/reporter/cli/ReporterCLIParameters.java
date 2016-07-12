package eu.isas.reporter.cli;

import com.compomics.util.experiment.identification.parameters_cli.IdentificationParametersCLIParams;
import org.apache.commons.cli.Options;

/**
 * Command line option parameters for ReporterCLI.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public enum ReporterCLIParameters {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // IMPORTANT: Any change here must be reported in the wiki: 
    // (once the wiki exists)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    ID("id", "The PeptideShaker project (.cpsx or .zip).", true),
    OUT("out", "Output file to save the project.", false),
    ISOTOPES("isotopes", "The isotope correction factors file (.xml file). Default values will be used if not provided, it is strongly advised to provide the values corresponding to the labelling kit used during the experiment.", false),
    
    METHODS("methods_file", "Path to the isotope correction factors file containing the quantification methods including isotopic correction factors. Default values will be used if not provided.", false),
    METHOD("method", "The reporter ion quantification method to use from the isotopic methods file in case multiple methods are listed in the file. Will be inferred from identification parameters if not provided.", false),
    
    ION_TOL("ion_tol", "The reporter ion m/z tolerance. Default will be inferred from the identification parameters and reporter method.", false),
    MOST_ACCURATE("most_accurate", "Indicates whether the ion within the m/z tolerance with the most accurate m/z should be selected (1: yes, 0: no). If no, the most intense ion will be selected. Default will be inferred from the identification parameters.", false),
    SAME_SPECTRA("same_spectra", "Indicates whether reporter ions are in the same spectra as the identification fragment ions (1: yes, 0: no). If no, the spectra from prescursor in an m/z and RT window around the identified precursor will be used. Default is 1.", false),
    PREC_WINDOW_MZ_TOL("prec_window_mz_tol", "If " + SAME_SPECTRA.name() + " is set to 0, the m/z tolerance to use. Default is 1.", false),
    PREC_WINDOW_MZ_TOL_PPM("prec_window_mz_tol_ppm", "If " + SAME_SPECTRA.name() + " is set to 0, indicates whether the m/z tolerance to use is in ppm (1: yes, 0: no). Default is 1.", false),
    PREC_WINDOW_RT_TOL("prec_window_rt_tol", "If " + SAME_SPECTRA.name() + " is set to 0, the rt tolerance in seconds to use. Default is 10. Will be used only if available in the mgf file.", false),
    
    THREADS("threads", "Number of threads to use for the processing, default: the number of cores on the machine.", false);

    /**
     * Short Id for the CLI parameter.
     */
    public String id;
    /**
     * Explanation for the CLI parameter.
     */
    public String description;
    /**
     * Boolean indicating whether the parameter is mandatory.
     */
    public boolean mandatory;

    /**
     * Constructor.
     *
     * @param id the id
     * @param description the description
     * @param mandatory is the parameter mandatory
     */
    private ReporterCLIParameters(String id, String description, boolean mandatory) {
        this.id = id;
        this.description = description;
        this.mandatory = mandatory;
    }

    /**
     * Creates the options for the command line interface based on the possible
     * values.
     *
     * @param aOptions the options object where the options will be added
     */
    public static void createOptionsCLI(Options aOptions) {
        
        for (ReporterCLIParameters reporterCLIParameters : values()) {
            aOptions.addOption(reporterCLIParameters.id, true, reporterCLIParameters.description);
        }
        
        // Path setup
        aOptions.addOption(PathSettingsCLIParams.ALL.id, true, PathSettingsCLIParams.ALL.description);
    }

    /**
     * Returns the options as a string.
     *
     * @return the options as a string
     */
    public static String getOptionsAsString() {

        String output = "";
        String formatter = "%-25s";

        output += "Mandatory Parameters:\n\n";
        output += "-" + String.format(formatter, ID.id) + " " + ID.description + "\n";
        output += "-" + String.format(formatter, ISOTOPES.id) + " " + ISOTOPES.description + "\n";

        output += "Reporter Ion Methods options:\n\n";
        output += "-" + String.format(formatter, METHODS.id) + " " + METHODS.description + "\n";
        output += "-" + String.format(formatter, METHOD.id) + " " + METHOD.description + "\n";
                
        output += "\n\nOutput:\n\n";
        output += "-" + String.format(formatter, OUT.id) + " " + OUT.description + "\n";
        
        output += "\n\nProcessing Options:\n\n";
        output += "-" + String.format(formatter, THREADS.id) + " " + THREADS.description + "\n";
        
        output += "\n\nAdvanced Options:\n\n";
//        output += "-" + String.format(formatter, REFERENCE_MASS.id) + " " + REFERENCE_MASS.description + "\n"; TODO
        
        output += "\n\nOptional Temporary Folder:\n\n";
        output += "-" + String.format(formatter, PathSettingsCLIParams.ALL.id) + " " + PathSettingsCLIParams.ALL.description + "\n";

        output += "\n\nOptional Input Parameters:\n\n";
        output += "-" + String.format(formatter, IdentificationParametersCLIParams.IDENTIFICATION_PARAMETERS.id) + " " + IdentificationParametersCLIParams.IDENTIFICATION_PARAMETERS.description + "\n";
        
        output += "\n\n\nFor identification parameters options:\nReplace eu.isas.reporter.cmd.ReporterCLI with eu.isas.reportergui.cmd.IdentificationParametersCLI\n\n";

        return output;
    }
    

}
