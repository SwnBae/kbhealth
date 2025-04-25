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

    @Column(length = 50, nullable = false)
    private String menu;

    @Column(nullable = false)
    private int calories;
}
