/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.ms;


import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.MsIssue;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListPeaksAction extends ActionSupport {


    private int    _start = 0;
    private int    _limit = 25;
    private String _sortProperty;
    private String _sortDirection;


    public void setSort(String[] sorters) {

        JSONArray json = JSONArray.fromObject(sorters[0]);
        JSONObject sorter = json.getJSONObject(0);
        _sortProperty = (String) sorter.get("property");
        _sortDirection = (String) sorter.get("direction");
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public void setLimit(int limit) {

        _limit = limit;
    }


    public void setStart(int start) {

        _start = start;
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public long getTotalCount() {

        return (Long) DAO.getCriteria(MsIssue.class)
            .setProjection(Projections.rowCount())
            .createAlias("msIon", "msIon", CriteriaSpecification.LEFT_JOIN)
            .add(Restrictions.eq("isCredible", Boolean.TRUE))
        .uniqueResult();
    }


    public List<SeqsRun> getMsIons() {

        Criteria criteria = DAO.getCriteria(MsIssue.class)
            .createAlias("msIon", "msIon", CriteriaSpecification.LEFT_JOIN)
            .add(Restrictions.eq("isCredible", Boolean.TRUE))
            .setFirstResult(_start)
        .setMaxResults(_limit);

        if (_sortProperty != null && _sortDirection != null) {
            if (_sortDirection.equals("ASC")) {
                criteria.addOrder(Order.asc(_sortProperty));
            } else if (_sortDirection.equals("DESC")) {
                criteria.addOrder(Order.desc(_sortProperty));
            }
        } else {
            criteria.addOrder(Order.desc("issueId"));
        }

        return criteria.list();
    }
}