package com.bantads.msconta.repository.cud;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bantads.msconta.constant.StatusConta;
import com.bantads.msconta.entity.cud.Conta;
import com.bantads.msconta.entity.r.ContaR;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
	public Conta findByNumeroConta(String numeroConta);
	public Conta findByIdCliente(Long idCliente);
	public List<Conta> findByStatusConta(StatusConta statusConta);
	public List<Conta> findByStatusContaAndIdGerente(StatusConta statusConta, Long idGerente);
	
    @Query("SELECT g.id " +
            "FROM Gerente g " +
            "ORDER BY g.qntClientes ASC")
    List<Long> findGerenteComMenorNumeroDeClientes();
    
    @Query("SELECT g.id " +
	       "FROM Gerente g " + 
	       "WHERE g.cpf != :cpf " +
	       "ORDER BY g.qntClientes ASC")
	List<Long> findGerenteComMenorNumeroDeClientesExcetoCpf(@Param("cpf") String cpf);
    
    @Query("SELECT g.id " +
            "FROM Gerente g " +
            "ORDER BY g.qntClientes DESC")
    List<Long> findGerenteComMaiorNumeroDeClientes();
    
    @Query("SELECT c.id " +
            "FROM Conta c " +
            "WHERE c.idGerente = :idGerente")
     List<Long> findContasDeGerente(@Param("idGerente") Long idGerente);
    
    @Query("SELECT c " +
            "FROM Conta c " +
            "WHERE c.idGerente = :idGerente AND c.statusConta = :statusConta")
    List<Conta> findByStatusContaAndGerente(StatusConta statusConta, Long idGerente);

    @Query("SELECT c " +
           "FROM Conta c " +
           "WHERE c.idGerente = :idGerente AND c.statusConta = :statusConta " +
           "ORDER BY c.saldo DESC")
    List<Conta> findMelhoresContasDeGerente(
    		@Param("idGerente") Long idGerente,
    		@Param("statusConta") StatusConta statusConta);
    
    @Query("SELECT c.id " +
            "FROM Conta c " +
            "WHERE c.idGerente = :idGerente AND c.statusConta = :statusConta")
    List<Long> findByStatusContaAndGerenteId(StatusConta statusConta, Long idGerente);

}
