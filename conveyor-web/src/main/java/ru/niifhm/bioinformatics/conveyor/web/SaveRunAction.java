/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import java.util.Date;
import org.hibernate.exception.ConstraintViolationException;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class SaveRunAction extends ActionSupport {


    private long   _runId;
    private String _name;
    private String _description;
    private long   _sequencerId;
    private long   _projectId;


    public void setSequencerId(long sequencerId) {

        _sequencerId = sequencerId;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            SeqsRun run;
            if (_runId > 0) {
                run = DAO.findById(SeqsRun.class, _runId);
            } else {
                run = new SeqsRun(new Date());
            }

            run.setName(_name);
            run.setDescription(_description);
            run.setSequencerId(_sequencerId);
            run.setSeqsProject(DAO.findById(SeqsProject.class, _projectId));

            DAO.save(run);
            _runId = run.getRunId();
        } catch (ConstraintViolationException e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save run [%s] Looks like run with name \"%s\" already exists",
                e.getClass().getName(),
                _name
            )));
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save run [%s] %s", e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public long getRunId() {

        return _runId;
    }


    public void setRunId(long id) {

        _runId = id;
    }


    public void setName(String name) {

        _name = name;
    }


    public void setProjectId(long projectId) {

        _projectId = projectId;
    }


    public void setDescription(String description) {

        _description = description;
    }
}