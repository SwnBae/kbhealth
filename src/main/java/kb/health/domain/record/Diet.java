package kb.health.domain.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    private String menu;
    private int calories;
}
