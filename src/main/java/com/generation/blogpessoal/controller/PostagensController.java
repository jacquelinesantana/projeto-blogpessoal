package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
public class PostagensController {

	@Autowired
	private PostagemRepository postagemRepository;
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){
		return ResponseEntity.ok(postagemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Postagem> getById(@PathVariable Long id){
		return postagemRepository.findById(id)
				.map(resposta-> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
				
	}
	
	//diferenciamos o mapping - consiguir consumir depois 
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
		//metodo que retorna uma lista de postagens
		/*
		 * @PathVariable - recebe o valor da variavel pela url para dentro da variavel titulo
		 */
		return ResponseEntity.ok(postagemRepository.
				findAllByTituloContainingIgnoreCase(titulo));
		//o retorno seria um status code OK e o corpo será o que ele achar no metodo 
		//findByTitulo -> ou seja, retorna os registros que contiverem a string no título
		
	}
	
	//Verbo post -> insert
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){
		if (temaRepository.existsById(postagem.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(postagemRepository.save(postagem));
			
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
	}
	
	//verbo put -> update
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){
		if (postagemRepository.existsById(postagem.getId())){
			
			if (temaRepository.existsById(postagem.getTema().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(postagemRepository.save(postagem));
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
			
		}			
			
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
	}
	
	
	/*
	 * retorno caso o meu metodo execute com sucesso DELETAR
	 */
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}") // verbo -> deletando 
	public void delete(@PathVariable Long id) {
		//tratando previnindo erros do retorno da postagem que foi pesquisada
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())//confirmando é vazio
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			//caso verdadeiro vamos executar essa excessão forçando o status code Not Found
		
		postagemRepository.deleteById(id);
		//executa o delete da JPA para o id informado
	}
	
	
	
	
	
}
