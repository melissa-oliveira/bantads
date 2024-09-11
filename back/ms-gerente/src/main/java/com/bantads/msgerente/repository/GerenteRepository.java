package com.bantads.msgerente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.msgerente.entity.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
	Gerente findByNome(String nome);
	Gerente findByEmail(String email);
	Gerente findByCpf(String cpf);
	
    @Query("SELECT g FROM Gerente g WHERE g.email = :email AND g.id != :id")
    Gerente findByEmailExcetoEste(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT g FROM Gerente g WHERE g.cpf = :cpf AND g.id != :id")
    Gerente findByCpfExcetoEste(@Param("cpf") String cpf, @Param("id") Long id);
}