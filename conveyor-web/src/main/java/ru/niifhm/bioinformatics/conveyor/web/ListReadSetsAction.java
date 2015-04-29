/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRunsFile;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListReadSetsAction {


    private int    _start  = 0;
    private int    _limit  = 25;
    private String _name;
    private long   _runId;
    private String _sortProperty;
    private String _sortDirection;
    private Logger _logger = Logger.getLogger(ListReadSetsAction.class);


    public void setSort(String[] sorters) {

        JSONArray json = JSONArray.fromObject(sorters[0]);
        JSONObject sorter = json.getJSONObject(0);
        _sortProperty = (String) sorter.get("property");
        _sortDirection = (String) sorter.get("direction");
    }


    public void setName(String name) {

        this._name = name;
    }


    public void setRunId(long runId) {

        _runId = runId;
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

        try {
            List<Long> fileIds = new ArrayList<Long>();
            if (_runId > 0) {
                fileIds = DAO.getCriteria(SeqsRunsFile.class)
                    .setProjection(Projections.property("fileId"))
                    .add(Restrictions.eq("seqsRun.runId", _runId))
                    .list();
            }

            if (_runId > 0 && fileIds.size() == 0) {
                return 0;
            }

            Criteria criteria = DAO.getCriteria(SeqsReadSet.class)
                .setProjection(Projections.rowCount())
                .createAlias("seqsFile", "seqsFile", CriteriaSpecification.LEFT_JOIN)
                .createAlias("seqsSample", "seqsSample", CriteriaSpecification.LEFT_JOIN);

            if (_runId > 0) {
                criteria.add(Restrictions.in("seqsFile.fileId", fileIds));
            }

            if (_name != null) {
                criteria.add(Restrictions.ilike("seqsFile.name", String.format(".%s", _name), MatchMode.ANYWHERE));
            }

            return (Long) criteria.uniqueResult();
        } catch (Exception e) {
            _logger.error(String.format("Cannot get count [%s] %s", e.getClass().getName(), e.getMessage()));
            return 0;
        }
    }


    public List<SeqsReadSet> getFiles() {

        try {
            List<Long> fileIds = new ArrayList<Long>();
            if (_runId > 0) {
                fileIds = DAO.getCriteria(SeqsRunsFile.class)
                    .setProjection(Projections.property("fileId"))
                    .add(Restrictions.eq("seqsRun.runId", _runId))
                    .list();
            }

            if (_runId > 0 && fileIds.size() == 0) {
                return new ArrayList<SeqsReadSet>();
            }

            Criteria criteria = DAO.getCriteria(SeqsReadSet.class)
                .createAlias("seqsFile", "seqsFile", CriteriaSpecification.LEFT_JOIN)
                .createAlias("seqsSample", "seqsSample", CriteriaSpecification.LEFT_JOIN)
                .setFirstResult(_start)
                .setMaxResults(_limit);

            if (_runId > 0) {
                criteria.add(Restrictions.in("seqsFile.fileId", fileIds));
            }

            if (_sortProperty != null && _sortDirection != null) {
                if (_sortDirection.equals("ASC")) {
                    criteria.addOrder(Order.asc(_sortProperty));
                } else if (_sortDirection.equals("DESC")) {
                    criteria.addOrder(Order.desc(_sortProperty));
                }
            }

            if (_name != null) {
                criteria.add(Restrictions.ilike("seqsFile.name", String.format("%s", _name), MatchMode.ANYWHERE));
            }

            return criteria.list();
        } catch (Exception e) {
            _logger.error(String.format("Cannot load files list [%s] %s", e.getClass().getName(), e.getMessage()));
            return new ArrayList<SeqsReadSet>();
        }
    }
}