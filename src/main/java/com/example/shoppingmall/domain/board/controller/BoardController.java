package com.example.shoppingmall.domain.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.shoppingmall.domain.board.dto.BoardRequestDto;
import com.example.shoppingmall.domain.board.dto.BoardResponseDto;
import com.example.shoppingmall.domain.board.dto.BoardUpdateRequestDto;

import com.example.shoppingmall.domain.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@PostMapping("/api/stores/{storeId}/users/{userId}/boards")
	public ResponseEntity<BoardResponseDto> createPost(@PathVariable Long storeId, @PathVariable Long userId, @RequestBody BoardRequestDto boardRequestDto) {
		BoardResponseDto boardResponseDto = boardService.createPost(storeId, userId, boardRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(boardResponseDto);
	}

	@GetMapping("/api/stores/{storeId}/users/{userId}/boards/{boardId}")
	public ResponseEntity<BoardResponseDto> findById(@PathVariable Long boardId , @PathVariable Long storeId, @PathVariable Long userId) {
		BoardResponseDto boardResponseDto = boardService.findById(boardId, storeId, userId);
		return ResponseEntity.ok(boardResponseDto);
	}

	@GetMapping("/api/stores/{storeId}/boards")
	public ResponseEntity<List<BoardResponseDto>> getAll(@PathVariable Long storeId) {
		return ResponseEntity.ok(boardService.getAllByStore(storeId));
	}

	@PutMapping("/api/stores/{storeId}/boards/{boardId}")
	public ResponseEntity<String> updatePost(@PathVariable Long storeId, @PathVariable Long boardId, @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
		boardService.updatePost(storeId, boardId, boardUpdateRequestDto);
		return ResponseEntity.ok("문의사항 수정 완료");
	}


	@DeleteMapping("/api/stores/{storeId}/boards/{boardId}")
	public ResponseEntity<String> deletePost(@PathVariable Long storeId, @PathVariable Long boardId) {
		boardService.deletePost(storeId,boardId);
		return ResponseEntity.ok("문의사항 삭제 완료");
	}

	@GetMapping("/api/boards/ranking")
	public ResponseEntity<List<BoardResponseDto>> getTopBoards() {
		List<BoardResponseDto> topBoards = boardService.getTopRankedBoard();
		return ResponseEntity.ok(topBoards);
	}
}

