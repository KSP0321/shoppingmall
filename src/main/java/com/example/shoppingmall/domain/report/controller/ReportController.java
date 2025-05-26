package com.example.shoppingmall.domain.report.controller;

import com.example.shoppingmall.domain.bookmark.controller.BaseController;
import com.example.shoppingmall.domain.report.dto.request.CreateReportRequest;
import com.example.shoppingmall.domain.report.dto.response.FindReportResponse;
import com.example.shoppingmall.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController extends BaseController {

    private final ReportService reportService;

    // 사용자가 가게 신고작성
    @PostMapping("/api/stores/{storeId}/reports")
    ResponseEntity<Void> createReport(@PathVariable Long storeId,
                                      @RequestBody CreateReportRequest request
    ){
        Long userId = getCurrentUserId();
        reportService.createReport(userId,storeId,request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // 특정 가게에 대한 신고목록
    @GetMapping("/api/stores/{storeId}/reports")
    ResponseEntity<List<FindReportResponse>> findAll(@PathVariable Long storeId){
        return ResponseEntity.ok(reportService.findAllReport(storeId));
    }
}

