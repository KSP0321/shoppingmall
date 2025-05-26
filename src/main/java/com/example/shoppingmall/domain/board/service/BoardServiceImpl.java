package com.example.shoppingmall.domain.board.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.shoppingmall.domain.board.dto.BoardRequestDto;
import com.example.shoppingmall.domain.board.dto.BoardResponseDto;
import com.example.shoppingmall.domain.board.dto.BoardUpdateRequestDto;
import com.example.shoppingmall.domain.board.entity.Board;
import com.example.shoppingmall.domain.board.repository.BoardRepository;
import com.example.shoppingmall.domain.store.entity.Store;
import com.example.shoppingmall.domain.store.repository.StoreRepository;
import com.example.shoppingmall.domain.user.entity.User;
import com.example.shoppingmall.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

	private final BoardRepository boardRepository;
	private final StoreRepository storeRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final UserRepository userRepository;

	@Override
	public BoardResponseDto createPost(Long storeId, Long userId, BoardRequestDto boardRequestDto) {

		if(boardRequestDto.getTitle()==null || boardRequestDto.getTitle().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 필수입니다.");
		}

		if(boardRequestDto.getContent() == null || boardRequestDto.getContent().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 필수입니다.");
		}

		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("스토어를 찾을 수 없습니다."));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));


		Board board = new Board(boardRequestDto.getTitle(), boardRequestDto.getContent(), store, user);
		Board saved = boardRepository.save(board);

		return new BoardResponseDto(
				saved.getId(),
				saved.getTitle(),
				saved.getContent()
		);
	}


	@Override
	public BoardResponseDto findById(Long id, Long storeId, Long userId) {
		Board board = boardRepository.findByIdAndStore_IdAndUser_Id(id, storeId, userId).orElseThrow(
				() -> new RuntimeException("문의사항이 존재하지 않습니다")
		);

		String viewedKey = "board:viewed:" + userId + ":" + id;
		String rankingKey = "board:ranking";

		//중복조회방지
		if (!Boolean.TRUE.equals(redisTemplate.hasKey(viewedKey))) {
			redisTemplate.opsForValue().increment(viewedKey);
			redisTemplate.expire(viewedKey, Duration.ofMinutes(30));
			redisTemplate.opsForZSet().incrementScore(rankingKey, id.toString(), 1.0);
		}

		return new BoardResponseDto(board);
	}


	@Override
	public List<BoardResponseDto> getAllByStore(Long storeId) {
		Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new IllegalArgumentException("스토어를 찾을 수 없습니다."));

		List<Board> boardList = boardRepository.findAllByStore(store);
		List<BoardResponseDto> responseDtoList = new ArrayList<>();

		for(Board board : boardList) {
			BoardResponseDto boardResponseDto = new BoardResponseDto(
					board.getId(), board.getTitle(), board.getContent()
			);

			responseDtoList.add(boardResponseDto);
		}
		return responseDtoList;
	}


	@Transactional
	@Override
	public void updatePost(Long storeId, Long id, BoardUpdateRequestDto boardUpdateRequestDto) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("문의사항을 찾을 수 없습니다"));

		board.updatedAt(boardUpdateRequestDto);
	}


	@Override
	public void deletePost(Long storeId, Long id) {
		Board board = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("문의사항을 찾을 수 없습니다"));

		boardRepository.delete(board);
	}



	@Override
	public List<BoardResponseDto> getTopRankedBoard() {
		Set<ZSetOperations.TypedTuple<String>> top = redisTemplate.opsForZSet()
				.reverseRangeWithScores("board:ranking", 0, 9);

		List<Long> boardIds = top.stream()
				.map(ZSetOperations.TypedTuple::getValue)
				.map(Long::valueOf)
				.toList();

		List<Board> boards = boardRepository.findAllById(boardIds);


		//조회된 boardId 순서와 일치시키기 위해 정렬
		return boardIds.stream()
				.map(id -> boards.stream()
						.filter(b -> b.getId().equals(id))
						.findFirst()
						.map(BoardResponseDto::new)
						.orElse(null))
				.filter(dto -> dto != null)
				.collect(Collectors.toList());
	}


	@Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
	@Transactional
	public void flushRankingToDatabase() {
		String rankingKey = "board:ranking";

		// 상위 랭킹 게시글 100개 가져오기
		Set<String> boardIds = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, 99);

		if (boardIds == null || boardIds.isEmpty())
			return;

		List<Long> ids = boardIds.stream().map(Long::valueOf).toList();
		List<Board> boards = boardRepository.findAllById(ids);

		for (Board board : boards) {
			Double score = redisTemplate.opsForZSet().score(rankingKey, board.getId().toString());
			if (score != null) { // 예) 기존 DB조회수에 score만큼 추가
				board.increaseViewCount(score.intValue());
			}
		}

		boardRepository.saveAll(boards);

		// 캐시 삭제
		redisTemplate.delete(rankingKey); // ZSET 초기화
		redisTemplate.delete(redisTemplate.keys("board:viewed:*"));
	}
}