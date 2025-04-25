package kb.health.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentMember {
    Long id;
    String account;
}
