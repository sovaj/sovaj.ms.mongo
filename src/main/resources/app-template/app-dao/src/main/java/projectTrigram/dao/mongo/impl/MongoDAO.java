#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.mongo.impl;

import com.mongodb.WriteResult;
import java.util.Date;
import java.util.List;
import ${package}.domain.BusinessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 * @author Mickael Dubois
 * @param <T>
 */
public abstract class MongoDAO<T extends BusinessObject> {

    @Autowired
    protected MongoOperations mongoOps;

    abstract public String getCollection();

    abstract public Class<T> getClazz();

    public void add(T p) {
        p.setCreationDate(new Date());
        p.setUpdatedDate(new Date());
        this.mongoOps.insert(p, getCollection());
    }

    public T getById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return this.mongoOps.findOne(query, getClazz(), getCollection());
    }

    public void update(T p) {   
        p.setUpdatedDate(new Date());
        this.mongoOps.save(p, getCollection());
    }

    public int deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        WriteResult result = this.mongoOps.remove(query, getClazz(), getCollection());
        return result.getN();
    }

    public List<T> list() {
        return this.mongoOps.findAll(getClazz(), getCollection());
    }

}
