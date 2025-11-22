
package com.example.HotelSOAP.comparator.clients.a2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.example.HotelSOAP.comparator.clients.a2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SearchAvailability_QNAME = new QName("http://service.agency.HotelTP.example.com/", "searchAvailability");
    private final static QName _MakeReservation_QNAME = new QName("http://service.agency.HotelTP.example.com/", "makeReservation");
    private final static QName _MakeReservationResponse_QNAME = new QName("http://service.agency.HotelTP.example.com/", "makeReservationResponse");
    private final static QName _SearchAvailabilityResponse_QNAME = new QName("http://service.agency.HotelTP.example.com/", "searchAvailabilityResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.example.HotelSOAP.comparator.clients.a2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MakeReservation }
     * 
     */
    public MakeReservation createMakeReservation() {
        return new MakeReservation();
    }

    /**
     * Create an instance of {@link SearchAvailability }
     * 
     */
    public SearchAvailability createSearchAvailability() {
        return new SearchAvailability();
    }

    /**
     * Create an instance of {@link SearchAvailabilityResponse }
     * 
     */
    public SearchAvailabilityResponse createSearchAvailabilityResponse() {
        return new SearchAvailabilityResponse();
    }

    /**
     * Create an instance of {@link MakeReservationResponse }
     * 
     */
    public MakeReservationResponse createMakeReservationResponse() {
        return new MakeReservationResponse();
    }

    /**
     * Create an instance of {@link AvailabilityOffer }
     * 
     */
    public AvailabilityOffer createAvailabilityOffer() {
        return new AvailabilityOffer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchAvailability }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.agency.HotelTP.example.com/", name = "searchAvailability")
    public JAXBElement<SearchAvailability> createSearchAvailability(SearchAvailability value) {
        return new JAXBElement<SearchAvailability>(_SearchAvailability_QNAME, SearchAvailability.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeReservation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.agency.HotelTP.example.com/", name = "makeReservation")
    public JAXBElement<MakeReservation> createMakeReservation(MakeReservation value) {
        return new JAXBElement<MakeReservation>(_MakeReservation_QNAME, MakeReservation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MakeReservationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.agency.HotelTP.example.com/", name = "makeReservationResponse")
    public JAXBElement<MakeReservationResponse> createMakeReservationResponse(MakeReservationResponse value) {
        return new JAXBElement<MakeReservationResponse>(_MakeReservationResponse_QNAME, MakeReservationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchAvailabilityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.agency.HotelTP.example.com/", name = "searchAvailabilityResponse")
    public JAXBElement<SearchAvailabilityResponse> createSearchAvailabilityResponse(SearchAvailabilityResponse value) {
        return new JAXBElement<SearchAvailabilityResponse>(_SearchAvailabilityResponse_QNAME, SearchAvailabilityResponse.class, null, value);
    }

}
