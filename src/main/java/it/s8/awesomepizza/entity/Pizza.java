package it.s8.awesomepizza.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pizza")
public class Pizza extends EntityInfo {

  @Id
  @Column(name = "pizza_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "pizza_id_seq", sequenceName = "pizza_id_seq", allocationSize = 1)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price")
  private BigDecimal price;

  @Version
  @Column(name = "version")
  private Integer version;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy hibernateproxy
            ? hibernateproxy.getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy hibernateproxy
            ? hibernateproxy.getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Pizza pizza = (Pizza) o;
    return getId() != null && Objects.equals(getId(), pizza.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hashCode(getId());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "("
        + "id = "
        + getId()
        + ", "
        + "name = "
        + getName()
        + ", "
        + "description = "
        + getDescription()
        + ", "
        + "version = "
        + getVersion()
        + ", "
        + "createdAt = "
        + getCreatedAt()
        + ", "
        + "modifiedAt = "
        + getModifiedAt()
        + ")";
  }
}
