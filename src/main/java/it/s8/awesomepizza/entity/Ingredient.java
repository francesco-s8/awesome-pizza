package it.s8.awesomepizza.entity;

import jakarta.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "ingredients")
public class Ingredient extends EntityInfo {

  @Id
  @Column(name = "ingredient_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(
      name = "ingredient_id_seq",
      sequenceName = "ingredient_id_seq",
      allocationSize = 1)
  private Long id;

  @Column(name = "name")
  private String name;

  @Builder.Default
  @ManyToMany(mappedBy = "ingredients", fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<Pizza> pizzas = new LinkedList<>();

  @Version
  @Column(name = "version")
  private Integer version;

  @Override
  public boolean equals(Object o) {
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
    Ingredient ingredient = (Ingredient) o;
    return getId() != null && Objects.equals(getId(), ingredient.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId());
  }
}
