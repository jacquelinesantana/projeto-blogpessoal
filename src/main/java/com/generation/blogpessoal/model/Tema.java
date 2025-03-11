package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity //indicação que isso é uma nova tabela no banco de dados
@Table(name="tb_temas")//indicar o nome dessa tabela no bd
public class Tema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//NotNull - não permite passar o campo vazio
	//
	@NotNull(message = "O Atributo Descrição é obrigatório")
	private String descricao;
	
	/*
	 * Tema => one / Postagem => Many
	 * fetch = Lazy pequisa preguiçosa
	 * 
	 * Cascade = como vai se comportar a tabela relacionada 
	 * em momentos de deletar dados
	 * 
	 */
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "tema"
			, cascade = CascadeType.REMOVE )
	@JsonIgnoreProperties("tema")
	private List<Postagem> postagem;
	/*
	 * tornamos a postagem uma lista pq 
	 * podemos ter mais de uma postagem para o mesmo tema
	 */
	
	
	public List<Postagem> getPostagem() {
		return postagem;
	}
	public void setPostagem(List<Postagem> postagem) {
		this.postagem = postagem;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	

}
