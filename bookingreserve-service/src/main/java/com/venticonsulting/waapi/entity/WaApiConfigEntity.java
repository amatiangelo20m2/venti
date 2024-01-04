package com.venticonsulting.waapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity(name = "WaApiConfig")
@Table(name = "WA_API_CONF",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"waapi_conf_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class WaApiConfigEntity implements Serializable {
    @Id
    @SequenceGenerator(
            name = "waapi_conf_id",
            sequenceName = "waapi_conf_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "waapi_conf_id"
    )
    @Column(
            name = "waapi_conf_id",
            updatable = false
    )
    private long waapiConfId;
    private String instanceId;
    private String displayName;
    private String formattedNumber;
    private String profilePicUrl;

    @Column(name = "last_qr_code", columnDefinition = "TEXT")
    private String lastQrCode;
    private String owner;
    private Date creationDate;
    private Date updateDate;
    private String instanceStatus;
    private String message;
    private String explanation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_conf_id")
    private RestaurantConfiguration restaurantConfiguration;

}
