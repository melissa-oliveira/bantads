package com.bantads.msconta.repository.cud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.dto.GerenteDashboardDTO;
import com.bantads.msconta.entity.cud.Gerente;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
	public Gerente findByCpf(String cpf);
	
	@Query("SELECT new com.bantads.msconta.dto.GerenteDashboardDTO(g.id, g.nome, g.qntClientes, " +
	       "SUM(CASE WHEN c.saldo >= 0 THEN c.saldo ELSE 0 END), " +
	       "SUM(CASE WHEN c.saldo < 0 THEN c.saldo ELSE 0 END)) " +
	       "FROM Gerente g " +
	       "LEFT JOIN Conta c ON g.id = c.idGerente " +
	       "GROUP BY g.id, g.nome, g.qntClientes")
	List<GerenteDashboardDTO> findGerenteDashboardData();
}
