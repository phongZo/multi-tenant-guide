package com.landingis.api.storage.master.criteria;

import com.landingis.api.storage.master.model.Customer;
import com.landingis.api.storage.master.model.Device;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceCriteria {
    private Long id;
    private String name;
    private Integer type;
    private Long customerId;
    private Integer status;
    private Long parentId;
    private Boolean onlyParent;
    private String posId;

    public Specification<Device> getSpecification() {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if(getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if(getCustomerId() != null) {
                    Join<Device, Customer> joinCustomer = root.join("customer", JoinType.INNER);
                    predicates.add(cb.equal(joinCustomer.get("id"), getCustomerId()));
                }

                if(getParentId() != null) {
                    Join<Device, Device> joinParent = root.join("parent", JoinType.INNER);
                    predicates.add(cb.equal(joinParent.get("id"), getParentId()));
                }

                if(getOnlyParent() == Boolean.TRUE) {
                    predicates.add(cb.isNull(root.get("parent")));
                }

                if(getType() != null) {
                    predicates.add(cb.equal(root.get("type"), getType()));
                }

                if(!StringUtils.isEmpty(getName())) {
                    String name = URLDecoder.decode(getName(), StandardCharsets.UTF_8);
                    predicates.add(cb.or(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"),
                            cb.equal(root.get("posId"), name),
                            cb.equal(root.get("sessionId"), name)));
                }

                if(getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if(getPosId() != null) {
                    predicates.add(cb.equal(root.get("posId"), getPosId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
