/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import java.util.Date;
import org.hibernate.exception.ConstraintViolationException;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.ripcm.bioinformatics.conveyor.core.XConfig;


/**
 * @author zeleniy
 */
public class SaveProjectAction extends ActionSupport {


    private long   _projectId;
    private String _name;
    private String _description;


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            SeqsProject project;
            if (_projectId > 0) {
                project = DAO.findById(SeqsProject.class, _projectId);
            } else {
                project = new SeqsProject(new Date());
            }

            project.setName(getName());
            project.setDescription(getDescription());
            project.setRootDir(XConfig.getInstance().getProperty("defaultProjectDirectory"));

            DAO.save(project);
            _projectId = project.getProjectId();
        } catch (ConstraintViolationException e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save project [%s] Looks like project with name \"%s\" already exists",
                e.getClass().getName(),
                getName()
            )));
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save project [%s] %s", e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public long getProjectId() {

        return _projectId;
    }


    public String getName() {

        return _name;
    }


    public String getDescription() {

        return _description;
    }


    public void setProjectId(long projectId) {

        _projectId = projectId;
    }


    public void setName(String name) {

        _name = name;
    }


    public void setDescription(String description) {

        _description = description;
    }
}