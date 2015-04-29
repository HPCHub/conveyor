/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class DeleteProjectAction extends ActionSupport {


    private long _projectId;


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            DAO.deleteById(SeqsProject.class, _projectId);
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot delete project [%s] %s", e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public void setProjectId(long projectId) {

        _projectId = projectId;
    }
}