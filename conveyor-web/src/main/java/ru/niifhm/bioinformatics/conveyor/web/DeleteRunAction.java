/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class DeleteRunAction extends ActionSupport {


    private long _runId;


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            DAO.deleteById(SeqsRun.class, _runId);
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot delete sample [%s] %s", e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }
}