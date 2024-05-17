package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbhelprequests")
public class UCSBHelpRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "REQUESTEREMAIL")
    private String requesterEmail;

    @Column(name = "TEAMID")
    private String teamId;

    @Column(name = "TABLEORBREAKOUTROOM")
    private String tableOrBreakoutRoom;

    @Column(name = "REQUESTTIME")
    private LocalDateTime requestTime;

    @Column(name = "EXPLANATION")
    private String explanation;

    @Column(name = "SOLVED")
    private boolean solved;
}
