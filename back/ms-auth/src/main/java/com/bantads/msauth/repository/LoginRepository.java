package com.bantads.msauth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.bantads.msauth.entity.Login;

@Repository
public interface LoginRepository extends MongoRepository<Login, String> {
	public Login findByEmail(String email);
	public Login findByCpf(String cpf);
    Login findByEmailAndIdNot(String email, String id);
    Login findByCpfAndIdNot(String cpf, String id);
}