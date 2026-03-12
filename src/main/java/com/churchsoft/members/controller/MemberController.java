package com.churchsoft.members.controller;


import com.churchsoft.global.dto.reponse.PageResult;
import com.churchsoft.members.constant.MemberStatus;
import com.churchsoft.members.constant.MinistryAffiliation;
import com.churchsoft.members.dto.response.*;
import com.churchsoft.members.entity.Member;
import com.churchsoft.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member Registration", description = "APIs for managing  member registrations")
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


    @GetMapping("/total-members")
    @Operation(
            summary = "Retrieve total number of registered church members",
            description = """
                    Returns the total number of members currently registered in the system.
                    This includes all membership statuses such as Active, Transfer, Suspended,
                    Visitors, and Inactive members.
                    
                    This endpoint is commonly used for:
                    - Church dashboard statistics
                    - Membership growth tracking
                    - Administrative reports
                    """
    )
    public Long getTotalMembers() {
        return memberService.getTotalMembers();
    }

    @GetMapping("/birthdays-this-week")
    @Operation(
            summary = "Retrieve members whose birthdays fall within the current week",
            description = """
                Returns a list of members whose birthdays occur within the current week.
                
                The response includes:
                - Member name
                - Gender
                - Date of birth
                - Image ID (profile image reference)
                - The age the member will turn
                - Number of days remaining until their birthday
                - Ministries the member belongs to
                
                The list is sorted by the closest upcoming birthday.
                
                This endpoint helps church administrators:
                - Prepare birthday announcements
                - Plan celebrations
                - Send birthday wishes to members.
                """
    )
    public List<BirthdayMemberDto> getBirthdaysThisWeek() {
        return memberService.getMembersBirthdayThisWeek();
    }


    @GetMapping("/new-members")
    @Operation(
            summary = "Retrieve newly joined members within the last 3 months",
            description = """
                    Returns a list of members who joined the church within the past three months.
            
                    """
    )
    public List<NewMemberDto> getNewMembers() {
        return memberService.getNewMembers();
    }

    @GetMapping("/ministry-leaders/{assembly}")
    @Operation(
            summary = "Retrieve ministry leaders for a specific assembly",
            description = """
                Returns members holding leadership roles within a specific church assembly.
                """
    )
    public AssemblyLeadershipDto getMinistryLeadersByAssembly(
            @PathVariable String assembly
    ) {
        return memberService.findMembersWithLeadershipRoleByAssembly(assembly);
    }

    @Operation(
            summary = "Fetch visitors pending membership confirmation",
            description = "Returns visitors who joined the church more than three months ago and require administrative confirmation."
    )
    @GetMapping("/review")
    public ResponseEntity<List<NewMemberDto>> getVisitorsDueForReview() {

        return ResponseEntity.ok(memberService.getVisitorsDueForReview());
    }


    @Operation(
            summary = "Get count of visitors pending membership review",
            description = "Returns the number of visitors who have stayed in the church for more than three months and require membership confirmation."
    )
    @GetMapping("/review/count")
    public ResponseEntity<Long> getPendingVisitorReviewCount() {

        return ResponseEntity.ok(memberService.getPendingVisitorReviewCount());
    }


    @Operation(
            summary = "Confirm visitor as active church member",
            description = "Updates a visitor's status from VISITOR to ACTIVE after administrative confirmation."
    )
    @PatchMapping("/{memberId}/activate")
    public ResponseEntity<String> activateMember(@PathVariable String memberId) {

        memberService.activateMember(memberId);

        return ResponseEntity.ok("Member successfully activated");
    }

    @Operation(
            summary = "Get all incomplete members by user ID",
            description = "Retrieves all member records created by the specified user that have not yet reached the required completion threshold (less than 70% profile completion)."
    )
    @GetMapping("/incomplete/{repUserId}")
    public ResponseEntity<List<Member>> getIncompleteMembers(@PathVariable Long repUserId) {

        List<Member> members = memberService.findByUserIdAndIsCompletedFalse(repUserId);

        return ResponseEntity.ok(members);
    }

}

