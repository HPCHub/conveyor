/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListRunsAction {


    private String _query;
    private long _projectId;
    private long _runId;
    private int  _start = 0;
    private int  _limit = 25;
    private String _sortProperty;
    private String _sortDirection;


    public void setSort(String[] sorters) {

        JSONArray json = JSONArray.fromObject(sorters[0]);
        JSONObject sorter = json.getJSONObject(0);
        _sortProperty = (String) sorter.get("property");
        _sortDirection = (String) sorter.get("direction");
    }



    public void setProjectId(long projectId) {

        _projectId = projectId;
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

        return DAO.getCount(SeqsRun.class);
    }


    public List<SeqsRun> getRuns() {

        Criteria criteria = DAO.getCriteria(SeqsRun.class)
            .setFirstResult(_start)
            .setMaxResults(_limit);

        if (_projectId > 0) {
            criteria.add(Restrictions.eq("seqsProject.projectId", _projectId));
        }

        if (_runId > 0) {
            criteria.add(Restrictions.eq("runId", _runId));
        }

        if (_query != null) {
            criteria.add(Restrictions.ilike("name", _query, MatchMode.START));
        }

        if (_sortProperty != null && _sortDirection != null) {
            if (_sortDirection.equals("ASC")) {
                criteria.addOrder(Order.asc(_sortProperty));
            } else if (_sortDirection.equals("DESC")) {
                criteria.addOrder(Order.desc(_sortProperty));
            }
        } else {
            criteria.addOrder(Order.desc("createDate"));
        }

        return criteria.list();
    }


    public void setRunId(long runId) {

        _runId = runId;
    }


    public void setQuery(String query) {

        _query = query;
    }
}