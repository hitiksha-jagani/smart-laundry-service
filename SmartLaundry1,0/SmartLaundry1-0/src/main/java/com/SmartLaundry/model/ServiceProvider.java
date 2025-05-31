package com.SmartLaundry.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ServiceProvider {
    @Id
    private int service_provider_id;
}
