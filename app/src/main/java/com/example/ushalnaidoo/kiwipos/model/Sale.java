package com.example.ushalnaidoo.kiwipos.model;

import com.example.ushalnaidoo.kiwipos.enums.SaleStatus;

public class Sale {
  private String id;
  private String notes;
  private Double amount;
  private Boolean takeAway;
  private SaleStatus status;
  private String time;

  public Sale(String id, String time, String notes, Double amount, Boolean takeAway, SaleStatus status) {
    this.id = id;
    this.notes = notes;
    this.amount = amount;
    this.takeAway = takeAway;
    this.status = status;
    this.time = time;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Boolean getTakeAway() {
    return takeAway;
  }

  public void setTakeAway(Boolean takeAway) {
    this.takeAway = takeAway;
  }

  public String getTime() {
    return time;
  }

  public SaleStatus getStatus() {
    return status;
  }

  public void setStatus(SaleStatus status) {
    this.status = status;
  }
}
