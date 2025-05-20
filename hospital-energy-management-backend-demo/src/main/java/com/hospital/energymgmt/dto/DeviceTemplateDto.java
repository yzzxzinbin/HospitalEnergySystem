package com.hospital.energymgmt.dto;

public class DeviceTemplateDto {

    private Long id;
    private String templateName;
    private String manufacturer;
    private String modelIdentifier;
    private String deviceCategory;
    private String description;
    private String specificationsJson; // Consider if you want to parse this in DTO or handle as string
    private Double nominalPowerWatts;
    private Double nominalWaterConsumptionLph;
    private Double nominalGasConsumptionM3ph;
    private Integer estimatedLifespanYears;
    private String imageUrl;

    // Constructors
    public DeviceTemplateDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelIdentifier() {
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecificationsJson() {
        return specificationsJson;
    }

    public void setSpecificationsJson(String specificationsJson) {
        this.specificationsJson = specificationsJson;
    }

    public Double getNominalPowerWatts() {
        return nominalPowerWatts;
    }

    public void setNominalPowerWatts(Double nominalPowerWatts) {
        this.nominalPowerWatts = nominalPowerWatts;
    }

    public Double getNominalWaterConsumptionLph() {
        return nominalWaterConsumptionLph;
    }

    public void setNominalWaterConsumptionLph(Double nominalWaterConsumptionLph) {
        this.nominalWaterConsumptionLph = nominalWaterConsumptionLph;
    }

    public Double getNominalGasConsumptionM3ph() {
        return nominalGasConsumptionM3ph;
    }

    public void setNominalGasConsumptionM3ph(Double nominalGasConsumptionM3ph) {
        this.nominalGasConsumptionM3ph = nominalGasConsumptionM3ph;
    }

    public Integer getEstimatedLifespanYears() {
        return estimatedLifespanYears;
    }

    public void setEstimatedLifespanYears(Integer estimatedLifespanYears) {
        this.estimatedLifespanYears = estimatedLifespanYears;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
