/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.ms;


import java.util.Arrays;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.MsIssue;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ValidateMsAction extends ActionSupport {


    private long   _issueId;


    public String validateMs() {

        try {
            MsIssue issue = DAO.findById(MsIssue.class, _issueId);
            issue.setIsCredible(true);
            DAO.save(issue);
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "cannot update msIssue with id [%s] [%s] %s", _issueId, e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public String invalidateMs() {

        try {
            MsIssue issue = DAO.findById(MsIssue.class, _issueId);
            issue.setIsCredible(false);
            DAO.save(issue);
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "cannot update msIssue with id [%s] [%s] %s", _issueId, e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public void setIssueId(long issueId) {

        _issueId = issueId;
    }


    public long getIssueId() {

        return _issueId;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }
}