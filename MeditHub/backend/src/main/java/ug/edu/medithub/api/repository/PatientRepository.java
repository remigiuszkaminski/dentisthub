package ug.edu.medithub.api.repository;

import ug.edu.medithub.api.models.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    @Query("{ 'email' : ?0 }")
    List<Patient> findByEmail(String email);

    @Query("{ 'name' : ?0 }")
    List<Patient> findByName(String name);

    @Query("{ 'id' : ?0 }")
    Optional<Patient> findById(String id);
}
