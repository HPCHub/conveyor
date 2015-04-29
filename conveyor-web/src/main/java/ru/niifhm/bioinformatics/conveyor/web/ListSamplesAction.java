/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListSamplesAction {


    private int _start = 0;
    private int _limit = 25;
    private long _sampleId;
    private String _query;


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

        return DAO.getCount(SeqsSample.class);
    }


    public List<SeqsSample> getSamples() {

        Criteria criteria = DAO.getCriteria(SeqsSample.class)
            .addOrder(Order.desc("sampleId"))
            .setFirstResult(_start)
            .setMaxResults(_limit);

        if (_sampleId > 0) {
            criteria.add(Restrictions.eq("sampleId", _sampleId));
        }

        if (_query != null) {
            criteria.add(Restrictions.ilike("name", _query, MatchMode.START));
        }

        return criteria.list();
    }


    public void setQuery(String query) {

        _query = query;
    }


    public void setSampleId(long sampleId) {

        _sampleId = sampleId;
    }
}