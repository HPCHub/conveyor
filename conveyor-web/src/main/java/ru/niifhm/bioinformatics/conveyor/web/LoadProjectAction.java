/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class LoadProjectAction {


    private long _projectId;


    public SeqsProject getProject() {

        return DAO.findById(SeqsProject.class, _projectId);
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setProjectId(long projectId) {

        _projectId = projectId;
    }
}
