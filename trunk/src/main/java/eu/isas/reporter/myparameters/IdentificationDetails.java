/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.isas.reporter.myparameters;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 * this customizable class contains all necessary identification details
 *
 * @author marc
 */
public class IdentificationDetails implements UrParameter {
    
    /**
     * boolean indicating whether the match is validated
     */
    private boolean isValidated;
    
    /**
     * Constructor
     */
    public IdentificationDetails() {
        
    }

    /**
     * Returns a boolean indicating whether the hit is validated
     * @return a boolean indicating whether the hit is validated
     */
    public boolean isValidated() {
        return isValidated;
    }

    /**
     * Sets whether the hit is validated
     * @param isValidated boolean indicating whether the hit is validated
     */
    public void setValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }    
    
    
    @Override
    public String getFamilyName() {
        return "reporter";
    }

    @Override
    public int getIndex() {
        return 1;
    }
}