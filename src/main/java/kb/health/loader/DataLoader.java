package kb.health.loader;

import kb.health.loader.service.DietLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DietLoadService dietLoadService;

    @Override
    public void run(String... args) {
//        int count = dietLoadService.saveAllFromApi();
//        System.out.println("총 저장된 식품 수: " + count);
    }
}

