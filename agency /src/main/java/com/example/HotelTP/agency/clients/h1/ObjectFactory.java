
package com.example.HotelTP.agency.clients.h1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.example.HotelTP.agency.clients.h1 package. 
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

    private final static QName _GetReservationsForAgencysResponse_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "getReservationsForAgencysResponse");
    private final static QName _BookResponse_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "bookResponse");
    private final static QName _Book_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "book");
    private final static QName _CheckAvailabilityResponse_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "checkAvailabilityResponse");
    private final static QName _CheckAvailability_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "checkAvailability");
    private final static QName _GetReservationsForAgencys_QNAME = new QName("http://service.server.HotelSOAP.example.com/", "getReservationsForAgencys");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.example.HotelTP.agency.clients.h1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetReservationsForAgencysResponse }
     * 
     */
    public GetReservationsForAgencysResponse createGetReservationsForAgencysResponse() {
        return new GetReservationsForAgencysResponse();
    }

    /**
     * Create an instance of {@link BookResponse }
     * 
     */
    public BookResponse createBookResponse() {
        return new BookResponse();
    }

    /**
     * Create an instance of {@link Book }
     * 
     */
    public Book createBook() {
        return new Book();
    }

    /**
     * Create an instance of {@link CheckAvailabilityResponse }
     * 
     */
    public CheckAvailabilityResponse createCheckAvailabilityResponse() {
        return new CheckAvailabilityResponse();
    }

    /**
     * Create an instance of {@link CheckAvailability }
     * 
     */
    public CheckAvailability createCheckAvailability() {
        return new CheckAvailability();
    }

    /**
     * Create an instance of {@link GetReservationsForAgencys }
     * 
     */
    public GetReservationsForAgencys createGetReservationsForAgencys() {
        return new GetReservationsForAgencys();
    }

    /**
     * Create an instance of {@link Reservation }
     * 
     */
    public Reservation createReservation() {
        return new Reservation();
    }

    /**
     * Create an instance of {@link AvailabilityOffer }
     * 
     */
    public AvailabilityOffer createAvailabilityOffer() {
        return new AvailabilityOffer();
    }

    /**
     * Create an instance of {@link Client }
     * 
     */
    public Client createClient() {
        return new Client();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReservationsForAgencysResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "getReservationsForAgencysResponse")
    public JAXBElement<GetReservationsForAgencysResponse> createGetReservationsForAgencysResponse(GetReservationsForAgencysResponse value) {
        return new JAXBElement<GetReservationsForAgencysResponse>(_GetReservationsForAgencysResponse_QNAME, GetReservationsForAgencysResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BookResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "bookResponse")
    public JAXBElement<BookResponse> createBookResponse(BookResponse value) {
        return new JAXBElement<BookResponse>(_BookResponse_QNAME, BookResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Book }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "book")
    public JAXBElement<Book> createBook(Book value) {
        return new JAXBElement<Book>(_Book_QNAME, Book.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckAvailabilityResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "checkAvailabilityResponse")
    public JAXBElement<CheckAvailabilityResponse> createCheckAvailabilityResponse(CheckAvailabilityResponse value) {
        return new JAXBElement<CheckAvailabilityResponse>(_CheckAvailabilityResponse_QNAME, CheckAvailabilityResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckAvailability }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "checkAvailability")
    public JAXBElement<CheckAvailability> createCheckAvailability(CheckAvailability value) {
        return new JAXBElement<CheckAvailability>(_CheckAvailability_QNAME, CheckAvailability.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReservationsForAgencys }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.server.HotelSOAP.example.com/", name = "getReservationsForAgencys")
    public JAXBElement<GetReservationsForAgencys> createGetReservationsForAgencys(GetReservationsForAgencys value) {
        return new JAXBElement<GetReservationsForAgencys>(_GetReservationsForAgencys_QNAME, GetReservationsForAgencys.class, null, value);
    }

}
