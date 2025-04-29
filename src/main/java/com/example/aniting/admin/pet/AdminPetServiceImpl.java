package com.example.aniting.admin.pet;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.dto.PetDTO;
import com.example.aniting.entity.Pet;
import com.example.aniting.repository.PetRepository;

@Service
public class AdminPetServiceImpl implements AdminPetService {

	@Autowired
	private PetRepository petRepository;

	@Override
	public List<PetDTO> getPetList(String species, String breed, String careLevel, String isSpecial, String keyword) {
		
		List<Pet> pets = petRepository.findAll();
		
		return pets.stream()
				.filter(pet -> {
	                if (species == null || species.isEmpty()) {
	                    return true;
	                }
	                if ("기타".equals(species)) {
	                    return !( "강아지".equals(pet.getSpecies()) || "고양이".equals(pet.getSpecies()) );
	                }
	                return species.equals(pet.getSpecies());
	            })
	            .filter(pet -> breed == null || breed.isEmpty() || (pet.getBreed() != null && pet.getBreed().contains(breed)))
	            .filter(pet -> careLevel == null || careLevel.isEmpty() || careLevel.equals(pet.getCareLevel()))
	            .filter(pet -> isSpecial == null || isSpecial.isEmpty() || isSpecial.equals(pet.getIsSpecial()))
	            .filter(pet -> {
	                if (keyword == null || keyword.isEmpty()) {
	                    return true;
	                }
	                return (pet.getPetNm() != null && pet.getPetNm().contains(keyword)) ||
	                       (pet.getPersonalityTags() != null && pet.getPersonalityTags().contains(keyword));
	            })
	            .map(this::toDTO)
	            .collect(Collectors.toList());
		
	}

	@Override
	public PetDTO getPetById(Long petId) {
		Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다. ID = " + petId));
        return toDTO(pet);
	}

	@Override
	public PetDTO updatePet(Long petId, PetDTO petDTO) {
		Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다. ID = " + petId));

        pet.setPetNm(petDTO.getPetNm());
        pet.setSpecies(petDTO.getSpecies());
        pet.setBreed(petDTO.getBreed());
        pet.setPersonalityTags(petDTO.getPersonalityTags());
        pet.setCareLevel(petDTO.getCareLevel());
        pet.setIsSpecial(petDTO.getIsSpecail());
        pet.setDescription(petDTO.getDescription());

        Pet updatedPet = petRepository.save(pet);
        return toDTO(updatedPet);
	}

	@Override
	public void deletePet(Long petId) {
		if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다. ID = " + petId);
        }
        petRepository.deleteById(petId);
	}
	
	private PetDTO toDTO(Pet pet) {
	    return new PetDTO(
	        pet.getPetId(),
	        pet.getPetNm(),
	        pet.getSpecies(),
	        pet.getBreed(),
	        pet.getPersonalityTags(),
	        pet.getCareLevel(),
	        pet.getIsSpecial(),
	        pet.getCategory(),
	        pet.getDescription()
	    );
	}

	
}
