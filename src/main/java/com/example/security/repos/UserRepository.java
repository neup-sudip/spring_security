package com.example.security.repos;

import com.example.security.entity.Customer;
import com.example.security.entity.Role;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository{

    private SessionFactory sessionFactory;

    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }

    public List<Customer> findAll(){
        String hql = "select customer from Customer customer sort by id ASC";
        Query<Customer> query = getSession().createQuery(hql, Customer.class);
        return query.getResultList();
    }

    public Optional<Customer> findById(long id){
        String hql = "select customer from Customer customer where customer.id = :id";
        Query<Customer> query = getSession().createQuery(hql, Customer.class).setParameter("id", id);
        return query.uniqueResultOptional();
    }

    public Optional<Customer> findByUsername(String username){
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> customer = cq.from(Customer.class);

        cq.where(cb.equal(customer.get("username"), username));

        Query<Customer> query = getSession().createQuery(cq);

        return query.uniqueResultOptional();
    }

    public Customer findByNotIdAndUsername(long id, String username){
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> customer = cq.from(Customer.class);

        cq.where(cb.notEqual(customer.get("id"), id));
        cq.where(cb.equal(customer.get("username"), username));

        Query<Customer> query = getSession().createQuery(cq);

        return query.uniqueResult();
    }

    public Customer findByUsernameAndPassword(String username, String password){
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> customer = cq.from(Customer.class);

        cq.where(cb.equal(customer.get("username"), username));
        cq.where(cb.notEqual(customer.get("password"), password));

        Query<Customer> query = getSession().createQuery(cq);

        return query.uniqueResult();
    }

    public Customer save(Customer customer){
        getSession().persist(customer);
        return customer;
    }

}
