package com.churchsoft.members.controller;


import com.churchsoft.global.dto.reponse.PageResult;
import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.response.JurisdictionsDistributionResponse;
import com.churchsoft.members.dto.response.NationalitySummaryResponse;
import com.churchsoft.members.dto.response.RegionalDistributionResponse;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/church-soft/v1.0/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Register a new Member")
    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member request) {
        return ResponseEntity.ok(memberService.createMember(request));
    }

    @Operation(summary = "Get Member by id")
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Get Member by user id")
    @GetMapping("/by-user-id/{id}")
    public ResponseEntity<Member> getMemberByUserId(@PathVariable Long id) {
        return memberService.findByUserId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get Member by member-id")
    @GetMapping("/by-member-id/{memberId}")
    public ResponseEntity<Member> getMemberByMemberId(@PathVariable String memberId) {
        return memberService.getMemberByMemberId(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());  // If not found, return 404
    }

    @Operation(summary = "Get all Members")
    @GetMapping
    public ResponseEntity<PageResult<Member>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Member> membersPage = memberService.getAllMembers(pageable);
        PageResult<Member> result = PageResult.from(membersPage, member -> member);

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Update Member Record")
    @PutMapping("update")
    public ResponseEntity<Member> updateMember(@Valid @RequestBody Member request) {
        return ResponseEntity.ok(memberService.updateMember(request));
    }
    @Operation(summary = "Delete Member by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Search Members by name - case insensitive")
    @GetMapping("/search-name")
    public ResponseEntity<List<Member>> searchMembers(@RequestParam String query) {
        List<Member> members = memberService.searchMembers(query);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "Get all Members by status (paginated)")
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResult<Member>> getMembersByStatus(
            @PathVariable MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Member> membersPage = memberService.getMembersByStatus(status, pageable);
        PageResult<Member> result = PageResult.from(membersPage, member -> member);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get all Members by ministry (paginated)")
    @GetMapping("/ministry/{ministry}")
    public ResponseEntity<PageResult<Member>> getMembersByMinistry(
            @PathVariable MinistryAffiliation ministry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Member> membersPage = memberService.getMembersByMinistry(ministry, pageable);
        PageResult<Member> result = PageResult.from(membersPage, member -> member);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Find the count of active members")
    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveMembersCount() {
        return ResponseEntity.ok(memberService.getActiveMembersCount());
    }
    @Operation(summary = "Retrieve members by their status")
    @PatchMapping("/{memberId}/status")
    public ResponseEntity<Member> updateMemberStatus(@PathVariable String memberId,
                                                     @RequestParam String status) {
        Member updatedMember = memberService.updateMemberStatus(memberId, status);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "Members registration statistics base on ministries and grand total")
    @GetMapping("/summary/nationality/{nationality}")
    public NationalitySummaryResponse getSummaryByNationality(@PathVariable String nationality) {
        return memberService.getSummaryByNationality(nationality);
    }

    @Operation(summary = "Regional Distribution by Nationality")
    @GetMapping("/summary/distribution/{nationality}")
    public JurisdictionsDistributionResponse getJurisdictionDistributionByNationality(@PathVariable String nationality) {
        return memberService.getJurisdictionDistributionByNationality(nationality);
    }

    @Operation(summary = "Total Coverage by Nationality")
    @GetMapping("/summary/coverage/nationality/{nationality}")
    public Map<String, Object> getTotalCoverageByNationality(@PathVariable String nationality) {
        return memberService.getTotalCoverageByNationality(nationality);
    }

    @GetMapping("/top-five-growing-church")
    @Operation(summary = "Get top N assemblies for a nationality with count and percentage")
    public ResponseEntity<List<Map<String, Object>>> getTopAssembliesByNationality(
            @RequestParam String nationality,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(memberService.getTopAssembliesByNationality(nationality, topN));
    }

    @GetMapping("/regional-distribution")
    @Operation(summary = "Get regional distribution of members using jurisdiction field")
    public ResponseEntity<List<RegionalDistributionResponse>> getRegionalDistribution() {
        return ResponseEntity.ok(memberService.getRegionalDistribution());
    }
}