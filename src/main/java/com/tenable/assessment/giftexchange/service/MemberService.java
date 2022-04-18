package com.tenable.assessment.giftexchange.service;

import com.tenable.assessment.dto.MemberDto;

import java.util.List;

public interface MemberService {
    List<MemberDto> getAllMembers();
    MemberDto getMemberById(String id);
    void createMember(MemberDto memberDto);
    MemberDto updateMember(MemberDto memberDto);
    void deleteMember(String id);
}
