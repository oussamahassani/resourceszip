package crm.chifco.com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "zones")
@EntityListeners(AuditingEntityListener.class)
public class Zone implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long zoneId;

  @Column(length = 100, nullable = false, unique = true)
  private String code;

  @Column(length = 150, nullable = false, unique = true)
  private String nom;

  @Column(length = 255)
  private String description;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "zone_gouvernorats", joinColumns = @JoinColumn(name = "zone_id"),
      inverseJoinColumns = @JoinColumn(name = "gouvernorat_id"))
  private List<Gouvernorat> gouvernorats = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "zone_users", joinColumns = @JoinColumn(name = "zone_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> utilisateurs = new ArrayList<>();

  @Column(name = "is_active")
  private boolean active = true;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  public Zone() {}

  public Zone(String code, String nom) {
    this.code = code;
    this.nom = nom;
  }

  public Zone(String code, String nom, String description) {
    this.code = code;
    this.nom = nom;
    this.description = description;
  }

  public Long getZoneId() {
    return zoneId;
  }

  public void setZoneId(Long zoneId) {
    this.zoneId = zoneId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Gouvernorat> getGouvernorats() {
    return gouvernorats;
  }

  public void setGouvernorats(List<Gouvernorat> gouvernorats) {
    this.gouvernorats = gouvernorats;
  }

  public List<User> getUtilisateurs() {
    return utilisateurs;
  }

  public void setUtilisateurs(List<User> utilisateurs) {
    this.utilisateurs = utilisateurs;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  @Override
  public String toString() {
    return "Zone{" + "zoneId=" + zoneId + ", code='" + code + '\'' + ", nom='" + nom + '\''
        + ", description='" + description + '\'' + ", active=" + active + '}';
  }
}
