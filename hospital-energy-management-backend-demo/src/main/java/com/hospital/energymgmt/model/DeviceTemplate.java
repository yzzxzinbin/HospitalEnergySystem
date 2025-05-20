package com.hospital.energymgmt.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device_templates")
public class DeviceTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model_identifier", nullable = false)
    private String modelIdentifier;

    @Column(name = "device_category")
    private String deviceCategory;

    @Lob // For TEXT type
    @Column(name = "description")
    private String description;

    @Lob // For TEXT type
    @Column(name = "specifications_json")
    private String specificationsJson;

    @Column(name = "nominal_power_watts")
    private Double nominalPowerWatts;

    @Column(name = "nominal_water_consumption_lph")
    private Double nominalWaterConsumptionLph;

    @Column(name = "nominal_gas_consumption_m3ph")
    private Double nominalGasConsumptionM3ph;

    @Column(name = "estimated_lifespan_years")
    private Integer estimatedLifespanYears;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public DeviceTemplate() {
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

    // equals, hashCode, toString (optional but recommended)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceTemplate that = (DeviceTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeviceTemplate{" +
                "id=" + id +
                ", templateName='" + templateName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", modelIdentifier='" + modelIdentifier + '\'' +
                ", deviceCategory='" + deviceCategory + '\'' +
                '}';
    }
}
