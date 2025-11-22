
package com.example.HotelSOAP.comparator.clients.a1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for searchAvailability complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="searchAvailability">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="maxPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="stars" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="persons" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchAvailability", propOrder = {
    "city",
    "startDate",
    "endDate",
    "minPrice",
    "maxPrice",
    "stars",
    "persons"
})
public class SearchAvailability {

    protected String city;
    protected String startDate;
    protected String endDate;
    protected Double minPrice;
    protected Double maxPrice;
    protected Integer stars;
    protected int persons;

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the minPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinPrice() {
        return minPrice;
    }

    /**
     * Sets the value of the minPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinPrice(Double value) {
        this.minPrice = value;
    }

    /**
     * Gets the value of the maxPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMaxPrice() {
        return maxPrice;
    }

    /**
     * Sets the value of the maxPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMaxPrice(Double value) {
        this.maxPrice = value;
    }

    /**
     * Gets the value of the stars property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStars() {
        return stars;
    }

    /**
     * Sets the value of the stars property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStars(Integer value) {
        this.stars = value;
    }

    /**
     * Gets the value of the persons property.
     * 
     */
    public int getPersons() {
        return persons;
    }

    /**
     * Sets the value of the persons property.
     * 
     */
    public void setPersons(int value) {
        this.persons = value;
    }

}
