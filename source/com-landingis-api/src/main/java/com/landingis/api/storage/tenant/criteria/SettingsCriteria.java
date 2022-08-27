package com.landingis.api.storage.tenant.criteria;

import com.landingis.api.storage.tenant.model.Settings;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class SettingsCriteria {
    private Long id;
    private String name;
    private String key;
    private String group;
    private Integer groupId;
    private Integer kind;
    private Integer status;
    public Specification<Settings> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Settings> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if(getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                if(getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }
                if(!StringUtils.isEmpty(getName())){
                    predicates.add(cb.like(cb.lower(root.get("name")), "%"+getName().toLowerCase()+"%"));
                }
                if(!StringUtils.isEmpty(getKey())){
                    predicates.add(cb.like(cb.lower(root.get("key")), "%"+getKey().toLowerCase()+"%"));
                }
                if(!StringUtils.isEmpty(getGroup())){
                    predicates.add(cb.like(cb.lower(root.get("group")), "%"+getGroup().toLowerCase()+"%"));
                }
                if(getGroupId() != null) {
                    predicates.add(cb.equal(root.get("groupId"), getGroupId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
