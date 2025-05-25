package kb.health.loader.service;

import kb.health.domain.record.Diet;
import kb.health.loader.client.FoodApiClient;
import kb.health.loader.dto.FoodItem;
import kb.health.loader.mapper.DietMapper;
import kb.health.repository.record.DietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietLoadService {

    private final DietRepository dietRepository;
    private final FoodApiClient foodApiClient;

    @Transactional
    public int saveAllFromApi() {
        List<FoodItem> items = foodApiClient.getAllFoods();
        List<Diet> diets = items.stream()
                .map(DietMapper::toEntity)
                .toList();
        dietRepository.saveAll(diets);
        return diets.size();
    }
}

