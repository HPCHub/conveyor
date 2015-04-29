/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class LoadSampleAction {


    private long _sampleId;


    public SeqsSample getSample() {

        return DAO.findById(SeqsSample.class, _sampleId);
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setSampleId(long sampleId) {

        _sampleId = sampleId;
    }
}
