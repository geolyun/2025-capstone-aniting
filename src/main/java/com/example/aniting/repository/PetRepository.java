package com.example.aniting.repository;

import com.example.aniting.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
	
    Optional<Pet> findByPetNm(String petNm);
    int countBySpecies(String species);
    int countByIsSpecial(String yn);

    @Query("SELECT p.petNm FROM Pet p")
    List<String> findAllPetNames();

    List<Pet> findBySpeciesOrBreed(String species, String breed);

}
