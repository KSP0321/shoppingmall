package com.example.shoppingmall.domain.board.scheduler;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shoppingmall.domain.board.entity.Board;
import com.example.shoppingmall.domain.board.repository.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardRankingScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final BoardRepository boardRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    @Transactional
    public void flushRankingToDatabase() {
        String rankingKey = "board:ranking";

        Set<String> boardIds = redisTemplate.opsForZSet()
                .reverseRange(rankingKey, 0, 99);

        if (boardIds == null || boardIds.isEmpty()) return;

        List<Long> ids = boardIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<Board> boards = boardRepository.findAllById(ids);

        for (Board board : boards) {
            Double score = redisTemplate.opsForZSet().score(rankingKey, board.getId().toString());
            if (score != null) {
                board.increaseViewCount(score.intValue());
            }
        }

        boardRepository.saveAll(boards);

        // 캐시 삭제
        redisTemplate.delete(rankingKey);
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("board:viewed:*")));
    }
}