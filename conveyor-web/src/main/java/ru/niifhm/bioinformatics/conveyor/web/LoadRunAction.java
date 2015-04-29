/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class LoadRunAction {


    private long _runId;


    public SeqsRun getRun() {

        return DAO.findById(SeqsRun.class, _runId);
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }
}