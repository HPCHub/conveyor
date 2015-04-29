/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.xcms;


import java.util.List;
import org.hibernate.criterion.Order;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.XcmsDataSet;
import ru.niifhm.bioinformatics.util.Property;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListDataSetsAction extends ActionSupport {


    public long getTotalCount() {

        return DAO.getCount(XcmsDataSet.class);
    }


    public List<Property> getDataSets() {

        return DAO.getCriteria(XcmsDataSet.class)
            .addOrder(Order.desc("xcmsDataSetId"))
        .list();
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public String execute() {

        return Action.SUCCESS;
    }
}