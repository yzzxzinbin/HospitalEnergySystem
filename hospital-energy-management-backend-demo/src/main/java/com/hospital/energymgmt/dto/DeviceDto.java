package com.hospital.energymgmt.dto;

import java.time.LocalDate;

public class DeviceDto {

    private Long id;
    private String name;
    private LocalDate installationDate;
    private String status;
    private Long roomId; // To send/receive Room ID
    private String roomNumber; // To display Room Number (optional, for convenience)
    private Long deviceTemplateId; // To send/receive DeviceTemplate ID
    private String deviceTemplateName; // To display DeviceTemplate Name (optional, for convenience)

    // Constructors
    public DeviceDto() {
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getDeviceTemplateId() {
        return deviceTemplateId;
    }

    public void setDeviceTemplateId(Long deviceTemplateId) {
        this.deviceTemplateId = deviceTemplateId;
    }

    public String getDeviceTemplateName() {
        return deviceTemplateName;
    }

    public void setDeviceTemplateName(String deviceTemplateName) {
        this.deviceTemplateName = deviceTemplateName;
    }
}
