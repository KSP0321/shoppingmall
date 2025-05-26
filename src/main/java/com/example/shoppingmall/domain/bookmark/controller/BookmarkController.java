package com.example.shoppingmall.domain.bookmark.controller;

import com.example.shoppingmall.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.shoppingmall.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor

public class BookmarkController extends BaseController {

    private final BookmarkService bookmarkService;

    //  즐겨찾기 추가
    @PostMapping("/api/stores/{storeId}/bookmarks")
    public ResponseEntity<Void> createBookmark(@PathVariable Long storeId) {
        Long userId = getCurrentUserId(); // JWT로부터 유저 ID 추출
        bookmarkService.createBookmark(userId, storeId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //즐겨찾기 목록 조회
    @GetMapping("/api/auths/me/bookmarks")
    public ResponseEntity<List<BookmarkResponseDto>> findBookmark() {
        Long userId = getCurrentUserId();
        List<BookmarkResponseDto> result = bookmarkService.findAllByUser(userId);
        return ResponseEntity.ok(result);
    }

    //  즐겨찾기 삭제
    @DeleteMapping("/api/stores/{storeId}/bookmarks")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long storeId) {
        Long userId = getCurrentUserId();
        bookmarkService.deleteBookmark(userId, storeId);
        return ResponseEntity.noContent().build();
    }

}
