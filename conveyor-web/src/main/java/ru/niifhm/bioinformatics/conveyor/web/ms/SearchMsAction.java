/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.ms;


import java.util.Collections;
import java.util.List;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.MsIon;
import ru.niifhm.bioinformatics.biodb.msdba.MsIssue;
import ru.niifhm.bioinformatics.biodb.msdba.XcmsIssue;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class SearchMsAction extends ActionSupport {


    private List<Long> _files;
    private long[] _xcmsIssuesIdList;
    private List<MsIssue> _msIssues;


    public void setIssues(long[] xcmsIssues) {

        _xcmsIssuesIdList = xcmsIssues;
    }


    public void setFiles(List<Long> files) {

        _files = files;
    }


    public long getTotalCount() {

        return _msIssues.size();
    }


    public List<MsIssue> getMsIons() {

        return _msIssues;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public String execute() {

        XcmsIssue xcmsIssue = (XcmsIssue) DAO.getCriteria(XcmsIssue.class)
            .add(Restrictions.eq("xcmsIssueId", _xcmsIssuesIdList[0]))
            .uniqueResult();

        if (xcmsIssue == null) {
            _msIssues = Collections.EMPTY_LIST;
            return Action.SUCCESS;
        }

        List<Long> idList = DAO.getCriteria(MsIon.class)
            .setProjection(Projections.property("ionId"))
            .add(Restrictions.in("proteomFile.fileId", _files))
            .add(Restrictions.between("retentionTime", xcmsIssue.getRtMin() * 60, xcmsIssue.getRtMax() * 60))
            .add(Restrictions.between("mass", xcmsIssue.getMzMin(), xcmsIssue.getMzMax()))
            .list();

        if (idList.size() == 0) {
            _msIssues = Collections.EMPTY_LIST;
            return Action.SUCCESS;
        }

        _msIssues = DAO.getCriteria(MsIssue.class)
            .add(Restrictions.in("msIon.ionId", idList))
            .list();

        return Action.SUCCESS;
    }
}