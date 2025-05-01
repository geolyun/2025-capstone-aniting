package com.example.aniting.admin.pet;

import java.util.List;
import java.util.Map;

import com.example.aniting.dto.PetDTO;

public interface AdminPetService {

	public List<PetDTO> getPetList(String species, String breed, String careLevel, String isSpecial, String keyword);
	public PetDTO getPetById(Long petId);
	public PetDTO updatePet(Long petId, PetDTO petDTO);
	public void deletePet(Long petId);
	public List<Map<String, Object>> findAllPets();

}
