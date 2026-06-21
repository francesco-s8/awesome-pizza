package it.s8.awesomepizza.entity;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pizza_order")
public class PizzaOrder extends EntityInfo {

  @Id
  @Column(name = "pizza_order_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "pizza_order_seq", sequenceName = "pizza_order_seq")
  private Long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "order_status")
  private String orderStatus;

  @Builder.Default
  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "pizza_order_items",
      joinColumns = @JoinColumn(name = "pizza_order_id"),
      inverseJoinColumns = @JoinColumn(name = "pizza_id"))
  private List<Pizza> pizzaList = new LinkedList<>();

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
    PizzaOrder pizzaOrder = (PizzaOrder) o;
    return getId() != null && Objects.equals(getId(), pizzaOrder.getId());
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
        + "username = "
        + getUsername()
        + ", "
        + "orderStatus = "
        + getOrderStatus()
        + ", "
        + "createdAt = "
        + getCreatedAt()
        + ", "
        + "modifiedAt = "
        + getModifiedAt()
        + ", "
        + "version = "
        + getVersion()
        + ")";
  }
}
