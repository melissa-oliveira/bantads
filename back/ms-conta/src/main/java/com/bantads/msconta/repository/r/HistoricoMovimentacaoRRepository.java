package com.bantads.msconta.repository.r;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.entity.r.HistoricoMovimentacaoR;

@Repository
public interface HistoricoMovimentacaoRRepository extends JpaRepository<HistoricoMovimentacaoR, Long> {
	public List<HistoricoMovimentacaoR> findAllByIdClienteOrigemOrIdClienteDestino(Long idClienteOrigem, Long idClienteDestino);

	@Query("SELECT h "
			+ "FROM HistoricoMovimentacaoR h "
			+ "WHERE ("
				+ "(h.dataHora BETWEEN :inicio AND :fim AND h.idClienteOrigem = :idClienteOrigem) OR "
				+ "(h.dataHora BETWEEN :inicio AND :fim AND h.idClienteDestino = :idClienteDestino))")
	public List<HistoricoMovimentacaoR> findAllByDataHoraBetweenAndIdClienteOrigemOrIdClienteDestino(
	    @Param("inicio") String inicio, 
	    @Param("fim") String fim, 
	    @Param("idClienteOrigem") Long idClienteOrigem, 
	    @Param("idClienteDestino") Long idClienteDestino
	);

}