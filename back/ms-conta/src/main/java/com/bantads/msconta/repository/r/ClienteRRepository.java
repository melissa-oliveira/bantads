package com.bantads.msconta.repository.r;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.entity.r.ClienteR;

@Repository
public interface ClienteRRepository extends JpaRepository<ClienteR, Long> {
	ClienteR findByCpf(String cpf);
}
