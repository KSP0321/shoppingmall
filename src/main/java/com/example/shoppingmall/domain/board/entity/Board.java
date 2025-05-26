package com.example.shoppingmall.domain.board.entity;

import com.example.shoppingmall.domain.board.dto.BoardRequestDto;
import com.example.shoppingmall.domain.board.dto.BoardUpdateRequestDto;
import com.example.shoppingmall.domain.store.entity.Store;
import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.global.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@NoArgsConstructor
@Getter
public class Board extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String content;

	private int viewCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	public Board(String title, String content, Store store, User user) {
		this.title = title;
		this.content = content;
		this.store = store;
		this.user = user;
	}

	public Board createBoard(BoardRequestDto dto, Store store, User user) {
		return new Board(dto.getTitle(), dto.getContent(), store, user);
	}

	public void updatedAt(BoardUpdateRequestDto boardUpdateRequestDto) {
		if(boardUpdateRequestDto.getTitle() != null) {
			this.title = boardUpdateRequestDto.getTitle();
		}
		if(boardUpdateRequestDto.getContent() != null) {
			this.content = boardUpdateRequestDto.getContent();
		}
	}

	public void increaseViewCount(int amount) {
		this.viewCount += amount;
	}

}