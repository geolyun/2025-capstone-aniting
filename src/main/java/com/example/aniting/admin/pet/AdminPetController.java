package com.example.aniting.admin.pet;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.PetDTO;

@RestController
@RequestMapping("/api/admin/pets")
public class AdminPetController {

	@Autowired
	private AdminPetService adminPetService;
	
	@GetMapping
    public List<PetDTO> getPetList(@RequestParam(required = false) String species,
                                   @RequestParam(required = false) String breed,
                                   @RequestParam(required = false) String careLevel,
                                   @RequestParam(required = false) String isSpecial,
                                   @RequestParam(required = false) String keyword) {
        return adminPetService.getPetList(species, breed, careLevel, isSpecial, keyword);
    }
	
	@GetMapping("/{petId}")
    public PetDTO getPetById(@PathVariable Long petId) {
        return adminPetService.getPetById(petId);
    }

    @PutMapping("/{petId}")
    public PetDTO updatePet(@PathVariable Long petId, @RequestBody PetDTO petDTO) {
        return adminPetService.updatePet(petId, petDTO);
    }

    @DeleteMapping("/{petId}")
    public void deletePet(@PathVariable Long petId) {
        adminPetService.deletePet(petId);
    }
    
    @DeleteMapping("/batch")
    public void deletePets(@RequestBody List<Long> petIds) {
        adminPetService.deletePets(petIds);
    }
    
    @GetMapping("/all")
    public List<Map<String, Object>> getAllPets() {
        return adminPetService.findAllPets();
    }
	
}
