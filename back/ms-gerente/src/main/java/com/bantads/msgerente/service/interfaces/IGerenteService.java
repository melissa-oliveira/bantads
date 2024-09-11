package com.bantads.msgerente.service.interfaces;

import com.bantads.msgerente.entity.Gerente;


public interface IGerenteService extends IBaseService<Long, Gerente> {
	Gerente findByNome(String nome);

	Gerente findById(Long id);
}
