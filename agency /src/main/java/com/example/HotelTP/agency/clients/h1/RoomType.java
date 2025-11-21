
package com.example.HotelTP.agency.clients.h1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoomType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RoomType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SINGLE"/>
 *     &lt;enumeration value="DOUBLE"/>
 *     &lt;enumeration value="TWIN"/>
 *     &lt;enumeration value="FAMILY"/>
 *     &lt;enumeration value="SUITE"/>
 *     &lt;enumeration value="DELUXE"/>
 *     &lt;enumeration value="PRESIDENTIAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RoomType")
@XmlEnum
public enum RoomType {

    SINGLE,
    DOUBLE,
    TWIN,
    FAMILY,
    SUITE,
    DELUXE,
    PRESIDENTIAL;

    public String value() {
        return name();
    }

    public static RoomType fromValue(String v) {
        return valueOf(v);
    }

}
