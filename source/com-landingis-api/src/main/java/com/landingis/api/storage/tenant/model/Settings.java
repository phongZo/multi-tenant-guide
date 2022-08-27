package com.landingis.api.storage.tenant.model;
import com.landingis.api.storage.master.model.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "settings")
public class Settings extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;       // close time,open time

    @Column(name = "settings_key", unique = true)
    private String key;        // openTime

    @Column(name = "settings_value")
    private String value;

    @Column(name = "description")
    private String description;

    @Column(name = "settings_group")
    private String group;        // General: 0::general , admin : 1::admin, customer: 2::store
    //split("::") --> aray --> phần tử 0 phải số , ptu sau là chuỗi

    private Integer groupId; // determine settings for customer site or admin site


    @Column(name = "editable")
    private boolean editable;

    private Integer kind;    //phân chia kiểu dữ liệu value
}
