package com.example.HotelTP.agency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IMPROVED VERSION - More explicit configuration
 * This version allows you to specify the exact service interface class in properties
 */
@Configuration
public class HotelPartnerConfig {

  @Bean
  public HotelClientRegistry hotelClientRegistry(HotelPartnersProperties properties) {
    return new HotelClientRegistry(properties);
  }

  @Component
  @ConfigurationProperties(prefix = "hotels")
  public static class HotelPartnersProperties {
    private List<HotelPartner> partners;

    public List<HotelPartner> getPartners() {
      return partners;
    }

    public void setPartners(List<HotelPartner> partners) {
      this.partners = partners;
    }

    public static class HotelPartner {
      private String id;
      private String name;
      private String wsdlUrl;
      private String serviceInterface; // NEW: explicit interface class name
      private String namespace = "http://service.server.HotelSOAP.example.com/";
      private String serviceName = "HotelServiceImplService";
      private String portName = "HotelServiceImplPort";
      private boolean enabled = true;

      // Getters and setters
      public String getId() { return id; }
      public void setId(String id) { this.id = id; }

      public String getName() { return name; }
      public void setName(String name) { this.name = name; }

      public String getWsdlUrl() { return wsdlUrl; }
      public void setWsdlUrl(String wsdlUrl) { this.wsdlUrl = wsdlUrl; }

      public String getServiceInterface() { return serviceInterface; }
      public void setServiceInterface(String serviceInterface) { this.serviceInterface = serviceInterface; }

      public String getNamespace() { return namespace; }
      public void setNamespace(String namespace) { this.namespace = namespace; }

      public String getServiceName() { return serviceName; }
      public void setServiceName(String serviceName) { this.serviceName = serviceName; }

      public String getPortName() { return portName; }
      public void setPortName(String portName) { this.portName = portName; }

      public boolean isEnabled() { return enabled; }
      public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
  }

  /**
   * Registry that manages all hotel SOAP clients dynamically
   */
  public static class HotelClientRegistry {
    private final Map<String, Object> hotelClients = new HashMap<>();
    private final Map<String, HotelPartnersProperties.HotelPartner> hotelConfigs = new HashMap<>();

    public HotelClientRegistry(HotelPartnersProperties properties) {
      if (properties.getPartners() != null) {
        for (HotelPartnersProperties.HotelPartner partner : properties.getPartners()) {
          if (partner.isEnabled()) {
            registerHotel(partner);
          }
        }
      }
      System.out.println("✅ Registered " + hotelClients.size() + " hotel partners");
    }

    private void registerHotel(HotelPartnersProperties.HotelPartner partner) {
      try {
        URL wsdlUrl = new URL(partner.getWsdlUrl());
        QName serviceName = new QName(partner.getNamespace(), partner.getServiceName());

        Service service = Service.create(wsdlUrl, serviceName);
        QName portName = new QName(partner.getNamespace(), partner.getPortName());

        // Try explicit service interface first
        Class<?> serviceInterface = null;

        if (partner.getServiceInterface() != null && !partner.getServiceInterface().isEmpty()) {
          // Option 1: Use explicitly configured interface
          try {
            serviceInterface = Class.forName(partner.getServiceInterface());
            System.out.println("  Using explicit interface: " + partner.getServiceInterface());
          } catch (ClassNotFoundException e) {
            System.err.println("  ⚠️ Explicit interface not found: " + partner.getServiceInterface());
          }
        }

        if (serviceInterface == null) {
          // Option 2: Try to infer from standard package naming
          String packageName = "com.example.HotelTP.agency.clients." + partner.getId().toLowerCase();
          String interfaceName = packageName + ".HotelService";
          try {
            serviceInterface = Class.forName(interfaceName);
            System.out.println("  Inferred interface: " + interfaceName);
          } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find SOAP client interface. " +
                    "Please generate stubs with: wsimport -keep -p " + packageName + " " +
                    partner.getWsdlUrl() +
                    " OR specify service-interface in properties");
          }
        }

        // Get the port with the correct interface type
        Object port = service.getPort(portName, serviceInterface);

        hotelClients.put(partner.getId(), port);
        hotelConfigs.put(partner.getId(), partner);

        System.out.println("✅ Registered hotel: " + partner.getId() +
                " (" + partner.getName() + ") at " + partner.getWsdlUrl());

      } catch (Exception e) {
        System.err.println("⚠️ Failed to register hotel " + partner.getId() + ": " + e.getMessage());
        e.printStackTrace();
      }
    }

    public Object getClient(String hotelId) {
      return hotelClients.get(hotelId);
    }

    public Map<String, Object> getAllClients() {
      return new HashMap<>(hotelClients);
    }

    public HotelPartnersProperties.HotelPartner getConfig(String hotelId) {
      return hotelConfigs.get(hotelId);
    }

    public boolean hasHotel(String hotelId) {
      return hotelClients.containsKey(hotelId);
    }
  }
}