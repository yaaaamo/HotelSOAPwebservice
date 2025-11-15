package com.example.HotelSOAP.server.model;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "RoomType")
@XmlEnum
public enum RoomType {
  SINGLE(1.00),
  DOUBLE(1.10),
  TWIN(1.10),
  FAMILY(1.30),
  SUITE(1.60),
  DELUXE(1.80),
  PRESIDENTIAL(2.50);

  private final double factor;
  RoomType(double factor) { this.factor = factor; }
  public double factor() { return factor; }
}


