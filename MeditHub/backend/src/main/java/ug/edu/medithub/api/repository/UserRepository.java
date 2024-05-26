package ug.edu.medithub.api.repository;

import ug.edu.medithub.api.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
    @Query("{ 'email' : ?0 }")
    List<User> findByEmail(String email);

    @Query("{ 'username' : ?0 }")
    List<User> findByUsername(String username);
}
