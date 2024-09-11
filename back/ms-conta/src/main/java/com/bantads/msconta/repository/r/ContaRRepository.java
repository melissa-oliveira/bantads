package com.bantads.msconta.repository.r;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.entity.r.ContaR;

@Repository
public interface ContaRRepository extends JpaRepository<ContaR, Long> {

    public ContaR findByNumeroConta(String numeroConta);
    public ContaR findByCliente_Id(Long idCliente);
    public List<ContaR> findByStatusConta(StatusConta statusConta);
    
    @Query("SELECT c " +
            "FROM ContaR c " +
            "WHERE c.gerente.id = :idGerente AND c.statusConta = :statusConta " +
            "ORDER BY c.cliente.nome ASC")
    public List<ContaR> findByStatusContaAndGerente(StatusConta statusConta, Long idGerente);

    @Query("SELECT c " +
           "FROM ContaR c " +
           "WHERE c.gerente.id = :idGerente AND c.statusConta = :statusConta " +
           "ORDER BY c.saldo DESC")
    List<ContaR> findMelhoresContasDeGerente(
    		@Param("idGerente") Long idGerente,
    		@Param("statusConta") StatusConta statusConta);
}