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
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepository {
    private SessionFactory sessionFactory;

    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }

    public Optional<Role> findById(long roleId){
        String hql = "select role from Role role where role.roleId = :id";
        Query<Role> query = getSession().createQuery(hql, Role.class).setParameter("id", roleId);
        return query.uniqueResultOptional();
    }

    public Optional<Role> findByNotIdAndName(long id, String name){
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> role = cq.from(Role.class);

        cq.where(cb.notEqual(role.get("roleId"), id));
        cq.where(cb.equal(role.get("name"), name));

        Query<Role> query = getSession().createQuery(cq);
        return query.uniqueResultOptional();
    }

    public Optional<Role> findByNotIdAndName1(long id, String name){
        String hql = "select role from Role role where role.roleId <> :id AND name = :name";
        Query<Role> query = getSession().createQuery(hql, Role.class).setParameter("id", id)
                .setParameter("name", name);
        return query.uniqueResultOptional();
    }



}
