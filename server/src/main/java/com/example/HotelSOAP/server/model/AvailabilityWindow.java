package com.example.HotelSOAP.server.model;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class AvailabilityWindow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "room_id")
  private Room room;

  @Column(name = "start_date")
  private LocalDate start;

  @Column(name = "end_date")
  private LocalDate end;

  private int units;   // number of rooms available in that window

  public AvailabilityWindow() {}

  public AvailabilityWindow(Room room, LocalDate start, LocalDate end, int units) {
    this.room = room;
    this.start = start;
    this.end = end;
    this.units = units;
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Room getRoom() { return room; }
  public void setRoom(Room room) { this.room = room; }

  public LocalDate getStart() { return start; }
  public void setStart(LocalDate start) { this.start = start; }

  public LocalDate getEnd() { return end; }
  public void setEnd(LocalDate end) { this.end = end; }

  public int getUnits() { return units; }
  public void setUnits(int units) { this.units = units; }
}

