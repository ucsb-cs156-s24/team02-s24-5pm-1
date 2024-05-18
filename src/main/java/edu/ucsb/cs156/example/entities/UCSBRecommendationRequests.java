package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbrecommendationrequests")
public class UCSBRecommendationRequests {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;
  
  @Column(name = "REQUESTEREMAIL")
  private String requesterEmail;
  @Column(name = "PROFESSOREMAIL")
  private String professorEmail;
  @Column(name = "EXPLANATION")
  private String explanation;
  @Column(name = "DATEREQUESTED")
  private LocalDateTime dateRequested;
  @Column(name = "DATENEEDED")
  private LocalDateTime dateNeeded;
  @Column(name = "DONE")
  private boolean done;
}