package com.example.HotelSOAP.comparator.config;

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


@Configuration
public class ComparatorConfig {

  @Bean
  public AgencyClientRegistry agencyClientRegistry(AgencyPartnersProperties properties) {
    return new AgencyClientRegistry(properties);
  }

  @Component
  @ConfigurationProperties(prefix = "agencies")
  public static class AgencyPartnersProperties {
    private List<AgencyPartner> partners;

    public List<AgencyPartner> getPartners() {
      return partners;
    }

    public void setPartners(List<AgencyPartner> partners) {
      this.partners = partners;
    }

    public static class AgencyPartner {
      private String id;
      private String name;
      private String wsdlUrl;
      private String serviceInterface;
      private String namespace = "http://service.agency.HotelTP.example.com/";
      private String serviceName = "AgencyServiceImplService";
      private String portName = "AgencyServiceImplPort";
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


  public static class AgencyClientRegistry {
    private final Map<String, Object> agencyClients = new HashMap<>();
    private final Map<String, AgencyPartnersProperties.AgencyPartner> agencyConfigs = new HashMap<>();

    public AgencyClientRegistry(AgencyPartnersProperties properties) {
      if (properties.getPartners() != null) {
        for (AgencyPartnersProperties.AgencyPartner partner : properties.getPartners()) {
          if (partner.isEnabled()) {
            registerAgency(partner);
          }
        }
      }
      System.out.println("✅ [Comparator] Registered " + agencyClients.size() + " agency partners");
    }

    private void registerAgency(AgencyPartnersProperties.AgencyPartner partner) {
      try {
        URL wsdlUrl = new URL(partner.getWsdlUrl());
        QName serviceName = new QName(partner.getNamespace(), partner.getServiceName());

        Service service = Service.create(wsdlUrl, serviceName);
        QName portName = new QName(partner.getNamespace(), partner.getPortName());

        Class<?> serviceInterface = null;

        if (partner.getServiceInterface() != null && !partner.getServiceInterface().isEmpty()) {
          try {
            serviceInterface = Class.forName(partner.getServiceInterface());
            System.out.println("  [Comparator] Using explicit interface: " + partner.getServiceInterface());
          } catch (ClassNotFoundException e) {
            System.err.println("  ⚠️ Explicit interface not found: " + partner.getServiceInterface());
          }
        }

        if (serviceInterface == null) {

          String packageName = "com.example.HotelSOAP.comparator.clients." + partner.getId().toLowerCase();
          String interfaceName = packageName + ".AgencyService";
          try {
            serviceInterface = Class.forName(interfaceName);
            System.out.println("  [Comparator] Inferred interface: " + interfaceName);
          } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find SOAP client interface for agency " + partner.getId() +
                    ". Please generate stubs or specify service-interface in properties");
          }
        }


        Object port = service.getPort(portName, serviceInterface);

        agencyClients.put(partner.getId(), port);
        agencyConfigs.put(partner.getId(), partner);

        System.out.println("✅ [Comparator] Registered agency: " + partner.getId() +
                " (" + partner.getName() + ") at " + partner.getWsdlUrl());

      } catch (Exception e) {
        System.err.println("⚠️ Failed to register agency " + partner.getId() + ": " + e.getMessage());
        e.printStackTrace();
      }
    }

    public Object getClient(String agencyId) {
      return agencyClients.get(agencyId);
    }

    public Map<String, Object> getAllClients() {
      return new HashMap<>(agencyClients);
    }

    public AgencyPartnersProperties.AgencyPartner getConfig(String agencyId) {
      return agencyConfigs.get(agencyId);
    }

    public boolean hasAgency(String agencyId) {
      return agencyClients.containsKey(agencyId);
    }

    public String getAgencyName(String agencyId) {
      AgencyPartnersProperties.AgencyPartner config = agencyConfigs.get(agencyId);
      return config != null ? config.getName() : agencyId;
    }
  }
}