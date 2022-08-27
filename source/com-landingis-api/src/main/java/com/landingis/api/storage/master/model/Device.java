package com.landingis.api.storage.master.model;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = TablePrefix.PREFIX_TABLE+"device")
public class Device extends Auditable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "pos_id")
    private String posId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "new_session_id")
    private String newSessionId;

    private Integer type;
    private Integer platform;

    @Column(name = "enabled_remview")
    private Boolean enabledRemview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Device parent;

    @Column(name = "time_last_used")
    private Date timeLastUsed;

    @Column(name = "isLogin")
    private Boolean isLogin;

    @Column(name = "time_last_online")
    private Date timeLastOnline;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "ext_date")
    private LocalDate extDate;

    @Column(name = "setting", columnDefinition = "LONGTEXT")
    private String setting;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "lat_long_limit")
    private String latLongLimit;

    @Column(name = "distance_limit")
    private Integer distanceLimit;

    @Column(name = "isDemo")
    private Boolean isDemo = false;

    @Column(name = "permission")
    private Integer permission;
}
