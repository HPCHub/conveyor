/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.ms;


import java.util.List;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.ProteomFile;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListFilesAction extends ActionSupport {


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public List<String> getFiles() {

        return DAO.getCriteria(ProteomFile.class)
            .add(Restrictions.eq("type", "metabolite"))
            .addOrder(Order.desc("fileId"))
        .list();
    }
}