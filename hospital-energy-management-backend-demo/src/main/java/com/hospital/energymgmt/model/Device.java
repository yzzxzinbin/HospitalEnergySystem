package com.hospital.energymgmt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 设备名称，例如 "呼吸机", "MRI扫描仪", "空调", "LED照明面板"
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 安装日期
     */
    @Column(name = "installation_date")
    private LocalDate installationDate;

    /**
     * 设备状态，例如 "Operational", "Maintenance", "Offline"
     */
    @Column(name = "status")
    private String status;

    /**
     * 所属房间ID, 外键关联到rooms表, 房间删除时此设备room_id置为NULL
     */
    @Column(name = "room_id")
    private Long roomId; // We'll likely map this to a Room entity later

    /**
     * 关联的设备模板ID，外键关联到device_templates表
     */
    @Column(name = "device_template_id")
    private Long deviceTemplateId; // We'll likely map this to a DeviceTemplate entity later

    // Constructors
    public Device() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getDeviceTemplateId() {
        return deviceTemplateId;
    }

    public void setDeviceTemplateId(Long deviceTemplateId) {
        this.deviceTemplateId = deviceTemplateId;
    }

    // toString() method (optional, but good for debugging)
    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", installationDate=" + installationDate +
                ", status='" + status + '\'' +
                ", roomId=" + roomId +
                ", deviceTemplateId=" + deviceTemplateId +
                '}';
    }
}
