package com.tenable.assessment.giftexchange.controller;

import com.tenable.assessment.api.FamilyMembersApi;
import com.tenable.assessment.dto.MemberDto;
import com.tenable.assessment.giftexchange.service.impl.MemberServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;

@RestController
public class FamilyMembersController implements FamilyMembersApi {

    private final MemberServiceImpl memberService;

    public FamilyMembersController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @Override
    public ResponseEntity<List<MemberDto>> membersGet() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @Override
    public ResponseEntity<Void> membersIdDelete(String id) {

        memberService.deleteMember(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<MemberDto> membersIdGet(String id) {

        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @Override
    public ResponseEntity<MemberDto> membersIdPut(String id, MemberDto memberDto) {
        //override id from path param
        memberDto.setId(id);
        return ResponseEntity.ok(memberService.updateMember(memberDto));
    }

    @Override
    public ResponseEntity<Void> membersPost(MemberDto memberDto) {
        memberService.createMember(memberDto);
        return ResponseEntity.status(HttpStatus.CREATED).build( );
    }
}
