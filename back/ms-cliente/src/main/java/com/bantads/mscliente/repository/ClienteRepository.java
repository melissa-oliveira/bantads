package com.bantads.mscliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.mscliente.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	public Cliente findByNome(String nome);
	public Cliente findByCpf(String cpf);
	
    @Query("SELECT c FROM Cliente c WHERE c.email = :email AND c.id != :id")
    Cliente findByEmailExcetoEste(@Param("email") String email, @Param("id") Long id);

    @Query("SELECT c FROM Cliente c WHERE c.cpf = :cpf AND c.id != :id")
    Cliente findByCpfExcetoEste(@Param("cpf") String cpf, @Param("id") Long id);
}