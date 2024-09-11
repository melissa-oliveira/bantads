package com.bantads.msconta.repository.r;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.dto.GerenteDashboardDTO;
import com.bantads.msconta.entity.r.GerenteR;

@Repository
public interface GerenteRRepository extends JpaRepository<GerenteR, Long> {
	public GerenteR findByCpf(String cpf);

	@Query("SELECT new com.bantads.msconta.dto.GerenteDashboardDTO(g.id, g.nome, g.qntClientes, " +
            "SUM(CASE WHEN c.saldo >= 0 THEN c.saldo ELSE 0 END), " +
            "SUM(CASE WHEN c.saldo < 0 THEN c.saldo ELSE 0 END)) " +
            "FROM GerenteR g " +
            "JOIN g.contas c " +
            "GROUP BY g.id, g.nome, g.qntClientes")
     List<GerenteDashboardDTO> findGerenteDashboardData();
}
