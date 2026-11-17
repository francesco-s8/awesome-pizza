package it.s8.awesomepizza.entity;

import jakarta.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.*;

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

  @Column(name = "available")
  @Builder.Default private Boolean isAvailable = true;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Ingredient that = (Ingredient) o;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getVersion(), that.getVersion()) && Objects.equals(getIsAvailable(), that.getIsAvailable());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getVersion(), getIsAvailable());
  }
}
