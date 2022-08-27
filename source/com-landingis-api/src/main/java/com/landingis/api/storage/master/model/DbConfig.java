package com.landingis.api.storage.master.model;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = TablePrefix.PREFIX_TABLE+"db_config")
@Data
public class DbConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;

    @Column(name = "driver_class_name")
    private String driverClassName;
    private boolean initialize;

    @OneToOne(fetch = FetchType.EAGER)
    private Device device;
}
