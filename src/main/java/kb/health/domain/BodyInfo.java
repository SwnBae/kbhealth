package kb.health.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter(AccessLevel.PROTECTED)
public class BodyInfo {

    @Column(name = "height_cm", nullable = false)
    private double height; // cm

    @Column(name = "weight_kg", nullable = false)
    private double weight; // kg

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private int age;
}

