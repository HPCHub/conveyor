/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.ms;


import java.io.InputStream;
import java.util.Arrays;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.MsIssueImage;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class LoadSpectreImageAction extends ActionSupport {


    private long _issueId;
    private InputStream _inputStream;
    private final static String CONTENT_TYPE = "image/png";


    public String getContentType() {

        return CONTENT_TYPE;
    }


    public void setIssueId(long issueId) {

        _issueId = issueId;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public InputStream getInputStream() {

        return _inputStream;
    }


    public String execute() {

        MsIssueImage issue = (MsIssueImage) DAO.getCriteria(MsIssueImage.class)
            .add(Restrictions.eq("issueId", _issueId))
        .uniqueResult();

        try {
            _inputStream = issue.getSpectre().getBinaryStream();
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot get spectre image input stream [SQLException] %s",
                e.getClass().getName(),
                e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }
}