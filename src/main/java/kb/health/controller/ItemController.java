package kb.health.controller;

import kb.health.Service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.domain.record.Diet;
import kb.health.domain.request.DietRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final RecordService recordService;

    @GetMapping
    public List<Diet> list(@LoginMember CurrentMember currentMember) {
        return recordService.getDietList();
    }

    @PostMapping
    public void create(@LoginMember CurrentMember currentMember, @RequestBody DietRequest dietRequest) {
        recordService.addDiet(dietRequest);
    }

    @GetMapping("/{dietId}")
    public Diet getItem(@LoginMember CurrentMember currentMember, @PathVariable Long dietId) {
        return recordService.getDiet(dietId);
    }

    @PutMapping("/{dietId}")
    public void update(@LoginMember CurrentMember currentMember, @PathVariable Long dietId, @RequestBody DietRequest dietRequest) {
        recordService.updateDiet(dietId, dietRequest);
    }

    @DeleteMapping("/{dietId}")
    public void delete(@LoginMember CurrentMember currentMember, @PathVariable Long dietId) {
        recordService.deleteDiet(dietId);
    }
}
